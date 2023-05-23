package project.tests;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import project.client.model.assets.Board;
import project.client.model.assets.GameModel;
import project.client.model.assets.Word;

public class testGame {
    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
        GameModel game = new GameModel();
        Board b = game.getBoard();
        Word w  = null;

        Method m;
        try {
            m = b.getClass().getDeclaredMethod("tryPlaceWord");
            m.setAccessible(true);
            int t = (int) m.invoke(b, w);
        } catch (NoSuchMethodException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //game.getBoard().tryPlaceWord(w);
    }
}
