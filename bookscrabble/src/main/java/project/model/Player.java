package project.model;

import project.assets.Rack;
import project.assets.Word;

public abstract class Player{
    private String name;
    private Integer score = 0;
    private Rack rack = null; //A Rack is created when the game starts
    public volatile boolean myTurn = false; //Used to determine if it's the player's turn
    
    public Player(String name){
       this.name = name; 
    }

    public Rack getRack(){ //Works via GameHandler.startGame()...
        if(rack == null) //Each Player has a one Rack per game
            return new Rack();
        return rack;    
    }
    
    public Integer getScore(){
        return score;
    }
    
    public String getName(){
        return name;
    }

    private void addScore(int score){ //When a word is placed call this func to update the score
        this.score += score;
    }

    public void changeName(String name){
        this.name = name;
    }

    public void placeWord(Word w) //When a *valid* word is placed call this func to update the rack and score
    {
        if(myTurn)
            addScore(rack.placeWord(w));
    }
}