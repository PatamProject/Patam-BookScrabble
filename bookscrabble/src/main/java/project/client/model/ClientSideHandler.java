package project.client.model;

import project.client.Error_Codes;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// This class is used to handle the host's responses
public class ClientSideHandler implements RequestHandler{
    private lightweightGameModel game;
    private PrintWriter out;
    private Map<String, Consumer<String[]>> commandHandler;
    private Map<String, Consumer<String[]>> responseHandler;
    private Map<String, Consumer<String[]>> errorHandler;
    private int id = 0;
    private int numOfChallenges = 0;

    public ClientSideHandler() {
        game = new lightweightGameModel();
        
        //ResponseHandler
        responseHandler = new HashMap<>(){{
            //Tried to join the game
            put("join", (String[] args) -> 
            { //Seccussful join, args[0] = id, args[1]... = players
                id = Integer.parseInt(args[0]);
                String[] connectedPlayers = new String[args.length - 1];
                for (int i = 1; i < args.length; i++) 
                    connectedPlayers[i - 1] = args[i];
    
                game.addPlayers(connectedPlayers);
                //TODO show connectedPlayers on screen
            });
    
            //Tried to place a word on the board
            put("Q", (String[] args) -> 
            { //Seccussful join, args[0] = id, args[1]... = players
                Integer score = Integer.parseInt(args[1]);
                if(score == 0) //Not boardLegal
                {
                    //TODO try again
                } else if(score == -1) //Not dictornaryLegal
                {
                    //TODO try again
                } else //Word was placed successfully
                {
                    game.myPlayer.addScore(score);
                    game.myPlayer.getRack().takeTiles(args[2]);
                }
            });
    
            //Tried to challenge a failed word placement
            put("C", (String[] args) -> 
            { //Seccussful join, args[0] = id, args[1]... = players
                Integer score = Integer.parseInt(args[1]);
                if(score == 0) //Not boardLegal
                {
                    numOfChallenges++;
                    //TODO try again
                } else if(score == -1) //Not dictornaryLegal
                {
                    numOfChallenges++;
                    //TODO try again
                } else //Word was placed successfully
                {
                    game.myPlayer.addScore(score);
                    game.myPlayer.getRack().takeTiles(args[2]);
                }
            }); 
        }};

        //CommandHandler
        commandHandler = new HashMap<>(){{
            //A new player joined the game
            put("!join", (String[] args) -> 
            { //args[0] = new player
                game.addPlayers(args[0]);
    
                //TODO show connectedPlayers on screen
            });
    
            //A player left the game
            put("!leave", (String[] args) -> 
            { //args[0] = leaving player
                game.removePlayer(args[0]);
    
                //TODO show connectedPlayers on screen
            });
    
            //Board update (replace the board completely)
            put("!board", (String[] args) -> 
            { 
                game.setBoard(args);
                //TODO show board on screen
            });
    
            //A player placed a word on the board
            put("!Q", (String[] args) -> 
            { handleWordPlacement(args);});
    
            //A player challenged a word placement and it was accepted
            put("!C", (String[] args) -> 
            { handleWordPlacement(args);});
        }};

        //ErrorHandler
        errorHandler = new HashMap<>(){{
            put(Error_Codes.UNKNOWN_CMD, (String[] args) -> 
            { 
                System.out.println("Unknown command");
            });

            put(Error_Codes.INVALID_ARGS, (String[] args) -> 
            { 
                System.out.println("Invalid arguments for command");
            });
            put(Error_Codes.MISSING_ARGS, (String[] args) -> 
            { 
                System.out.println("Missing arguments for command");
            });
    
            put(Error_Codes.SERVER_ERR, (String[] args) -> 
            { 
                System.out.println("Server error");
            });
    
            put(Error_Codes.ACCESS_DENIED, (String[] args) -> 
            { 
                System.out.println("Access denied, you are not premitted to use this command");
            });
    
            put(Error_Codes.SERVER_FULL, (String[] args) -> 
            { 
                System.out.println("Server is full, try again later");
            });
    
            put(Error_Codes.GAME_STARTED, (String[] args) -> 
            {
                System.out.println("Game has already started, you can't join now");
            });
    
            put(Error_Codes.NAME_TAKEN, (String[] args) -> 
            { 
                System.out.println("Name is already taken, try another name");
            });
     
            put(Error_Codes.NOT_YOUR_TURN, (String[] args) -> 
            { 
                System.out.println("It's not your turn, you can't use this command now");
            });
        }};
    }

    @Override
    public void handleClient(String sender, String commandName, String[] args, OutputStream outToClient) {
        if(sender.equals(ClientModel.myName)) //A response to a command sent by this client
            responseHandler.get(commandName).accept(args);
        else if(sender.charAt(0) == '!') //Game update from host
            commandHandler.get(commandName).accept(args);
        else //Error from host
            errorHandler.get(commandName).accept(args);    
    }

    public void handleWordPlacement(String[] args) //Adds score to the player placing the word
    { //args[0] = player, args[1] = score
        game.players.put(args[0], game.players.getOrDefault(args[0], 0) + Integer.parseInt(args[1]));
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