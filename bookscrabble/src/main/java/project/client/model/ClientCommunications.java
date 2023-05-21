package project.client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientCommunications extends Communications{
    private Socket toHostSocket; // A socket to the host
    private Scanner inFromHost;

    public ClientCommunications(String hostIP, int hostPort) { // Ctor
        super(new GuestSideHandler());
        try {
            toHostSocket = new Socket(hostIP, hostPort);
            inFromHost = new Scanner(toHostSocket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void run() { // A method that consistently receives messages from the host
        while (!toHostSocket.isClosed()) { // The socket will be open until the game is over
            try {
                if(inFromHost.hasNextLine()) {
                    super.getRequestHandler().handleClient(toHostSocket.getInputStream(), toHostSocket.getOutputStream());
                    inFromHost.next();
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