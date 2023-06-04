package project.client.model;

import java.util.HashMap;
import java.util.LinkedList;

import project.client.MyLogger;


public class GameModel {
    //PlayerModel myPlayer;
    String myTiles;
    HashMap<String,Integer> players; //Name of player and their score
    LinkedList<String> playersOrder; //The order of the players in the game (0 goes first...)
    String board;

    public GameModel() {
        //myPlayer = new PlayerModel(ClientModel.getName());
        playersOrder = new LinkedList<>(); //Will be updated by startGame
        this.players = new HashMap<>();
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public String[] getPlayers() {
        return players.keySet().toArray(new String[players.size()]);
    }

    public void addPlayers(String... players) {
        for (String player : players)
        {
            this.players.put(player, 0); //Each player starts with score 0
        }
    }

    public void removePlayer(String player)
    {
        players.remove(player);
        playersOrder.remove(player);
    }

    public void updateScore(String player, int score)
    {
        int oldScore = players.get(player);
        players.remove(player);
        players.put(player, oldScore + score);
    }

    public boolean isStringLegal(char[] word) //We check for allowed chars only
    {
        for (char c : word)
            if (c < 'A' || c > 'Z')
                return false;
        return true;    
    }

    public boolean nextTurn()
    {
        String currentPlayer = playersOrder.poll();
        if(currentPlayer == null)
            return false;
        playersOrder.add(currentPlayer);
        MyLogger.nextPlayer(currentPlayer);
        return isItMyTurn();
    }

    public String getCurrentPlayersName() {return playersOrder.peek();}

    public String getBoard() {
        return board;
    }

    public boolean isItMyTurn()
    {
        boolean res = playersOrder.peek().equals(ClientModel.getName());
        if(res)
            ClientCommunications.lock.notifyAll();
        return res; 
    }
}
