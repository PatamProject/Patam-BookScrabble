package project.client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import project.client.Error_Codes;

public class ClientCommunications extends Communications{
    private Socket toHostSocket; // A socket to the host
    private Scanner inFromHost;

    public ClientCommunications(String hostIP, int hostPort) { // Ctor
        super(new ClientSideHandler());
        try {
            toHostSocket = new Socket(hostIP, hostPort);
            inFromHost = new Scanner(toHostSocket.getInputStream());
            PrintWriter out = new PrintWriter(toHostSocket.getOutputStream());  
            out.println("0$"+ClientModel.myName+"&join"); // Send a message to the host that the client wants to join with id = 0 (marked after "$")
            out.flush();
            out.close();
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
                    // if(isError(request)) //If the host sent an error
                    //     super.getRequestHandler().handleError(request);
                        
                    String[] tmp = request.split(":");
                    String commandName = tmp[0].substring(1);
                    String[] args = tmp[1].split(",");
                    String sender;
                    if(request.charAt(0) == '!') //Game update!
                        sender = "!"; //To allow the handler to know that this is a game update from the host
                    else //A reply from the host
                        sender = ClientModel.myName;
                    
                    super.getRequestHandler().handleClient(sender, commandName, args, toHostSocket.getOutputStream());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                super.getRequestHandler().close();
            }
        }
    }

    public void sendAMessage(int id, String message) { // A method that sends a message to the host
        try (PrintWriter outToHost = new PrintWriter(toHostSocket.getOutputStream())) {
            outToHost.println(id + "$" + message); //Adds the ID to the beginning of the message
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
