package project.client;

import java.util.HashMap;
import java.util.Scanner;

import project.client.model.ClientModel;

public class MyLogger {
    private static Scanner myScanner = new Scanner(System.in);

    public MyLogger(){}

    public static synchronized void println(String message)
    {
        System.out.println(message);
        System.out.flush();
    }

    public static synchronized void print(String message)
    {
        System.out.print(message);
        System.out.flush();
    }

    public static synchronized void logError(String errorMessage)
    {
        System.err.println(errorMessage);
        System.out.flush(); 
    }
    public static void printBoard(String input) 
    { // Example: AB-C&D--E
        String[] board = null;
        if (input == null)
        {
            board = new String[15];
            board[0]  = "---------------";
            board[1]  = "---------------";
            board[2]  = "---------------";
            board[3]  = "---------------";
            board[4]  = "---------------";
            board[5]  = "---------------";
            board[6]  = "---------------";
            board[7]  = "---------------";
            board[8]  = "---------------";
            board[9]  = "---------------";
            board[10] = "---------------";
            board[11] = "---------------";
            board[12] = "---------------";
            board[13] = "---------------";
            board[14] = "---------------";
        } else {
            board = input.split("&");
        }
            
        for (String s : board) 
        {
            for (int i = 0; i < s.length(); i++)
                print(s.charAt(i) + " ");
            println("");
        }
    }

    public static void printTiles(String tiles)
    {
        for (int i = 0; i < tiles.length(); i++)
            print(tiles.charAt(i) + " ");
    }

    public static void playerJoined(String name)
    {
        println("Player " + name + " has joined the game!");
    }

    public static void playertLeft(String name)
    {
        println("Player " + name + " has left the game!");
    }

    public static void gameStarted(String... players) //First player to play is the first in the list
    {
        println("Game started!");
        println("Player " + players[0] + " is playing first!");
        HashMap<String, Integer> startingScores = new HashMap<>();
        for (int i = 0; i < players.length; i++)
            startingScores.put(players[i], 0);
            
        printPlayerAndScore(startingScores);
        printBoard(null); //Print empty board
    }

    public static void printPlayerAndScore(HashMap<String, Integer> players)
    {
        for (String name : players.keySet())
            println(name + " has " + players.get(name) + " points!");
    }

    public static void nextPlayer(String nextPlayer)
    {
        println("Player " + nextPlayer + " is playing now!");
    }

    public static void playerPlacedWord(String player, int score, String commandName)
    {
        if(ClientModel.getName().equals(player))
        {
            println("You got " + score + " points!");
            return;
        }

        if(commandName.equals("C"))
            println("Player " + player + " challenged a word placement and got " + score + " points!");
        else
            println("Player " + player + " placed a word and got " + score + " points!");
    }

    public static void failedWordPlacement(int score)
    {
        if(score == 0)
            println("Invalid word placement! Try again.");
        else // score == -1
            println("Illegal word! Try again.");
    }

    public static void gameEnded(String player)
    {
        println("Player " + player + " won the game!");
    }

    public static void disconnectedFromHost()
    {
        logError("Connection to host is lost!");
    }

    // public void useless() {

    //     // Host logging game progress
    //     log("Waiting for other players to join...");
    //     log("Maximum amount of players achieved.");

    //     // Game logic and progress updates
    //     log("Its your turn to play!");
    //     log("Its dickFace turn to play."); // change
    //     log("You won the game!");
    //     log("Player dickFace won the game!"); // change

    //     // Client logging game errors
    //     logError("Host connection Failed.");
    //     logError("Connection lost.");
    //     logError("Connection to player dickFace Failed.");
    //     logError("Invalid input. Please try again.");

    //     // Host logging game errors
    //     logError("No players in sight."); // If no one connects the host
    //     logError("Not enough players to start."); // If the host wants to play alone

    //     // Client game cleanup and exit
    //     log("Closing connection to the host...");
    //     log("Connection to host is closed.");
    //     log("Game ended. Thanks for playing!");

    //     // Host game cleanup and exit
    //     log("Closing Connections to guests...");
    //     log("Connections to guests are closed.");
    // }

    public static Scanner getScanner()
    {
        return myScanner;
    }

    public static void close()
    {
        myScanner.close();
    }
    
}
