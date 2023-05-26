package project.tests;
import project.client.model.assets.PlayerModel;
import project.client.model.assets.Rack;
import project.client.model.assets.Tile;

public class TestPlayerModel
{
    PlayerModel player1;
    PlayerModel player2;
    PlayerModel player3;
    PlayerModel player4;

    public TestPlayerModel(){
        player1 = new PlayerModel("John");
        player2 = new PlayerModel("Mike");
    }

    public void testGetRack()
    {
        if(player1.getRack() == null || player2.getRack() == null)
            System.out.println("testGetRack Failed");
    }

    public void testGetScore() {
        if(player1.getScore() != 0 || player2.getScore() != 0)
            System.out.println("testGetScore Failed");
    }

    public void testGetName() {
        if(!player1.getName().equals("John") || !player2.getName().equals("Mike"))
            System.out.println("testGetName Failed");
    }

    public void testAddScore()
    {
        player1.addScore(5);
        player2.addScore(0);
        if(player1.getScore() != 5 || player2.getScore() != 0)
            System.out.println("testAddScore Failed");
    }

    public static void main(String[] args)
    {
        TestPlayerModel testPlayerModel = new TestPlayerModel();
        testPlayerModel.testGetRack();
        testPlayerModel.testGetScore();
        testPlayerModel.testGetName();
        testPlayerModel.testAddScore();
        System.out.println("done");
    }
}
