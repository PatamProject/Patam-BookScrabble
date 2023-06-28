package bookscrabble.client.model.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import bookscrabble.client.model.ClientModel;
import bookscrabble.client.model.MyHostServer;

public class GameManager{
    Board board;
    HashMap<String,Player> players; //Maps between player's name to player's object
    LinkedList<String> playersOrder; //The order of the players in the game (0 goes first...)
    public final int MAX_PLAYERS = 4;

    public GameManager() { //Ctor
        this.board = Board.getBoard();
        this.players = new HashMap<>();
        this.playersOrder = new LinkedList<>();
    }

    public String[] startGame() throws Exception { // Starts the game and returns a string that contains all names and racks by the random order of play
        //The format returned from this method is an array of strings each built: "p1%tiles"
        initialRacks(); // Initial racks (7 tiles for each player)
        setRandomPlayOrder();
        ArrayList<String> dupPlayersOrder = getPlayersOrder(); // Random order of play as an ArrayList
        String[] output = new String[dupPlayersOrder.size()];
        int i = 0;
        for (String player : dupPlayersOrder) // Creating a string that contains all names and racks by the random order of play
            output[i++] = player + "%" + players.get(player).getRack().toString();
        return output;
    }
    
    private void initialRacks() throws Exception{ // Each player receives his initial rack
        for (String player : playersOrder)
            players.get(player).getRack().takeTilesFromBag();
    }

    public void nextTurn() { // Switching turns and checks for conclusion of the game
        String currentPlayer = playersOrder.poll();
        if(currentPlayer == null)
            return;
        playersOrder.add(currentPlayer);
        checkEndGameConditions();
    }

    // Getters
    public Board getBoard() {return board;}
    public String getCurrentPlayersName() {return playersOrder.peek();}
    public Player getPlayer(String pName) {return players.get(pName) != null ? players.get(pName) : null;}
    public ArrayList<String> getPlayersOrder() {return new ArrayList<>(playersOrder);}
    public int getPlayersAmount() {return players.size();}

    private void setRandomPlayOrder() { //Randomize the order of the players
        ArrayList<String> shuffle = new ArrayList<>();
        LinkedList<String> order = new LinkedList<>(playersOrder);
        while(!order.isEmpty())
            shuffle.add(order.poll());

        Collections.shuffle(shuffle);
        while(!shuffle.isEmpty())
            order.add(shuffle.remove(0));
        playersOrder = order;
    }

    public boolean addNewPlayer(String pName) { // Adds a new player to the game if possible
        if(players.size() < MAX_PLAYERS && pName != null)
        {
            players.put(pName, new Player(pName));
            playersOrder.add(pName);
            return true;
        }
        return false;
    }

    public boolean isGameEnded() {return !MyHostServer.getHostServer().isGameRunning;}
    private boolean checkEndGameConditions() { //Game ends when the bag is empty or there are less than 2 players
        if(Tile.Bag.isEmpty() || players.size() < 2) {
            MyHostServer.getHostServer().isGameRunning = false;
            return true;
        }
        return false;
    }

    public boolean removePlayer(String pName)
    { // Removes a player from the game and checks if there's enough players to continue the game
        if(players.containsKey(pName)) {
            players.remove(pName);
            playersOrder.remove(pName);
        }
        if(ClientModel.isGameRunning && players.size() <= 1) {
            MyHostServer.getHostServer().isGameRunning = false;
            return false;
        }
        return true;
    }

    public String getWinner() { //The winner is the player with the highest score
        Player winner = players.values().iterator().next();
        for(Player p : players.values()) {
            if(p.getScore() > winner.getScore())
                winner = p;
            else if(p.getScore().equals(winner.getScore()))
                if(p.getRack().size() < winner.getRack().size())
                    winner = p;
        }
        if(ClientModel.isGameRunning)
        {
            ClientModel.isGameRunning = false;
            MyHostServer.getHostServer().isGameRunning = false;
            return winner.getName();
        }
        else
            return "";
    }
    
    public Word fromStringToWord(String pName, final String tiles, final int row,final int col,final boolean vertical)
    { //A word is created from the tiles taken from the player and from the board respectively 
        Player p = players.get(pName);
        if(p == null)
            return null;
        
        int tmpRow = row, tmpCol = col;
        Tile tile;
        ArrayList<Tile> tilesArr = new ArrayList<>();
        Tile[][] tilesOnBoard = board.getTiles(); //copy of board tiles. Be careful to not create extra tiles!
        for (int i = 0; i < tiles.length(); i++)
        {
            if(tilesOnBoard[tmpRow][tmpCol] != null && tiles.charAt(i) == tilesOnBoard[tmpRow][tmpCol].letter) //Tile is on the board
            {
                tilesArr.add(null); //Word on board, do not take
                //tilesOnBoard[tmpRow][tmpCol] if doesnt work?
            }
            else
            {
                tile = p.getRack().takeTileFromRack(tiles.charAt(i)); 
                if(tilesOnBoard[tmpRow][tmpCol] == null && tile != null) //Tile is on the rack
                    tilesArr.add(tile);
                else //Can't find tile / tile placed on another tile
                    return null; 
            }
                
            //Adjust tmpRow and tmpCol according to vertical
            if(vertical)
                tmpRow++;
            else
                tmpCol++;
        }
        Tile[] wordTiles = tilesArr.toArray(new Tile[tilesArr.size()]);
        return new Word(wordTiles, row, col, vertical);
    }
    
    public String[] getStringsToSendToBS(Word w) //Uses getWords() to fetch all the words created from the word w
    {
        //We check if w is boardLegal (inside getWords())
        //We get all the words created from the word w
        //We query all the words
        //We use tryPlaceWord() to check boardLegal for all words
        
        ArrayList<Word> words;
        words = board.getWords(w); //checks boardLegal
        if(words == null)
            return null; //Word is not board legal
        
        String[] queryWords = new String[words.size()]; //We change all the words to strings for query
        for (int i = 0; i < words.size(); i++)
            queryWords[i] = words.get(i).toString();
        return queryWords;
    }

    public int tryPlaceWord(String pName, Word w)
    { //Returns the score of the word. Must check boardLegal and dictionaryLegal before using!!!
        Player p = players.get(pName);
        ArrayList<Tile> tilesFromRack = new ArrayList<>();
        for (int i = 0; i < w.length; i++) //Now we know what tiles are taken from the player
            if(w.tiles[i] != null)
                tilesFromRack.add(w.tiles[i]);
        
        int score = board.tryPlaceWord(w);
        if(score == 0) //Return tiles back to player.rack
            p.getRack().returnTilesToRack(tilesFromRack.toArray(new Tile[tilesFromRack.size()]));
        
        return score;
    }
}
