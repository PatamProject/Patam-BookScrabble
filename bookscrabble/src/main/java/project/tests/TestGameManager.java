package project.tests;
import project.client.model.assets.*;
import project.client.model.assets.Board;
import project.client.model.assets.GameManager;
import project.client.model.assets.PlayerModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class TestGameManager
{
    GameManager gameManager;

    public TestGameManager()
    {
        gameManager = new GameManager();
    }

    public void testGetPlayer()
    {
        if(gameManager.getPlayer("Yaakov")!=null)
            System.out.println("testGetPlayer Failed");
    }

    public void testAddNewPlayer()
    {
        boolean flag = true;
        flag = gameManager.addNewPlayer("Yaakov");
        if(gameManager.getPlayer("Yaakov")==null)
            System.out.println("testAddNewPlayer Failed");
        flag = gameManager.addNewPlayer("Braha");
        flag = gameManager.addNewPlayer("Yaakov");
        if(!flag)
            System.out.println("testAddNewPlayer Failed");
        flag = gameManager.addNewPlayer("Zvolon");
        flag = gameManager.addNewPlayer("Arnold");
        flag = gameManager.addNewPlayer("Yafit");
        if(flag)
            System.out.println("testAddNewPlayer Failed");
    }

    public void testRemovePlayer()
    {
        boolean flag = true;
        flag = gameManager.removePlayer("Yaakov");
        if(!flag)
            System.out.println("testRemovePlayer Failed");
        flag = gameManager.removePlayer("Shalom");
        if(flag)
            System.out.println("testRemovePlayer Failed");
        if(gameManager.getPlayer("Yaakov") != null)
            System.out.println("testRemovePlayer Failed");
        flag = gameManager.removePlayer("Braha");
        flag = gameManager.removePlayer("Zvolon");
        flag = gameManager.removePlayer("Arnold");
        if(!gameManager.gameEnded)
            System.out.println("testRemovePlayer Failed");
        gameManager.gameEnded = false;
    }

    public void testSetRandomPlayOrder()
    {
       boolean flag = true;
       flag = gameManager.addNewPlayer("player1");
       flag = gameManager.addNewPlayer("player2");
       flag = gameManager.addNewPlayer("player3");
       flag = gameManager.addNewPlayer("player4");
       if(!flag)
           System.out.println("testAddNewPlayer Failed");
       //gameManager.setRandomPlayOrder();
       gameManager.removePlayer("player1");
       gameManager.removePlayer("player2");
       gameManager.removePlayer("player3");
       gameManager.removePlayer("player4");
    }

    public void testTilesToString()
    {
        boolean flag = true;
        flag = gameManager.addNewPlayer("player1");
        // if(gameManager.tilesToString("player1") != null)
        //     System.out.println("testTilesToString Failed");
        String tilesToTake = "WPLXK";
        gameManager.getPlayer("player1").getRack().takeTiles(tilesToTake);
        //String player1tiles = gameManager.tilesToString("player1");
        // if(!haveSameLetters(tilesToTake, player1tiles))
        //     System.out.println("testTilesToString Failed");
        gameManager.removePlayer("player1");
    }

    private boolean haveSameLetters(String str1, String str2) {
        char[] charArray1 = str1.toCharArray();
        char[] charArray2 = str2.toCharArray();
        Arrays.sort(charArray1);
        Arrays.sort(charArray2);
        return Arrays.equals(charArray1, charArray2);
    }

    public void testPlaceWord()
    {
        boolean flag = true;
        flag = gameManager.addNewPlayer("player1");
        flag = gameManager.addNewPlayer("player2");
        if(!flag)
            System.out.println("testAddNewPlayer Failed");
        gameManager.getPlayer("player1").getRack().takeTiles("IS");
        gameManager.getPlayer("player2").getRack().takeTiles("ANT");
        Word w1 = gameManager.createWordFromClientInput("player1","IS",7,7,true);
        int score1 = gameManager.getScoreFromWord("player1",w1);
        Word w2 = gameManager.createWordFromClientInput("player2","ANTS",8,4,false);
        String[] newWords = gameManager.getWordsFromClientInput(w2);
        int score2 = gameManager.getScoreFromWord("player2",w2);
        if(score1 != 4 || score2 != 5 || !newWords[0].equals("ANTS"))
            System.out.println("testPlaceWord Failed");
    }

    public void testGetWinner()
    {
        String pName = gameManager.getWinner();
        if(!pName.equals("E,player2"))
            System.out.println("testGetWinner Failed");
    }
    public static void main(String[] args)
    {
        TestGameManager testGameModel = new TestGameManager();
        testGameModel.testGetPlayer();
        testGameModel.testAddNewPlayer();
        testGameModel.testRemovePlayer();
        testGameModel.testSetRandomPlayOrder();
        testGameModel.testTilesToString();
        testGameModel.testPlaceWord();
        testGameModel.testGetWinner();
        System.out.println("done");
    }
}
