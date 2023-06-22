package bookscrabble.client.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

import bookscrabble.client.MyLogger;

public class GameModel extends Observable {
    String myTiles; //The tiles I have
    HashMap<String,Integer> playersAndScores; //Name of player and their score
    LinkedList<String> playersOrder; //The order of the players in the game (0 goes first...)
    String board; //The game board as a string
    boolean isMyTurn;
    private String lastErrorReceived;

    public GameModel() { //Ctor
        playersOrder = new LinkedList<>(); //Will be updated by startGame
        this.playersAndScores = new HashMap<>();
    }

    public void setBoard(String board) { //Updates the string representation of the board
        this.board = board;
        setChanged();
        notifyObservers();
    }

    public void addPlayers(String... players) { //Adds the players of the game
        for (String player : players)
            this.playersAndScores.put(player, 0); //Each player starts with score 0
        setChanged();
        notifyObservers();
    }

    public void removePlayer(String player)
    {
        playersAndScores.remove(player);
        playersOrder.remove(player);
    }

    public void updateScore(String player, int score) //Increasing the players score if a placement of a word is successful
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

    public boolean nextTurn() //Switching turns between players
    {
        String prevPlayer = playersOrder.poll();
        if(prevPlayer == null)
            return false;
        playersOrder.add(prevPlayer);
        MyLogger.nextPlayer(playersOrder.peek());
        return isItMyTurn();
    }


    public boolean isItMyTurn() //Checks who is playing
    {
        isMyTurn = playersOrder.peek().equals(ClientModel.getName());
        if(isMyTurn)
            ClientCommunications.unlock();
        setChanged();
        notifyObservers();
        return isMyTurn;
    }

    public void close() //CLose method for gameModel
    {
        playersAndScores.clear();
        playersOrder.clear();
        myTiles = "";
        board = "";
    }

    public void wordPlacement(boolean isLegal) //Notifies observers if a placement of a word is successful
    {
        setChanged();
        notifyObservers(isLegal);
    }

    public void setErrorMessage(String error)
    {
        lastErrorReceived = error;
        setChanged();
        notifyObservers();
    }

    public String getErrorMessage() {
        return lastErrorReceived;
    }

    //Getters
    public Integer getMyScore() {return playersAndScores.get(ClientModel.getName());}
    public String getMyTiles() {return myTiles;}
    public String getBoard() {return board;}
    public String getCurrentPlayersName() {return playersOrder.peek();}
    public HashMap<String,Integer> getPlayersAndScores() {return playersAndScores;}
    public String getPlayersAndScoresAsString()
    {
        StringBuilder sb = new StringBuilder();
        for(String player : playersOrder) //From HashMap to String
            sb.append(player).append(",").append(playersAndScores.get(player)).append("-");
        return sb.toString();
    }
}
