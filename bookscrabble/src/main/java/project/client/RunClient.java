package project.client;

import project.client.model.ClientModel;
import project.client.model.MyHostServer;

import java.util.Scanner;

public class RunClient{
    static ClientModel myClient;
    static Scanner scanner = MyLogger.getScanner();
    public static boolean exitUponGameStartOrGameClosed = false;

    public RunClient() // always running
    {
        String name = getPlayerName();
        boolean isHost = chooseIfHost();

        if(isHost)
        {
            do
            {
                hostGame(name); 
            } while (!checkConnectionToHost()); 
            hostStartMenu();
        }
        else
        {
            do {
                joinGame(name);
            } while (!checkConnectionToHost()); 
            guestStartMenu();
        }     
    } 
    
    private String getPlayerName()
    {
        System.out.println("Welcome to the game!");
        System.out.println("Please enter your name: ");
        String name;
        do {
            name = scanner.nextLine();
        } while (name.length() == 0);
        System.out.println("Welcome " + name + "!");
        return name;
    }

    private boolean chooseIfHost()
    {
        System.out.println("Are you joining a game or creating a new one? Choose 1 or 2.");
        System.out.println("1. Join a game");
        System.out.println("2. Create a new game");
        boolean invalidInput = false;
        do {
            invalidInput = false;
            String input = scanner.nextLine();
            if (input.equals("1")) { //Guest client
                return false;
            } else if (input.equals("2")) { //Host client
                return true;
            } else {
                System.out.println("Invalid input. Please try again.");
                invalidInput = true;
            }
        } while (invalidInput);
        return false;
    }

    private void hostGame(String name) //First we create MyHostServer and then we create the client itself
    {
        System.out.println("Enter the port number you want to host the game on: ");
        String hostPort = scanner.nextLine();

        System.out.println("You need to connect to the BookScrabble server.");
        System.out.println("Please enter the BookScrabble Server IP: ");
        String bs_IP = scanner.nextLine();

        System.out.println("Please enter the correct port: ");
        String bs_port = scanner.nextLine();

        MyHostServer.getHostServer().start(Integer.parseInt(hostPort), Integer.parseInt(bs_port), bs_IP);
        //Now we create the host itself
        myClient = new ClientModel(true, "localhost", Integer.parseInt(hostPort), name); 
    }

    private void joinGame(String name)
    {
        System.out.println("Enter the host's IP address:");
        String ip = scanner.nextLine();
        System.out.println("Enter the host's port number:");
        String port = scanner.nextLine();
        System.out.println("Connecting to host...");
        myClient = new ClientModel(false, ip, Integer.parseInt(port) , name); //Guest client
    }

    private boolean checkConnectionToHost() //Returns true if connected to host
    {
        if(!myClient.isConnectedToHost)
        {
            System.out.println("Could not connect to host.");
            System.out.println("Trying again...");
            myClient = null;
            return false;
        }
        return true;
    }

    private void hostStartMenu()
    {
        System.out.println("Created game lobby successfully! Waiting for other players to join...");
        System.out.println("Type '!start' to begin the game, '!exit' to close or '!who' to see who's connected.");
        System.out.println("Remember, a game is played with 2-4 players.");
        
        do {
            if(scanner.hasNextLine()) {
                String input = scanner.nextLine();

                if (input.equals("!start"))
                exitUponGameStartOrGameClosed = MyHostServer.getHostServer().startGame();
                else if (input.equals("!exit")) {
                    System.out.println("Exiting...");
                    myClient.close();
                    exitUponGameStartOrGameClosed = true;
                } else if(input.equals("!who")){
                    String[] players = MyHostServer.getHostServer().getConnectedClients();
                    for(int i = 0; i < players.length; i++)
                        MyLogger.println((players[i])+ " is connected.");
                }
                else
                    System.out.println("Invalid input. Please try again.");   
            }
        } while (!exitUponGameStartOrGameClosed); 
    }

    private void guestStartMenu()
    {
        System.out.println("Joined game successfully! Waiting for host to start the game...");
        System.out.println("Type '!exit' to close the game.");
        
        do {
            if(scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if (input.equals("exit")) {
                    System.out.println("Exiting...");
                    myClient.close();
                    exitUponGameStartOrGameClosed = true;
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            }
        } while (!exitUponGameStartOrGameClosed);
    }
}