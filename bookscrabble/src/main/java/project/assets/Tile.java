package project.assets;
import java.util.Random;

public class Tile {
    public final char letter;
	public final int score;

    private Tile(char letter, int score)
    {
        this.letter = letter;
        this.score = score;
    }

    public static class Bag{ //nested class
        private static Bag myBag = null; //singelton
        private static int num_Of_Tiles = 98;
        final int finalTilesAmount[] = {9,2,2,4,12,2,3,2,9,1,1,4,2,6,8,2,1,6,4,6,4,2,2,1,2,1};
        int currentTilesAmount[] = {9,2,2,4,12,2,3,2,9,1,1,4,2,6,8,2,1,6,4,6,4,2,2,1,2,1};
        final Tile TilesArray[] = 
        {new Tile('A', 1), new Tile('B', 3), new Tile('C', 3),
        new Tile('D', 2), new Tile('E', 1), new Tile('F', 4),
        new Tile('G', 2), new Tile('H', 4), new Tile('I', 1),
        new Tile('J', 8), new Tile('K', 5), new Tile('L', 1),
        new Tile('M', 3), new Tile('N', 1), new Tile('O', 1),
        new Tile('P', 3), new Tile('Q', 10), new Tile('R', 1),
        new Tile('S', 1), new Tile('T', 1), new Tile('U', 1),
        new Tile('V', 4), new Tile('W', 4), new Tile('X', 8),
        new Tile('Y', 4), new Tile('Z', 10)};  // (char)-65 to get it's location 

        static Bag getBag() //singleton design
        {
            if(myBag != null)
                return myBag;
            myBag = new Bag();    
            return myBag;
        }

        private Bag(){} //works only via getBag

        private void incTilesAmount(){num_Of_Tiles++;}
        void decTilesAmount(){num_Of_Tiles--;}

        Tile getRand() //returns a random tile from the bag
        {
            if(num_Of_Tiles == 0)
                return null;

            int[] remainingTiles = getRemainingTiles(); //an array of the indexes of reamining tiles
            Random rn = new Random();
            int num = rn.nextInt(remainingTiles.length);   
            currentTilesAmount[remainingTiles[num]]--;
            decTilesAmount();
            return TilesArray[remainingTiles[num]];
        }

        private int[] getRemainingTiles() //helping method for getRand
        {
            int[] currentQuantities = getQuantities();
            int count = 0;
            for (int i = 0; i < currentQuantities.length; i++) { //counts how many different types of tiles are left
                if(currentQuantities[i] > 0)
                    count++;
            }
            int[] remainingTiles = new int[count];
            for (int i = 0,j = 0;i< currentQuantities.length;i++) {
                if(currentQuantities[i] > 0) //creates an array of indexes of existing tiles in bag
                {
                    remainingTiles[j] = i;
                    j++;
                }
            }
            return remainingTiles;

        }

        Tile getTile(char c) //returns a specific tile if left in bag
        {
            int n = (int)c;
            if(n >= 65 && n <= 90) // A <= c >= Z
                if(currentTilesAmount[n - 65] > 0) //Tiles that exist in bag
                {
                    currentTilesAmount[n - 65]--;
                    decTilesAmount();
                    return TilesArray[n - 65];
                }
            return null;            
        }

        void put(Tile t) //puts t back into the bag
        {
            int loc = (int)t.letter - 65; //location of t in TilesAmonut
            if(currentTilesAmount[loc] >= 0 && currentTilesAmount[loc] + 1 <= finalTilesAmount[loc])
            {
                currentTilesAmount[loc]++;
                incTilesAmount();
            }
        }

        int size(){ return num_Of_Tiles;}

        public int[] getQuantities() //returns a copy of TilesAmount
        {
            int[] copy = new int[26];
            System.arraycopy(currentTilesAmount, 0, copy, 0, 26);
            return copy;
        }      
    } //end of class Bag
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + letter;
        result = prime * result + score;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tile other = (Tile) obj;
        if (letter != other.letter)
            return false;
        if (score != other.score)
            return false;
        return true;
    }      
}
