package bookscrabble.tests;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import bookscrabble.client.model.MyHostServer;

public class TestMyHostServer {

    public static void main(String[] args) {
        // Create an instance of MyHostServer
        MyHostServer hostServer = MyHostServer.getHostServer();

        // Start the server
        hostServer.start(8009, 5555, "localhost");

        // Simulate some client interactions
        simulateClientInteraction(hostServer, "Client1");
        simulateClientInteraction(hostServer, "Client2");
        simulateClientInteraction(hostServer, "Client3");

        // Close the server
        hostServer.close();
    }

    private static void simulateClientInteraction(MyHostServer hostServer, String clientName) {
        // Simulate a client joining and sending commands
        // You can modify this method to test different scenarios
        try {
            // Simulate the client joining
            Thread.sleep(1000); // Wait for 1 second before joining
            simulateJoinCommand(hostServer, clientName);

            // Simulate sending commands
            Thread.sleep(1000); // Wait for 1 second before sending commands
            simulateCommand(hostServer, clientName, "startGame");
            //simulateCommand(hostServer, clientName, "endGame");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void simulateJoinCommand(MyHostServer hostServer, String clientName) {
        // Simulate the join command sent by a client
        String joinCommand = "'0':'ClientName'&'join'";
        simulateClientRequest(hostServer, clientName, joinCommand);
    }

    private static void simulateCommand(MyHostServer hostServer, String clientName, String commandName) {
        // Simulate a command sent by a client
        String command = "'1':'clientName'&'startGame'";
        simulateClientRequest(hostServer, clientName, command);
    }

    private static void simulateClientRequest(MyHostServer hostServer, String clientName, String request) {
        // Simulate a client request by sending a request to the server
        try{
            // Create a socket to the server
            Socket aClient = new Socket("localhost", 8009);
            //split the request into id and message
            String[] tmp = request.split(":");
            String id = tmp[0].substring(0);
            String req = tmp[1];
            //split the req into sender and command
            String[] Command;
            Command = req.split("&");
            String senderName = Command[0].substring(1);
            String command = Command[1].substring(0, Command[1].length()-1);
            // Create a PrintWriter to send the request to the server
            PrintWriter out = new PrintWriter(aClient.getOutputStream(), true);
            //send the command to the server
            out.println(request);







            // Simulate reading the response from the server- not finished yet
            Scanner in = new Scanner(aClient.getInputStream());
            String response = in.nextLine();
            if(response != Command[1]){
                System.out.println("Error: Response from server does not match command sent");
                System.out.println("Response: " + response);
                System.out.println("Command: " + Command[1]);
            }
            System.out.println("Response from server for " + senderName + ": " + command );

            // Close the socket
            aClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

   
    

