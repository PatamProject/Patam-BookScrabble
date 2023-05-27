package project.client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import project.client.MyLogger;

public class ClientCommunications implements Communications{
    private ClientSideHandler requestHandler;
    private static Socket toHostSocket; // A socket to the host
    private Scanner inFromHost;

    public ClientCommunications(String hostIP, int hostPort) throws IOException { // Ctor
        toHostSocket = new Socket(hostIP, hostPort);
        requestHandler = new ClientSideHandler(toHostSocket.getOutputStream());
        inFromHost = new Scanner(toHostSocket.getInputStream());
    }
    
    @Override
    public void run() { // A method that consistently receives messages from the host
        sendAMessage(0,ClientModel.myName+"&join"); // Send a message to the host that the client wants to join with id = 0
        while (!toHostSocket.isClosed()) { // The socket will be open until the game is over
            try {
                if(inFromHost.hasNextLine()) {
                    String request = inFromHost.nextLine(); // "!'takeTile':'Y'"
                    // if(isError(request)) //If the host sent an error
                    //     super.getRequestHandler().handleError(request);
                        
                    String[] tmp = request.split(":");
                    String commandName = tmp[0].substring(1);
                    String[] args = tmp[1].split(",");
                    String sender;
                    if(request.charAt(0) == '!') //Game update!
                    {
                        sender = "!"; //To allow the handler to know that this is a game update from the host
                        if(commandName.equals("!startGame"))
                            gameStarted();
                    }
                    else //A reply from the host
                        sender = ClientModel.myName;
                    
                    requestHandler.handleClient(sender, commandName, args, toHostSocket.getOutputStream());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } 
        }
        requestHandler.close();
    }

    public static void sendAMessage(int id, String message) { // A method that sends a message to the host
        try (PrintWriter outToHost = new PrintWriter(toHostSocket.getOutputStream())) {
            outToHost.println(id + ":" + message); //Adds the ID to the beginning of the message
            outToHost.flush();
        } catch (IOException e) { 
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() { // Closing the connection to the host
        try {
            inFromHost.close();
            toHostSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
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
        requestHandler.isGameStarted = true;
        new Thread(()-> {        
            try {
                Scanner scanner = MyLogger.getScanner();
                MyLogger.gameStarted(requestHandler.game.getCurrentPlayersName());
                while(requestHandler.isGameStarted) //While the game is running
                {
                    
                    //TODO - Print scores
                    //TODO - update board after each turn

                    if(requestHandler.game.isItMyTurn()) //My turn and I can now place a word
                    {
                        System.out.println("It's your turn to play! Enter word to place: ");
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
                                    System.out.println("Illegal word!");
                                    allowedInput = false;
                                }
                            } while(!allowedInput);

                            do
                            {
                                allowedInput = true;
                                System.out.println("Enter row and col of starting character:");
                                row = scanner.nextInt();
                                col = scanner.nextInt();
                                if(row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE)
                                {
                                    System.out.println("Illegal row or col!");
                                    allowedInput = false;
                                }
                            } while(!allowedInput);

                            do
                            {
                                allowedInput = true;
                                System.out.println("Enter 1 for vertical, 0 for horizontal:");
                                int vertical = scanner.nextInt();
                                if(vertical != 0 && vertical != 1)
                                {
                                    System.out.println("Illegal input!");
                                    allowedInput = false;
                                }
                                else
                                    isVertical = (vertical == 1);
                                
                            } while(!allowedInput);
                            
                            String message = word + "," + row + "," + col + "," + isVertical;
                            sendAMessage(requestHandler.getId(), message);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

}
