package project.server.serverHandler;

import project.server.serverHandler.BookScrabbleHandler;
import project.server.serverHandler.GuiHandler;
import project.server.serverHandler.MyServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class GameHandler {
    MyServer dbServer, guiServer; // Servers
    String dbName, guiName; // IP
    Game game;
    Socket socket;
    ArrayList<Player> turnOrder; // The order of the players turns
    final int dbPort, guiPort; // Ports
    boolean isHost; // A flag to see if the client is the host


    public GameHandler(String dbName, String guiName, int dbPort, int guiPort, boolean isHost) { // Ctor
        this.dbPort = dbPort;
        this.guiPort = guiPort;
        this.isHost = isHost;
        this.dbName = dbName;
        this.guiName = guiName;
        this.turnOrder = new ArrayList<>();
        this.dbServer = new MyServer(dbPort , new BookScrabbleHandler()); // This server is used by many hosts of unrelated games
        dbServer.start(); // Always runs in the background
    }

    public void StartGame() {
        if (isHost) {
            this.guiServer = new MyServer(guiPort, new GuiHandler()); // This server is used for a single game
            guiServer.start(); // Always runs in the background of the game
        }
        game.startGame();

        // methods:
        // receive initial data
        //
        // loop
    }

    public boolean messageToDBServer (String message) { // A boolean method to send a query/challenge to the DB server
        String response = messageMethod(message, dbName, dbPort); // Answer from server
        return Objects.equals(response, "true"); // Returns true according to the servers answer
    }

    public String messageToGuiServer (String message) { // A method to send a String to the Gui server
        return messageMethod(message, guiName, guiPort); // Returns String according to the servers answer
    }

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

    public void CloseGame() {
        if (isHost)
            guiServer.close();
        //game.closeGame;
        if (game.players.size() == 0)
            dbServer.close();
    }
}