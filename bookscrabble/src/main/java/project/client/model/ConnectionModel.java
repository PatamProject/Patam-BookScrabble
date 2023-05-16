package project.client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Scanner;

public class ConnectionModel extends Observable {
    ServerSocket hostInputSocket;
    String hostAddress, managerAddress;
    String hostResponse, serverResponse;
    private final int HOST_PORT = 5001, MANAGER_PORT = 5002;

    public ConnectionModel(String hostAddress, String serverAddress) {
        this.hostAddress = hostAddress;
        this.managerAddress = serverAddress;
    }

    public String getHostResponse() {return hostResponse;}

    public String getServerResponse() {return serverResponse;}

    public void modelCommunications(String message) {
        if (!hostAddress.equals("localhost"))
            clientCommunications(message);
        else
            hostCommunications();
    }

    private void clientCommunications(String message) {
        try (Socket socketToHost = new Socket(hostAddress, HOST_PORT);
             Scanner inFromHost = new Scanner(socketToHost.getInputStream());
             PrintWriter outToHost = new PrintWriter(socketToHost.getOutputStream())) {
            outToHost.println(message);
            outToHost.flush();
            if (inFromHost.hasNext()) {
                hostResponse = inFromHost.next();
                setChanged();
                notifyObservers();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void hostCommunications() {
        try {
            hostInputSocket = new ServerSocket(HOST_PORT);
            hostInputSocket.setSoTimeout(2000);
            Socket clientSocket = hostInputSocket.accept();

            Scanner inFromClient = new Scanner(clientSocket.getInputStream());
            PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream());
            String clientMessage = inFromClient.next();

            Socket managerSocket = new Socket(managerAddress, MANAGER_PORT);
            Scanner fromManager = new Scanner(managerSocket.getInputStream());
            PrintWriter toManager = new PrintWriter(managerSocket.getOutputStream());
            toManager.println(clientMessage);
            toManager.flush();
            if (fromManager.hasNext())
                serverResponse = fromManager.next();
            fromManager.close();
            toManager.close();
            managerSocket.close(); // need to create a condition to close

            outToClient.println(serverResponse);
            outToClient.flush();
            setChanged();
            notifyObservers();
            inFromClient.close();
            outToClient.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}