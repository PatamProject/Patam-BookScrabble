package project.client.model;

import project.client.Error_Codes;
import project.client.model.assets.GameModel;
import project.client.model.assets.Word;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// This class is used to handle the guest's requests
public class HostSideHandler implements RequestHandler{
    private GameModel game;
    private PrintWriter out;
    private Map<String, Consumer<String[]>> commandHandler;

    public HostSideHandler() {
        game = new GameModel();
        commandHandler = new HashMap<>();
        //List of commands and their handlers: (First agrument is always the name of the player, please refer to the ConnectionProtocol for more information)
        //Add a new player to the game
        commandHandler.put("join", (String[] args) -> 
        { //Added to connectedClients in MyHostServer
            ArrayList<String> players = new ArrayList<String>();
            for (int i = 2; i < args.length; i++)
                players.add(args[i]);
            
            game.addNewPlayer(args[0]); //Add to gameModel
            out.println("join:"+args[1] + "," + players); //Send ID and players to the client
            MyHostServer.updateAll("!join:" + args[0], args[0]);
        });
        //Remove a player from the game
        commandHandler.put("leave", (String[] args) -> 
        {
            //Return tiles to back? Delete playerModel completely from view?
            game.removePlayer(args[0]); //Remove from gameModel
            try { //Close the socket
                MyHostServer.connectedClients.get(args[0]).close();
            } catch (IOException e) {
                e.printStackTrace();
            } 
            MyHostServer.connectedClients.remove(args[0]); //Remove from connectedClients
            MyHostServer.updateAll("!leave:" + args[0], args[0]);            
        });

        commandHandler.put("skipTurn", (String[] args) -> 
        {
            String nextPlayer = game.nextPlayer(); //get next player and update ALL players
            MyHostServer.updateAll("!skipTurn:" + nextPlayer, null); 
        });

        commandHandler.put("startGame", (String[] args) -> 
        {
            if(args[0].equals(ClientModel.myName)) //is the host
            {
                game.startGame();
                MyHostServer.updateAll("!startGame:", args[0]); //TODO
            }
            else //Not the host
            {
                out.println(Error_Codes.ACCESS_DENIED); //unauthorized
            }
        });

        commandHandler.put("endGame", (String[] args) -> 
        {
            if(args[0].equals(ClientModel.myName)) //is the host
            {
                String winner = game.getWinner();
                stopGame();
                MyHostServer.updateAll("!endGame:" + winner + ",Host ended game.", null);
            }
            else //Not the host
            {
                out.println(Error_Codes.ACCESS_DENIED); //unauthorized
            }
        });

        //Place a word on the board and query the words created (args[0] = name, args[1] = word, args[2] = row, args[3] = col, args[4] = isVertical) 
        commandHandler.put("Q", (String[] args) -> handlerBSrequests("Q", args));
        commandHandler.put("C", (String[] args) -> handlerBSrequests("C",args));    
    }
    
    private void handlerBSrequests(String commandName ,String[] args)
    {
        Word w = game.createWordFromClientInput(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Boolean.parseBoolean(args[4]));
        if(w == null) //No word was created from the client input (Not boardLegal)
        {
            out.println(commandName+":0"); //Score = 0, word not placed due to boardLegal
            return;
        }
        String[] words = game.getWordsFromClientInput(w); //Get all words created from the client input
        if(words == null || words.length == 0) //No words were created from the client input (Not boardLegal)
        {
            out.println(commandName+":0"); //Score = 0, word not placed due to boardLegal
            return;
        }
        //Check if all words are dictornaryLegal
        Boolean areWordsLegal = true; 
        for (int i = 0; i < words.length; i++)
            areWordsLegal |= ClientModel.getHostServer().msgToBSServer(words[i]);
        
        if(!areWordsLegal) //Not all words are dictornaryLegal
        {
            out.println(commandName+":-1"); //Score = -1
        }
        else
        {
            Integer score = game.getScoreFromWord(args[0], w);
            String tiles = "";
            if(score != 0)
            {
                try { //Take tiles from bag
                    tiles = game.getPlayer(args[0]).getRack().takeTilesFromBag();
                } catch (Exception e) {
                    String winner = game.getWinner(); //Game ended, bag is empty
                    MyHostServer.updateAll("!endGame:" + winner, null);
                    stopGame(); //Send winner and stop game
                } 
            }
            else
            {
                out.println(commandName+":"+score + "," + tiles); //Send score to client
                MyHostServer.updateAll(args[0]+"&"+commandName+":"+score, args[0]); //Send score to all players
            }
        }
    }

    @Override
    public void handleClient(String sender, String commandName, String[] args, OutputStream outToClient) {
        if(game.getPlayer(args[0]) == null && commandName != "join") //If the player is not in the game and the command is not join
            out.println(Error_Codes.ACCESS_DENIED);
        else
        {
            out = new PrintWriter(outToClient, true);
            commandHandler.get(commandName).accept(args);
        }
    }

    public void stopGame() //Stops the game, finds and sends a winner to all players, closes game for everyone
    {
        //TODO
    }

    @Override
    public void close() {
        out.close();
    }
}