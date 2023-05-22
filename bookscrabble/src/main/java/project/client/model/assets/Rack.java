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

    public int size(){return tiles.size();}
    
    //take tile from bag at random
    public String takeTilesFromBag(int num) throws Exception 
    { //returns the letter of the tile taken
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            Tile tile = Bag.getBag().getRand();
            if(tile == null)
            {
                throw new Exception("Bag is empty!");
            }
            tiles.put(tile, tiles.getOrDefault(tile, 0) + 1);
            sb.append(tile.letter);
        }
        return new StringBuilder().toString();
    }

    public void takeTiles(String tiles) //Take tiles from bag specified by 'tiles' string
    {
        for (int i = 0; i < tiles.length(); i++) {
            Tile tile = Bag.getBag().getTile(tiles.charAt(i));
            this.tiles.put(tile, this.tiles.getOrDefault(tile, 0) + 1);
        }
    }

    public Tile takeTileFromRack(char letter) //returns the tile taken
    {
        Tile t;
        for (Tile tile : tiles.keySet()) {
            if(tile.letter == letter)
            {
                t = tile;
                tiles.put(tile, tiles.get(tile) - 1);
                if(tiles.get(tile) == 0)
                    tiles.remove(tile);
                return t;
            }
        }
        return null;
    }

    public void removeTiles(Tile... tilesToRemove)
    {
        for (int i = 0; i < tilesToRemove.length; i++) {
            if(tiles.containsKey(tilesToRemove[i]))
            {
                tiles.put(tilesToRemove[i], tiles.get(tilesToRemove[i]) - 1);
                if(tiles.get(tilesToRemove[i]) == 0)
                    tiles.remove(tilesToRemove[i]);
            }
        }
    }

    public void addTiles(Tile... tilesToAdd) //Used to pass tiles back to rack after a failed work placement
    {
        for (int i = 0; i < tilesToAdd.length; i++) {
            tiles.put(tilesToAdd[i], tiles.getOrDefault(tilesToAdd[i], 0) + 1);
        }
    }

    public boolean isEmpty(){return size() == 0;}
}