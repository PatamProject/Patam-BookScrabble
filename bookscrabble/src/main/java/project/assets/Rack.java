package project.assets;

import java.util.ArrayList;

import project.assets.Tile.Bag;

public class Rack {
    ArrayList<Tile> tiles;
    public final int START_SIZE = 7;

    public Rack()
    {
        tiles = new ArrayList<>(){{
            for (int i = 0; i < START_SIZE; i++)
                add(Bag.getBag().getRand());
        }};
    }

    public void takeTile()
    {
        tiles.add(Bag.getBag().getRand());
    }

    public Integer placeWord(Word w){
        return 1;
    }
}
