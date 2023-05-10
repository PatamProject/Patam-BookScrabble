package project.server.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Game {
    Board board;
    HashMap<String,Player> players; //Maps between player's name to player's object
    ArrayList<String> playersOrder; //The order of the players in the game (0 goes first...)
    boolean gameEnded = false;
    public final int MAX_PLAYERS = 4;

    //NewGameStarted
    //TileTaken
    //TileGiven
    //TilePlaced
    //WordPlaced

    public Game(){
        this.board = Board.getBoard();
        this.players = new HashMap<>();
    }

    public Board getBoard(){return board;}

    public void startGame(){
        // for (Player player : players.values()) {
        //     player.getRack();
        // }

        // new Thread(()->{
        //     try {
        //         while(!gameEnded)
        //         {
        //             for (int i = 0; i < players.size(); i++) {
        //                 //player i turn
        //                 //allow player i to placeWord
        //                 //allow player i to takeTiles
        //                 //turn ends
        //                 //checkEndGameConditions
        //                 //...
        //             }
        //             //checkEndGameConditions    
        //             //...
        //         }
        //     } catch (Exception e) {
        //         throw new RuntimeException(e);
        //     }
        // }).start();
    }

    public void setRandomPlayOrder(){ //randomize the order of the players
        ArrayList<String> suffle = new ArrayList<>();
        for (int i = 0; i < players.keySet().size(); i++) {
            suffle.add(playersOrder.get(i));
        }
        Collections.shuffle(suffle);
        playersOrder = suffle;
    }

    public boolean addNewPlayer(String pName){
        if(players.size() < MAX_PLAYERS)
        {
            String suffix = "";
            if(players.containsKey(pName))
                suffix = "2";

            players.put(pName + suffix, new Player(pName + suffix));
            playersOrder.add(pName + suffix);
            return true;
        }    
        return false;
    }

    public boolean checkEndGameConditions(){ //Game ends when the bag is empty or there are less than 2 players
        if(Tile.Bag.isEmpty() || players.size() < 2)
        {
            gameEnded = true;
            return true;
        }
        return false;
    }

    public void playerLeftGame(String pName){
        if(players.containsKey(pName))
            players.remove(pName);
        if(players.size() <= 1)
            gameEnded = true;
    }

    public int getPlayersAmount(){
        return players.size();
    }

    //the winner is the player with highest score
    public String getWinner(){
        Player winner = players.values().iterator().next();
        for(Player p : players.values())
        {
            if(p.getScore() > winner.getScore())
                winner = p;
            else if(p.getScore() == winner.getScore())
            {
                if(p.getRack().size() < winner.getRack().size())
                    winner = p;
            }
        }
        gameEnded = true;
        return winner.getName();
    }

    public String placeWord(String pName, Word w){
        if(!players.containsKey(pName))
            return "0";

        int score = board.tryPlaceWord(w);
        if(score != 0)
        {
            players.get(pName).addScore(score);
            return new StringBuilder().append(score).toString();
        }
        return "0";
    }
}