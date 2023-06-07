package project.client.model;

import project.client.Error_Codes;
import project.client.model.assets.GameManager;
import project.client.model.assets.Word;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// This class is used to handle the guest's requests
public class HostSideHandler implements RequestHandler{
    private GameManager game;
    private PrintWriter out;
    private Map<String, Consumer<String[]>> commandHandler;

    public HostSideHandler() {
        game = new GameManager();
        commandHandler = new HashMap<>(){{
            //List of commands and their handlers: (First argument is always the name of the player, please refer to the ConnectionProtocol for more information)
            //Add a new player to the game
            put("join", (String[] args) -> 
            { //Added to connectedClients in MyHostServer
                ArrayList<String> players = new ArrayList<>(Arrays.asList(args).subList(2, args.length));
                StringBuilder sendPlayers = new StringBuilder();
                for (String p : players) //Create a string of players to send to the client with commas between them
                    sendPlayers.append(p).append(",");
                sendPlayers = new StringBuilder(sendPlayers.substring(0, sendPlayers.length() - 1)); //Trim last comma

                game.addNewPlayer(args[0]); //Add to gameModel
                out.println("join:"+args[1] + "," + sendPlayers); //Send ID and players to the client
                MyHostServer.updateAll("!join:" + args[0], args[0]);
            });
            //Remove a player from the game
            put("leave", (String[] args) -> 
            {
                //Return tiles to back? Delete playerModel completely from view?
                game.removePlayer(args[0]); //Remove from gameModel
                try { //Close the socket
                    MyHostServer.getHostServer().connectedClients.get(args[0]).close();
                } catch (IOException e) {
                    e.printStackTrace();
                } 
                MyHostServer.getHostServer().connectedClients.remove(args[0]); //Remove from connectedClients
                MyHostServer.updateAll("!leave:" + args[0], args[0]);
                if (game.getPlayersAmount() < 2)
                    get("endGame");
            });
    
            put("skipTurn", (String[] args) -> 
            {
                game.nextTurn(); // Switch to next player and update ALL players that the game continues
                if (!game.isGameEnded())
                    MyHostServer.updateAll("!skipTurn: " + game.getCurrentPlayersName(), null);
                else {
                    String winner = game.getWinner();
                    MyHostServer.updateAll("!endGame:" + winner, null);
                }
            });
    
            //Start the game (Host only)
            put("startGame", (String[] args) ->
            {
                if (args[0].equals(ClientModel.getName()) && game.getPlayersAmount() >= 2) //Enough players and is the host
                { //Each player and tiles are sent in this format from startGame(): "p1%tiles"
                    try {
                        MyHostServer.getHostServer().isGameRunning = true; //Set gameStarted to true
                        String[] tilesAndPlayers = game.startGame(); //Start the game on the host's side
                        StringBuilder playersOrder = new StringBuilder();
                        for (String tilesAndPlayer : tilesAndPlayers)
                            playersOrder.append(tilesAndPlayer.split("%")[0]).append(",");
                        playersOrder = new StringBuilder(playersOrder.substring(0, playersOrder.length() - 1)); //Trim last comma

                        for (String tilesAndPlayer : tilesAndPlayers) {
                            String playerName = tilesAndPlayer.split("%")[0];
                            String tiles = tilesAndPlayer.split("%")[1];
                            MyHostServer.sendUpdate("!startGame:" + tiles + "," + playersOrder, playerName); //Send startGame command to all players
                        }
                    } catch (Exception e) {
                        out.println(Error_Codes.SERVER_ERR);
                    }
                } //else not host
            });
    
            //End the game and declare a winner (Host only)
            put("endGame", (String[] args) -> 
            {
                if(args[0].equals(ClientModel.getName()) || game.getPlayersAmount() < 2) //Is the host
                {
                    String winner = game.getWinner();
                    MyHostServer.updateAll("!endGame:" + winner + ",Host ended game.", null);
                }
                else //Not the host
                    out.println(Error_Codes.ACCESS_DENIED); //unauthorized
            });
    
            //Place a word on the board and query the words created (args[0] = name, args[1] = word, args[2] = row, args[3] = col, args[4] = isVertical) 
            put("Q", (String[] args) -> handleBSRequest("Q", args));
            put("C", (String[] args) -> handleBSRequest("C",args));
        }};
    }
    
    private void handleBSRequest(String commandName , String[] args)
    {
        Word w = game.fromStringToWord(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Boolean.parseBoolean(args[4]));
        if(w == null) //No word was created from the client input (Not boardLegal)
        {
            out.println(commandName+":0"); //Score = 0, word not placed due to boardLegal
            return;
        }
        String[] words = game.getStringsToSendToBS(w); //Get all words created from the client input
        if(words == null || words.length == 0) //No words were created from the client input (Not boardLegal)
        {
            out.println(commandName+":0"); //Score = 0, word not placed due to boardLegal
            return;
        }
        //Check if all words are dictionaryLegal
        Boolean areWordsLegal = true;
        for (String word : words)
            areWordsLegal &= MyHostServer.getHostServer().msgToBSServer(commandName + "," + word);
        
        if(!areWordsLegal) //Not all words are dictionaryLegal
            out.println(commandName+":-1"); //Score = -1
        else
        {
            Integer score = game.tryPlaceWord(args[0], w);
            if(score != 0)
            {
                try { //Take tiles from bag
                    game.getPlayer(args[0]).getRack().takeTilesFromBag();
                    String currentTiles = game.getPlayer(args[0]).getRack().toString(); //The current tiles of the player
                    game.nextTurn(); //next player's turn
                    out.println(commandName+":"+score + "," + currentTiles); //Send score and tiles to client
                    MyHostServer.updateAll("!"+commandName+":"+ args[0] + "," +score, args[0]); //Send score to all players
                    MyHostServer.updateAll("!board:" + game.getBoard().toString(), null); //Send board to all players
                } catch (Exception e) {
                    String winner = game.getWinner(); //Game ended, bag is empty
                    MyHostServer.updateAll("!endGame:" + winner, null);
                } 
            }
            else
                out.println(commandName+":0"); //Score = 0, word not placed
        }
    }

    @Override
    public void handleClient(String sender, String commandName, String[] args, OutputStream outToClient) {
        if(game.getPlayer(args[0]) == null && !commandName.equals("join")) //If the player is not in the game and the command is not join
            out.println(Error_Codes.ACCESS_DENIED);
        else
        {
            out = new PrintWriter(outToClient, true);
            commandHandler.get(commandName).accept(args);
        }
    }

    @Override
    public void close() {out.close();}
}