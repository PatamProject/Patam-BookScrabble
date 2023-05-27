package project.client;

import project.client.model.ClientModel;
import project.client.model.MyHostServer;

import java.util.Scanner;

public class RunClient{
    static ClientModel myClient;

    public RunClient() {
        boolean isHost = false;
        Scanner scanner = MyLogger.getScanner();
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
                isHost = false;
                System.out.println("Enter the host's IP address:");
                String ip = scanner.nextLine();
                System.out.println("Enter the host's port number:");
                String port = scanner.nextLine();
                System.out.println("Connecting to host...");
                myClient = new ClientModel(isHost, ip, Integer.parseInt(port) , name); //Guest client

            } else if (input.equals("2")) {
                isHost = true;
                System.out.println("Enter the port number you want to host the game on:");
                String port = scanner.nextLine();
                System.out.println("Creating game on port " + port + "...");
                myClient = new ClientModel(isHost, "localhost", Integer.parseInt(port) , name); //Host client
                System.out.println("Waiting for other players to join...");
            
            } else {
                System.out.println("Invalid input. Please try again.");
                invalidInput = true;
            }
        } while (invalidInput);

        if(isHost)
        {
            scanner.nextLine();
            System.out.println("Type 'start' to begin the game or 'exit' to close.");
            System.out.println("Remember, a game is played with 2-4 players.");
            boolean exit = false;
            
            do {
                if(scanner.hasNextLine()) {
                    String input = scanner.nextLine();

                    if (input.equals("start")) {
                        exit = ClientModel.myHostServer.startGame();
                    } else if (input.equals("exit")) {
                        System.out.println("Exiting...");
                        exit = true;
                    }
                    else
                    {
                        System.out.println("Invalid input. Please try again.");
                    }
                }
            } while (!exit);
        }
        else //Guest client
        {
            System.out.println("Joined game successfully! Waiting for host to start...");
            System.out.println("Type 'exit' to close game.");
            boolean exit = false;
            do {
                String input = scanner.nextLine();
                if (input.equals("exit")) {
                    System.out.println("Exiting...");
                    exit = true;
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            } while (!exit);
        }
    }    
}