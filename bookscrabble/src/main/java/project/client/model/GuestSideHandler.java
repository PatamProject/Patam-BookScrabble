package project.client.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import project.client.Error_Codes;
import project.client.model.assets.GameModel;

public class GuestSideHandler implements RequestHandler{
    GameModel game;
    Scanner in;
    PrintWriter out;

    public GuestSideHandler(){}

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
                        case "N":
                        case "E":
                            
                            break;
                    
                        default:
                            break;
                    }
                case "E":

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
