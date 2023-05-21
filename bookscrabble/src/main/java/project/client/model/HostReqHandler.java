package project.client.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import project.BookScrabbleServer.Error_Codes;
import project.client.model.assets.GameModel;
import project.client.model.assets.PlayerModel;

public class HostReqHandler implements ClientHandler{
    GameModel game;
    Scanner in;
    PrintWriter out;

    public HostReqHandler(){}

    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        in = new Scanner(inFromClient);
        out = new PrintWriter(outToClient);
        String line = in.next();
        String[] name_body_split = line.split("&");
            if(name_body_split.length != 2)
            {
                out.println(Error_Codes.MISSING_ARGS);
                out.flush();
                return;
            }

        String[] args = name_body_split[1].split(",");
        if(args == null || args.length < 1)
        {
            out.println(Error_Codes.UNKNOWN_CMD); //No arguments
            out.flush();
            return;
        }
        else //Refer to communication protocol!!! (Q,C are handled in --- class)
        {
            String playerName = name_body_split[0];
            if(game.getPlayer(playerName) == null) //Player not in game
            {
                if(args[0] == "1") //Add player to game
                    game.addNewPlayer(playerName);
                else
                    out.println(Error_Codes.ACCESS_DENIED); //Player not in game thus can't use any command other than "1"
            }
            else //Player already in game
            {
                PlayerModel player = game.getPlayer(playerName);
                switch (args[0]) {
                    case "0":
                        game.removePlayer(playerName);
                        break;
                    case "1":
                        out.println(Error_Codes.ACCESS_DENIED); //Can't join twice
                        break;
                    case "2":
                        if(args.length != 2)
                        {
                            out.println(Error_Codes.MISSING_ARGS); //Missing arguments
                            break;
                        }
                        String[] wordArgs = args[1].split("-");
                        if(wordArgs.length != 4)
                        {
                            out.println(Error_Codes.MISSING_ARGS); //Missing arguments
                            break;
                        }
                        int score = game.placeWord(playerName, game.getWordFromString(playerName ,wordArgs[0], Integer.parseInt(wordArgs[1]) , Integer.parseInt(wordArgs[2]), Boolean.parseBoolean(wordArgs[3])));
                        if(score == 0)
                            out.println("2,0"); //Invalid word / No score given
                        else
                        {
                            out.println("2,"+score);
                            //Next turn
                        }
                        break;
                    case "3":
                        try {
                            out.println("3,"+player.getRack().takeTilesFromBag(1));
                        } catch (Exception e) {
                            out.println("E,"+game.getWinner());
                            //Stop game
                        }          
                        break;         
                    default:
                        break;
                }
            }
        }
        out.flush();
    }
    
    private void createGame() {game = new GameModel();}
    
    @Override
    public void close() {
        in.close();
        out.close();
    }
}
