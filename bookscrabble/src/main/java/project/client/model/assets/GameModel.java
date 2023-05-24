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

    public GameModel(){ //Ctor
        this.board = Board.getBoard();
        this.players = new HashMap<>();
        Queue<String> playersOrder = new LinkedList<>();
    }

    public void startGame(){ //TODO
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

    public String nextPlayer()
    {
        //TODO
        return null;
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

    public Integer getScoreFromWord(String pName, Word w)
    { //Returns the score of the word. Must check boardLegal and dictornaryLegal before using!!!
        PlayerModel p = players.get(pName);
        ArrayList<Tile> tilesFromRack = new ArrayList<>();
        for (int i = 0; i < w.length; i++) //Now we know what tiles are taken from the player
            if(w.tiles[i] != null)
                tilesFromRack.add(w.tiles[i]);
        
        Integer score = board.tryPlaceWord(w);
        if(score == 0) //Return tiles back to player.rack
            p.getRack().addTiles(tilesFromRack.toArray(new Tile[tilesFromRack.size()]));
        
        return score;
    }

    public Word createWordFromClientInput(String pName, final String tiles, final int row,final int col,final boolean vertical)
    { //A word is created from the tiles taken from the player and from the board respectively 
        PlayerModel p = players.get(pName);
        if(p == null)
            return null;

        int tmpRow = row, tmpCol = col;
        ArrayList<Tile> tilesArr = new ArrayList<>();
        Tile[][] tilesOnBoard = board.getTiles(); //copy of board tiles. Be careful to not create extra tiles!
        for (int i = 0; i < tiles.length(); i++)
        {
            if(tilesOnBoard[tmpRow][tmpCol] != null && tiles.charAt(i) == tilesOnBoard[tmpRow][tmpCol].letter) //Tile is on the board
            {
                tilesArr.add(null); //Word on board, do not take
                //tilesOnBoard[tmpRow][tmpCol] if doesnt work?
            }
            else if(p.getRack().takeTileFromRack(tiles.charAt(i)) != null) //Tile is on the rack
            {
                tilesArr.add(p.getRack().takeTileFromRack(tiles.charAt(i)));
            }
            else{ return null; } //Can't find tile!
            
            //Adjust tmpRow and tmpCol according to vertical
            if(vertical)
                tmpRow++;    
            else
                tmpCol++;
        }
        Tile[] wordTiles = tilesArr.toArray(new Tile[tilesArr.size()]);
        return new Word(wordTiles, row, col, vertical);
    }
    
    public String[] getWordsFromClientInput(Word w) //Uses getWords() to fetch all the words created from the word w
    {
        //We check if w is boardLegal (inside getWords())
        //We get all the words created from the word w
        //We query all the words
        //We use tryPlaceWord() to check boardLegal for all words

        ArrayList<Word> words = new ArrayList<>();
        words = board.getWords(w); //checks boardLegal
        if(words == null)
            return null; //Word is not board legal

        String[] queryWords = new String[words.size()]; //We change all the words to strings for query
        for (int i = 0; i < words.size(); i++) {
            queryWords[i] = words.get(i).toString();
        }
        return queryWords;
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
