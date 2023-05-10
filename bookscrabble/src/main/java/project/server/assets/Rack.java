package project.server.assets;
import project.server.assets.Tile.Bag;
import java.util.HashMap;


public class Rack {
    HashMap<Tile,Integer> tiles; //Maps between tiles and the amount of each tile
    public final int START_SIZE = 7; //every player starts with 7 tiles

    //constructor
    public Rack()
    {
        tiles = new HashMap<>(){{
            for (int i = 0; i < START_SIZE; i++)
            {
                Tile tile = Bag.getBag().getRand();
                put(tile, getOrDefault(tile, 0) + 1);
            }
        }};
    }

    //getters
    public Tile[] getTiles() {
        Tile[] arr = new Tile[tiles.size()];
        int i = 0;
        for (Tile tile : tiles.keySet()) {
            arr[i] = tile;
            i++;
        }
        return arr;
    }

    public int size(){return tiles.size();}
    
    //take tile from bag
    public String takeTile() //returns the letter of the tile taken
    {
        Tile tile = Bag.getBag().getRand();
        if(tile == null)
            return "0"; //bag is empty
        tiles.put(tile, tiles.getOrDefault(tile, 0) + 1);
        return new StringBuilder().append(tile.letter).toString();
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

    public boolean isEmpty(){return size() == 0;}
}