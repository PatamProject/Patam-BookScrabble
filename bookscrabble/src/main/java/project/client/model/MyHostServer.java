package project.client.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import project.client.Error_Codes;
import project.client.MyLogger;

public class MyHostServer{
    private static MyHostServer myHostServer = null; //singleton
    private HostSideHandler requestHandler;
    HashMap<String, Socket> connectedClients; // HashMap to keep track of connected clients by name
    ExecutorService threadPool;
    private int hostPort, bookScrabblePort; // Ports
    private String BookScrabbleServerIP; // IP
    private volatile Integer playerCount = 0; //Will be given as an ID to the player, we allow it to go above MAX_CLIENTS because we only check if it's 0 or not
    private volatile boolean stopServer = false;
    public volatile boolean isGameRunning = false;
    private final int MAX_CLIENTS = 4;
 
    public static MyHostServer getHostServer() //singleton design
    {
        if(myHostServer != null)
            return myHostServer;
        myHostServer = new MyHostServer();
        return myHostServer;
    }

    private MyHostServer() { // Ctor
        requestHandler = new HostSideHandler();
        threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        connectedClients = new HashMap<>();
        stopServer = false;
        isGameRunning = false;
    }

    public void start(int hostPort, int bsPort, String bs_IP) {
        this.hostPort = hostPort;
        bookScrabblePort = bsPort;
        BookScrabbleServerIP = bs_IP;
        if(!msgToBSServer("S,hello")) //Check connection to BS server
        {
            MyLogger.logError("Unable to connect to BookScrabble server!");
            MyLogger.println("Host did not start! \nUse '!start' to try again or change BookScrabble server IP and or port.");
            return;
        }
        //Connected to BS server    
        new Thread(()-> {
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void run() throws Exception {
        ServerSocket hostSocket = new ServerSocket(hostPort);
        MyLogger.println("Host is listening on port " + hostPort);
        while (!stopServer) {
            try {
                Socket clientSocket = hostSocket.accept();
                MyLogger.println("A new client has connected: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
                threadPool.execute(() -> handleClientConnection(clientSocket)); //Handle client in a separate thread
            } catch (SocketTimeoutException e) {
                MyLogger.println("Socket exception in MyHostServer: " + e.getMessage());
                //Host is still listening
            }
        }

        // Close all client connections
        for (Socket clientSocket : connectedClients.values()) {
            closeConnection(clientSocket, new PrintWriter(clientSocket.getOutputStream()), new Scanner(clientSocket.getInputStream()));
        }

        connectedClients.clear();
        hostSocket.close();
    }

    private void handleClientConnection(Socket clientSocket) //Runs in a separate thread
    {
        Scanner in = null;
        PrintWriter out = null;
        while (clientSocket.isConnected())
        {
            try {
                in = new Scanner(clientSocket.getInputStream());
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                
                //Client is connected and sent a request
                String request = in.nextLine(); // "'id':'senderName'&'commandName':'args1','args2',...'"
                MyLogger.println("Host received a new request: " + request);
                String[] user_body_split = request.split("&");
                if(user_body_split.length != 2){ //Must contain a sender name and a body
                    throwError(Error_Codes.UNKNOWN_CMD, out);
                    continue;  
                }
                String[] id_sender = user_body_split[0].split(":");
                if(id_sender.length != 2){ //Must contain a sender name and a body
                    throwError(Error_Codes.UNKNOWN_CMD, out);
                    continue;  
                }
                String id = id_sender[0]; //Sender's ID
                String sender = id_sender[1]; //Sender's name

                //Now we check the client to see if he's new or not and if allowed to join
                if(connectedClients.get(sender) == null && id.equals("0") && connectedClients.size() < MAX_CLIENTS && !isGameRunning){ 
                    //Add new client to the HashMap and to the game
                    connectedClients.put(sender, clientSocket);
                    playerCount++; //Increment player count
                    ArrayList<String> args = new ArrayList<>(){{add(sender); add(playerCount.toString());}}; //Add sender and ID
                    args.addAll(connectedClients.keySet()); //Add all players in game to the arguments
                    requestHandler.handleClient(sender, "join", args.toArray(new String[args.size()]), clientSocket.getOutputStream());
                    continue; //Add new player and continue
                } else if(connectedClients.get(sender) == null && id.equals("0") && (connectedClients.size() >= MAX_CLIENTS || isGameRunning)) { //Server is full / game has started
                    if(isGameRunning)
                    {
                        throwError(Error_Codes.GAME_STARTED, out);
                        closeConnection(clientSocket, out, in);
                    }
                    else //Server full
                    {
                        throwError(Error_Codes.SERVER_FULL, out);
                        closeConnection(clientSocket, out, in);
                    }
                    continue; 
                } else if(connectedClients.get(sender) != null && id.equals("0")){ //Name taken
                    throwError(Error_Codes.NAME_TAKEN, out);
                    closeConnection(clientSocket, out, in);
                }
                //Now we have a known client.
                String[] command_args = user_body_split[1].split(":");
                String commandName = command_args[0];
                String[] tmp, args;
                if(command_args.length == 1) //No arguments (startGame, endGame, join, leave, skipTurn)
                {
                    args = new String[1];
                    args[0] = sender;
                } 
                else //Arguments exist
                {
                    tmp = command_args[1].split(","); //Split the arguments 
                    args = new String[tmp.length + 1]; //Add the sender name to the arguments
                    args[0] = sender;
                    for (int i = 1; i < args.length; i++)
                        args[i] = tmp[i-1];  
                }
                //Check request
                ArrayList<String> acceptableCommands = new ArrayList<>(){{
                    add("startGame");
                    add("endGame");
                    add("join");
                    add("leave");
                    add("skipTurn");
                    add("C"); //challenge
                    add("Q"); //query
                }};


                if(!sender.equals(ClientModel.getName()) && (commandName.equals("startGame") || commandName.equals("endGame")))
                    throwError(Error_Codes.ACCESS_DENIED, out); //Host only commands
                else if (commandName.equals("startGame") && connectedClients.size() < 2)
                    throwError(Error_Codes.NOT_ENOUGH_PLAYERS, out);
                else if(commandName.equals("leave")){
                    requestHandler.handleClient(sender, commandName, args ,clientSocket.getOutputStream());
                    break;
                } else if(acceptableCommands.contains(commandName)) //Known command
                    requestHandler.handleClient(sender, commandName, args ,connectedClients.get(sender).getOutputStream());
                else //Unknown command
                    throwError(Error_Codes.UNKNOWN_CMD, out);
                
            } catch (Exception e) { //Client disconnected
                closeConnection(clientSocket, out, in); //Remove client from the HashMap
                return;
            }
        } //End of while loop
        closeConnection(clientSocket, out, in); //Remove client from the HashMap
    }
    
    private void closeConnection(Socket clientSocket, PrintWriter out, Scanner in) {
        try {
            String name = "";
            for (String client : connectedClients.keySet())
                if(connectedClients.get(client).equals(clientSocket))
                {
                    name = client;
                    if(out != null)
                        out.close();
                    if(in != null)
                        in.close();
                    clientSocket.close();
                    break;
                }
            
            connectedClients.remove(name);
            MyLogger.println("Player " + name + " has left the game.");
        } catch (IOException e) {
            MyLogger.println("Error closing connection: " + e.getMessage());
        }
    }

    Boolean msgToBSServer(String message) { // A method that communicates with the BookScrabbleServer
        String response = null;
        try {
            Socket socket = new Socket(BookScrabbleServerIP, bookScrabblePort); // A socket for a single use
            try (Scanner inFromServer = new Scanner(socket.getInputStream());
                PrintWriter outToServer = new PrintWriter(socket.getOutputStream())) {
                outToServer.println(message); // Sends the message
                outToServer.flush();
                if(inFromServer.hasNext())
                    response = inFromServer.next(); // The response from the BookScrabbleServer

                if(response == null) //Invalid response from the BookScrabbleServer
                {
                    MyLogger.logError("Invalid response from the BookScrabbleServer!");
                    return false;
                    //Failed to communicate with the BookScrabbleServer
                } else if(message.equals("S,hello")) //First message from the BookScrabbleServer
                    return response.equals("Hello");
                else //The BookScrabbleServer responded with true/false
                    return response.equals("true");
            } catch (IOException e) {
                MyLogger.logError("Error in MyHostServer: " + e.getMessage());
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            MyLogger.logError("Error with BS communication: " + e.getMessage());
        }
        //Failed to communicate with the BookScrabbleServer
        return false;
    }

    public static void updateAll(String message, String doNotSendToPlayer) { // A method to update all relevant clients with new information
            
        for (String player : MyHostServer.getHostServer().connectedClients.keySet()) {
            if(!player.equals(doNotSendToPlayer))
            {
                try{
                    PrintWriter out = new PrintWriter(MyHostServer.getHostServer().connectedClients.get(player).getOutputStream());
                    out.println(message);
                    out.flush();
                } catch (Exception e) {
                    MyLogger.logError("Error updating " + player + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public static void sendUpdate(String msg, String player) { // A method to send a message to a specific client
        try{
            PrintWriter out = new PrintWriter(MyHostServer.getHostServer().connectedClients.get(player).getOutputStream());
            out.println(msg);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean startGame() { // A method to start the game
        if(connectedClients.size() > 1)
        {
            try {
                requestHandler.handleClient(ClientModel.getName(), "startGame", new String[]{ClientModel.getName()}, connectedClients.get(ClientModel.getName()).getOutputStream());
            } catch (IOException e) {
                MyLogger.logError("Failed to start game on MyHostServer: " + e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        else
        {
            MyLogger.println("Not enough players to start the game");
            return false;
        }
    }

    void throwError(String error, PrintWriter out) { // A method to send an error message to a client
        MyLogger.logError(error);
        try{
            out.println(error);
        } catch (Exception e) {
            MyLogger.logError("ThrowError failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String[] getConnectedClients() {return connectedClients.keySet().toArray(new String[connectedClients.size()]);}

    public void close() // A method to close the hostServer
    {
        MyLogger.println("Stopping server...");
        stopServer = true;
        playerCount = 0;
    } 
}