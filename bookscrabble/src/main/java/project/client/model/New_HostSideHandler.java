package project.client.model;

import project.client.Error_Codes;
import project.client.model.assets.GameModel;
import project.client.model.assets.Word;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

// This class is used to handle the guest's requests
public class New_HostSideHandler implements New_RequestHandler{
    private GameModel game;
    private PrintWriter out;
    private Map<String, Consumer<String[]>> commandHandler;

    public New_HostSideHandler() {
        game = new GameModel();
        commandHandler = new HashMap<>();
        //List of commands and their handlers: (First agrument is always the name of the player, please refer to the ConnectionProtocol for more information)
        //Add a new player to the game
        commandHandler.put("join", (String[] args) -> 
        { 
            //Added to connectedClients in MyHostServer
            game.addNewPlayer(args[0]); //Add to gameModel
            out.println("join:true"); //Send true to the client
            New_MyHostServer.updateAll("join:" + args[0], args[0]);
            
        });
        //Remove a player from the game
        commandHandler.put("leave", (String[] args) -> 
        {
            //Return tiles to back? Delete playerModel completely from view?
            game.removePlayer(args[0]); //Remove from gameModel
            try { //Close the socket
                New_MyHostServer.connectedClients.get(args[0]).close();
            } catch (IOException e) {
                e.printStackTrace();
            } 
            New_MyHostServer.connectedClients.remove(args[0]); //Remove from connectedClients
            New_MyHostServer.updateAll("leave:" + args[0], args[0]);            
        });

        commandHandler.put("skipTurn", (String[] args) -> 
        {
            game.nextPlayer();       
        });

        //Place a word on the board and query the words created (args[0] = name, args[1] = word, args[2] = row, args[3] = col, args[4] = isVertical) 
        commandHandler.put("Q", (String[] args) -> handlerBSrequests("Q", args));
        commandHandler.put("C", (String[] args) -> handlerBSrequests("C",args));    
    }
    
    private void handlerBSrequests(String commandName ,String[] args)
    {
        Word w = game.createWordFromClientInput(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Boolean.parseBoolean(args[4]));
        String[] words = game.getWordsFromClientInput(w); //Get all words created from the client input
        if(words == null || words.length == 0) //No words were created from the client input (Not boardLegal)
        {
            out.println(commandName+":0"); //Score = 0, word not placed due to boardLegal
            return;
        }
        //Check if all words are dictornaryLegal
        Boolean areWordsLegal = true; 
        for (int i = 0; i < words.length; i++)
            areWordsLegal |= New_ClientModel.getHostServer().msgToBSServer(words[i]);
        
        if(!areWordsLegal) //Not all words are dictornaryLegal
        {
            out.println(commandName+":-1"); //Score = -1
        }
        else
        {
            Integer score = game.placeWord(args[0], w);
            out.println(commandName+":"+score); //Send score to client
            New_MyHostServer.updateAll(args[0]+"&"+commandName+":"+score, args[0]); //Send score to all players
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
/*String msgToBS = commandName + "," + commandArgs[0];
                            Boolean result = msgToBSServer(msgToBS, aClient.getOutputStream());
                            if(result == null) //If the BookScrabbleServer is not available
                            {
                                throwError(Error_Codes.SERVER_ERR, out);
                                continue;
                            }
                            else //If the BookScrabbleServer returned a result
                            {
                                String[] newCommandArgs = new String[commandArgs.length + 1];
                                for(int i = 0; i < commandArgs.length; i++)
                                    newCommandArgs[i] = commandArgs[i];
                                if(result) //If the word is valid
                                    newCommandArgs[newCommandArgs.length - 1] = "1";
                                else //If the word is not valid
                                    newCommandArgs[newCommandArgs.length - 1] = "0";  
                                    
                                super.getRequestHandler().handleClient(sender, "placeWord", newCommandArgs, connectedClients.get(sender).getOutputStream());    
                            }
                            break; */