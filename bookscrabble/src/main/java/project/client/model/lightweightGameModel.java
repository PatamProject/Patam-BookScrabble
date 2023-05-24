package project.client.model;

import java.util.HashMap;

import project.client.model.assets.PlayerModel;

public class lightweightGameModel {
    PlayerModel myPlayer;
    HashMap<String,Integer> players;
    String[] board;
    public final int BOARD_SIZE = 15;

    public lightweightGameModel() {
        myPlayer = new PlayerModel(ClientModel.getName());
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
    }
}
