package project.client.model;

import java.util.HashMap;
import java.util.PriorityQueue;

import project.client.model.assets.PlayerModel;

public class GameModel {
    PlayerModel myPlayer;
    HashMap<String,Integer> players; //Name of player and their score
    PriorityQueue<String> playersOrder; //The order of the players in the game (0 goes first...)
    String board;

    public GameModel() {
        myPlayer = new PlayerModel(ClientModel.getName());
        playersOrder = new PriorityQueue<>(); //Will be updated by startGame
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
            this.players.put(player, 0); //Each player starts with score 0
    }

    public void removePlayer(String player)
    {
        players.remove(player);
        playersOrder.remove(player);
    }

    public void updateScore(String player, int score)
    {
        if(myPlayer.getName().equals(player))
            myPlayer.addScore(score);

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
        return isItMyTurn();
    }

    public String getBoard() {
        return board;
    }

    public boolean isItMyTurn(){return playersOrder.peek().equals(ClientModel.myName);}
}
