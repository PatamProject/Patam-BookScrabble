package project.model;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import project.assets.Board;

public class Game {
    Board board;
    ArrayList<Player> players;
    private Integer currentPlayerIndex = 0;
    private volatile boolean gameEnded = false;

    public Game(){
        this.board = Board.getBoard();
        this.players = new ArrayList<>();
    }

    public boolean addNewPlayer(Player p){
        if(players.size() < 4)
        {
            players.add(p);
            return true;
        }    

        return false;
    }

    public void startGame(){
        
        //setDefaultPlayerStats();
        //setGamePlayOrder();
        startGameLoop();
        //startGameEndLoop();

        
    }

    private void startGameLoop(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //System.out.println("Your turn has ended");
                //...
                turnEnded();
            }
        };

        while(!gameEnded)
        {
            while(!turnEnded())
            {
                timer.schedule(timerTask, 120 * 1000); //Each player has 120 seconds to play

            }

            
        }
    }

    private void nextPlayerTurn(){
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    private boolean turnEnded(){
        nextPlayerTurn();
        return true;
    }

    public boolean playerPlacesWord(String word, int row, int col, boolean isVertical){
        //...
        return true;
    }
    

}

// for (int i = 0; i < players.size(); i++) {
//     //     try {
//     //         timer.schedule(timerTask, 120 * 1000); //Each player has 120 seconds to play
            

//     //     } catch (Exception e) {
//     //         //Player disconnected
//     //         //System.out.println("Player " + "" + "disconnected");
//     //         //gameEndLoop...unfreeeze thread
//     //         //nextPlayerTurn();
//     //     }
//     // }