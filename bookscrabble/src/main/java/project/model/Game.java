package project.model;

import java.util.ArrayList;
import project.assets.Board;

public class Game {
    Board board;
    ArrayList<Player> players; 

    public Game(){
        this.board = Board.getBoard();
        this.players = new ArrayList<>();
    }

    public boolean addNewPlayer(Player p){
        if(players.size() < 4)
        {
            players.add(p);
            return true;
        }    

        return false;
    }

    public void startGame(){
        // for(Player p : players)
        //     p.getRack();
    }

}
