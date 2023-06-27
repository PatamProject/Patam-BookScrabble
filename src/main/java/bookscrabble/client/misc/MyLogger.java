package bookscrabble.client.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

import bookscrabble.client.model.ClientModel;

public class MyLogger { // A class to print to the CMD
    private static Scanner myScanner = new Scanner(System.in);
    private static PrintWriter myWriter = getWriter();
    private String path = "/bookscrabble/logs/log.txt";

    public MyLogger(){}

    private boolean initWriter()
    {
        try {
            myWriter = new PrintWriter(getClass().getResource(path).getFile());
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }
    // private PrintWriter initWriter() //getClass().getResourceAsStream("/bookscrabble/logs/log.txt")
    // {
    //     myWriter = new PrintWriter(getClass().getResource(path).getFile());
    //     String baseDirectory = Paths.get("").toAbsolutePath().toString();
    //     Path filePath = Paths.get(baseDirectory,"Patam-BookScrabble", "src", "main", "resources", "bookscrabble", "logs", "log.txt");
    //     File logFile = filePath.toFile();   //Patam-BookScrabble\src\main\resources\bookscrabble\logs\log.txt
    //     if(logFile.exists())
    //         logFile.delete();
        
    //     try {
    //         logFile.createNewFile();
    //         writer = new PrintWriter(logFile);
    //     } catch (Exception e) {
    //         System.out.println("Failed to create log file! " + e.getMessage());
    //     }
    //     return writer;
    // }

    public static synchronized void println(String message)
    {
        System.out.println(message);
        System.out.flush();
        if(getWriter() != null)
            getWriter().println(message);
    }

    public static synchronized void print(String message)
    {
        System.out.print(message);
        System.out.flush();
        if(getWriter() != null)
            getWriter().println(message);
    }

    public static synchronized void logError(String errorMessage)
    {
        System.err.println(errorMessage);
        System.out.flush(); 
        if(getWriter() != null)
            getWriter().println("ERROR:" + errorMessage);
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
        println("   0 1 2 3 4 5 6 7 8 9 1011121314");    
        for (int i = 0; i < board.length; i++) {
            if(i < 10)
                print(i + "  ");
            else
                print(i + " ");
            for(int j = 0; j < board.length; j++)
                print(board[i].charAt(j) + " ");
            println("");
        }
    }

    public static void printTiles(String tiles)
    {
        for (int i = 0; i < tiles.length(); i++)
            print(tiles.charAt(i) + " ");
        println("");    
    }

    public static void joinedGame() {println("Joined game successfully!\nWaiting for the host to start the game!");}

    public static void playerJoined(String name){println("Player " + name + " has joined the game!");}

    public static void playerLeft(String name) {println("Player " + name + " has left the game!");}

    public static void gameStarted(String tiles, String... players) //First player to play is the first in the list
    {
        println("Game started!");
        println("Use !help to see all available commands!");
        println("Player " + players[0] + " is playing first!");
        HashMap<String, Integer> startingScores = new HashMap<>();
        for (String player : players)
            startingScores.put(player, 0);
            
        printPlayerAndScore(startingScores);
        printBoard(null); //Print empty board
        printTiles(tiles);
    }

    public static void printPlayerAndScore(HashMap<String, Integer> players)
    {
        for (String name : players.keySet())
            println(name.equals(ClientModel.getMyName()) ? "You have " + players.get(name) + " points!" : name + " has " + players.get(name) + " points!");
    }

    public static void nextPlayer(String nextPlayer){ println("Player " + nextPlayer + " is playing now!");}

    public static void playerPlacedWord(String player, int score, String commandName)
    {
        if(ClientModel.getMyName().equals(player))
        {
            println("You got " + score + " points!");
            return;
        }

        if(commandName.equals("C"))
            println("Player " + player + " challenged a word placement and got " + score + " points!");
        else
            println("Player " + player + " placed a word and got " + score + " points!");
    }

    public static void failedWordPlacement(int score, boolean isChallenge)
    {
        println(score == 0 ? "Invalid word placement!" : "Illegal word!"); // else - score == -1
        println(isChallenge ? "Your challenge has failed! Choose another word!" : "You can either try again or skip your turn or challenge the dictionary!");
    }

    public static void gameEnded(String player) {println("Player " + player + " won the game!");}

    public static void disconnectedFromHost() {logError("Connection to host is lost!");}

    public static Scanner getScanner()
    { 
        if(myScanner == null)
            myScanner = new Scanner(System.in);
        return myScanner;
    }

    private static PrintWriter getWriter()
    {
        // if(myWriter == null)
        //     myWriter = initWriter();
        return myWriter;
    }
    
    public static void close()
    {
        myScanner.close();
        myWriter.close();
    }  
}