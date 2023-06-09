package project.client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import project.client.MyLogger;

public class ClientCommunications{
    private ClientSideHandler requestHandler;
    private Socket toHostSocket; // A socket to the host
    private Scanner inFromHost;
    private static PrintWriter outToHost;
    private static Object lock = new Object();

    public ClientCommunications(String hostIP, int hostPort) throws IOException, InterruptedException { // Ctor
        toHostSocket = new Socket();
        toHostSocket.connect(new InetSocketAddress(hostIP, hostPort), 5000);
        outToHost = new PrintWriter(toHostSocket.getOutputStream());
        requestHandler = new ClientSideHandler(outToHost);
        inFromHost = new Scanner(toHostSocket.getInputStream());
    }

    public void start() throws RuntimeException, NoSuchElementException{
        new Thread(()-> {
            run();
        }).start();
    }

    public void run() throws RuntimeException , NoSuchElementException { // A method that consistently receives messages from the host
        sendAMessage(0,ClientModel.getName()+"&join"); // Send a message to the host that the client wants to join with id = 0
        while (!toHostSocket.isClosed()) { // The socket will be open until the game is over
            try {
                String request = inFromHost.nextLine(); // "!'takeTile':'Y'"
                MyLogger.println("Client received: " + request);
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
                }
                else //A reply from the host
                    sender = ClientModel.getName();
                
                requestHandler.handleClient(sender, commandName, args, toHostSocket.getOutputStream()); 
            } catch (NoSuchElementException | IOException e) {
                if(e instanceof NoSuchElementException)
                    throw new NoSuchElementException(); //Disconnected from host
                e.printStackTrace();
            } 
        }
        MyLogger.disconnectedFromHost();
        requestHandler.close();
    }

    public static void sendAMessage(int id, String message) { // A method that sends a message to the host
        outToHost.println(id + ":" + message); //Adds the ID to the beginning of the message
        outToHost.flush();
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
        boolean waitingForReply = false, allowedInput, isVertical = false;
        int currentScore = 0, row = 0, col = 0, myID = requestHandler.getId();
        String word = null, myName = ClientModel.getName();
        try {
            Scanner scanner = MyLogger.getScanner();
            MyLogger.gameStarted(requestHandler.game.myTiles, requestHandler.game.playersOrder.toArray(new String[requestHandler.game.playersOrder.size()]));
            //TODO : add a timer for turn time, game time, etc.
            while(requestHandler.isGameRunning) //While the game is running
            {    
                if(requestHandler.game.isItMyTurn()) //My turn and I can now place a word
                {
                    if (myID == 1)
                        MyLogger.println("Write '!exit' to finish the game.");
                    else
                        MyLogger.println("Write '!exit' to quit the game.");

                    MyLogger.println("Enter a word to place or write '!skip' to skip your turn: ");
                    boolean skipTurn = false;
                    waitingForReply = false;
                    do
                    {
                        allowedInput = false;
                        if(scanner.hasNextLine())
                        {
                            word = scanner.nextLine();
                            if(word.equals("!skip"))
                            {
                                sendAMessage(myID, myName + "&skipTurn");
                                skipTurn = true;
                                continue;
                            }
//                            if(word.equals("!exit")) {
//                                sendAMessage(myID, myID == 1 ? myName + "&endGame" : myName + "&leave");
//                                //requestHandler.isGameRunning = false;
//                                toHostSocket.close();
//                                continue;
//                            }
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

                    currentScore = requestHandler.game.getPlayersAndScores().get(myName); //We save the current score to check if it changed after trying to place the word
                    String message = myName + "&Q:" + word + "," + row + "," + col + "," + isVertical;
                    sendAMessage(myID, message);
                    waitingForReply = true;
                } //End of if(myTurn)

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
                            {
                                MyLogger.println("Illegal input! Try again");
                                allowedInput = false;
                            }
                            else
                            {
                                allowedInput = true;
                                switch (res) {
                                    case 1: //Trying again
                                        shouldPlayerWaitAgain = false; //Player should not wait since he wants to try again
                                        break;
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

    public Socket getToHostSocket() { return toHostSocket;}
}
