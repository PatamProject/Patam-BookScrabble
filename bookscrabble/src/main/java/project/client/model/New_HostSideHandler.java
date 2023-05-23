package project.client.model;

import project.client.Error_Codes;
import project.client.model.assets.GameModel;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// This class is used to handle the guest's requests
public class New_HostSideHandler implements New_RequestHandler{
    private GameModel game;
    private PrintWriter out;
    private Map<String, Consumer<String[]>> commandHandlers;

    public New_HostSideHandler() {
        game = new GameModel();
        commandHandlers = new HashMap<>();

        //List of commands and their handlers: (First agrument is always the name of the player, please refer to the ConnectionProtocol for more information)
        //Add a new player to the game
        commandHandlers.put("join", (String[] args) -> 
        { 
            //Added to connectedClients in MyHostServer
            game.addNewPlayer(args[0]); //Add to gameModel
            out.println("join:true"); //Send true to the client
            New_MyHostServer.updateAll("join:" + args[0], args[0]);
            
        });
        //Remove a player from the game
        commandHandlers.put("leave", (String[] args) -> 
        {
            //Return tiles to back? Delete playerModel completely from view?
            game.removePlayer(args[0]); //Remove from gameModel
            New_MyHostServer.connectedClients.remove(args[0]); //Remove from connectedClients
            New_MyHostServer.updateAll("leave:" + args[0], args[0]);
            
        });
        //Place a word on the board (args[0] = name, args[1] = word, args[2] = row, args[3] = col, args[4] = isVertical, args[5] = Q/C return value (T = 1/F = 0))
        commandHandlers.put("placeWord", (String[] args) -> //NOT FINISHED
        {
            Integer[] score_tiles = game.placeWord(args[0], args[1] ,Integer.parseInt(args[2]), Integer.parseInt(args[3]), Boolean.parseBoolean(args[4]));
            if(score_tiles[0] == 0) //Score of 0 means that the word was not placed (Not boardLegal)
            {
                out.println("placeWord:0");
            }
            else if(Integer.parseInt(args[5]) == 0)//Word was not placed due to dictornaryLegal
            {
                out.println("placeWord:-1");
            }
            else //Word was placed successfully
            {
                try {
                    String tilesTaken = game.getPlayer(args[0]).getRack().takeTilesFromBag(score_tiles[1]); //Num of tiles to take from the bag at random
                    out.println("placeWord:" + score_tiles[0] + "," + tilesTaken); //Send score to client and tiles taken from bag
                    String msg = "placeWord:"+args[0]+","+score_tiles[0]+","+score_tiles[1]+","+args[1]+","+args[2]+","+args[3]+","+args[4];
                    //Msg format: placeWord:playerName,score,numOfTilesTaken,word,row,col,isVertical
                    New_MyHostServer.updateAll(msg,args[0]);
                } catch (Exception e) {
                    stopGame();
                }
            }
        });
       //Take a tile from the bag (Skip turn/No words were placed) (args[0] = name)
        commandHandlers.put("takeTile", (String[] args) ->
        {
            try {
                String tile = game.getPlayer(args[0]).getRack().takeTilesFromBag(1);
                out.println("takeTile:" + tile);
            } catch (Exception e) {
                stopGame();
            }    
        });

        
    }

    public void stopGame() //Stops the game, finds and sends a winner to all players, closes game for everyone
    {
        //TODO
    }

    @Override
    public void handleClient(String sender, String commandName, String[] args, OutputStream outToClient) {
        if(game.getPlayer(args[0]) == null && commandName != "join") //If the player is not in the game and the command is not join
            out.println(Error_Codes.ACCESS_DENIED);
        else
            commandHandlers.get(commandName).accept(args);
    }

    @Override
    public void close() {
        out.close();
    }
}
