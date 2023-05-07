package project.model;

import project.server.BookScrabbleHandler;
import project.server.MyServer;
import project.server.TestserverAndClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class GameHandler {
    MyServer server;
    Game game;
    int port;
    public GameHandler(int port) {
        this.port = port;
        server = new MyServer(port , new BookScrabbleHandler());
    }
    public void StartGame() {
        server.start();
        // loop
    }
    public boolean MessageToServer (String message) { // A boolean method to send a query/challenge to the game server
        String response = null; // Answer from server
        try {
            Socket socket = new Socket("localhost", port);

            try (Scanner inFromServer = new Scanner(socket.getInputStream()); PrintWriter outToServer = new PrintWriter(socket.getOutputStream())) {
                outToServer.println(message);
                outToServer.flush();
                if(inFromServer.hasNext())
                   response = inFromServer.next();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Objects.equals(response, "true"); // Returns true according to the servers answer
    }

    public void CloseGame() {
        server.close();
    }

}


