package project.server.assets;

public class Player{
    private String name;
    private Integer score = 0;
    private Rack rack = null; //A Rack is created when the game starts
    
    public Player(String name){
       this.name = name; 
    }

    public Rack getRack()
    {
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

    void addScore(int score){ //When a word is placed call this func to update the score
        this.score += score;
    }
}