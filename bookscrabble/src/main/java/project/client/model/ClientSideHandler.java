package project.client.model;

import project.client.Error_Codes;
import project.client.MyLogger;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

// This class is used to handle the host's responses
public class ClientSideHandler implements RequestHandler{
    GameModel game;
    public PrintWriter out;
    private Map<String, Consumer<String[]>> commandHandler;
    private Map<String, Consumer<String[]>> responseHandler;
    private Map<String, Consumer<String[]>> errorHandler;
    private int id = 0;
    private int numOfChallenges = 0;
    boolean isGameStarted = false;

    public ClientSideHandler(PrintWriter out) {
        game = new GameModel();
        this.out = out;
        
        //ResponseHandler (responses from the server to the client's request)
        responseHandler = new HashMap<>(){{
            //Tried to join the game
            put("join", (String[] args) -> 
            { //Successful join, args[0] = id, args[1]... = players
                id = Integer.parseInt(args[0]);
                String[] connectedPlayers = new String[args.length - 1];
                System.arraycopy(args, 1, connectedPlayers, 0, args.length - 1);
    
                game.addPlayers(connectedPlayers);
            });
    
            //Tried to place a word on the board
            put("Q", (String[] args) -> 
            { //Successful word placement, args[0] = score, args[1]... = players
                Integer score = Integer.parseInt(args[1]);
                if(score == 0 || score == -1) //Not boardLegal
                    MyLogger.failedWordPlacement(score);
                
                else //Word was placed successfully
                {
                    game.updateScore(ClientModel.getName(), score); //Update score
                    game.myPlayer.getRack().takeTiles(args[2]); //Take tiles
                    game.nextTurn(); //Next turn
                    MyLogger.playerPlacedWord(ClientModel.getName(), score, "Q");
                }
            });
    
            //Tried to challenge a failed word placement
            put("C", (String[] args) -> 
            { //Successful join, args[0] = id, args[1]... = players
                Integer score = Integer.parseInt(args[1]);
                MyLogger.log("Challenging dictonary...");
                if(score == 0 || score == -1) //Not boardLegal
                {
                    numOfChallenges++;
                    MyLogger.failedWordPlacement(score);
                } 
                else //Word was placed successfully
                {
                    game.updateScore(ClientModel.getName(), score); //Update score
                    game.myPlayer.getRack().takeTiles(args[2]); //Take tiles
                    game.nextTurn(); //Next turn
                    MyLogger.playerPlacedWord(ClientModel.getName(), score, "C");
                }
            }); 

            put("hello", (String[] args) -> 
            { //Used for testing
                MyLogger.log("Hello recieved");
            });
        }};

        //CommandHandler (commands from the host to the client about game updates)
        commandHandler = new HashMap<>(){{
            //A new player joined the game
            put("!join", (String[] args) -> 
            { //args[0] = new player
                game.addPlayers(args[0]);
                MyLogger.playerJoined(args[0]);
            });
    
            //A player left the game
            put("!leave", (String[] args) -> 
            { //args[0] = leaving player
                game.removePlayer(args[0]);
                MyLogger.playertLeft(args[0]);
            });

            //Game started, tiles are sent to each player individually
            put("!startGame", (String[] args) -> 
            { //args[0] = tiles, args[1] = player1, args[2] = player2, ... 
                game.myPlayer.getRack().takeTiles(args[0]); //Take tiles
                //Add players by order
                String[] players = new String[args.length - 1];
                for(int i = 1; i < args.length; i++)
                {
                    players[i - 1] = args[i];
                    game.playersOrder.add(args[i]); //Add players by order
                }
                game.addPlayers(players); //Add players to the game
                //Game started in ClientCommunication
            });

            put("!endGame", (String[] args)->{
                isGameStarted = false;
                MyLogger.gameEnded(args[0]);
            });
    
            //Board update (replace the board completely)
            put("!board", (String[] args) -> 
            { 
                game.setBoard(args[0]);
                MyLogger.printBoard(game.getBoard());
            });
    
            //A player placed a word on the board
            put("!Q", (String[] args) -> 
            { handleWordPlacement(args, "Q");});
    
            //A player challenged a word placement and it was accepted
            put("!C", (String[] args) -> 
            { handleWordPlacement(args , "C");});
        }};

        //ErrorHandler (errors from the host)
        errorHandler = new HashMap<>(){{
            put(Error_Codes.UNKNOWN_CMD, (String[] args) -> 
            { 
                MyLogger.log("Unknown command");
            });

            put(Error_Codes.INVALID_ARGS, (String[] args) -> 
            { 
                MyLogger.log("Invalid arguments for command");
            });
            put(Error_Codes.MISSING_ARGS, (String[] args) -> 
            { 
                MyLogger.log("Missing arguments for command");
            });
    
            put(Error_Codes.SERVER_ERR, (String[] args) -> 
            { 
                MyLogger.log("Server error");
            });
    
            put(Error_Codes.ACCESS_DENIED, (String[] args) -> 
            { 
                MyLogger.log("Access denied, you are not premitted to use this command");
            });
    
            put(Error_Codes.SERVER_FULL, (String[] args) -> 
            { 
                MyLogger.log("Server is full, try again later");
            });
    
            put(Error_Codes.GAME_STARTED, (String[] args) -> 
            {
                MyLogger.log("Game has already started, you can't join now");
            });
    
            put(Error_Codes.NAME_TAKEN, (String[] args) -> 
            { 
                MyLogger.log("Name is already taken, try another name");
            });
     
            put(Error_Codes.NOT_YOUR_TURN, (String[] args) -> 
            { 
                MyLogger.log("It's not your turn, you can't use this command now");
            });

            put(Error_Codes.NOT_ENOUGH_PLAYERS, (String[] args) -> 
            { 
                MyLogger.log("Can't start game, not enough players");
            });

            put(Error_Codes.GAME_ENDED, (String[] args) -> //Do we need this?
            { 
                MyLogger.log("The game has ended, you can't use this command now");
            });
        }};
    }

    @Override
    public void handleClient(String sender, String commandName, String[] args, OutputStream outToClient) {
        if(sender.equals(ClientModel.getName())) //A response to a command sent by this client
            responseHandler.get(commandName).accept(args);
        else if(sender.charAt(0) == '!') //Game update from host
            commandHandler.get(commandName).accept(args);
        else //Error from host
            errorHandler.get(commandName).accept(args);    
    }

    public void handleWordPlacement(String[] args, String commandName) //Adds score to the player placing the word
    { //args[0] = player, args[1] = score
        game.players.put(args[0], game.players.getOrDefault(args[0], 0) + Integer.parseInt(args[1]));
        MyLogger.playerPlacedWord(args[0], Integer.parseInt(args[1]), commandName);
    }

    public int getId(){return id;}

    @Override
    public void close() {
        out.close();
    }
}