package project.client.model;

import project.client.Error_Codes;
import project.client.model.assets.GameModel;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// This class is used to handle the host's responses
public class New_GuestSideHandler implements New_RequestHandler{
    private GameModel game;
    private PrintWriter out;
    private Map<String, Consumer<String[]>> commandHandlers;

    public New_GuestSideHandler() {
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


// public class New_GuestSideHandler implements RequestHandler {
//     private GameModel game;
//     private Scanner in;
//     private PrintWriter out;
//     private Map<String, CommandHandler> commandHandlers;

//     public New_GuestSideHandler() {
//         commandHandlers = new HashMap<>();
//         commandHandlers.put("0", this::handleJoinGameError);
//         commandHandlers.put("1", this::handleAddPlayers);
//         commandHandlers.put("2", this::handlePlaceWord);
//         commandHandlers.put("3", this::handleTakeTile);
//         commandHandlers.put("G", this::handleGameUpdate);
//         commandHandlers.put("E", this::handleGameEnd);
//     }

//     @Override
//     public void handleClient(InputStream inFromClient, OutputStream outToClient) {
//         in = new Scanner(inFromClient);
//         out = new PrintWriter(outToClient);

//         if (!in.hasNextLine()) {
//             out.flush();
//             return;
//         }

//         String line = in.nextLine();
//         String[] args = line.split(",");
//         if (args.length < 1) {
//             out.flush();
//             return;
//         }

//         CommandHandler commandHandler = commandHandlers.get(args[0]);
//         if (commandHandler != null) {
//             commandHandler.handleCommand(args);
//         } else {
//             System.out.println("Error: " + args[0]);
//         }

//         out.flush();
//     }

//     @Override
//     public void close() {
//         in.close();
//         out.close();
//     }

//     private void handleJoinGameError(String[] args) {
//         System.out.println(Error_Codes.SERVER_ERR + ",Can't join game.");
//     }

//     private void handleAddPlayers(String[] args) {
//         if (args.length < 2) {
//             // Error_Codes.MISSING_ARGS //Missing arguments
//             return;
//         }

//         for (int i = 1; i < args.length; i++) {
//             game.addNewPlayer(args[i]); // Add players to local game
//         }
//     }

//     private void handlePlaceWord(String[] args) {
//         if (args.length != 2) {
//             // Error_Codes.MISSING_ARGS //Missing arguments
//             return;
//         }

//         if (args[1] != "0" && args.length == 3) { // Word is valid, score and tiles are given
//             PlayerModel player = game.getPlayer(ClientModel.myName);
//             player.addScore(Integer.parseInt(args[1]));
//             player.getRack().takeTiles(args[2]);
//         } else {
//             // try again TODO
//         }
//     }

//     private void handleTakeTile(String[] args) {
//         if (args.length != 2) {
//             // Error_Codes.MISSING_ARGS //Missing arguments
//             return;
//         }

//         PlayerModel player = game.getPlayer(ClientModel.myName);
//         player.getRack().takeTiles(args[1]);
//     }

//     private void handleGameUpdate(String[] args) {
//         if (args.length < 3) {
//             // Error_Codes.MISSING_ARGS //Missing arguments
//             return;
//         }

//         String command = args[1];
//         switch (command) {
//             case "S":
//                 createGame();
//                 for (int i = 2; i < args.length; i++) {
//                     String[] playerTilesSplit = args[i].split("-");
//                     if (playerTilesSplit.length != 2) {
//                         // Error_Codes.INVALID_ARGS //Invalid arguments
//                         break;
//                     }

//                     PlayerModel player = game.getPlayer(playerTilesSplit[0]);
//                     if (player == null) {
//                         // Error_Codes.INVALID_ARGS //Invalid arguments (player doesn't exist)
//                         break;
//                     }
//                     player.getRack().takeTiles(playerTilesSplit[1]);
//                 }
//                 break;
//             case "P":
//             case "N":
//             case "E":
//                 // Handle other game updates
//                 break;
//             default:
//                 break;
//         }
//     }

//     private void handleGameEnd(String[] args) {
//         // Handle game end
//     }

//     private void createGame() {
//         game = new GameModel();
//     }

//     private interface CommandHandler {
//         void handleCommand(String[] args);
//     }
// }
