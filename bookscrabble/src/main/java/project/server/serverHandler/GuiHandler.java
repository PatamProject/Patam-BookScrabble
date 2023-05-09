package project.server.serverHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import project.model.Game;
import project.assets.Board;

public class GuiHandler implements ClientHandler {
    Scanner in;
    PrintWriter out;

    public GuiHandler(){}

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        in = new Scanner(inFromClient);
        out = new PrintWriter(outToClient);
        String line = in.next();
        line = line.substring(2);

        String[] args = line.split(",");

        if (args[0] == "1") { // Example for in: 1,word,row,col,T/F
            //...
            out.println(Game.board.tryPlaceWord());
        }
        // List of classifications:
        // 1 = Check if a word is placeable on the board. The score is returned as string.
        // 2 =
        // 3 =

        out.flush();
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}