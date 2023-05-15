package project.server.serverHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class ServerManager {
    HashMap<ServerManager,Socket> games;
    MyServer BookScrabbleServer, GameServer; // Servers
    String bookScrabbleServerName, gameServerName; // IP
    final int BookScrabblePort = 5001, GamePort = 5002; // Ports
    Socket socket; // Host's socket

    public ServerManager(Socket socket) { // Ctor
        this.socket = socket;
        BookScrabbleServer = new MyServer(BookScrabblePort , new BookScrabbleHandler()); // This server is used by many hosts of unrelated games
        BookScrabbleServer.start(); // Always runs in the background
        GameServer = new MyServer(GamePort, new GameHandler());
        GameServer.start();
    }

     public String messageToBookScrabbleServer (String message) { // A method to send a query/challenge to the book scrabble server
         return messageMethod(message, bookScrabbleServerName, BookScrabblePort); // Answer from server
     }

     public String messageToGameServer (String message) { // A method to send a String to the game server
         return messageMethod(message,  gameServerName, GamePort); // Returns String according to the servers answer
     }

    private String messageMethod (String message, String serverName, int port) { // A method that communicates with the MyServer instances
        String response = null;
        try {
            socket = new Socket(serverName, port);
            games.put(this, socket);

            try (Scanner inFromServer = new Scanner(socket.getInputStream());
                 PrintWriter outToServer = new PrintWriter(socket.getOutputStream())) {
                outToServer.println(message);
                outToServer.flush();
                if (inFromServer.hasNext())
                    response = inFromServer.next();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}