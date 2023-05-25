package project.client.model;

import java.util.ArrayList;
import java.util.HashMap;

import project.client.model.assets.PlayerModel;

public class lightweightGameModel {
    PlayerModel myPlayer;
    HashMap<String,Integer> players; //Name of player and their score
    ArrayList<String> playersOrder;
    String[] board;
    public final int BOARD_SIZE = 15;

    public lightweightGameModel() {
        myPlayer = new PlayerModel(ClientModel.getName());
        playersOrder = new ArrayList<>(); //Will be updated by startGame
        this.players = new HashMap<>();
        this.board = new String[BOARD_SIZE];
    }

    public void setBoard(String[] board) {
        this.board = board;
    }

    public String[] getPlayers() {
        return players.keySet().toArray(new String[players.size()]);
    }

    public void addPlayers(String... players) {
        for (int i = 0; i < players.length; i++)
            this.players.put(players[i], 0); //Each player starts with score 0 
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
        for (int i = 0; i < word.length; i++)
            if(word[i] < 'A' || word[i] > 'Z')
                return false;
        return true;    
    }

    public boolean placeWord(String word, int row, int col, boolean isVertical)
    {
        //TODO
        return false;
    }

    public boolean challengeWord()
    {
        //TODO
        return false;
    }

    public boolean isItMyTurn()
    {
        return playersOrder.get(0).equals(myPlayer.getName());
    }

    public void nextTurn()
    {
        String player = playersOrder.remove(0);
        playersOrder.add(player);
    }

}
