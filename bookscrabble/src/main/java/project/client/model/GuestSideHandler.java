package project.client.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import project.client.Error_Codes;
import project.client.model.assets.GameModel;
import project.client.model.assets.PlayerModel;

public class GuestSideHandler implements RequestHandler{
    GameModel game;
    Scanner in;
    PrintWriter out;

    public GuestSideHandler(){game = new GameModel();}

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        in = new Scanner(inFromClient);
        out = new PrintWriter(outToClient);
        String line = in.next();
        String[] args = line.split(",");
        if(args == null || args.length < 1)
        {
            //Error_Codes.UNKNOWN_CMD //No arguments
            out.flush();
            return;
        }
        else //Refer to communication protocol!!! (Q,C are handled in --- class)
        {
            switch (args[0]) {
                case "0":
                    System.out.println(Error_Codes.SERVER_ERR+",Can't join game.");
                    break;
                case "1":
                    if(args.length < 2)
                    {
                        //Error_Codes.MISSING_ARGS //Missing arguments
                        break;
                    }
                    for(int i = 1; i < args.length; i++)
                        game.addNewPlayer(args[i]); //Add players to local game
                    break;
                case "2":
                    if(args.length != 2)
                    {
                       //Error_Codes.MISSING_ARGS //Missing arguments
                        break;
                    }
                    if(args[1] != "0" && args.length == 3) //Word is valid, score and tiles are given
                    {
                        game.getPlayer(ClientModel.myName).addScore(Integer.parseInt(args[1]));
                        game.getPlayer(ClientModel.myName).getRack().takeTiles(args[2]);
                    }   
                    else //Word is invalid, no score given
                    {
                        //try again TODO
                    }
                    break;
                case "3":
                    if(args.length != 2)
                    {
                        //Error_Codes.MISSING_ARGS //Missing arguments
                        break;
                    }
                    game.getPlayer(ClientModel.myName).getRack().takeTiles(args[1]);
                    break; 
                case "G":
                    if(args.length < 3)
                    {
                        //Error_Codes.MISSING_ARGS //Missing arguments
                        break;
                    }
                    switch (args[1]) {
                        case "S":
                            createGame();
                            for (int i = 2; i < args.length; i++) {
                                String[] player_tiles_split = args[i].split("-");
                                if(player_tiles_split.length != 2)
                                {
                                    //Error_Codes.INVALID_ARGS //Invalid arguments
                                    break;
                                }
                                if(game.getPlayer(player_tiles_split[0]) == null)
                                    //Error_Codes.INVALID_ARGS //Invalid arguments (player doesn't exist)
                                game.getPlayer(player_tiles_split[0]).getRack().takeTiles(player_tiles_split[1]);              
                            }                         
                        case "P":
                            if(args.length != 4)
                            {
                                //Error_Codes.MISSING_ARGS //Missing arguments
                                break;
                            }
                            String newPlayer = args[3];
                            switch (args[2]) {
                                case "N":
                                    game.addNewPlayer(newPlayer);
                                case "L":
                                    game.removePlayer(newPlayer);
                                case "W":
                                    if(args.length != 7)
                                    {
                                        //Error_Codes.MISSING_ARGS //Missing arguments
                                        break;
                                    }
                                    PlayerModel newPlayerModel = game.getPlayer(newPlayer);
                                    String[] wordAgrs = args[6].split("-");
                                    if(wordAgrs.length != 4)
                                    {
                                        //Error_Codes.MISSING_ARGS //Missing arguments
                                        break;
                                    }
                                    game.placeWord(newPlayer, wordAgrs[0], Integer.parseInt(wordAgrs[1]), Integer.parseInt(wordAgrs[2]), Boolean.parseBoolean(wordAgrs[3]));
                                    newPlayerModel.addScore(Integer.parseInt(args[4]));
                                    newPlayerModel.getRack().takeTiles(args[5]);
                                    break;
                                default:
                                    //Error_Codes.INVALID_ARGS //Invalid argument
                                    break;
                            }
                            break;
                        case "N":
                            if(args.length != 4)
                            {
                                //Error_Codes.MISSING_ARGS //Missing arguments
                                break;
                            }
                            String newPlayer2 = args[2];
                            game.getPlayer(newPlayer2).getRack().takeTiles(args[3]);
                        default:
                            //Error_Codes.INVALID_ARGS //Invalid argument
                            break;
                    }
                case "E":
                    if(args.length != 2)
                    {
                        //Error_Codes.MISSING_ARGS //Missing arguments
                        break;
                    }
                    game.gameEnded = true;
                    String winner = args[1];
                    //TODO Show winner on screen
                    break;
                case Error_Codes.UNKNOWN_CMD:
                case Error_Codes.INVALID_ARGS:
                case Error_Codes.MISSING_ARGS:
                case Error_Codes.ACCESS_DENIED:
                case Error_Codes.SERVER_ERR:
                    System.out.println("Error: "+args[0]);
                    break;
                default:
                    //Error_Codes.UNKNOWN_CMD //No arguments
                    break;
            }
        }
        out.flush();
    }
    
    private void createGame() {game = new GameModel();}
    
    @Override
    public void close() {
        in.close();
        out.close();
    }
}
