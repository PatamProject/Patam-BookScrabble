package project.client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import project.client.Error_Codes;

public class New_ClientCommunications extends New_Communications{
    private Socket toHostSocket; // A socket to the host
    private Scanner inFromHost;

    public New_ClientCommunications(String hostIP, int hostPort) { // Ctor
        super(new New_GuestSideHandler());
        try {
            toHostSocket = new Socket(hostIP, hostPort);
            inFromHost = new Scanner(toHostSocket.getInputStream());
            PrintWriter out = new PrintWriter(toHostSocket.getOutputStream());  
            out.println(New_ClientModel.myName+"&join"); // Send a message to the host that the client wants to join
            String response = inFromHost.nextLine();
            if(response.equals(Error_Codes.SERVER_FULL))
            {
                System.out.println("Server is full, try again later");
                close();    
            } else if(response.equals(Error_Codes.SERVER_ERR) || response.equals(Error_Codes.ACCESS_DENIED))
            {
                System.out.println("Server error!");
                close();
            } else if(response.equals(Error_Codes.GAME_STARTED)) {
                System.out.println("Game has already started!");
                close();
            }  //else join was successful
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void run() { // A method that consistently receives messages from the host
        while (!toHostSocket.isClosed()) { // The socket will be open until the game is over
            try {
                if(inFromHost.hasNextLine()) {
                    String request = inFromHost.nextLine(); // "!'takeTile':'Y'"
                    String[] tmp = request.split(":");
                    String commandName = tmp[0].substring(1);
                    String[] args = tmp[1].split(",");
                    String sender;
                    if(request.charAt(0) == '!') //Game update!
                        sender = "!"+New_ClientModel.myName; //To allow the handler to know that this is a game update from the host
                    else //A reply from the host
                        sender = New_ClientModel.myName;
                    
                    super.getRequestHandler().handleClient(sender, commandName, args, toHostSocket.getOutputStream());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                super.getRequestHandler().close();
            }
        }
    }

    public void sendAMessage(String message) { // A method that sends a message to the host
        try (PrintWriter outToHost = new PrintWriter(toHostSocket.getOutputStream())) {
            outToHost.println(message);
            outToHost.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() { // Closing the connection to the host
        try {
            inFromHost.close();
            toHostSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
