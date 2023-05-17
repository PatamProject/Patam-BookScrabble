package project.client.model;

import project.server.serverHandler.ServerManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Scanner;

public class ConnectionModel extends Observable {
    String hostAddress;
    final int hostPort = 5000;
    String hostResponse, serverResponse;

    public ConnectionModel(String hostAddress) {this.hostAddress = hostAddress;}

    public String getHostResponse() {return hostResponse;}

    public String getServerResponse() {return serverResponse;}

    public void modelCommunications(String message) {
        if (!hostAddress.equals("localhost")) {
            try (Socket socketToHost = new Socket(hostAddress, hostPort);
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

        else {
            try {
                Socket clientSocket;
                try (ServerSocket hostInputSocket = new ServerSocket(hostPort)) {
                    clientSocket = hostInputSocket.accept();
                }

                Scanner inFromClient = new Scanner(clientSocket.getInputStream());
                PrintWriter outToClient = new PrintWriter(clientSocket.getOutputStream());
                String clientMessage = inFromClient.next();

                Socket hostOutputSocket = new Socket();
                ServerManager manager = new ServerManager(hostOutputSocket);
                if (clientMessage.startsWith("1") || clientMessage.startsWith("2"))
                    serverResponse = manager.messageToGameServer(clientMessage);
                else
                    serverResponse = manager.messageToBookScrabbleServer(clientMessage);

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
}