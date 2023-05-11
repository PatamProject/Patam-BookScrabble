package project.server.serverHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import project.server.assets.Game;

public class GameHandler implements ClientHandler {
    Scanner in;
    PrintWriter out;
    Game game;

    public GameHandler(){}

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        in = new Scanner(inFromClient);
        out = new PrintWriter(outToClient);
        String line = in.next();
        line = line.substring(2);

        String[] args = line.split(",");

        switch (args[0]) {
            case "1":
            //...
                break;
            case "2":
                switch (args[1]) {
                    case "G":
                        switch (args[2]) {
                            case "N":
                                createGame();
                                break;
                            case "S":
                                //startGame();
                                break;
                            default:
                                break;
                        }
                        break;
                    case "P":

                        break;
                    case "T":
                        
                        break;
                
                    default:
                        break;
                }
                break;
            //...
                
            default:
                break;
        }


        if (args[0] == "1") { // Example for in: 1,word,row,col,T/F
            //...
            //out.println(game.getBoard().tryPlaceWord());
        }

        

        // List of classifications:
        // 1 = Check if a word is placeable on the board. The score is returned as string.
        // 2 =
        // 3 =

        out.flush();
    }

    private void createGame() {
        game = new Game();
    }



    @Override
    public void close() {
        in.close();
        out.close();
    }


    //200->NewPlayerJoined
    //void game.newPlayerJoined(String name);


}