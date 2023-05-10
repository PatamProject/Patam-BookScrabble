package project.server.serverHandler;

import project.server.assets.Game;
import project.server.serverHandler.BookScrabbleHandler;
import project.server.serverHandler.GameHandler;
import project.server.serverHandler.MyServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class ServerManager {
    HashMap<ServerManager,Socket> games;
    MyServer BookScrabbleServer, GameServer; // Servers
    final int BookScrabblePort = 5001, GamePort = 5002; // Ports
    Socket socket; // Host's socket

    public ServerManager() { // Ctor
        BookScrabbleServer = new MyServer(BookScrabblePort , new BookScrabbleHandler()); // This server is used by many hosts of unrelated games
        BookScrabbleServer.start(); // Always runs in the background
        GameServer = new MyServer(GamePort, new GameHandler());
        GameServer.start();
    }

    // public void StartGame() {
    //     if (isHost) {
    //         this.GameServer = new MyServer(GamePort, new GameHandler()); // This server is used for a single game
    //         GameServer.start(); // Always runs in the background of the game
    //     }
    //     game.startGame();

    //     // methods:
    //     // receive initial data
    //     //
    //     // loop
    // }

    // public boolean messageToDBServer (String message) { // A boolean method to send a query/challenge to the DB server
    //     String response = messageMethod(message, dbName, BookScrabblePort); // Answer from server
    //     return Objects.equals(response, "true"); // Returns true according to the servers answer
    // }

    // public String messageToGuiServer (String message) { // A method to send a String to the Gui server
    //     return messageMethod(message, guiName, GamePort); // Returns String according to the servers answer
    // }

    private String messageMethod (String message, String serverName, int port) {
        String response = null;
        try {
            socket = new Socket(serverName, port);

            try (Scanner inFromServer = new Scanner(socket.getInputStream()); PrintWriter outToServer = new PrintWriter(socket.getOutputStream())) {
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

    // public void CloseGame() {
    //     if (isHost)
    //         GameServer.close();
    //     //game.closeGame;
    //     if (game.players.size() == 0)
    //         BookScrabbleServer.close();
    // }
}