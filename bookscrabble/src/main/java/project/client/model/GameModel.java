package project.client.model;

import java.util.HashMap;
import java.util.LinkedList;

import project.client.MyLogger;


public class GameModel {
    String myTiles; //The tiles I have
    HashMap<String,Integer> playersAndScores; //Name of player and their score
    LinkedList<String> playersOrder; //The order of the players in the game (0 goes first...)
    String board;

    public GameModel() {
        playersOrder = new LinkedList<>(); //Will be updated by startGame
        this.playersAndScores = new HashMap<>();
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public void addPlayers(String... players) {
        for (String player : players)
        {
            this.playersAndScores.put(player, 0); //Each player starts with score 0
        }
    }

    public void removePlayer(String player)
    {
        playersAndScores.remove(player);
        playersOrder.remove(player);
    }

    public void updateScore(String player, int score)
    {
        int oldScore = playersAndScores.get(player);
        playersAndScores.remove(player);
        playersAndScores.put(player, oldScore + score);
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
        String prevPlayer = playersOrder.poll();
        if(prevPlayer == null)
            return false;
        playersOrder.add(prevPlayer);
        MyLogger.nextPlayer(playersOrder.peek());
        return isItMyTurn();
    }

    public String getCurrentPlayersName() {return playersOrder.peek();}

    public String getBoard() {
        return board;
    }

    public HashMap<String,Integer> getPlayersAndScores()
    {
        return playersAndScores;
    }

    public boolean isItMyTurn()
    {
        boolean res = playersOrder.peek().equals(ClientModel.getName());
        if(res)
            ClientCommunications.unlock();
        return res; 
    }

    public void close()
    {
        playersAndScores.clear();
        playersOrder.clear();
        board = "";
    }
}
