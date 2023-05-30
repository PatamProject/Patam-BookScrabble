package project.client;

import java.util.Scanner;

import project.client.model.ClientModel;

public class MyLogger {
    private static Scanner myScanner = new Scanner(System.in);

    public MyLogger(){}

    public static void log(String message) {System.out.println(message);}
    public static void logError(String errorMessage) {System.err.println(errorMessage);}
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
                System.out.print(s.charAt(i) + " ");
            System.out.print("\n");
        }
    }

    public static void playerJoined(String name)
    {
        log("Player " + name + " has joined the game!");
    }

    public static void playertLeft(String name)
    {
        log("Player " + name + " has left the game!");
    }

    public static void gameStarted(String firstPlayingPlayer)
    {
        log("Game started!");
        log("Player " + firstPlayingPlayer + " is playing first!");
        printBoard(null); //Print empty board
    }

    public static void nextPlayer(String nextPlayer)
    {
        log("Player " + nextPlayer + " is playing now!");
    }

    public static void playerPlacedWord(String player, int score, String commandName)
    {
        if(ClientModel.getName().equals(player))
        {
            log("You got " + score + " points!");
            return;
        }

        if(commandName.equals("C"))
            log("Player " + player + " challenged a word placement and got " + score + " points!");
        else
            log("Player " + player + " placed a word and got " + score + " points!");
    }

    public static void failedWordPlacement(int score)
    {

        if(score == 0)
            log("Invalid word placement! Try again.");
        else // score == -1
            log("Invalid word! Try again.");
    }

    public static void gameEnded(String player)
    {
        log("Player " + player + " won the game!");
    }

    public static void disconnectedFromHost()
    {
        logError("Connection to host is lost!");
    }

    public void useless() {

        // Host logging game progress
        log("Waiting for other players to join...");
        log("Maximum amount of players achieved.");

        // Game logic and progress updates
        log("Its your turn to play!");
        log("Its dickFace turn to play."); // change
        log("You won the game!");
        log("Player dickFace won the game!"); // change

        // Client logging game errors
        logError("Host connection Failed.");
        logError("Connection lost.");
        logError("Connection to player dickFace Failed.");
        logError("Invalid input. Please try again.");

        // Host logging game errors
        logError("No players in sight."); // If no one connects the host
        logError("Not enough players to start."); // If the host wants to play alone

        // Client game cleanup and exit
        log("Closing connection to the host...");
        log("Connection to host is closed.");
        log("Game ended. Thanks for playing!");

        // Host game cleanup and exit
        log("Closing Connections to guests...");
        log("Connections to guests are closed.");
    }

    public static Scanner getScanner()
    {
        return myScanner;
    }

    public static void close()
    {
        myScanner.close();
    }
    
}
