package project.client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

import project.client.MyLogger;

public class ClientCommunications{
    private ClientSideHandler requestHandler;
    private Socket toHostSocket; // A socket to the host
    private Scanner inFromHost;
    private static PrintWriter outToHost;
    private static Object lock = new Object();

    public ClientCommunications(String hostIP, int hostPort) throws IOException, InterruptedException { // Ctor
        toHostSocket = new Socket(hostIP, hostPort);
        outToHost = new PrintWriter(toHostSocket.getOutputStream());
        requestHandler = new ClientSideHandler(outToHost);
        inFromHost = new Scanner(toHostSocket.getInputStream());
    }
    
    public void run() throws ConnectException { // A method that consistently receives messages from the host
        sendAMessage(0,ClientModel.getName()+"&join"); // Send a message to the host that the client wants to join with id = 0
        while (!toHostSocket.isClosed()) { // The socket will be open until the game is over
            try {
                String request = inFromHost.nextLine(); // "!'takeTile':'Y'"
                MyLogger.println("Client received: " + request);
                if(request.charAt(0) == '#') //If the host sent an error
                {
                    requestHandler.handleClient("#", request.substring(1), null, null); //Error
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
                                }
                            }
                            new Thread(()-> gameStarted()).start();
                        }
                        continue;
                    }
                }
                else //A reply from the host
                    sender = ClientModel.getName();
                
                requestHandler.handleClient(sender, commandName, args, toHostSocket.getOutputStream()); 
            } catch (RuntimeException | IOException e) {
                if(e instanceof RuntimeException)
                    throw new ConnectException("Host disconnected! " + e.getMessage());
                throw new RuntimeException(e);
            } 
        }
        MyLogger.disconnectedFromHost();
        requestHandler.close();
    }

    public static void sendAMessage(int id, String message) { // A method that sends a message to the host
        outToHost.println(id + ":" + message); //Adds the ID to the beginning of the message
        outToHost.flush();
    }

    public void start() throws ConnectException{
        new Thread(()-> {
            try {
                run();
            } catch (Exception e) {
                try {
                    throw new ConnectException(e.getMessage());
                } catch (Exception e1) {}
            }
        }).start();
    }
    
    public void close() { // Closing the connection to the host
        try {
            inFromHost.close();
            toHostSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gameStarted()
    {
        int BOARD_SIZE = 15;
        try {
            Scanner scanner = MyLogger.getScanner();
            MyLogger.gameStarted(requestHandler.game.myTiles, requestHandler.game.playersOrder.toArray(new String[requestHandler.game.playersOrder.size()]));
            //TODO : add a timer for turn time, game time, etc.
            while(requestHandler.isGameRunning) //While the game is running
            {    
                boolean isTryingAgain = false;
                if(requestHandler.game.isItMyTurn()) //My turn and I can now place a word
                {
                    MyLogger.println("It's your turn to play! Enter a word to place or use !skip to skip your turn: ");
                    boolean allowedInput;
                    String word = null;
                    int row = 0, col = 0; 
                    boolean isVertical = false;
                    boolean skipTurn = false;
                    do
                    {
                        allowedInput = false;
                        if(scanner.hasNextLine())
                        {
                            word = scanner.nextLine();
                            if(word.equals("!skip") || word.equals("!skipTurn") || word.equals("!skipturn") || word.equals("!skip turn"))
                            {
                                sendAMessage(requestHandler.getId(), ClientModel.getName() +"&skipTurn");
                                skipTurn = true;
                                continue;
                            }
                            word = word.toUpperCase();
                            if(!requestHandler.game.isStringLegal(word.toCharArray()))
                            {
                                MyLogger.println("Illegal word!");
                                allowedInput = false;
                            }
                            else
                                allowedInput = true;
                        }
                    } while(!allowedInput);

                    if(skipTurn) //If the player chose to skip his turn
                        continue;

                    do
                    {
                        allowedInput = false;
                        MyLogger.println("Enter row and col of starting character:");
                        if(scanner.hasNextLine())
                        {
                            row = scanner.nextInt();
                            col = scanner.nextInt();
                            if(row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE)
                            {
                                MyLogger.println("Illegal row or col!\nRemember that the board is " + BOARD_SIZE + "x" + BOARD_SIZE + "!");
                                allowedInput = false;
                            }
                            else
                                allowedInput = true;
                        }
                    } while(!allowedInput);

                    do
                    {
                        allowedInput = false;
                        MyLogger.println("How is the word placed?\nEnter 1 for vertical, 0 for horizontal:");
                        if(scanner.hasNextLine())
                        {
                            int vertical = scanner.nextInt();
                            if(vertical != 0 && vertical != 1)
                            {
                                MyLogger.println("Illegal input! Try again");
                                allowedInput = false;
                            }
                            else
                            {
                                isVertical = (vertical == 1);
                                allowedInput = true;
                            }
                        }               
                    } while(!allowedInput);
                    scanner.nextLine(); //Clear the buffer

                    int currentScore = requestHandler.game.getPlayersAndScores().get(ClientModel.getName());
                    String message = ClientModel.getName() + "&Q:" + word + "," + row + "," + col + "," + isVertical;
                    sendAMessage(requestHandler.getId(), message); 
                    waitForTurn(); //Wait for my turn  
                    //When a reply is received, we check if the score changed
                    if(requestHandler.game.getPlayersAndScores().get(ClientModel.getName()).equals(currentScore)) //word was not placed
                    { 
                        MyLogger.println("Choose one of the following:\nEnter 1 to try again.\nEnter 2 to skip your turn\nEnter 3 to challange the dictionary");
                        do
                        {
                            allowedInput = false;
                            int decision = scanner.nextInt();
                            if(decision != 1 && decision != 2 && decision != 3)
                            {
                                MyLogger.println("Illegal input! Try again");
                                allowedInput = false;
                            }
                            else
                            {
                                allowedInput = true;
                                switch (decision) {
                                    case 1: //Trying again
                                        isTryingAgain = true;
                                        break;
                                    case 2:
                                        sendAMessage(requestHandler.getId(), ClientModel.getName() +"&skipTurn");
                                        break;
                                    case 3:
                                        String challengeMsg = ClientModel.getName() + "&C:" + word + "," + row + "," + col + "," + isVertical;
                                        sendAMessage(requestHandler.getId(), challengeMsg); 
                                        break;
                                    default:
                                        allowedInput = false;
                                }
                            }            
                            scanner.nextLine(); //Clear the buffer  
                        } while(!allowedInput);    
                    } //End of if(word was not placed)
                } //End of if(my turn)

                if(!isTryingAgain) //Player needs to wait for a reply and not tryingAgain
                    waitForTurn(); //Wait for my turn 
            } //End of while
        } catch (Exception e) {
            MyLogger.logError("Error in gameStarted(): " + e.getMessage());
            e.printStackTrace();
        }
        MyLogger.println("Game closed!");
    }

    public static void waitForTurn() { // A method that waits for the player's turn
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
}
