package project.client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerResponseModel {
    private Socket serverSocket;
    private BufferedReader inputReader;
    private PrintWriter outputWriter;

    public ServerResponseModel(Socket serverSocket) {
        this.serverSocket = serverSocket;
        try {
            inputReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            outputWriter = new PrintWriter(serverSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendRequest(String request) {
        outputWriter.println(request);
    }

    public String receiveResponse() {
        try {
            return inputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void closeConnection() {
        try {
            inputReader.close();
            outputWriter.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

