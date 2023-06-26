package bookscrabble.client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Function;

import bookscrabble.client.misc.MyLogger;
import bookscrabble.client.misc.RunClient;

public class ClientCommunications{
    private ClientSideHandler requestHandler;
    private Socket toHostSocket; // A socket to the host
    private Scanner inFromHost;
    private static PrintWriter outToHost;
    private static Object lock = new Object();
    public static boolean isGameRunningInTerminal = false;

    public ClientCommunications(String hostIP, int hostPort) throws IOException, InterruptedException { // Ctor
        toHostSocket = new Socket();
        toHostSocket.connect(new InetSocketAddress(hostIP, hostPort), 5000);
        outToHost = new PrintWriter(toHostSocket.getOutputStream());
        requestHandler = new ClientSideHandler(outToHost);
        inFromHost = new Scanner(toHostSocket.getInputStream());
    }

    Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
    @Override
    public void uncaughtException(Thread th, Throwable e){
        try {
            if(isGameRunningInTerminal)
                RunClient.disconnectedFromHost(); //never caught
            else 
            {
                if(e.getMessage().equals("Disconnected from host!"))
                    throw new ConnectException(e.getMessage());
                else if(e.getMessage().equals("endGame"))
                    throw new ConnectException(e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (Exception e2) {}
    }};

    public void start() throws Exception {
        Thread t = new Thread (()-> {
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        t.setUncaughtExceptionHandler(h);
        t.start();

        // try {
        //     new Thread(()-> {
        //         try {
        //             run();
        //         } catch (Exception e) {
        //             throw new RuntimeException(e);
        //         }
        //     }).start();
        // } catch (Exception e) { //disconnected from host
        //     if(isGameRunningInTerminal)
        //         RunClient.disconnectedFromHost(); //never caught
        //     else 
        //     {
        //         if(e.getMessage().equals("Disconnected from host!"))
        //             throw new ConnectException(e.getMessage());
        //         else if(e.getMessage().equals("endGame"))
        //             throw new ConnectException(e.getMessage());
        //         throw new RuntimeException(e);
        //     }
        // }
    }

    public void run() throws Exception { // A method that consistently receives messages from the host
        sendAMessage(0,ClientModel.getMyName()+"&join"); // Send a message to the host that the client wants to join with id = 0
        while (!toHostSocket.isClosed()) { // The socket will be open until the game is over
            String request = inFromHost.nextLine(); // "!'takeTile':'Y'"
            //MyLogger.println("Client received: " + request);
            if(request.charAt(0) == '#') //If the host sent an error
            {
                requestHandler.handleClient("#", request, null, null); //Error
                toHostSocket.close();
                throw new ConnectException(request.substring(1));
            }
                
            String[] tmp = request.split(":");
            String commandName = tmp[0];
            String[] args = tmp[1].split(",");
            String sender;
            if(request.charAt(0) == '!') //Game update!
            {
                sender = "!"; //To allow the handler to know that this is a game update from the host
                if(commandName.equals("!startGame"))
                {
                    synchronized (lock) {
                        requestHandler.handleClient(sender, commandName, args, toHostSocket.getOutputStream());
                        lock.notifyAll();
                    }

                    synchronized (lock) {
                        if(!requestHandler.isGameRunning)
                        {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                MyLogger.logError("Unable to unlock!");
                                throw new RuntimeException(e);
                            }
                        }
                        new Thread(this::gameStarted).start();
                    }
                    continue;
                }
                else if(commandName.equals("!endGame"))
                {
                    requestHandler.handleClient(sender, commandName, args, toHostSocket.getOutputStream());
                    Thread.sleep(1000);
                    throw new ConnectException("Game ended by host!"); 
                }
            }
            else //A reply from the host
                sender = ClientModel.getMyName();
            
            requestHandler.handleClient(sender, commandName, args, toHostSocket.getOutputStream()); 
        }
        MyLogger.disconnectedFromHost();
        requestHandler.close();
    }

    public static void sendAMessage(int id, String message) { // A method that sends a message to the host
        outToHost.println(id + ":" + message); //Adds the ID to the beginning of the message
        outToHost.flush();
    }

    public void gameStarted()
    {
        int BOARD_SIZE = 15;
        boolean waitingForReply = false, allowedInput, isVertical = false, exit = false;
        int currentScore = 0, row = 0, col = 0, myID = requestHandler.getId();
        String word = null, myName = ClientModel.getMyName();
        ArrayList<String> userCommands = new ArrayList<>(){{
            add("!help");
            add("!exit");
            add("!skip");
            add("!who");
            add("!scores");
            add("!board");
            add("!tiles");
        }};

        HashMap<String, Function<Void, Boolean>> commands = new HashMap<>(){{
            put("!help", (args) -> {
                MyLogger.println("Available commands:");
                for(int i = 0; i < userCommands.size(); i++)
                    if(i != userCommands.size() - 1)
                        MyLogger.print(userCommands.get(i) + " , ");
                    else
                        MyLogger.println(userCommands.get(i));
                MyLogger.println("");    
                return false;
            });
            put("!exit", (args) -> {
                sendAMessage(myID, myID == 1 ? myName + "&endGame" : myName + "&leave");
                return true;
            });
            put("!skip", (args) -> {
                sendAMessage(myID, myName + "&skipTurn");
                return true;
            });
            put("!who", (args) -> {
                MyLogger.println("Players in the game:");
                for(String player : requestHandler.game.playersOrder)
                    MyLogger.println(player);
                return false;
            });
            put("!scores", (args) -> {
                MyLogger.println("Scores:");
                HashMap<String,Integer> playersAndScores = requestHandler.game.getPlayersAndScores();
                for(String player : playersAndScores.keySet())
                    MyLogger.println(player + " has " + playersAndScores.get(player) + " points.");
                return false;
            });
            put("!board", (args) -> {
                MyLogger.println("Board:");
                MyLogger.printBoard(requestHandler.game.board);
                return false;
            });
            put("!tiles", (args) -> {
                MyLogger.println("Your tiles:");
                MyLogger.printTiles(requestHandler.game.myTiles);
                return false;
            });
        }};

        try {
            Scanner scanner = MyLogger.getScanner();
            MyLogger.gameStarted(requestHandler.game.myTiles, requestHandler.game.playersOrder.toArray(new String[requestHandler.game.playersOrder.size()]));
            while(requestHandler.isGameRunning) //While the game is running
            {    
                if(requestHandler.game.isItMyTurn()) //My turn and I can now place a word
                {
                    MyLogger.println("Enter a word to place or write '!help' to see available commands: ");
                    boolean skipTurn = false;
                    waitingForReply = false;
                    do
                    {
                        allowedInput = false;
                        word = scanner.nextLine();
                        if(commands.containsKey(word))
                        {
                            if(word.equals("!skip"))
                            {
                                skipTurn = commands.get(word).apply(null);
                                allowedInput = true;
                                break;
                            }
                            else if(word.equals("!exit"))   
                            {
                                exit = commands.get(word).apply(null);
                                allowedInput = true;
                                break;
                            }
                            else
                                allowedInput = commands.get(word).apply(null);
                        }
                        else
                        {
                            word = word.toUpperCase();
                            if(!requestHandler.game.isStringLegal(word.toCharArray()))
                                MyLogger.println("Illegal word or command!");
                            else
                                allowedInput = true;             
                        }
                    } while(!allowedInput);

                    if(skipTurn);
                    if(exit)
                    {
                        requestHandler.isGameRunning = false;
                        break;    
                    }
                    else
                    {
                        do
                        {
                            allowedInput = false;
                            MyLogger.println("Enter row and col of starting character:");
                            row = scanner.nextInt();
                            col = scanner.nextInt();
                            scanner.nextLine();
                            if(row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE)
                                MyLogger.println("Illegal row or col!\nRemember that the board is " + BOARD_SIZE + "x" + BOARD_SIZE + "!");
                            else
                                allowedInput = true;
                            
                        } while(!allowedInput);
    
                        do
                        {
                            allowedInput = false;
                            MyLogger.println("How is the word placed?\nEnter 1 for vertical, 0 for horizontal:");
                            if(scanner.hasNextLine())
                            {
                                int vertical = scanner.nextInt();
                                if(vertical != 0 && vertical != 1)
                                    MyLogger.println("Illegal input! Try again");
                                else
                                {
                                    isVertical = (vertical == 1);
                                    allowedInput = true;
                                }
                            }               
                        } while(!allowedInput);
                        scanner.nextLine(); //Clear the buffer
    
                        currentScore = requestHandler.game.getPlayersAndScores().get(myName); //We save the current score to check if it changed after trying to place the word
                        String message = myName + "&Q:" + word + "," + row + "," + col + "," + isVertical;
                        sendAMessage(myID, message);
                        waitingForReply = true;
                    } //End of if(myTurn)
                }

                waitForReply(); //Wait for my turn / Wait for a reply

                if(waitingForReply)
                {
                    boolean shouldPlayerWaitAgain = false;
                    int updatedScore = requestHandler.game.getPlayersAndScores().get(myName);
                    if(currentScore == updatedScore) //word was not placed since the score did not change
                    {
                        MyLogger.println("Choose one of the following:\nEnter 1 to try again.\nEnter 2 to skip your turn\nEnter 3 to challenge the dictionary");
                        do
                        {
                            allowedInput = false;
                            String decision = scanner.nextLine();
                            int res = Integer.parseInt(decision);
                            if(res != 1 && res != 2 && res != 3)
                                MyLogger.println("Illegal input! Try again");
                            else
                            {
                                allowedInput = true;
                                switch (res) {
                                    case 1: //Trying again
                                        break; //Player should not wait since he wants to try again
                                    case 2: //Skipping turn
                                        sendAMessage(myID, myName +"&skipTurn");
                                        shouldPlayerWaitAgain = true;
                                        break;
                                    case 3: //Challenging the dictionary
                                        String challengeMsg = myName + "&C:" + word + "," + row + "," + col + "," + isVertical;
                                        sendAMessage(myID, challengeMsg);
                                        shouldPlayerWaitAgain = true;
                                        break;
                                    default:
                                        allowedInput = false;
                                }
                            }            
                        } while(!allowedInput);    
                    }

                    if(shouldPlayerWaitAgain)
                        waitForReply(); //Wait for my turn / Wait for a reply
                }

            } //End of while
        } catch (Exception e) {
            MyLogger.logError("Error in gameStarted(): " + e.getMessage());
            e.printStackTrace();
        }

        MyLogger.println("Game closed!");
        close();
    }

    public void close() { // Closing the connection to the host
        try {
            inFromHost.close();
            toHostSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void waitForReply() { // A method that waits for the player's turn
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                MyLogger.logError("Unable to unlock!");
            }
        }
    }

    public static void unlock() { //Releases the lock and allows the player to play
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public ClientSideHandler getRequestHandler() { return requestHandler;}
    public Socket getToHostSocket() { return toHostSocket;}
    public int getMyID() { return requestHandler.getId();}
}
