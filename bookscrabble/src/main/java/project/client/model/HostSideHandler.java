package project.client.model;

import project.client.Error_Codes;
import project.client.model.assets.GameModel;
import project.client.model.assets.PlayerModel;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

//This class is used to handle the guest's requests
public class HostSideHandler implements RequestHandler{
    GameModel game;
    Scanner in;
    PrintWriter out;

    public HostSideHandler(){game = new GameModel();}

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        in = new Scanner(inFromClient);
        out = new PrintWriter(outToClient);
        String line = in.next();
        String[] name_body_split = line.split("&");
            if(name_body_split.length != 2)
            {
                out.println(Error_Codes.MISSING_ARGS);
                out.flush();
                return;
            }

        String[] args = name_body_split[1].split(",");
        if(args == null || args.length < 1)
        {
            out.println(Error_Codes.UNKNOWN_CMD); //No arguments
            out.flush();
            return;
        }
        else //Refer to communication protocol!!! (Q,C are handled in --- class)
        {
            String playerName = name_body_split[0];
            if(game.getPlayer(playerName) == null) //Player not in game
            {
                if(args[0] == "1") //Add player to game
                {
                    game.addNewPlayer(playerName);
                    //Update all players
                }
                else
                    out.println(Error_Codes.ACCESS_DENIED); //Player not in game thus can't use any command other than "1"
            }
            else //Player already in game
            {
                PlayerModel player = game.getPlayer(playerName);
                switch (args[0]) {
                    case "0":
                        game.removePlayer(playerName);
                        MyHostServer.updateAll("G,P,L,"+playerName,outToClient);
                        break;
                    case "1":
                        out.println(Error_Codes.ACCESS_DENIED); //Can't join twice
                        break;
                    case "2":
                        if(args.length != 2)
                        {
                            out.println(Error_Codes.MISSING_ARGS); //Missing arguments
                            break;
                        }
                        String[] wordArgs = args[1].split("-");
                        if(wordArgs.length != 4)
                        {
                            out.println(Error_Codes.MISSING_ARGS); //Missing arguments
                            break;
                        }
                        Integer[] score_tiles = game.placeWord(playerName, wordArgs[0], Integer.parseInt(wordArgs[1]) , Integer.parseInt(wordArgs[2]), Boolean.parseBoolean(wordArgs[3]));
                        if(score_tiles[0] == 0)
                            out.println("2,0"); //Invalid word / No score given / Try again
                        else
                        {
                            try {
                                String tilesTaken = player.getRack().takeTilesFromBag(score_tiles[1]);
                                out.println("2,"+score_tiles[0]+","+tilesTaken);
                                String msg = "G,P,W,"+playerName+","+score_tiles[0]+","+tilesTaken+","+wordArgs[0]+","+wordArgs[1]+","+wordArgs[2]+","+wordArgs[3];
                                MyHostServer.updateAll(msg,outToClient);
                            } catch (Exception e) {
                                stopGame(outToClient);
                            }
                        }
                        break;
                    case "3":
                        try {
                            String t = player.getRack().takeTilesFromBag(1);
                            out.println("3,"+player.getRack().takeTilesFromBag(1));
                            MyHostServer.updateAll("G,N,"+playerName+","+t,outToClient);
                        } catch (Exception e) {
                            stopGame(outToClient);
                        }  
                    case "S": //Start game
                        //if Host
                        startGame(true,outToClient);
                        //else
                        //out.println(Error_Codes.ACCESS_DENIED); //Only host can start game
                        break;         
                    default:
                        out.println(Error_Codes.MISSING_ARGS); //Unknown command
                        break;
                }
            }
        }
        out.flush();
    }
    
    private void startGame(boolean isHost, OutputStream outToClient)
    {
        if(isHost)
        {
            game.startGame();
        }
        else
        {
            //Give players their tiles from ---
        }


        MyHostServer.updateAll("S.........",outToClient);
    }

    private void stopGame(OutputStream outToClient)
    {
        out.println("E,"+game.getWinner());
        MyHostServer.updateAll("E,"+game.getWinner(),outToClient);
        game.gameEnded = true;
    }

    @Override
    public void close() {
        in.close();
        out.close();
    }
}

