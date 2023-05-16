package project.server.serverHandler;

import project.server.Constants;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Scanner;

public class ServerManager {
    private HashMap<String, MyServer> gameServers;
    private ServerSocket managerSocket;
    private MyServer BookScrabbleServer; // This server is used by many hosts of unrelated games
    private final int MANAGER_PORT = 5002, BookScrabblePort = 5003, STARTING_GAME_PORT = 5004; // Ports
    private int gameCounter = 0; // Counter to assign unique game IDs
    Boolean closeServerManager = false;

    public ServerManager() { // Ctor
        gameServers = new HashMap<>();
        try {
            managerSocket = new ServerSocket(MANAGER_PORT);
            System.out.println("ServerManager: Listening on port " + MANAGER_PORT);
            BookScrabbleServer = new MyServer(BookScrabblePort , new BookScrabbleHandler());
            BookScrabbleServer.start(); // Always runs in the background
            Thread connectionThread = new Thread(this::messageManagement);
            connectionThread.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     public void messageManagement () { // A method to classify a message and send it to a specific client handler
        try {
            managerSocket.setSoTimeout(2000);
            while(!closeServerManager)
            {
                try {
                    Socket socket = managerSocket.accept();
                    try (socket; Scanner fromHost = new Scanner(socket.getInputStream());
                         PrintWriter toHost = new PrintWriter(socket.getOutputStream())) {

                        String fullMessage = fromHost.next(); // hostID&message
                        String[] split = fullMessage.split("&");
                        String hostID = split[0];
                        String message = split[1];

                        if (!gameServers.containsKey(hostID)) // If hostID isn't in gameServers
                            gameServers.put(hostID, null);

                        serverClassification(hostID, message, toHost);
                        toHost.flush();
                    }
                } catch (SocketTimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
            managerSocket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
     }

    private void serverClassification(String hostID, String message, PrintWriter toHost) {
        char firstLetter = message.charAt(0);
        String subMessage = message.substring(2);

        switch(firstLetter) {
            case '1':
                char letter = subMessage.charAt(0);
                switch(letter) {
                    case 'N':
                        if (gameServers.get(hostID) == null) {
                            gameServers.replace(hostID, new MyServer(STARTING_GAME_PORT + gameCounter, new GameHandler()));
                            gameCounter++;
                        }
                        toHost.println(Constants.OK);
                        break;
                    case 'R':
                        int port = gameServers.get(hostID).getPort();
                        gameServers.replace(hostID, new MyServer(port, new GameHandler()));
                        toHost.println(Constants.OK);
                        break;
                    case 'D':
                        gameServers.get(hostID).close();
                        gameServers.remove(hostID);
                        break;
                    case 'E':
                        //...Maybe to do
                        break;
                    default:
                        break;
                }
                break;
            case '2':
                String response = messageMethod(subMessage, gameServers.get(hostID).getPort());
                String[] arrResponse = response.split(",");
                if(arrResponse[0].equals("600")) {
                    gameServers.get(hostID).close();
                    gameServers.remove(hostID);
                }
                toHost.println(response);
                break;
            case '3':
                toHost.println(messageMethod(subMessage, BookScrabblePort));
                break;
            default:
                break;
        }
    }

    private String messageMethod (String message, int port) { // A method that communicates with MyServer instances
        String response = null;
        try {
            Socket socket = new Socket("localhost", port);

            try (Scanner inFromServer = new Scanner(socket.getInputStream());
                 PrintWriter outToServer = new PrintWriter(socket.getOutputStream())) {
                outToServer.println(message);
                outToServer.flush();
                if (inFromServer.hasNext())
                    response = inFromServer.next();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return response; // The response from the server
    }

    public void closeServerManager() {
        closeServerManager = true;
        gameServers.values().forEach(MyServer::close);
        BookScrabbleServer.close();
    }
  }