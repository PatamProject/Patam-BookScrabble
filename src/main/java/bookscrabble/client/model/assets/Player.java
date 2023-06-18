package bookscrabble.client.model.assets;

public class Player{
    private String name;
    private Integer score = 0;
    private Rack rack;
    
    public Player(String name){
       this.name = name; 
       rack = new Rack();
    }

    public Rack getRack()
    {
        return rack;    
    }
    
    public Integer getScore(){
        return score;
    }
    
    public String getName(){
        return name;
    }

    public void addScore(int score){ //When a word is placed call this func to update the score
        this.score += score;
    }
}