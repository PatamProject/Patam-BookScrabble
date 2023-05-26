package project.client;

import project.client.model.ClientModel;
import project.server.serverHandler.BookScrabbleHandler;
import project.server.serverHandler.MyServer;
import java.util.Scanner;

public class RunClient{
    static ClientModel myClient;

    public static void main(String[] args) {
        MyServer myServer = new MyServer(1234,new BookScrabbleHandler()); //Local server
        myServer.start();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the game!");
        System.out.println("Please enter your name:");
        String name;
        do {
            name = scanner.nextLine();
        } while (name.length() == 0);
        System.out.println("Welcome " + name + "!\nAre you joining a game or creating a new one? Choose 1 or 2.");
        System.out.println("1. Join a game");
        System.out.println("2. Create a new game");
        boolean invalidInput = false;
        do {
            invalidInput = false;
            String input = scanner.nextLine();
            if (input.equals("1")) {
                System.out.println("Enter the host's IP address:");
                String ip = scanner.nextLine();
                System.out.println("Enter the host's port number:");
                String port = scanner.nextLine();
                System.out.println("Connecting to host...");
                myClient = new ClientModel(false, ip, Integer.parseInt(port) , name); //Guest client

            } else if (input.equals("2")) {
                System.out.println("Enter the port number you want to host the game on:");
                String port = scanner.nextLine();
                System.out.println("Creating game on port " + port + "...");
                myClient = new ClientModel(true, "localhost", Integer.parseInt(port) , name); //Host client
                System.out.println("Waiting for other players to join...");
            } else {
                System.out.println("Invalid input. Please try again.");
                invalidInput = true;
            }
        } while (invalidInput);
        scanner.close();
    }

    public boolean welcomeClient()
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the game!");
        System.out.println("Please enter your name:");
        String name = scanner.nextLine();
        System.out.println("Are you joining a game or creating a new one? Choose 1 or 2.");
        System.out.println("1. Join a game");
        System.out.println("2. Create a new game");
        boolean invalidInput = false;
        do {
            invalidInput = false;
            String input = scanner.nextLine();
            if (input.equals("1")) {
                System.out.println("Enter the host's IP address:");
                String ip = scanner.nextLine();
                System.out.println("Enter the host's port number:");
                String port = scanner.nextLine();
                System.out.println("Connecting to host...");
                myClient = new ClientModel(false, ip, Integer.parseInt(port) , name); //Guest client

            } else if (input.equals("2")) {
                System.out.println("Enter the port number you want to host the game on:");
                String port = scanner.nextLine();
                System.out.println("Creating game on port " + port + "...");
                myClient = new ClientModel(true, "localhost", Integer.parseInt(port) , name); //Host client
                System.out.println("Waiting for other players to join...");
            } else {
                System.out.println("Invalid input. Please try again.");
                invalidInput = true;
            }
        } while (invalidInput);
        scanner.close();
        return true;
    }
}