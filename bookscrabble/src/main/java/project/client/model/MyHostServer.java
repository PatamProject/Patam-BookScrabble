package project.client.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;

public class MyHostServer extends Communications{
    private final int HOST_PORT, BOOK_SCRABBLE_PORT; // Ports
    private final String BookScrabbleServerIP; // IP
    private static ArrayList<Socket> clients; // Array to keep track of connected clients
    private volatile boolean stopServer = false;

    public MyHostServer(int port, int bsPort, String bs_IP) { // Ctor
        super(new HostSideHandler());
        HOST_PORT = port;
        BOOK_SCRABBLE_PORT = bsPort;
        BookScrabbleServerIP = bs_IP;
        clients = new ArrayList<>();
        stopServer = false;
    }

    @Override
    protected void run() throws Exception { // A method that operates as the central junction between all users and the BookScrabbleServer
        ServerSocket hostSocket = new ServerSocket(HOST_PORT);
        hostSocket.setSoTimeout(2000);
        while(!stopServer) // Loop's until the end of the game
        {
            try {
                Socket aClient = hostSocket.accept();
                if (!clients.contains(aClient))
                    clients.add(aClient); // All clients are stored here

                try {
                    if(!bsServerToClient(aClient)) // True means that the client will receive his response from the BookScrabbleServer
                        super.getRequestHandler().handleClient(aClient.getInputStream(), aClient.getOutputStream()); // False means that the client will receive his response from the handler
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    super.getRequestHandler().close();
                }
            } catch (SocketTimeoutException e) {}
        }
        clients.forEach(s-> { // All sockets will be closed after the game has ended (reusing them for all messages)
            try {
                s.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clients.clear();
        hostSocket.close();
    }

    private Boolean bsServerToClient(Socket aClient) { // A boolean method that returns true if the client received a response from the BookScrabbleServer
        boolean happened = false;
        String message, response;
        try (Scanner inFromClient = new Scanner(aClient.getInputStream());
             PrintWriter outToClient = new PrintWriter(aClient.getOutputStream())){
            if (inFromClient.hasNext()) {
                message = inFromClient.next();
                if (message.startsWith("Q") || message.startsWith("C")) { // Checks if the message should be sent to the BookScrabbleServer or not
                    response = hostToBSServer(message); // The response from the BookScrabbleServer
                    outToClient.println(response); // Send it back to the client
                    outToClient.flush();
                    happened = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return happened; // Return outcome
    }

    private String hostToBSServer(String message) { // A method that communicates with the BookScrabbleServer
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
        return response;
    }

    public static void updateAll(String message, OutputStream toClient) { // A method to update all relevant clients with new information
        clients.forEach(c-> {
            try {
                if (c.getOutputStream() != toClient) { // Preventing from the message to be sent twice
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
    }

    @Override
    public void close() {stopServer = true;} // A method to close the hostServer
}