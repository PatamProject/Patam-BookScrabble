package project.client.model;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Scanner;

import project.client.Error_Codes;

public class New_MyHostServer extends New_Communications{
    private final int HOST_PORT, BOOK_SCRABBLE_PORT; // Ports
    private final String BookScrabbleServerIP; // IP
    static HashMap<String, Socket> connectedClients; // HashMap to keep track of connected clients by name
    private volatile boolean stopServer = false;
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
                    String request = in.nextLine(); // "'sender'&'takeTile':'Y'"
                    String[] user_body_split = request.split("&");
                    String sender = user_body_split[0]; //Name of the sender
    
                    if(user_body_split.length != 2){ //Must contain a sender name and a body
                        throwError(Error_Codes.MISSING_ARGS, out);
                        continue;  
                    }
    
                    if(connectedClients.get(sender) == null && connectedClients.size() < MAX_CLIENTS){ 
                        //Add new client to the HashMap and to the game
                        connectedClients.put(sender, aClient);
                        String[] tmpArgs = {sender}; 
                        super.getRequestHandler().handleClient(sender, "join", tmpArgs, aClient.getOutputStream());
                    } else if(connectedClients.get(sender) == null && connectedClients.size() >= MAX_CLIENTS) { //Server is full
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
                    switch (commandName) { //NOT FINISHED
                        case "C": //challange
                        case "Q": //query
                            String msgToBS = commandName + "," + commandArgs[0];
                            Boolean result = msgToBSServer(msgToBS, aClient.getOutputStream());
                            if(result == null) //If the BookScrabbleServer is not available
                            {
                                throwError(Error_Codes.SERVER_ERR, out);
                                continue;
                            }
                            else //If the BookScrabbleServer returned a result
                            {
                                String[] newCommandArgs = new String[commandArgs.length + 1];
                                for(int i = 0; i < commandArgs.length; i++)
                                    newCommandArgs[i] = commandArgs[i];
                                if(result) //If the word is valid
                                    newCommandArgs[newCommandArgs.length - 1] = "1";
                                else //If the word is not valid
                                    newCommandArgs[newCommandArgs.length - 1] = "0";  
                                    
                                super.getRequestHandler().handleClient(sender, "placeWord", newCommandArgs, connectedClients.get(sender).getOutputStream());    
                            }
                            break;
                        default: //All other commands are handled by the handler
                            super.getRequestHandler().handleClient(sender, commandName, commandArgs ,connectedClients.get(sender).getOutputStream());
                            break;
                    }   
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

    Boolean msgToBSServer(String message, OutputStream outToClient) { // A method that communicates with the BookScrabbleServer
        String response = null;
        try {
            Socket socket = new Socket(BookScrabbleServerIP, BOOK_SCRABBLE_PORT); // A socket for a single use
            try (Scanner inFromServer = new Scanner(socket.getInputStream());
                 PrintWriter outToServer = new PrintWriter(socket.getOutputStream())) {
                outToServer.println(message); // Sends the message
                outToServer.flush();
                if (inFromServer.hasNext())
                    response = inFromServer.next(); // The response from the BookScrabbleServer
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response == null)
        {
            throwError(Error_Codes.SERVER_ERR, new PrintWriter(outToClient));
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
    }

    private void addClientToMap(String name, Socket client) { // A method to add a client to the HashMap safely
        connectedClients.put(name+"_"+prevent_duplicate_names, client);
        prevent_duplicate_names++;
    }

    private boolean isClientInMap(String name) { // A method to check if a client is in the HashMap safely
        return connectedClients.containsKey(name);
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