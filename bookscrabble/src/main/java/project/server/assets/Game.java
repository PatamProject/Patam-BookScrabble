package project.server.assets;

import java.util.ArrayList;

public class Game {
    Board board;
    ArrayList<Player> players;
    int size=0; 
    private volatile boolean gameEnded = false;

    //NewPlayerJoined(String name) {players.add(new Player(name);}
    //PlayerLeft
    //NewGameStarted
    //TileTaken
    //TileGiven
    //TilePlaced
    //WordPlaced




    public Game(){
        this.board = Board.getBoard();
        this.players = new ArrayList<>();
    }

    public boolean addNewPlayer(Player p){
        if(players.size() < 4)
        {
            players.add(p);
            size++;
            return true;
        }    

        return false;
    }

    public void startGame(){
        // for(Player p : players)
        //     p.getRack();
    }

    //the game end when thier are no tiles left in the bag  
    //of the players have no more tiles of the players dont have option to a word
    public void endGame(){
        //if the bag is empty return true
        if(Tile.Bag.isEmpty())
            gameEnded = true;
        else
        {
            //chack if thier is at less then 2 players
            if(players.size() < 2)
                gameEnded = true;

        }
        
    }

    //player want to leave the game
    public void leaveGame(Player p){
        size--;
        players.remove(p);
    }

    //the winner is the player with biggest score
    public Player getWinner(){
        Player winner = players.get(0);
        for(Player p : players)
        {
            if(p.getScore() > winner.getScore())
                winner = p;
        }
        return winner;
    }

    //check if the word is correct by the dictionary
    public void checkWord(Word w){
        

    }


}