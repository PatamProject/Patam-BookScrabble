package bookscrabble.client.model.assets;
import java.util.ArrayList;

public class Board {
    private static Board myBoard = null; //singleton
    Tile[][] tiles = new Tile[SIZE][SIZE];
    static final int SIZE = 15;
    static int num_Of_Tiles_Placed = 0;
    public static final int 
    NO_BONUS = 0, DOUBLE_LETTER_BONUS = 1, TRIPLE_LETTER_BONUS = 2, DOUBLE_WORD_BONUS = 3, TRIPLE_WORD_BONUS = 4, 
    CENTER = 5, S_LEFT = 6, S_RIGHT = 7, S_UP = 8, S_DOWN = 9; //flags
    public static final int[][] bonusMatrix = //a copy of the board used to calculate bonus points
    {{4,0,0,1,0,0,0,4,0,0,0,1,0,0,4}, 
     {0,3,0,0,0,2,0,0,0,2,0,0,0,3,0},
     {0,0,3,0,0,0,1,0,1,0,0,0,3,0,0},
     {1,0,0,3,0,0,0,1,0,0,0,3,0,0,1},
     {0,0,0,0,3,0,0,0,0,0,3,0,0,0,0},
     {0,2,0,0,0,2,0,0,0,2,0,0,0,2,0},
     {0,0,1,0,0,0,1,0,1,0,0,0,1,0,0},
     {4,0,0,1,0,0,0,5,0,0,0,1,0,0,4},
     {0,0,1,0,0,0,1,0,1,0,0,0,1,0,0},
     {0,2,0,0,0,2,0,0,0,2,0,0,0,2,0},
     {0,0,0,0,3,0,0,0,0,0,3,0,0,0,0},
     {1,0,0,3,0,0,0,1,0,0,0,3,0,0,1},
     {0,0,3,0,0,0,1,0,1,0,0,0,3,0,0},
     {0,3,0,0,0,2,0,0,0,2,0,0,0,3,0},
     {4,0,0,1,0,0,0,4,0,0,0,1,0,0,4}}; 

    public static Board getBoard() //singleton design
    {
        if(myBoard != null)
            return myBoard;
        myBoard = new Board();    
        return myBoard;
    }

    private Board(){} //works only via getBoard

    Tile[][] getTiles() //returns a copy of tiles
    {
        Tile[][] copy = new Tile[SIZE][SIZE];
        int i = 0;
        for (Tile[] tarr : tiles) {
            System.arraycopy(tarr, 0, copy[i], 0, tarr.length);
            i++;
        }
        return copy;
    }

    boolean boardLegal(Word w) ////boardLegal checks if the word being placed is legally placed:
    //1-A word must be fully inside the board
    //2-A word must not be placed on top of existing tiles
    //3-The first word must be placed in the center square
    //4-A word must be connected to other words unless it's the first word
    {
        if(!isInBoard(w)) //1
            return false;
        else if(!isPlacedOverTiles(w)) //2
            return false;
        else if(num_Of_Tiles_Placed == 0) 
            if(isContained(w, SIZE/2, SIZE/2)) //3
                return true;
            else
                return false;    
        else if(!isConnected(w)) //4
            return false;
        
        return true;    
    }

    private boolean isContained(Word w, int row, int col) //is square (row,col) contained in the word?
    {
        if(w.isVertical())
        {
            if(w.getCol() == col)
                if(row >= w.getRow() && row <= (w.getRow() + w.length - 1))
                    return true;
            return false;        
        }
        //else
        if(w.getRow() == row)
            if(col >= w.getCol() && col <= (w.getCol() + w.length - 1))
                return true;
        return false;     
    } 

    private boolean isInBoard(Word w) // is the word fully inside the board?
    {
        int row = w.getRow();
        int col = w.getCol();
        if(w.isVertical())
        {
            if(row < 0 || row + w.length -1 > SIZE - 1)
                return false;
            return true;    
        }
        //else
        if(col < 0 || col + w.length -1 > SIZE - 1)
                return false;
        return true;  
    }

    private boolean isPlacedOverTiles(Word w) //is the word placed over existing tiles
    {
       for (int i = w.getRow(),j = w.getCol(), index = 0; index < w.tiles.length; index++) {
            if(w.tiles[index] != null)
            {
                if(tiles[i][j] == null)
                {
                    if(w.isVertical())
                        i++;
                    else
                        j++;
                    continue;    
                }
                else //tile on tile -> not allowed           if(tiles[i][j].letter != w.tiles[index].letter)
                    return false;
            }
            else //tile in word is null
            {
                if(tiles[i][j] == null) //null tile on null tile -> not allowed
                    return false;
            }
                
            if(w.isVertical())
                i++;
            else
                j++;    
       }
        return true;
    }
    
    private boolean isConnected(Word w) //isConnected checks all squares bordering the word
    {
        int Rindex = 0, Cindex = 0;
        for (Tile t : w.tiles) { 
            if(t == null) //the word has a null char inside
                if(tiles[w.getRow() + Rindex][w.getCol() + Cindex] != null)
                    return true; //null char in word has an existing tile placed on board
                else
                    return false;

            if(w.isVertical()) Rindex++;
            else Cindex++;    
        }
        //now will go around the word to look for a tile
        Rindex = w.getRow();
        Cindex = w.getCol();
        if(w.isVertical())
        {
            Cindex--; //left side
            for (int count = 0; count <  2 * w.length; count++,Rindex++) {
                if(inBoard(Rindex, Cindex))
                    if(tiles[Rindex][Cindex] != null)
                        return true;
                if(count == w.length)
                {
                    Cindex += 2; //right side
                    Rindex = w.getRow();
                }
            }
            return false;
        }
        else //not vertical
        {
            Rindex--; //Under the word
            for (int count = 0; count <  2 * w.length; count++,Cindex++) {
                if(inBoard(Rindex, Cindex))
                    if(tiles[Rindex][Cindex] != null)
                        return true;
                if(count == w.length)
                {
                    Rindex += 2; //above the word
                    Cindex = w.getCol();
                }
            }
            return false;
        }  
    }

    private boolean inBoard(int row, int col) //is (row,col) inside the board
    {
        if(row < 0 || row > SIZE || col < 0 || col > SIZE)
            return false;    
        return true;    
    }

    boolean dictionaryLegal(Word... words) //Not used due to sever-client separation
    {
        return true;
    }

    ArrayList<Word> getWords(Word w) // returns all the words created by placing 'w' on the board
    {
        if(!boardLegal(w)) // the word must be boardLegal
            return null;   
        Tile[][] tmpBoard = getTiles();
        ArrayList<Word> newWords = new ArrayList<>();
        int startIndex, endIndex;
        placeWord(tmpBoard, w); //place word temporarily
        if(w.isVertical())
        {
            //get vertical word
            startIndex = searchWordIndex(tmpBoard, w.getRow(), w.getCol(), S_UP);
            endIndex = searchWordIndex(tmpBoard, w.getRow()+w.length-1, w.getCol(), S_DOWN);
            newWords.add(getWord(tmpBoard, startIndex,w.getCol(), endIndex - startIndex + 1, w.isVertical()));

            for (int count = 0; count < w.length; count++) { //get horizontal words
                if(w.tiles[count] == null)
                    continue;
                startIndex = searchWordIndex(tmpBoard, w.getRow() + count, w.getCol(), S_LEFT);
                endIndex = searchWordIndex(tmpBoard, w.getRow() + count, w.getCol(), S_RIGHT);
                if(startIndex != endIndex)
                    newWords.add(getWord(tmpBoard, w.getRow()+count,startIndex, endIndex - startIndex + 1, !w.isVertical()));           
            }
        }
        else //not vertical
        {
            //get horizontal word
            startIndex = searchWordIndex(tmpBoard, w.getRow(), w.getCol(), S_LEFT);
            endIndex = searchWordIndex(tmpBoard, w.getRow(), w.getCol()+w.length-1, S_RIGHT);
            newWords.add(getWord(tmpBoard, w.getRow(),startIndex, endIndex - startIndex + 1, w.isVertical()));

            for (int count = 0; count < w.length; count++) { //get vertical words
                if(w.tiles[count] == null)
                    continue;
                startIndex = searchWordIndex(tmpBoard, w.getRow(), w.getCol() + count, S_UP);
                endIndex = searchWordIndex(tmpBoard, w.getRow(), w.getCol() + count, S_DOWN);
                if(startIndex != endIndex)
                    newWords.add(getWord(tmpBoard, startIndex,w.getCol()+count, endIndex - startIndex + 1, !w.isVertical()));  
            }
        }
        return newWords; //All words are now boardLegal
    }

    private int searchWordIndex(Tile[][] boardTiles, int row, int col, int direction) //returns the starting index of the word on 'boardTiles'
    {
        for (int count = 0; count < SIZE; count++)
        {
            if(!inBoard(row,col) || 
            boardTiles[row][col] == null) //tile outside board or null
                break; //stop checking
        
            switch (direction) { //checks the next tile according to direction
                case S_UP:
                    row--;
                    break;
                case S_RIGHT:
                    col++;
                    break;
                case S_DOWN:
                    row++;
                    break;
                case S_LEFT:
                    col--;
                    break;
            } 
        }

        switch (direction) { //go back one tile
            case S_UP:
                row++;
                break;
            case S_RIGHT:
                col--;
                break;
            case S_DOWN:
                row--;
                break;
            case S_LEFT:
                col++;
                break;
        }

        if(direction == S_UP || direction == S_DOWN) // vertical
            return row;
        else //(not vertical)
            return col;  
    }

    private Word getWord(Tile[][] boardTiles, int row, int col, int size, boolean isVertical) //creates a word using the tiles on the 'boardTiles'
    {   
        Word newWord;
        Tile[] newTiles = new Tile[size];
        if(isVertical)
            for (int i = 0; i < size; i++) 
                newTiles[i] = boardTiles[row + i][col];
        else
            for (int i = 0; i < size; i++)
                newTiles[i] = boardTiles[row][col + i];
        
        newWord = new Word(newTiles,row,col,isVertical);
        return newWord;
    }

    int getScore(Word w) //returns the score of a given word
    {
        int score = 0;
        int wordMult = 1;
        int tileScore;
        for (int i = w.getRow(), j = w.getCol(), count = 0; count < w.length;count++)
        {
            if(w.tiles[count] == null)
                tileScore = tiles[i][j].score;
            else
                tileScore = w.tiles[count].score;
            switch (bonusMatrix[i][j]) 
            {
                case NO_BONUS:
                case CENTER:
                    score += tileScore;
                    break;
                case DOUBLE_LETTER_BONUS:
                    score += 2*tileScore;
                    break;
                case TRIPLE_LETTER_BONUS:
                    score += 3*tileScore;
                    break;
                case DOUBLE_WORD_BONUS:
                    score += tileScore;
                    wordMult *= 2;
                    break;
                case TRIPLE_WORD_BONUS:
                    score += tileScore;
                    wordMult *= 3;
                    break;
            }

                if(w.isVertical()) i++;
                else j++;
        }

        return score * wordMult;    
    }

    int tryPlaceWord(Word w) //places a word on the board if it follows all the rules
    {
        int score = 0, i = 0;
        if(boardLegal(w) && dictionaryLegal(w))
        {
            ArrayList<Word> newWords = getWords(w); //check all new words
            for (Word word : newWords) {
                if(!dictionaryLegal(word))
                    return 0;
            }
            if(num_Of_Tiles_Placed == 0) //first word
            {
                placeWord(tiles, w);  //place word permanently  
                score += 2 * getScore(newWords.get(0));
                i++;
            }
            else
                placeWord(tiles, w);  //place word permanently  
            for (; i < newWords.size(); i++) //calc score for all the new words created
                score += getScore(newWords.get(i));        
            return score;    
        }
        else
            return 0; 
    }

    private void placeWord(Tile[][] boardTiles ,Word w) //helping method for tryPlaceWord, this method places a word on a given board
    {
        for (int row = w.getRow(), col = w.getCol(), count = 0; count < w.getWordLength(); count++) {
            if(w.tiles[count] != null)
                boardTiles[row][col] = w.tiles[count];
            
            if(w.isVertical()) row++;
            else col++;
        }

        if(boardTiles == tiles) //same address -> word is placed on board
            num_Of_Tiles_Placed += w.getWordLength();
    }

    @Override
    public String toString()
    {
        String str = "";
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(tiles[i][j] == null)
                    str += "-";
                else
                    str += tiles[i][j].letter;
            }

            if(i != SIZE - 1)
                str += "&";
        }
        return str;  
    }
}