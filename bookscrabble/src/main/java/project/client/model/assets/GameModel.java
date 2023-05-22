package project.client.model.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class GameModel{
    Board board;
    HashMap<String,PlayerModel> players; //Maps between player's name to player's object
    Queue<String> playersOrder; //The order of the players in the game (0 goes first...)
    public boolean gameEnded = false;
    public final int MAX_PLAYERS = 4;

    public GameModel(){
        this.board = Board.getBoard();
        this.players = new HashMap<>();
        Queue<String> playersOrder = new LinkedList<>();
    }

    public void startGame(){
        //Make sure to let players take tiles before!
        setRandomPlayOrder();

        while(!playersOrder.isEmpty() && !gameEnded)
        {
            String playingPlayer = playersOrder.poll();
            //String input = getPlayerInput();
            //TODO

            checkEndGameConditions();
        }
    }

    public Board getBoard(){return board;}
    public PlayerModel getPlayer(String pName)
    {
        if(players.get(pName) != null)
            return players.get(pName);
        else 
            return null;    
    }

    public ArrayList<String> getPlayersOrder()
    {
        ArrayList<String> playOrder = new ArrayList<>();
        for (String p : playersOrder) {
            playOrder.add(p);
        }
        return playOrder;
    }
    public int getPlayersAmount(){return players.size();}
    public boolean isGameEnded(){return gameEnded;}

    public void setRandomPlayOrder(){ //randomize the order of the players
        ArrayList<String> suffle = new ArrayList<>();
        Queue<String> order = new LinkedList<>(playersOrder);
        while(!order.isEmpty())
            suffle.add(order.poll());
        
        Collections.shuffle(suffle);
        while(!suffle.isEmpty())
            order.add(suffle.remove(0));
        playersOrder = order;
    }

    public boolean addNewPlayer(String pName){
        if(players.size() < MAX_PLAYERS || players.containsKey(pName))
        {
            players.put(pName, new PlayerModel(pName));
            playersOrder.add(pName);
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

    public boolean removePlayer(String pName){
        if(players.containsKey(pName)) {
            players.remove(pName);
            return true;
        }
        if(players.size() <= 1)
            gameEnded = true;
        return false;
    }

    //the winner is the player with highest score
    public String getWinner(){
        PlayerModel winner = players.values().iterator().next();
        for(PlayerModel p : players.values())
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
        String value = "E,".concat(winner.getName());
        return value;
    }

    public Integer[] placeWord(String pName, final String tiles, final int row,final int col,final boolean vertical)
    { //Returns the score([0]) and the number of tiles taken from the rack([1])
        PlayerModel p = players.get(pName);
        if(p == null)
            return null;

        int tmpRow = row, tmpCol = col;
        ArrayList<Tile> tilesArr = new ArrayList<>();
        ArrayList<Tile> tilesFromRack = new ArrayList<>();
        Tile[][] tilesOnBoard = board.getTiles(); //copy of board tiles. Be careful to not create extra tiles!
        int tilesTakenCount = 0;
        for (int i = 0; i < tiles.length(); i++)
        {
            if(p.getRack().takeTileFromRack(tiles.charAt(i)) != null) //Tile is on the rack
            {
                tilesFromRack.add(p.getRack().takeTileFromRack(tiles.charAt(i)));
                tilesTakenCount++;
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
        tilesArr.addAll(tilesFromRack);
        Integer[] result = new Integer[2];
        Tile[] wordTiles = tilesArr.toArray(new Tile[tilesArr.size()]);
        Word w = new Word(wordTiles, row, col, vertical);
        if(!players.containsKey(pName) || w == null)
        {
            result[0] = 0;
            result[1] = 0;
        }
        else
        {
            int score = board.tryPlaceWord(w);
            if(score == 0)
            { //Return tiles back to rack
                p.getRack().addTiles(tilesFromRack.toArray(new Tile[tilesFromRack.size()]));
                result[0] = 0;
                result[1] = 0;
            }
            else
            {
                p.addScore(score);
                result[0] = score;
                result[1] = tilesTakenCount;
            }
        }
        return result;
    }
    public String tilesToString(String pName) { //returns the tiles of player 'pName' as a string
        PlayerModel p = players.get(pName);
        if(p == null)
            return null;

        Tile[] tiles = players.get(pName).getRack().getTiles();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tiles.length; i++) {
            sb.append(tiles[i].letter);
        }
        return sb.toString();
    }
}
