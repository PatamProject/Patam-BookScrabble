package project.client.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import project.client.Error_Codes;
import project.client.MyLogger;

public class MyHostServer{
    private static MyHostServer myHostServer = null; //singelton
    private HostSideHandler requestHandler;
    private int hostPort, bookScrabblePort; // Ports
    private String BookScrabbleServerIP; // IP
    HashMap<String, Socket> connectedClients; // HashMap to keep track of connected clients by name
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
            MyLogger.log("Host did not start! \nUse 'start' to try again or change BookScrabble server IP and port using 'setBSIP' and 'setBSPort'");
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
        hostSocket.setSoTimeout(30000);
        MyLogger.log("Host is listening on port " + hostPort);
        while (!stopServer) {
            try {
                Socket clientSocket = hostSocket.accept();
                Scanner in = new Scanner(clientSocket.getInputStream());
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                if(in.hasNextLine()){
                    String request = in.nextLine(); // "'id':'senderName'&'commandName':'args1','args2',...'"
                    MyLogger.log("Host received: " + request);
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
                    //Now we have a known client and will process all future requests in a separate thread
                    new Thread(() -> handleClientConnection(clientSocket,sender,id)).start();
                }
            } catch (SocketTimeoutException e) {
                MyLogger.log("Socket exception: " + e.getMessage());
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

    private void handleClientConnection(Socket clientSocket, String clientName, String id) //Runs in a separate thread
    {
        try 
        {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
            Scanner in = new Scanner(clientSocket.getInputStream());  
            // Handle client requests
            while (clientSocket.isConnected()) 
            {
                if (in.hasNextLine()) 
                {
                    String request = in.nextLine(); // "'id':'sender'&'commandName':'args1','args2',...'"
                    String id_sender = request.split("&")[0];
                    String[] command_args = request.split("&")[1].split(":");
                    if(id_sender == null || command_args == null || !id_sender.split(":")[0].equals(id) 
                    || !id_sender.split(":")[1].equals(clientName)) //Check if the request is valid
                    {
                        throwError(Error_Codes.SERVER_ERR, out);
                        continue;
                    }
                    String commandName = command_args[0];
                    String[] tmp, args;
                    if(command_args.length == 1) //No arguments (startGame, endGame)
                    {
                        args = new String[1];
                        args[0] = clientName;
                    } 
                    else //Arguments exist
                    {
                        tmp = command_args[1].split(","); //Split the arguments 
                        args = new String[tmp.length + 1]; //Add the sender name to the arguments
                        args[0] = clientName;
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

                    if(!clientName.equals(ClientModel.getName()) && (commandName.equals("startGame") || commandName.equals("endGame"))) 
                    { //Host only commands
                        throwError(Error_Codes.ACCESS_DENIED, out);
                        continue;  
                    } else if (commandName.equals("startGame") && connectedClients.size() < 2) 
                    {
                        throwError(Error_Codes.NOT_ENOUGH_PLAYERS, out);
                    } else if(commandName.equals("leave")){
                        requestHandler.handleClient(clientName, commandName, args ,clientSocket.getOutputStream());
                        break;
                    } else if(acceptableCommands.contains(commandName)) //Known command
                        requestHandler.handleClient(clientName, commandName, args ,connectedClients.get(clientName).getOutputStream());
                    else //Unknown command
                        throwError(Error_Codes.UNKNOWN_CMD, out);
                } //End of if statement
            } //End of while loop

           closeConnection(clientSocket, out, in); //Remove client from the HashMap
        } catch (IOException e) {
            MyLogger.log("Error in MyHostServer: " + e.getMessage());
        }
    }
    
    private void closeConnection(Socket clientSocket, PrintWriter out, Scanner in) {
        try {
            out.close();
            in.close();
            clientSocket.close();
            connectedClients.values().removeIf(v -> v.equals(clientSocket));
        } catch (IOException e) {
            MyLogger.log("Error closing connection: " + e.getMessage());
        }
    }

    public void stopServer()
    {
        stopServer = true;
        playerCount = 0;
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
            e.printStackTrace();
        }
        //Failed to communicate with the BookScrabbleServer
        return false;
    }

    public static void updateAll(String message, String doNotSendToPlayer) { // A method to update all relevant clients with new information
        try{
            OutputStream doNotSendStream;
            if(doNotSendToPlayer == null) //If the message is for everyone doNotSendToPlayer is null
                doNotSendStream = null;
            else
                doNotSendStream = MyHostServer.getHostServer().connectedClients.get(doNotSendToPlayer).getOutputStream();    
            
                MyHostServer.getHostServer().connectedClients.values().forEach(c-> {
                try {
                    if (c.getOutputStream() != doNotSendStream) { // Preventing from the message to be sent twice
                        try {
                            PrintWriter out = new PrintWriter(c.getOutputStream());
                            out.println(message);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            if(doNotSendToPlayer != null)
                doNotSendStream.flush();
                     
        } catch (IOException e) {
            e.printStackTrace();
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
                e.printStackTrace();
            }
            return true;
        }
        else
        {
            MyLogger.log("Not enough players to start the game");
            return false;
        }
    }

    void throwError(String error, PrintWriter out) { // A method to send an error message to a client
        MyLogger.logError(error);
        try{
            out.println(error);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getConnectedClients()
    {
        return connectedClients.keySet().toArray(new String[connectedClients.size()]);
    }

    public void setBSIP(String ip) { BookScrabbleServerIP = ip;}

    public void setBSPort(int port) { bookScrabblePort = port; }

    public void close() // A method to close the hostServer
    {
        stopServer = true;
        playerCount = 0;
    } 
}