package project.tests;
import project.client.model.MyHostServer;
import project.client.model.assets.*;
import project.client.model.assets.GameManager;

import java.util.ArrayList;

public class TestGameManager {
    final int MAX_PLAYERS = 4, RACK_SIZE = 7;

    public void testMethod1()
    {
        // Tests the following methods:
        // createWordFromClientInput
        // getWordsFromClientInput
        // getScoreFromWord

        GameManager gameManager = new GameManager();
        gameManager.addNewPlayer("player1");
        gameManager.addNewPlayer("player2");
        gameManager.getPlayer("player1").getRack().takeTiles("IS");
        gameManager.getPlayer("player2").getRack().takeTiles("ANT");

        Word w1 = gameManager.createWordFromClientInput("player1","IS",7,7,true);
        if (!w1.toString().equals("IS"))
            System.out.println("Problem with createWordFromClientInput method");

        String[] arrWords = gameManager.getWordsFromClientInput(w1);
        if (!arrWords[0].equals(w1.toString()))
            System.out.println("Problem with getWordsFromClientInput method");

        int score1 = gameManager.getScoreFromWord("player1",w1);
        Word w2 = gameManager.createWordFromClientInput("player2","ANTS",8,4,false);
        String[] newWords = gameManager.getWordsFromClientInput(w2);
        if (!newWords[0].equals("ANTS"))
            System.out.println("Problem with getWordsFromClientInput method");

        int score2 = gameManager.getScoreFromWord("player2",w2);
        if(score1 != 4 || score2 != 5)
            System.out.println("Problem with getScoreFromWord method");
    }

    public void testMethod2()
    {
        // Tests the following methods:
        // getBoard
        // addNewPlayer
        // getPlayersOrder
        // getPlayersAmount
        // removePlayer
        // isGameEnded
        // getPlayer

        GameManager gameManager = new GameManager();
        if (gameManager.getBoard() == null)
            System.out.println("Problem with getBoard");

        String[] names1 = {"Assaf Lots", "Braha", "Zvolon", "Arnold"};
        String[] names2 = {"Assaf Lots", null, "Zvolon", "Arnold"};
        // String[] names3 = {"Assaf Lots", "Assaf Lots", "Zvolon", "Arnold"}; // Doubled name problem solved by using ID in MyHostServer

        boolean flag = true;
        for (String name : names1)
            flag &= gameManager.addNewPlayer(name);

        ArrayList<String> playersOrder = gameManager.getPlayersOrder();
        if (!flag || gameManager.getPlayersAmount() != MAX_PLAYERS || playersOrder.size() != MAX_PLAYERS)
            System.out.println("Problem with addNewPlayer method");

        if(gameManager.addNewPlayer("Yafit"))
            System.out.println("Problem with addNewPlayer method");
        if(gameManager.addNewPlayer("Assaf Lots"))
            System.out.println("Problem with addNewPlayer method");

        gameManager.removePlayer("Yafit");
        if (gameManager.getPlayersAmount() != MAX_PLAYERS || playersOrder.size() != MAX_PLAYERS)
            System.out.println("Problem with removePlayer method");

        testMethod2Helper(gameManager, playersOrder);
        MyHostServer.isGameRunning = false;
        flag = true;
        for (String name : names2)
            flag &= gameManager.addNewPlayer(name);

        playersOrder = gameManager.getPlayersOrder();
        if (flag || gameManager.getPlayersAmount() != MAX_PLAYERS - 1 || playersOrder.size() != MAX_PLAYERS - 1)
            System.out.println("Problem with addNewPlayer method");

        if(!gameManager.addNewPlayer("Yafit"))
            System.out.println("Problem with addNewPlayer method");
        if(gameManager.addNewPlayer("Assaf Lots"))
            System.out.println("Problem with addNewPlayer method");

        playersOrder = gameManager.getPlayersOrder();
        testMethod2Helper(gameManager, playersOrder);
    }

    private void testMethod2Helper(GameManager gameManager, ArrayList<String> playersOrder) {
        boolean flag;
        flag = true;
        for (String player : playersOrder)
            flag &= gameManager.removePlayer(player);

        if (flag || !gameManager.isGameEnded() || gameManager.getPlayersAmount() != 0 || gameManager.getPlayersOrder().size() != 0)
            System.out.println("Problem with removePlayer method");
        playersOrder.clear();
    }

    public void testMethod3() {
        //Tests the following methods:
        // startGame
        // initialRacks
        // setRandomPlayOrder
        // nextTurn
        // checkEndGameConditions

        GameManager gameManager = new GameManager();
        String[] names = {"Assaf Lots", "Braha", "Zvolon", "Arnold"};
        for (String name : names)
            gameManager.addNewPlayer(name);

        ArrayList<String> playersOrder = gameManager.getPlayersOrder();
        String[] result;
        try {
            result = gameManager.startGame();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (result.length != MAX_PLAYERS)
            System.out.println("Problem with startGame method");

        for (String name : names)
            if (gameManager.getPlayer(name).getRack().size() != RACK_SIZE)
                System.out.println("Problem with initialRacks method");

        boolean flag = true;
        for (int i = 0; i < playersOrder.size(); i++)
            flag &= playersOrder.get(i).equals(gameManager.getPlayersOrder().get(i));
        if(flag)
            System.out.println("Problem with setRandomPlayOrder method");

        String name = gameManager.getCurrentPlayersName();
        gameManager.nextTurn();
        if (name.equals(gameManager.getCurrentPlayersName()))
            System.out.println("Problem with nextTurn method");

        GameManager game = new GameManager();
        game.addNewPlayer("Alice");
        game.nextTurn();
        if (!game.isGameEnded())
            System.out.println("Problem with checkEndGameConditions method");
    }

    public void testMethod4()
    {
        //Tests the following methods:
        // getWinner

        int score = 10;
        GameManager gameManager = new GameManager();
        String[] names = {"Assaf Lots", "Braha", "Zvolon", "Arnold"};
        for (String name : names) {
            gameManager.addNewPlayer(name);
            gameManager.getPlayer(name).addScore(score++);
        }

        try {
            gameManager.startGame();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String pName = gameManager.getWinner();
        if(!pName.equals("E,Arnold") || !gameManager.isGameEnded())
            System.out.println("Problem with getWinner method");

        MyHostServer.isGameRunning = false;
        gameManager.getPlayer("Zvolon").addScore(1);
        Tile[] rack = gameManager.getPlayer("Arnold").getRack().getTiles();
        gameManager.getPlayer("Arnold").getRack().removeTiles(rack);
        pName = gameManager.getWinner();
        if (!pName.equals("E,Arnold") || !gameManager.isGameEnded())
            System.out.println("Problem with getWinner method");
        MyHostServer.isGameRunning = false;
    }

    public static void main(String[] args)
    {
        TestGameManager testGameManager = new TestGameManager();
        testGameManager.testMethod1();
        testGameManager.testMethod2();
        testGameManager.testMethod3();
        testGameManager.testMethod4();
        System.out.println("Done!");
    }
}