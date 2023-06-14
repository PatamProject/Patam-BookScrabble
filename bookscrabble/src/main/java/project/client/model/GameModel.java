package project.client.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

import project.client.MyLogger;


public class GameModel extends Observable {
    String myTiles; //The tiles I have
    HashMap<String,Integer> playersAndScores; //Name of player and their score
    LinkedList<String> playersOrder; //The order of the players in the game (0 goes first...)
    String board;
    boolean isMyTurn;

    public GameModel() {
        playersOrder = new LinkedList<>(); //Will be updated by startGame
        this.playersAndScores = new HashMap<>();
    }

    public void setBoard(String board) {
        this.board = board;
        setChanged();
        notifyObservers();
    }

    public void addPlayers(String... players) {
        for (String player : players)
            this.playersAndScores.put(player, 0); //Each player starts with score 0
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
        setChanged();
        notifyObservers();
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

    public HashMap<String,Integer> getPlayersAndScores() {return playersAndScores;}

    public boolean isItMyTurn()
    {
        isMyTurn = playersOrder.peek().equals(ClientModel.getName());
        if(isMyTurn)
            ClientCommunications.unlock();
        setChanged();
        notifyObservers();
        return isMyTurn;
    }

    public void close()
    {
        playersAndScores.clear();
        playersOrder.clear();
        board = "";
    }

    public void wordPlacement(boolean isLegal) 
    {
        setChanged();
        notifyObservers(isLegal);
    }

    //Setters


    //Getters
    public int getMyScore() {return playersAndScores.get(ClientModel.getName());}
    public String getMyTiles() {return myTiles;}
    public boolean getIsMyTurn() {return isMyTurn;}
    public String getBoard() {return board;}
    public String getCurrentPlayersName() {return playersOrder.peek();}
    public String getPlayersAndScoresAsString()
    {
        StringBuilder sb = new StringBuilder();
        for(String player : playersOrder)
            sb.append(player).append(",").append(playersAndScores.get(player)).append("-");
        return sb.toString();
    }
}