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
    public Player getPlayer(String pName) {return players.get(pName);}
    public ArrayList<String> getPlayersOrder(){return playersOrder;}
    public int getPlayersAmount(){return players.size();}
    public boolean isGameEnded(){return gameEnded;}

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

    public String playerLeftGame(String pName){
        if(players.containsKey(pName)) {
            players.remove(pName);
            return pName;
        }
        if(players.size() <= 1)
            gameEnded = true;
        return "0";
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
        if(!players.containsKey(pName) || w == null)
            return "0";

        int score = board.tryPlaceWord(w);
        if(score != 0)
        {
            players.get(pName).addScore(score);
            return new StringBuilder().append(score).toString();
        }
        return "0";
    }

    public Word getWordFromString(String pName, final String tiles, final int row,final int col,final boolean vertical)
    {
        Player p = players.get(pName);
        if(p == null)
            return null;

        int tmpRow = row, tmpCol = col;
        ArrayList<Tile> tilesArr = new ArrayList<>();
        Tile[][] tilesOnBoard = board.getTiles(); //copy of board tiles. Be careful to not create extra tiles!
        for (int i = 0; i < tiles.length(); i++)
        {
            if(p.getRack().takeTileFromRack(tiles.charAt(i)) != null) //Tile is on the rack
            {
                tilesArr.add(p.getRack().takeTileFromRack(tiles.charAt(i)));
            }
            else if(tiles.charAt(i) == tilesOnBoard[tmpRow][tmpCol].letter) //Tile is on the board
            {
                tilesArr.add(tilesOnBoard[tmpRow][tmpCol]);
            }
            else
                return null; //Can't find tile!

            if(vertical)
                tmpCol++;
            else
                tmpRow++;    
        }
        Tile[] wordTiles = tilesArr.toArray(new Tile[tilesArr.size()]);
        return new Word(wordTiles, row, col, vertical);
    }
    public String tilesToString(String pName) {
        Player p = players.get(pName);
        if(p == null)
            return "0";

        Tile[] tiles = players.get(pName).getRack().getTiles();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tiles.length; i++) {
            sb.append(tiles[i].letter);
        }
        return sb.toString();
    }
}
