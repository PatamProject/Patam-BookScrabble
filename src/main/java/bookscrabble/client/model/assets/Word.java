package bookscrabble.client.model.assets;
import java.util.Arrays;

public class Word {

    Tile[] tiles = null;
	private int row,col; //starting tile
    final int length;
    private boolean vertical;

    public Word(Tile[] tiles, int row, int col, boolean vertical)
    {        
        this.row = row;
        this.col = col;
        this.tiles = tiles;
        this.length = tiles.length;
        this.vertical = vertical;
    }

    Tile[] getTiles(){return tiles;}
    int getRow(){return row;}
    int getCol(){return col;}
    boolean isVertical(){return vertical;}
    int getWordLength(){return length;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Tile tile : tiles) 
            sb.append(tile.letter);
        return sb.toString();    
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Word other = (Word) obj;
        if (!Arrays.equals(tiles, other.tiles))
            return false;
        if (row != other.row)
            return false;
        if (col != other.col)
            return false;
        if (length != other.length)
            return false;
        if (vertical != other.vertical)
            return false;
        return true;
    }
}
