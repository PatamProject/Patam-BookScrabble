package project.server.serverHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

import project.server.assets.Game;

public class GameHandler {
    MyServer server;
    Game game;
    Socket socket;
    int port;

    public GameHandler(int port) {
        this.port = port;
        server = new MyServer(port , new BookScrabbleHandler());
    }

    public void StartGame() {
        server.start();

        // loop
    }

    public Boolean MessageToServer (String message) {
       String response;
        try {
            socket = new Socket("localhost", port);
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(message);
            out.flush();

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            response = in.readLine();

            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Objects.equals(response, "true");
    }

    public void CloseGame() {
        server.close();
    }
}
