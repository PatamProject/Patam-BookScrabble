package project.client.model;

import project.client.Error_Codes;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

// This class is used to handle the host's responses
public class ClientSideHandler implements RequestHandler{
    private lightweightGameModel game;
    public PrintWriter out;
    private Map<String, Consumer<String[]>> commandHandler;
    private Map<String, Consumer<String[]>> responseHandler;
    private Map<String, Consumer<String[]>> errorHandler;
    private int id = 0;
    private int numOfChallenges = 0;
    boolean isGameStarted = false;
    private boolean wordAccepted = false;

    public ClientSideHandler(OutputStream out) {
        game = new lightweightGameModel();
        this.out = new PrintWriter(out, true);
        
        //ResponseHandler (responses from the server to the client's request)
        responseHandler = new HashMap<>(){{
            //Tried to join the game
            put("join", (String[] args) -> 
            { //Seccussful join, args[0] = id, args[1]... = players
                id = Integer.parseInt(args[0]);
                String[] connectedPlayers = new String[args.length - 1];
                for (int i = 1; i < args.length; i++) 
                    connectedPlayers[i - 1] = args[i];
    
                game.addPlayers(connectedPlayers);
            });
    
            //Tried to place a word on the board
            put("Q", (String[] args) -> 
            { //Seccussful word placement, args[0] = score, args[1]... = players
                Integer score = Integer.parseInt(args[1]);
                if(score == 0) //Not boardLegal
                {
                    //TODO try again
                } else if(score == -1) //Not dictornaryLegal
                {
                    //TODO try again
                } else //Word was placed successfully
                {
                    wordAccepted = true;
                    game.updateScore(ClientModel.getName(), score); //Update score
                    game.myPlayer.getRack().takeTiles(args[2]); //Take tiles
                    game.nextTurn(); //Next turn
                    wordAccepted = false;
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
                    wordAccepted = true;
                    game.myPlayer.addScore(score);
                    game.myPlayer.getRack().takeTiles(args[2]);
                    wordAccepted = false;
                }
            }); 
        }};

        //CommandHandler (commands from the host to the client about game updates)
        commandHandler = new HashMap<>(){{
            //A new player joined the game
            put("!join", (String[] args) -> 
            { //args[0] = new player
                game.addPlayers(args[0]);
            });
    
            //A player left the game
            put("!leave", (String[] args) -> 
            { //args[0] = leaving player
                game.removePlayer(args[0]);
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
                gameStarted(); //Start the game on client side
            });

            put("!endGame", (String[] args)->{
                System.out.println("Game ended!" + args[0] + " won the game!");
                isGameStarted = false;
            });
    
            //Board update (replace the board completely)
            put("!board", (String[] args) -> 
            { 
                game.setBoard(args);
            });
    
            //A player placed a word on the board
            put("!Q", (String[] args) -> 
            { handleWordPlacement(args);});
    
            //A player challenged a word placement and it was accepted
            put("!C", (String[] args) -> 
            { handleWordPlacement(args);});
        }};

        //ErrorHandler (errors from the host)
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

            put(Error_Codes.NOT_ENOUGH_PLAYERS, (String[] args) -> 
            { 
                System.out.println("Can't start game, not enough players");
            });

            put(Error_Codes.GAME_ENDED, (String[] args) -> //Do we need this?
            { 
                System.out.println("The game has ended, you can't use this command now");
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

    public void gameStarted()
    {
        isGameStarted = true;
        new Thread(()-> {        
            try {
                Scanner scanner = new Scanner(System.in);
                while(isGameStarted)
                {
                    //TODO - Print board and tiles
                    //TODO - Print scores
                    //TODO - update board after each turn


                    if(game.isItMyTurn()) //My turn and I can now place a word
                    {
                        System.out.println("It's your turn to play! Enter word to place: ");
                        if(scanner.hasNextLine())
                        {
                            boolean allowedinput = true;
                            String word;
                            int row, col; 
                            boolean isVertical = false;
                            do
                            {
                                allowedinput = true;
                                word = scanner.nextLine();
                                if(!game.isStringLegal(word.toUpperCase().toCharArray()))
                                {
                                    System.out.println("Illegal word!");
                                    allowedinput = false;
                                }
                            } while(!allowedinput);

                            do
                            {
                                allowedinput = true;
                                System.out.println("Enter row and col of starting character:");
                                row = scanner.nextInt();
                                col = scanner.nextInt();
                                if(row < 0 || row >= game.BOARD_SIZE || col < 0 || col >= game.BOARD_SIZE)
                                {
                                    System.out.println("Illegal row or col!");
                                    allowedinput = false;
                                }
                            } while(!allowedinput);

                            do
                            {
                                allowedinput = true;
                                System.out.println("Enter 1 for vertical, 0 for horizontal:");
                                int vertical = scanner.nextInt();
                                if(vertical != 0 && vertical != 1)
                                {
                                    System.out.println("Illegal input!");
                                    allowedinput = false;
                                }
                                else
                                    isVertical = (vertical == 1);
                                
                            } while(!allowedinput);
                            
                            String message = word + "," + row + "," + col + "," + isVertical;
                            out.println(id + ":" + ClientModel.myName + "&Q" + message); //Adds the ID to the beginning of the message
                            out.flush();

                            while(!wordAccepted) //TODO
                            {
                                if(wordAccepted)
                                    System.out.println("Word placed successfully!");
                                else
                                {
                                    System.out.println("Word placement failed!");
                                    if(game.challengeWord())
                                        System.out.println("Challenge successful!");
                                    else
                                        System.out.println("Challenge failed!");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void close() {
        out.close();
    }
}