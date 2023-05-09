package project.server.assets;
import project.server.assets.Tile.Bag;
import java.util.HashMap;


public class Rack {
    //fields
    HashMap<Tile,Integer> tiles;
    //every player starts with 7 tiles
    public final int START_SIZE = 7;
    //num of tiles on the rack
    static int size = 7;
    //constructor
    public Rack()
    {
        tiles = new HashMap<>(){{
            for (int i = 0; i < START_SIZE; i++)
            {
                Tile tile = Bag.getBag().getRand();
                put(tile, getOrDefault(tile, i) + 1);
            }
        }};
    }
    //getters
    public HashMap<Tile,Integer> getAllTiles() {
        return tiles;
    }
    //take tile from bag
    public void takeTile()
    {
        Tile tile = Bag.getBag().getRand();
        tiles.put(tile, tiles.getOrDefault(tile, 0) + 1);
        //update size
        size++;
    }
    // returns true if the tile was successfully removed
    public boolean removeTile(Tile tile)
    {
        if(tiles.containsKey(tile))
        {
            tiles.put(tile, tiles.get(tile) - 1);
            if(tiles.get(tile) == 0)
                tiles.remove(tile);
            return true;
        }
        return false;
    }
    //remove a tile from tiles and return the hash map
    public HashMap<Tile,Integer> removeTileFromRack(Tile tile)
    {
        if(tiles.containsKey(tile))
        {
            //update size
            tiles.put(tile, tiles.get(tile) - 1);
            size--;
            //update num of tiles in bag after taking a tile
            if(tiles.get(tile) == 0)
                //remove the tile from the hash map
                tiles.remove(tile);
        }
        return tiles;
    }

    //this function gets a word remove the tiles from reck and return the score of the word
    public Integer placeWord(Word w){
        removeWord(w.getTiles());
        return getScore(w.getTiles());
    } 

    //removes a word from the hash map
    private void removeWord(Tile[] tilesList){
        for(Tile tile : tilesList){
            removeTileFromRack(tile);
        }        
    }

    //calculate the score of the word from TilesArray
    private Integer getScore(Tile[] tilesList){
        int score = 0;
        for (Tile tile : tilesList) {
            score += tile.score;
        }
        return score;
    }

    public static boolean isEmpty(){return Bag.getBag().isEmpty() && size==0;}
}