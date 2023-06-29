package bookscrabble.client.view;

public class PlayerAndScore { // Represents a row in the table that is shown to the user while playing
    private String nameColumn;
    private Integer scoreColumn;

    public PlayerAndScore(String name, Integer score) { // Ctor
        this.nameColumn = name;
        this.scoreColumn = score;
    }

    // Getters
    public String getNameColumn() {return nameColumn;}
    public Integer getScoreColumn() {return scoreColumn;}
}