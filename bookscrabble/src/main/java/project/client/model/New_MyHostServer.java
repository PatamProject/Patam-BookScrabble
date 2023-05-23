package project.client.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import project.client.Error_Codes;

public class New_MyHostServer extends New_Communications{
    private final int HOST_PORT, BOOK_SCRABBLE_PORT; // Ports
    private final String BookScrabbleServerIP; // IP
    static HashMap<String, Socket> connectedClients; // HashMap to keep track of connected clients by name
    private volatile boolean stopServer = false;
    private boolean gameStarted = false;
    public final int MAX_CLIENTS = 4;
    private int prevent_duplicate_names = 0;
    String HostName;

    public New_MyHostServer(String hostName, int port, int bsPort, String bs_IP) { // Ctor
        super(new New_HostSideHandler());
        this.HostName = hostName;
        HOST_PORT = port;
        BOOK_SCRABBLE_PORT = bsPort;
        BookScrabbleServerIP = bs_IP;
        connectedClients = new HashMap<>();
        stopServer = false;
        gameStarted = false;
        try { // Add the host to the HashMap
            //connectedClients.put(HostName, new Socket("localhost",New_ClientModel.myPort)); // ?
        } catch (IOException e) {} 
    }

    @Override
    protected void run() throws Exception { // A method that operates as the central junction between all users and the BookScrabbleServer
        ServerSocket hostSocket = new ServerSocket(HOST_PORT);
        hostSocket.setSoTimeout(2000);
        while(!stopServer) // Loop's until the end of the game
        {
            try{ // Accepts a client
                Socket aClient = hostSocket.accept();
                try (Scanner in = new Scanner(aClient.getInputStream());
                PrintWriter out = new PrintWriter(aClient.getOutputStream());)
                {
                    if (!in.hasNextLine()) { // If the client didn't send a message
                        throwError(Error_Codes.MISSING_ARGS, out);
                        continue; 
                    }
                    String request = in.nextLine(); // "'sender'&'commandName':'args1,args2,...'"
                    String[] user_body_split = request.split("&");
                    String sender = user_body_split[0]; //Name of the sender
    
                    if(user_body_split.length != 2){ //Must contain a sender name and a body
                        throwError(Error_Codes.MISSING_ARGS, out);
                        continue;  
                    }
    
                    if(connectedClients.get(sender) == null && connectedClients.size() < MAX_CLIENTS && !gameStarted){ 
                        //Add new client to the HashMap and to the game
                        connectedClients.put(sender, aClient);
                        String[] tmpArgs = {sender}; 
                        super.getRequestHandler().handleClient(sender, "join", tmpArgs, aClient.getOutputStream());
                    } else if(connectedClients.get(sender) == null && (connectedClients.size() >= MAX_CLIENTS || gameStarted)) { //Server is full / game has started
                        if(gameStarted)
                            throwError(Error_Codes.GAME_STARTED, out);
                        else
                            throwError(Error_Codes.SERVER_FULL, out);
                        continue; 
                    }
                    //Now we have a known client and will process his request
                    String[] body = user_body_split[1].split(":");
                    String commandName = body[0]; //Split the body to command and arguments
                    String tmp[] = body[1].split(","); //Split the arguments
                    String commandArgs[] = new String[tmp.length + 1]; //Add the sender name to the arguments
                    commandArgs[0] = sender;
                    for(int i = 0; i < tmp.length; i++)
                        commandArgs[i+1] = tmp[i];
                    //Check request
                    ArrayList<String> acceptableCommands = new ArrayList<>(){{
                        add("startGame");
                        add("join");
                        add("leave");
                        add("skipTurn");
                        add("C"); //challange
                        add("Q"); //query
                    }};

                    if(acceptableCommands.contains(commandName))
                        super.getRequestHandler().handleClient(sender, commandName, commandArgs ,connectedClients.get(sender).getOutputStream());
                    else
                        throwError(Error_Codes.UNKNOWN_CMD, out);
        
                } catch (Exception e) {
                } finally {
                    super.getRequestHandler().close();
                }
            } catch (SocketTimeoutException e){} 
        }
        //Runs only when close() is called
        connectedClients.values().forEach(c-> { // All sockets will be closed after the game has ended (reusing them for all messages)
            try {
                c.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        connectedClients.clear();
        hostSocket.close();
    }

    Boolean msgToBSServer(String message) { // A method that communicates with the BookScrabbleServer
        String response = null;
        try {
            PrintWriter outToHost = new PrintWriter(connectedClients.get(HostName).getOutputStream());
            Socket socket = new Socket(BookScrabbleServerIP, BOOK_SCRABBLE_PORT); // A socket for a single use
            try (Scanner inFromServer = new Scanner(socket.getInputStream());
                PrintWriter outToServer = new PrintWriter(socket.getOutputStream())) {
                outToServer.println(message); // Sends the message
                outToServer.flush();
                if(inFromServer.hasNext())
                {
                    response = inFromServer.next(); // The response from the BookScrabbleServer
                    //Add a loop? Wait?
                }

                if(response == null) //Invalid response
                {
                    throwError(Error_Codes.SERVER_ERR, new PrintWriter(outToHost));
                    return false;
                    //Failed to communicate with the BookScrabbleServer
                } 
                else //The BookScrabbleServer responded with true/false
                {
                    if(response == "true") //Word accepted
                        return true;
                    else //Word rejected
                        return false;
                }
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
        try (OutputStream originClient = connectedClients.get(doNotSendToPlayer).getOutputStream()) {
            connectedClients.values().forEach(c-> {
                try {
                    if (c.getOutputStream() != originClient) { // Preventing from the message to be sent twice
                        try (PrintWriter out = new PrintWriter(c.getOutputStream())){
                            out.println(message);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void throwError(String error, PrintWriter out) { // A method to send an error message to a client
        try{
            out.println(error);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        out.flush();
        out.close();
    }

    @Override
    public void close() {stopServer = true;} // A method to close the hostServer
}