package bookscrabble.tests;

import bookscrabble.client.model.assets.Rack;
import bookscrabble.client.model.assets.Tile;

public class TestRack {
    private Rack rack;

    public TestRack(){
        rack = new Rack();
    }

    public void testTakeTiles() {
        rack.takeTiles("ABC");
        Tile[] tiles = rack.getTiles();
        boolean flag = true;
        for(Tile t : tiles)
        {
            if(t.letter != 'A' && t.letter != 'B' &&  t.letter != 'C' )
                flag = false;
        }
        if(!flag)
            System.out.println(" testTakeTiles Failed");
    }

    public void testTakeTilesFromBag() {
        try {
            rack.takeTilesFromBag();
        } catch (Exception e) {
            System.out.println(" testTakeTilesFromBag Exception caught: " + e.getMessage());
        }
        Tile[] tiles = rack.getTiles();
        if(rack.size() > 7)
            System.out.println(" testTakeTilesFromBag Failed");
    }

    public void testTakeTileFromRack() {
        Tile t = rack.takeTileFromRack('Z');
        if(t == null)
            return;
       else if ( rack.size() != 5)
            System.out.println(" testTakeTileFromRack Failed");
    }

    public void testRemoveTiles() {
        boolean flag = false;
        Tile[] tiles = rack.getTiles();
        Tile t = tiles[0];
        for(int i=1;i<tiles.length;i++)
            if(t == tiles[i])
                flag = true;
        rack.removeTiles(t);
        t = rack.takeTileFromRack(t.letter);
        if(t!=null && !flag)
            System.out.println(" testRemoveTiles Failed");
    }

    public void testAddTiles()
    {
        Tile t1 = rack.getTiles()[0];
        Tile t2 = rack.getTiles()[1];
        rack.removeTiles(t1);
        rack.removeTiles(t2);
        //rack.addTiles(t1,t2);
        Tile t3 = rack.takeTileFromRack(t1.letter);
        Tile t4 = rack.takeTileFromRack(t2.letter);
        if(t1==null || t2==null)
            System.out.println(" testAddTiles Failed");
    }

    public void testIsEmpty()
    {
        rack.removeTiles(rack.getTiles());
        if(!rack.isEmpty())
            System.out.println(" testIsEmpty Failed");
    }

    public static void main(String[] args) {
        TestRack testRack = new TestRack();
        testRack.testTakeTiles();
        testRack.testTakeTilesFromBag();
        testRack.testTakeTileFromRack();
        testRack.testRemoveTiles();
        testRack.testAddTiles();
        testRack.testIsEmpty();
        System.out.println("done");
    }
}