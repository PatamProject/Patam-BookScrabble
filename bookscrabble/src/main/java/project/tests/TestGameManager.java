package project.tests;
import project.client.model.assets.*;
import project.client.model.assets.GameManager;

import java.util.ArrayList;
import java.util.Arrays;

public class TestGameManager {
    final int MAX_PLAYERS = 4;
    //startGame
    //initial rack
    //next turn
    //

    public void testMethod1()
    {
        //Tests the following methods:
        // addNewPlayer
        // getPlayersOrder
        // getPlayersAmount
        // removePlayer
        // isGameEnded
        // getPlayer

        String[] names1 = {"Assaf Lots", "Braha", "Zvolon", "Arnold"};
        String[] names2 = {"Assaf Lots", "Assaf Lots", "Zvolon", "Arnold"};
        testMethod1Helper(names1);
        testMethod1Helper(names2);
    }

    private void testMethod1Helper(String[] names) {
        GameManager gameManager = new GameManager();
        boolean flag = true;
        for (String name : names)
            flag &= gameManager.addNewPlayer(name);

        ArrayList<String> playersOrder = gameManager.getPlayersOrder();
        if (!flag || gameManager.getPlayersAmount() != MAX_PLAYERS || playersOrder.size() != MAX_PLAYERS)
            System.out.println("Problem with addNewPlayer method");

        if (gameManager.getPlayer("Bob") != null || gameManager.getPlayer("Zvolon") == null)
            System.out.println("Problem with getPlayer Method");

        if(gameManager.addNewPlayer("Yafit"))
            System.out.println("Problem with addNewPlayer method");
        if(gameManager.addNewPlayer("Assaf Lots"))
            System.out.println("Problem with addNewPlayer method");

        gameManager.removePlayer("Yafit");
        if (gameManager.getPlayersAmount() != MAX_PLAYERS || playersOrder.size() != MAX_PLAYERS)
            System.out.println("Problem with removePlayer method");

        flag = true;
        for (String player : playersOrder)
            flag &= gameManager.removePlayer(player);

        if (flag || !gameManager.isGameEnded() || gameManager.getPlayersAmount() != 0 || gameManager.getPlayersOrder().size() != 0)
            System.out.println("Problem with removePlayer method");
    }

    public void testSetRandomPlayOrder()
    {
       GameManager gameManager = new GameManager();
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
        GameManager gameManager = new GameManager();
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
        GameManager gameManager = new GameManager();
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
        GameManager gameManager = new GameManager();
        String pName = gameManager.getWinner();
        if(!pName.equals("E,player2"))
            System.out.println("testGetWinner Failed");
    }
    public static void main(String[] args)
    {
        TestGameManager testGameManager = new TestGameManager();
        testGameManager.testMethod1();
        testGameManager.testSetRandomPlayOrder();
        testGameManager.testTilesToString();
        testGameManager.testPlaceWord();
        testGameManager.testGetWinner();
        System.out.println("Done!");
    }
}