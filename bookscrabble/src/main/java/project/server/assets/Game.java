package project.server.assets;

import java.util.ArrayList;
import java.util.Collections;

public class Game {
    Board board;
    ArrayList<Player> players;
    boolean gameEnded = false;
    public final int MAX_PLAYERS = 4;

    //NewGameStarted
    //TileTaken
    //TileGiven
    //TilePlaced
    //WordPlaced

    public Game(){
        this.board = Board.getBoard();
        this.players = new ArrayList<>();
    }

    public Board getBoard(){return board;}

    public void startGame(){
        for (Player player : players) {
            player.getRack();
        }

        new Thread(()->{
            try {
                while(!gameEnded)
                {
                    for (int i = 0; i < players.size(); i++) {
                        //player i turn
                        //allow player i to placeWord
                        //allow player i to takeTiles
                        //turn ends
                        //checkEndGameConditions
                        //...
                    }
                    //checkEndGameConditions    
                    //...
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void setRandomPlayOrder(){ //randomize the order of the players
        ArrayList<Integer> suffle = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            suffle.add(i);
        }
        Collections.shuffle(suffle);
        ArrayList<Player> newOrder = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            newOrder.add(players.get(suffle.get(i)));
        }
        players = newOrder;
    }

    public boolean addNewPlayer(String pName){
        if(players.size() < MAX_PLAYERS)
        {
            String suffix = "";
            for (Player p : players) {
                if(p.getName().equals(pName)) //name already exists
                {
                    suffix = "2";
                    break;
                }
            }
            players.add(new Player(pName + suffix));
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

    //player wants to leave the game
    public void leaveGame(String pName){
        players.removeIf(p -> p.getName().equals(pName));
        if(players.size() <= 1)
            gameEnded = true;
    }

    public int getPlayersAmount(){
        return players.size();
    }

    //the winner is the player with biggest score
    public String getWinner(){
        Player winner = players.get(0);
        for(Player p : players)
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

    public boolean placeWord(Player p, Word w){
        int score = board.tryPlaceWord(w);
        if(score != 0)
        {
            p.addScore(score);
            return true;
        }
        return false;
    }
}