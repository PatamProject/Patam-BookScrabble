package project.server.serverHandler;

import project.server.Constants;
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
        if(args == null || args.length < 2)
            out.println(Constants.UNKNOWN_CMD); //No arguments
        else 
        { //Refer to server protocol!!!
            switch (args[1]) {
                case "G":
                    switch (args[2]) {
                        case "N":
                            createGame();
                            out.println(Constants.OK);
                            break;
                        case "S":
                            //out.println(game.startGame());
                            break;
                        default:
                            out.println(Constants.INVALID_ARGS+",2"); //Unknown argument for G at agrs[2]
                            break;
                    }
                    break;
                case "P":
                    if(args.length < 3)
                    {
                        out.println(Constants.MISSING_ARGS); //Missing arguments
                        break;
                    }  
                    switch (args[2]) {
                        case "N":
                            if(game.addNewPlayer(args[3]))
                                out.println(Constants.OK+",True");
                            else
                                out.println(Constants.OK+"False");
                            break;
                        case "S":
                            out.println(Constants.OK+game.getPlayer(args[3]).getScore());
                            break;
                        case "T":
                            out.println(Constants.OK+game.tilesToString(args[3]));
                            break;
                        case "W": // Example: P,W,pName,word-row-col-true/false
                            String[] wordArgs = args[3].split("-");
                            if(wordArgs.length != 4)
                            {
                                out.println(Constants.MISSING_ARGS); //Missing arguments
                                break;
                            }
                            out.println(Constants.OK+game.placeWord(args[3], game.getWordFromString(args[3] ,wordArgs[0], Integer.parseInt(wordArgs[1]) , Integer.parseInt(wordArgs[2]), Boolean.parseBoolean(wordArgs[3]))));
                            break;
                        case "L":
                            out.println(Constants.OK+game.playerLeftGame(args[3]));
                            break;
                        default:
                            out.println(Constants.INVALID_ARGS+",2"); //Invalid argument for P at agrs[2]
                            break;
                    }
                    break;
                case "T":
                    String response = game.getPlayer(args[2]).getRack().takeTileFromBag();
                    if (response.equals("0"))
                        out.println(Constants.GAME_ENDED+game.getWinner());
                    else
                        out.println(Constants.OK+game.getPlayer(args[2]).getRack().takeTileFromBag());
                    break;
                default:
                    out.println(Constants.INVALID_ARGS+",1"); //Invalid argument at args[1]
                    break;
            }    
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