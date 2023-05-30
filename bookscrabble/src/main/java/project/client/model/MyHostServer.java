package project.client.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

import project.client.Error_Codes;
import project.client.MyLogger;

public class MyHostServer implements Communications{
    private HostSideHandler requestHandler;
    private final int HOST_PORT, BOOK_SCRABBLE_PORT ,MAX_CLIENTS = 4;
    private final String BookScrabbleServerIP; // IP
    static HashMap<String, Socket> connectedClients; // HashMap to keep track of connected clients by name
    BlockingQueue<String> myTasks;
    private volatile Integer playerCount = 0; //Will be given as an ID to the player, we allow it to go above MAX_CLIENTS because we only check if it's 0 or not
    private volatile boolean stopServer = false;
    public static volatile boolean isGameRunning = false;
 
    public MyHostServer(int port, int bsPort, String bs_IP) { // Ctor
        requestHandler = new HostSideHandler();
        HOST_PORT = port;
        BOOK_SCRABBLE_PORT = bsPort;
        BookScrabbleServerIP = bs_IP;
        connectedClients = new HashMap<>();
        stopServer = false;
        isGameRunning = false;
    }

    // @Override
    // public void run() throws Exception { // A method that operates as the central junction between all users and the BookScrabbleServer
    //     ServerSocket hostSocket = new ServerSocket(HOST_PORT);
    //     PrintWriter out = null;
    //     Scanner in = null;
    //     while(!stopServer) // Loop's until the end of the game
    //     {
    //         try{ // Accepts a client
    //             Socket aClient = hostSocket.accept();
    //             try {
    //                 out = new PrintWriter(aClient.getOutputStream());
    //                 in = new Scanner(aClient.getInputStream());

    //                 String request = in.nextLine(); // "'id':'senderName'&'commandName':'args1','args2',...'"
    //                 MyLogger.log("Host received: " + request);
    //                 String[] user_body_split = request.split("&");
    //                 String id_sender = user_body_split[0];
    //                 String id = id_sender.split(":")[0]; //Sender's ID
    //                 String sender = id_sender.split(":")[1]; //Sender's name
    
    //                 if(user_body_split.length != 2){ //Must contain a sender name and a body
    //                     throwError(Error_Codes.MISSING_ARGS, aClient.getOutputStream());
    //                     continue;  
    //                 }

    //                 //Now we check the client to see if he's new or not and if allowed to join
    //                 if(connectedClients.get(sender) == null && id.equals("0") && connectedClients.size() < MAX_CLIENTS && !isGameRunning){ 
    //                     //Add new client to the HashMap and to the game
    //                     connectedClients.put(sender, aClient);
    //                     playerCount++; //Increment player count
    //                     ArrayList<String> args = new ArrayList<>(){{add(sender); add(playerCount.toString());}}; //Add sender and ID
    //                     args.addAll(connectedClients.keySet()); //Add all players in game to the arguments
    //                     requestHandler.handleClient(sender, "join", args.toArray(new String[args.size()]), aClient.getOutputStream());
    //                     continue; //Add new player and continue
    //                 } else if(connectedClients.get(sender) == null && id.equals("0") && (connectedClients.size() >= MAX_CLIENTS || isGameRunning)) { //Server is full / game has started
    //                     if(isGameRunning)
    //                         throwError(Error_Codes.GAME_STARTED, aClient.getOutputStream());
    //                     else
    //                         throwError(Error_Codes.SERVER_FULL, aClient.getOutputStream());
    //                     continue; 
    //                 } else if(connectedClients.get(sender) != null && id.equals("0")){ //Name taken
    //                     throwError(Error_Codes.NAME_TAKEN, aClient.getOutputStream());
    //                     continue;
    //                 }

    //                 //Now we have a known client and will process his request
    //                 String[] body = user_body_split[1].split(":");
    //                 String commandName = body[0]; //Split the body to command and arguments
    //                 String[] tmp, commandArgs;
    //                 if(body.length == 1) //No arguments (startGame, endGame)
    //                 {
    //                     commandArgs = new String[1];
    //                     commandArgs[0] = sender;
    //                 } 
    //                 else //Arguments exist
    //                 {
    //                     tmp = body[1].split(","); //Split the arguments 
    //                     commandArgs = new String[tmp.length + 1]; //Add the sender name to the arguments
    //                     commandArgs[0] = sender;
    //                     for (int i = 1; i < commandArgs.length; i++)
    //                         commandArgs[i] = tmp[i-1];  
    //                 }

    //                 //Check request
    //                 ArrayList<String> acceptableCommands = new ArrayList<>(){{
    //                     add("startGame");
    //                     add("endGame");
    //                     add("join");
    //                     add("leave");
    //                     add("skipTurn");
    //                     add("C"); //challenge
    //                     add("Q"); //query
    //                 }};

    //                 if(!sender.equals(ClientModel.getName()) && (commandName.equals("startGame") || commandName.equals("endGame"))) 
    //                 { //Host only commands
    //                     throwError(Error_Codes.ACCESS_DENIED, aClient.getOutputStream());
    //                     continue;  
    //                 } else if (commandName.equals("startGame") && connectedClients.size() < 2) 
    //                 {
    //                     throwError(Error_Codes.NOT_ENOUGH_PLAYERS, aClient.getOutputStream());
    //                 }
    //                 else if(acceptableCommands.contains(commandName)) //Known command
    //                     requestHandler.handleClient(sender, commandName, commandArgs ,connectedClients.get(sender).getOutputStream());
    //                 else //Unknown command
    //                     throwError(Error_Codes.UNKNOWN_CMD, aClient.getOutputStream());
                        
    //             } catch (Exception e) {
    //                 MyLogger.log("Error in MyHostServer: " + e.getMessage());
    //             }
    //         } catch (SocketTimeoutException e){} 
    //     }
    //     //Runs only when close() is called
    //     requestHandler.close();
    //     if (out != null) out.close();
    //     if(in != null) in.close(); 
    //     connectedClients.values().forEach(c-> { // All sockets will be closed after the game has ended (reusing them for all messages)
    //         try {
    //             c.close();
    //         } catch (IOException e) {
    //             throw new RuntimeException(e);
    //         }
    //     });
    //     connectedClients.clear();
    //     hostSocket.close();
    // }

    @Override
    public void run() throws Exception {
        ServerSocket hostSocket = new ServerSocket(HOST_PORT);
        hostSocket.setSoTimeout(10000);

        while (!stopServer) {
            try {
                Socket clientSocket = hostSocket.accept();
                new Thread(() -> handleClientConnection(clientSocket)).start();
            } catch (SocketTimeoutException e) {
                // Continue listening for new client connections
            }
        }

        // Close all client connections
        for (Socket clientSocket : connectedClients.values()) {
            closeConnection(clientSocket);
        }

        connectedClients.clear();
        hostSocket.close();
    }


    private void handleClientConnection(Socket clientSocket) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            Scanner in = new Scanner(clientSocket.getInputStream());
    
            // Handle client requests
            while (clientSocket.isConnected()) {
                if (in.hasNextLine()) {
                    String request = in.nextLine(); // "'id':'senderName'&'commandName':'args1','args2',...'"
                    MyLogger.log("Host received: " + request);
                    String[] user_body_split = request.split("&");
                    String id_sender = user_body_split[0];
                    String id = id_sender.split(":")[0]; //Sender's ID
                    String sender = id_sender.split(":")[1]; //Sender's name
    
                    if(user_body_split.length != 2){ //Must contain a sender name and a body
                        throwError(Error_Codes.MISSING_ARGS, aClient.getOutputStream());
                        continue;  
                    }

                    //Now we check the client to see if he's new or not and if allowed to join
                    if(connectedClients.get(sender) == null && id.equals("0") && connectedClients.size() < MAX_CLIENTS && !isGameRunning){ 
                        //Add new client to the HashMap and to the game
                        connectedClients.put(sender, aClient);
                        playerCount++; //Increment player count
                        ArrayList<String> args = new ArrayList<>(){{add(sender); add(playerCount.toString());}}; //Add sender and ID
                        args.addAll(connectedClients.keySet()); //Add all players in game to the arguments
                        requestHandler.handleClient(sender, "join", args.toArray(new String[args.size()]), aClient.getOutputStream());
                        continue; //Add new player and continue
                    } else if(connectedClients.get(sender) == null && id.equals("0") && (connectedClients.size() >= MAX_CLIENTS || isGameRunning)) { //Server is full / game has started
                        if(isGameRunning)
                            throwError(Error_Codes.GAME_STARTED, aClient.getOutputStream());
                        else
                            throwError(Error_Codes.SERVER_FULL, aClient.getOutputStream());
                        continue; 
                    } else if(connectedClients.get(sender) != null && id.equals("0")){ //Name taken
                        throwError(Error_Codes.NAME_TAKEN, aClient.getOutputStream());
                        continue;
                    }

                    //Now we have a known client and will process his request
                    String[] body = user_body_split[1].split(":");
                    String commandName = body[0]; //Split the body to command and arguments
                    String[] tmp, commandArgs;
                    if(body.length == 1) //No arguments (startGame, endGame)
                    {
                        commandArgs = new String[1];
                        commandArgs[0] = sender;
                    } 
                    else //Arguments exist
                    {
                        tmp = body[1].split(","); //Split the arguments 
                        commandArgs = new String[tmp.length + 1]; //Add the sender name to the arguments
                        commandArgs[0] = sender;
                        for (int i = 1; i < commandArgs.length; i++)
                            commandArgs[i] = tmp[i-1];  
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
                    { //Host only commands
                        throwError(Error_Codes.ACCESS_DENIED, aClient.getOutputStream());
                        continue;  
                    } else if (commandName.equals("startGame") && connectedClients.size() < 2) 
                    {
                        throwError(Error_Codes.NOT_ENOUGH_PLAYERS, aClient.getOutputStream());
                    }
                    else if(acceptableCommands.contains(commandName)) //Known command
                        requestHandler.handleClient(sender, commandName, commandArgs ,connectedClients.get(sender).getOutputStream());
                    else //Unknown command
                        throwError(Error_Codes.UNKNOWN_CMD, aClient.getOutputStream());
                        
                } catch (Exception e) {
                    MyLogger.log("Error in MyHostServer: " + e.getMessage());
                }
                    // Check if the client wants to disconnect
                    if (shouldDisconnect(request)) {
                        break;
                    }
                }
            }
    
            closeConnection(clientSocket, out, in);
        } catch (IOException e) {
            MyLogger.log("Error in MyHostServer: " + e.getMessage());
        }
    }
    
    private void closeConnection(Socket clientSocket, PrintWriter out, Scanner in) {
        try {
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            MyLogger.log("Error closing connection: " + e.getMessage());
        }
    }
    
    private boolean shouldDisconnect(String request) {
        // Determine if the client wants to disconnect based on the request
        // Return true if the client should disconnect, false otherwise
        // Example condition: request.equals("disconnect")
        return false;
    }





















    Boolean msgToBSServer(String message) { // A method that communicates with the BookScrabbleServer
        String response = null;
        try {
            Socket socket = new Socket(BookScrabbleServerIP, BOOK_SCRABBLE_PORT); // A socket for a single use
            try (Scanner inFromServer = new Scanner(socket.getInputStream());
                PrintWriter outToServer = new PrintWriter(socket.getOutputStream())) {
                outToServer.println(message); // Sends the message
                outToServer.flush();
                if(inFromServer.hasNext())
                    response = inFromServer.next(); // The response from the BookScrabbleServer

                if(response == null) //Invalid response from the BookScrabbleServer
                {
                    throwError(Error_Codes.SERVER_ERR,connectedClients.get(ClientModel.getName()).getOutputStream());
                    return false;
                    //Failed to communicate with the BookScrabbleServer
                } else if(message.equals("S,hello")) //First message from the BookScrabbleServer
                    return response.equals("Hello");
                else //The BookScrabbleServer responded with true/false
                    return response.equals("true");
            } catch (IOException e) {
                throw new RuntimeException(e);
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
                doNotSendStream = connectedClients.get(doNotSendToPlayer).getOutputStream();    
            
            connectedClients.values().forEach(c-> {
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
            PrintWriter out = new PrintWriter(connectedClients.get(player).getOutputStream());
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

    void checkBSConnection()
    {
        if(msgToBSServer("S,hello"))
            MyLogger.log("Connected to BookScrabbleServer!");
        else
            MyLogger.log("Couldn't connect to BookScrabbleServer!");    
    }

    void throwError(String error, OutputStream out) { // A method to send an error message to a client
        MyLogger.logError(error);
        PrintWriter pw = new PrintWriter(out);
        try{
            pw.println(error);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        pw.flush();
    }

    public String[] getConnectedClients()
    {
        return connectedClients.keySet().toArray(new String[connectedClients.size()]);
    }

    @Override
    public void close() // A method to close the hostServer
    {
        stopServer = true;
        playerCount = 0;
    } 

    @Override
    public void start() {
        new Thread(()-> {
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}