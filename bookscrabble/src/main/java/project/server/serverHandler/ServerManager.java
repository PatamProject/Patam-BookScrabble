package project.server.serverHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class ServerManager {
    private HashMap<String, MyServer> games; // Maps unique host ID to MyServer object
    private MyServer bookScrabbleServer; // Book Scrabble server
    private ServerSocket mainServerSocket;
    private final int MAIN_PORT = 5001, BOOKSCRABBLE_PORT = 5002, STARTING_GAME_PORT = 5003;
    private int gameCounter = 1; // Counter to assign unique game IDs
    private boolean isServerRunning = false;

    public ServerManager() {
        games = new HashMap<>();
        try {
            mainServerSocket = new ServerSocket(MAIN_PORT);
            System.out.println("ServerManager: Listening on port " + MAIN_PORT);
            bookScrabbleServer = new MyServer(BOOKSCRABBLE_PORT, new BookScrabbleHandler());
            bookScrabbleServer.start();
            isServerRunning = true;
            Thread connectionThread = new Thread(this::listenForConnections);
            connectionThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForConnections() {
    //     while (isServerRunning) {
    //         try {
    //             Socket clientSocket = mainServerSocket.accept();
    //             // Check if the host is known
    //             if (hostServerMap.containsKey(clientSocket)) {
    //                 // Retrieve the corresponding MyServer object for the host
    //                 MyServer gameServer = hostServerMap.get(clientSocket);
    //                 // Pass the clientSocket to the existing MyServer object
    //                 gameServer.addClient(clientSocket);
    //             } else {
    //                 // Create a new MyServer object for the game
    //                 int gameId = gameCounter++;
    //                 MyServer gameServer = new MyServer(clientSocket, new GameHandler(gameId));
    //                 games.put(gameId, gameServer);
    //                 gameServer.start();
    //             }
    //         } catch (IOException e) {
    //             e.printStackTrace();
    //         }
    //     }
     }




//     public String messageToBookScrabbleServer (String message) { // A method to send a query/challenge to the book scrabble server
//         return messageMethod(message, bookScrabbleServerName, BOOKSCRABBLE_PORT); // Answer from server
//     }

//     public String messageToGameServer (String message) { // A method to send a String to the game server
//         return messageMethod(message,  gameServerName, GAME_PORT); // Returns String according to the servers answer
//     }

//     private String messageMethod (String message, String serverName, int port) { // A method that communicates with the MyServer instances
//         String response = null;
//         try {
//             socket = new Socket(serverName, port);
//             gamesOLD.put(this, socket);

//             try (Scanner inFromServer = new Scanner(socket.getInputStream());
//                     PrintWriter outToServer = new PrintWriter(socket.getOutputStream())) {
//                 outToServer.println(message);
//                 outToServer.flush();
//                 if (inFromServer.hasNext())
//                     response = inFromServer.next();
//             } catch (IOException e) {
//                 throw new RuntimeException(e);
//             } finally {
//                 socket.close();
//             }
//         } catch (IOException e) {
//             throw new RuntimeException(e);
//         }
//         return response;
//     }
}



