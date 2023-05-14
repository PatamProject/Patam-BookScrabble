package project.server.serverHandler;

import project.server.assets.Game;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class GameHandler implements ClientHandler {
    Scanner in;
    PrintWriter out;
    Game game;

    public GameHandler(){}

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        in = new Scanner(inFromClient);
        out = new PrintWriter(outToClient);
        String line = in.next();
        String[] args = line.split(",");

        switch (args[0]) {
            case "1":
            //...
                break;
            case "2":
                switch (args[1]) {
                    case "G":
                        switch (args[2]) {
                            case "N":
                                createGame();
                                break;
                            case "S":
                                //out.println(game.startGame());
                                break;
                            default:
                                break;
                        }
                        break;
                    case "P":
                        switch (args[2]) {
                            case "N":
                                if(game.addNewPlayer(args[3]))
                                    out.println("True");
                                else
                                    out.println("False");
                                break;
                            case "S":
                                out.println(game.getPlayer(args[3]).getScore());
                                break;
                            case "T":
                                out.println(game.tilesToString(args[3]));
                                break;
                            case "W": // Example: 2,P,W,pName,word-row-col-T/F
                                out.println(game.placeWord(args[3], game.StringToWord(args[4])));
                                break;
                            case "L":
                                out.println(game.playerLeftGame(args[3]));
                                break;
                            default:
                                break;
                        }
                        break;
                    case "T":
                        String response = game.getPlayer(args[2]).getRack().takeTile();
                        if (response.equals("0"))
                            out.println(game.getWinner());
                        else
                            out.println(game.getPlayer(args[2]).getRack().takeTile());
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        out.flush();
    }

    private void createGame() {game = new Game();}

    @Override
    public void close() {
        in.close();
        out.close();
    }
}