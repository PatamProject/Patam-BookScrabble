package project.client.model.assets;
import project.client.model.assets.Tile.Bag;

import java.util.HashMap;


public class Rack {
    HashMap<Tile,Integer> tiles; //Maps between tiles and the amount of each tile
    public final int START_SIZE = 7; //every player starts with 7 tiles

    //constructor
    public Rack()
    {
        tiles = new HashMap<>();
    }

    public Rack(String tilesToTake)
    {
        tiles = new HashMap<>();
        takeTiles(tilesToTake);
    }

    //getters
    public Tile[] getTiles() {
        return tiles.keySet().toArray(new Tile[tiles.size()]);
    }

    public int size()
    {
        int size = 0;
        for (Tile tile : tiles.keySet()) {
            int amount = tiles.get(tile);
            size += amount;
        }
        return size;
    }
    
    //take tile from bag at random
    public String takeTilesFromBag() throws Exception 
    { //returns the letter of the tile taken
        int tilesToTake = START_SIZE - size();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tilesToTake && i < START_SIZE; i++) {
            Tile tile = Bag.getBag().getRand();
            if(tile == null)
                throw new Exception("Bag is empty!");
            
            tiles.put(tile, tiles.getOrDefault(tile, 0) + 1);
            sb.append(tile.letter);
            if(size() == START_SIZE) //if rack is full
                break;
        }
        return new StringBuilder().toString();
    }

    public void takeTiles(String tiles) //Take tiles from bag specified by 'tiles' string
    {
        for (int i = 0; i < tiles.length(); i++) {
            Tile tile = Bag.getBag().getTile(tiles.charAt(i));
            if(tile != null)
                this.tiles.put(tile, this.tiles.getOrDefault(tile, 0) + 1);
        }
    }

    public Tile takeTileFromRack(char letter) //returns the tile taken
    {
        Tile t = null;
        for (Tile tile : tiles.keySet()) {
            if(tile.letter == letter)
            {
                t = tile;
                int currentAmount = tiles.get(tile);
                tiles.put(tile, currentAmount - 1);
                if(currentAmount == 0)
                    tiles.remove(tile);
                break;
            }
        }
        return t;
    }

    public void removeTiles(Tile... tilesToRemove)
    {
        for (Tile tile : tilesToRemove)
            if (tiles.containsKey(tile)) {
                tiles.put(tile, tiles.get(tile) - 1);
                if (tiles.get(tile) == 0)
                    tiles.remove(tile);
            }
    }

    public void returnTilesToRack(Tile... tilesToAdd) //Used to pass tiles back to rack after a failed work placement
    {
        for (Tile tile : tilesToAdd)
            tiles.put(tile, tiles.getOrDefault(tile, 0) + 1);
    }

    @Override
    public String toString() //returns a string of all the tiles in the rack (including duplicates)
    {
        StringBuilder sb = new StringBuilder();
        for (Tile tile : tiles.keySet())
            for (int i = 0; i < tiles.get(tile); i++) {
                sb.append(tile.letter);
            }
        return sb.toString();
    }

    public boolean isEmpty(){return size() == 0;}
}