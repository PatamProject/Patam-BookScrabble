package bookscrabble.client.model;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import bookscrabble.client.misc.Error_Codes;
import bookscrabble.client.misc.MyLogger;
import bookscrabble.client.misc.RunClient;

// This class is used to handle the host's responses and updates
public class ClientSideHandler implements RequestHandler{
    GameModel game;
    PrintWriter out;
    private Map<String, Consumer<String[]>> commandHandler;
    private Map<String, Consumer<String[]>> responseHandler;
    private Map<String, Consumer<String[]>> errorHandler;
    private String myName;
    private int id = 0;
    private int numOfChallenges = 0;
    boolean isGameRunning = false;

    public ClientSideHandler(PrintWriter out) { //Ctor
        myName = ClientModel.getMyName();
        game = GameModel.getGameModel();
        this.out = out;

        //ResponseHandler (responses from the server to the client's request)
        responseHandler = new HashMap<>(){{
            //Tried to join the game
            put("join", (String[] args) -> 
            { //Successful join, args[0] = id, args[1]... = players
                id = Integer.parseInt(args[0]);
                String[] connectedPlayers = new String[args.length - 1];
                connectedPlayers[0] = myName; //Adding myself to the beginning of the array
                for (int i = 2; i < args.length; i++) //Adding the rest of the players
                    if(!args[i].equals(myName)) //Not me
                        connectedPlayers[i - 1] = args[i];

                game.addPlayers(connectedPlayers); //Add the rest of existing players
                for (String p : connectedPlayers)
                    MyLogger.playerJoined(p);

                if(id != 1) //Not the host
                    MyLogger.joinedGame();         
            });
    
            //Tried to place a word on the board
            put("Q", (String[] args) -> 
            { //Successful word placement, args[0] = score, args[1] = tiles
                Integer score = Integer.parseInt(args[0]);
                if(score == 0 || score == -1) //Not boardLegal
                {
                    game.wordPlacement(false);
                    MyLogger.failedWordPlacement(score, false);
                    ClientCommunications.unlock();
                }
                else //Word was placed successfully
                {
                    game.updateScore(myName, score); //Update score
                    game.myTiles = args[1]; //Updated the tiles
                    game.wordPlacement(true);
                    game.nextTurn(); //Next turn
                    MyLogger.playerPlacedWord(myName, score, "Q");
                    MyLogger.printTiles(game.myTiles); //Print my tiles
                }
            });
    
            //Tried to challenge a failed word placement
            put("C", (String[] args) -> 
            { //Successful word placement, args[0] = score, args[1] = tiles
                Integer score = Integer.parseInt(args[0]);
                MyLogger.println("Challenging dictionary...");
                if(score == 0 || score == -1) //Not boardLegal
                {
                    numOfChallenges++;
                    game.wordPlacement(false);
                    MyLogger.failedWordPlacement(score, true);
                    ClientCommunications.unlock();
                } 
                else //Word was placed successfully
                {
                    game.updateScore(myName, score); //Update score
                    game.myTiles = args[1]; //Updated the tiles
                    game.wordPlacement(true);
                    game.nextTurn(); //Next turn
                    MyLogger.playerPlacedWord(myName, score, "C");
                    MyLogger.printTiles(game.myTiles); //Print my tiles
                }
            }); 

            put("hello", (String[] args) -> 
            { //Used for testing
                MyLogger.println("Hello received");
            });
        }};

        //CommandHandler (commands from the host to the client about game updates)
        commandHandler = new HashMap<>(){{
            //A new player joined the game
            put("!join", (String[] args) -> 
            { //args[0] = new player
                MyLogger.playerJoined(args[0]); //This function is for notification only
                game.addPlayers(args[0]);
                game.setPlayerUpdateMessage(args[0] + " joined the lobby!\n");
            });
    
            //A player left the game
            put("!leave", (String[] args) -> 
            { //args[0] = leaving player
                MyLogger.playerLeft(args[0]); //This function is for notification only
                game.removePlayer(args[0]);
                game.setPlayerUpdateMessage(args[0] + " left the lobby!\n");
            });

            //Game started, tiles are sent to each player individually
            put("!startGame", (String[] args) -> 
            { //args[0] = tiles, args[1] = player1, args[2] = player2, ...
                game.playersAndScores.clear(); //Clear the players and scores
                game.myTiles = args[0]; //Updated the tiles    
                //Add players by order
                String[] players = new String[args.length - 1];
                for(int i = 1; i < args.length; i++)
                {
                    players[i - 1] = args[i];
                    game.playersOrder.add(args[i]); //Add players by order
                }
                game.addPlayers(players); //Add players to the game
                isGameRunning = true;
                RunClient.exit = true; //Close the manu to allow the user to play the game
                //Game started in ClientCommunication
                ClientModel.getClientModel().startGame();
            });

            put("!endGame", (String[] args)->{
                if(isGameRunning == true)
                {
                    MyLogger.gameEnded(args[0]);
                    game.setPlayerUpdateMessage(args[0] + " won the game!\n");
                }
                isGameRunning = false;
                try {
                    throw new Exception("endGame");
                } catch (Exception e) {}
            });
    
            //Board update (replace the board completely)
            put("!board", (String[] args) -> 
            { 
                game.setBoard(args[0]);
                MyLogger.printPlayerAndScore(game.getPlayersAndScores());
                MyLogger.printBoard(game.getBoard());
                MyLogger.printTiles(game.myTiles);
            });

            put("!skipTurn", (String[] args) -> 
            { 
                game.nextTurn();
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
                game.setErrorMessage("Unknown command");
                MyLogger.println(game.getErrorMessage());
            });

            put(Error_Codes.INVALID_ARGS, (String[] args) -> 
            { 
                game.setErrorMessage("Invalid arguments for command");
                MyLogger.println(game.getErrorMessage());
            });
            put(Error_Codes.MISSING_ARGS, (String[] args) -> 
            { 
                game.setErrorMessage("Missing arguments for command");
                MyLogger.println(game.getErrorMessage());
            });
    
            put(Error_Codes.SERVER_ERR, (String[] args) -> 
            { 
                game.setErrorMessage("Server error");
                MyLogger.println(game.getErrorMessage());
            });
    
            put(Error_Codes.ACCESS_DENIED, (String[] args) -> 
            { 
                game.setErrorMessage("Access denied, you are not permitted to use this command");
                MyLogger.println(game.getErrorMessage());
            });
    
            put(Error_Codes.SERVER_FULL, (String[] args) -> 
            { 
                game.setErrorMessage("Server is full, try again later");
                MyLogger.println(game.getErrorMessage());
            });
    
            put(Error_Codes.GAME_STARTED, (String[] args) -> 
            {
                game.setErrorMessage("Game has already started, you can't join now");
                MyLogger.println(game.getErrorMessage());
            });
    
            put(Error_Codes.NAME_TAKEN, (String[] args) -> 
            { 
                game.setErrorMessage("Name is already taken, try another name");
                MyLogger.println(game.getErrorMessage());
            });
     
            put(Error_Codes.NOT_YOUR_TURN, (String[] args) -> 
            { 
                game.setErrorMessage("It's not your turn, you can't use this command now");
                MyLogger.println(game.getErrorMessage());
            });

            put(Error_Codes.NOT_ENOUGH_PLAYERS, (String[] args) -> 
            { 
                game.setErrorMessage("Can't start game, not enough players");
                MyLogger.println(game.getErrorMessage());
            });

            put(Error_Codes.GAME_ENDED, (String[] args) -> //Do we need this?
            { 
                game.setErrorMessage("The game has ended, you can't use this command now");
                MyLogger.println(game.getErrorMessage());
            });
        }};
    }

    @Override
    public void handleClient(String sender, String commandName, String[] args, OutputStream outToClient) {
        if(sender.equals(myName)) //A response to a command sent by this client
            responseHandler.get(commandName).accept(args);
        else if(sender.charAt(0) == '!') //Game update from host
            commandHandler.get(commandName).accept(args);
        else //Error from host
            errorHandler.get(commandName).accept(args);    
    }

    public void handleWordPlacement(String[] args, String commandName) //Adds score to the player placing the word
    { //args[0] = player, args[1] = score
        game.playersAndScores.put(args[0], game.playersAndScores.getOrDefault(args[0], 0) + Integer.parseInt(args[1]));
        MyLogger.playerPlacedWord(args[0], Integer.parseInt(args[1]), commandName);

        if(ClientModel.getMyName().equals(args[0]))
        {
            game.setPlayerUpdateMessage("You got " + Integer.parseInt(args[1]) + " points!\n");
        }
        else
        {
            if(commandName.equals("C"))
                game.setPlayerUpdateMessage("Player " + args[0] + " challenged a word placement and got " + Integer.parseInt(args[1]) + " points!\n");
            else
                game.setPlayerUpdateMessage("Player " + args[0] + " placed a word and got " + Integer.parseInt(args[1]) + " points!\n");
        }
        game.nextTurn(); //Next turn
    }

    public int getId(){return id;}

    @Override
    public void close() {
        game.close();
        out.close();
    }
}