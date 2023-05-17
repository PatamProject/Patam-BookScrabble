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
                            case "W": // Example: 2,P,W,pName,word-row-col-true/false
                                String[] wordArgs = args[4].split("-");
                                out.println(game.placeWord(args[3], game.getWordFromString(args[3] ,wordArgs[0], Integer.parseInt(wordArgs[1]) , Integer.parseInt(wordArgs[2]), Boolean.parseBoolean(wordArgs[3]))));
                                break;
                            case "L":
                                String response = game.playerLeftGame(args[3]);
                                if (response.equals("0"))
                                    out.println(game.getWinner());
                                else
                                    out.println(response);
                                break;
                            default:
                                break;
                        }
                        break;
                    case "T":
                        String response = game.getPlayer(args[2]).getRack().takeTileFromBag();
                        if (response.equals("0"))
                            out.println(game.getWinner());
                        else
                            out.println(game.getPlayer(args[2]).getRack().takeTileFromBag());
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