package project.tests;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import project.client.model.assets.Board;
import project.client.model.assets.GameManager;
import project.client.model.assets.Word;

public class testGame {
    public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {
        GameManager game = new GameManager();
        Board b = game.getBoard();
        Word w  = null;

        Method m;
        try {
            m = b.getClass().getDeclaredMethod("tryPlaceWord");
            m.setAccessible(true);
            int t = (int) m.invoke(b, w);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }

        //game.getBoard().tryPlaceWord(w);
    }
}
