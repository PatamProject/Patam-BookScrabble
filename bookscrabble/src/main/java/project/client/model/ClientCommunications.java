package project.client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import project.client.MyLogger;

public class ClientCommunications{
    private ClientSideHandler requestHandler;
    private static Socket toHostSocket; // A socket to the host
    private Scanner inFromHost;
    private static PrintWriter outToHost;
    public static Object lock = new Object();

    public ClientCommunications(String hostIP, int hostPort) throws IOException, InterruptedException { // Ctor
        toHostSocket = new Socket(hostIP, hostPort);
        outToHost = new PrintWriter(toHostSocket.getOutputStream());
        requestHandler = new ClientSideHandler(outToHost);
        inFromHost = new Scanner(toHostSocket.getInputStream());
    }
    
    public void run() { // A method that consistently receives messages from the host
        sendAMessage(0,ClientModel.getName()+"&join"); // Send a message to the host that the client wants to join with id = 0
        while (!toHostSocket.isClosed()) { // The socket will be open until the game is over
            try {
                if(inFromHost.hasNextLine()) {
                    String request = inFromHost.nextLine(); // "!'takeTile':'Y'"
                    MyLogger.println("Client received: " + request);
                    if(request.charAt(0) == '#') //If the host sent an error
                    {
                        requestHandler.handleClient("#", request.substring(1), null, null); //Error
                        continue;
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
                                gameStarted();
                            }
                            continue;
                        }
                    }
                    else //A reply from the host
                        sender = ClientModel.getName();
                    
                    requestHandler.handleClient(sender, commandName, args, toHostSocket.getOutputStream());
                }
            } catch (IOException e) {
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

    public void close() { // Closing the connection to the host
        try {
            inFromHost.close();
            toHostSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(()-> {
            try {
                run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void gameStarted()
    {
        int BOARD_SIZE = 15;
        try {
            Scanner scanner = MyLogger.getScanner();
            MyLogger.gameStarted(requestHandler.game.playersOrder.toArray(new String[requestHandler.game.playersOrder.size()]));
            
            while(requestHandler.isGameRunning) //While the game is running
            {    
                if(requestHandler.game.isItMyTurn()) //My turn and I can now place a word
                {
                    MyLogger.println("It's your turn to play! Enter a word to place: ");
                    if(scanner.hasNextLine())
                    {
                        boolean allowedInput;
                        String word;
                        int row, col; 
                        boolean isVertical = false;
                        do
                        {
                            allowedInput = true;
                            word = scanner.nextLine();
                            if(!requestHandler.game.isStringLegal(word.toUpperCase().toCharArray()))
                            {
                                MyLogger.println("Illegal word!");
                                allowedInput = false;
                            }
                        } while(!allowedInput);

                        do
                        {
                            allowedInput = true;
                            MyLogger.println("Enter row and col of starting character:");
                            row = scanner.nextInt();
                            col = scanner.nextInt();
                            if(row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE)
                            {
                                MyLogger.println("Illegal row or col!");
                                allowedInput = false;
                            }
                        } while(!allowedInput);

                        do
                        {
                            allowedInput = true;
                            MyLogger.println("Enter 1 for vertical, 0 for horizontal:");
                            int vertical = scanner.nextInt();
                            if(vertical != 0 && vertical != 1)
                            {
                                MyLogger.println("Illegal input!");
                                allowedInput = false;
                            }
                            else
                                isVertical = (vertical == 1);
                            
                        } while(!allowedInput);
                        
                        String message = word + "," + row + "," + col + "," + isVertical;
                        sendAMessage(requestHandler.getId(), message); 
                        lock.wait(); //Wait for the host to reply
                    }
                    else //Not my turn
                        lock.wait(); //Wait for my turn
                }
            }
        } catch (Exception e) {
            MyLogger.logError("Error in gameStarted: " + e.getMessage());
        }
        MyLogger.println("Game closed!");
    }

}
