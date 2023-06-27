package bookscrabble.client.view;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;

import java.io.IOException;

import bookscrabble.client.misc.MyLogger;

public class GameWindowDisplayer {
    private final int GRID_SIZE = 15, SQUARE_SIZE = 55, MAX_TILE_SIZE = 7;
    private int rowNode=0, colNode=0;
    private String myTiles;
    private GridPane gridPane;

    public void completeBoard(GridPane gridPane ,HBox hBox, String myTiles)
    {
        this.gridPane = gridPane;
        char[] tmpArr = myTiles.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : tmpArr)
            sb.append(c).append(" ");
        this.myTiles = sb.toString().trim();
        for (int i = 0; i < 225; i++) {
            Node node = gridPane.getChildren().get(i);
            Rectangle rec = (Rectangle) node;
            GridPane.setRowIndex(node, rowNode);
            GridPane.setColumnIndex(node, colNode);
            colNode++;
            if (colNode == 15) {
                colNode = 0;
                rowNode++;
            }
            rec.setWidth(SQUARE_SIZE);
            rec.setHeight(SQUARE_SIZE);
            GridPane.setHgrow(rec, Priority.ALWAYS);
            GridPane.setVgrow(rec, Priority.ALWAYS);
        }
        gridPane.setPrefSize(SQUARE_SIZE * GRID_SIZE, SQUARE_SIZE * GRID_SIZE);
        gridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        gridPane.autosize();
        finishBoard(gridPane);
        insertImage(hBox);
    }

    private void insertImage(HBox hBox)
    {
        String[] tileArr = myTiles.split(" ");
        for(int i=0;i<MAX_TILE_SIZE;i++)
        {
            try {
                String imageTile = tileArr[i].concat("tile.png");
                String imagePath = "/bookscrabble/pictures/tiles/"+ imageTile;
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                StackPane stackPane = (StackPane) hBox.getChildren().get(i);
                Group group = (Group) stackPane.getChildren().get(0);
                ImageView imageView = (ImageView) group.getChildren().get(1);
                imageView.setImage(image);
                group.setId(tileArr[i]);
            } catch (Exception e) {
                MyLogger.logError("Error with insertImage(): " + e.getMessage());
            }
        }
    }

//    public void putTile(String letter, int row, int col)
//    {
//        Rectangle rectangle = (Rectangle) getNodeByRowColumnIndex(gridPane,row,col);
//        String imageTile = letter.concat("tile.png");
//        String imagePath = "bookscrabble/resources/ImageTile/"+ imageTile;
//        Image image = new Image(imagePath);
//        ImagePattern imagePattern = new ImagePattern(image);
//        rectangle.setFill(imagePattern);
//    }

    private void setColorAndText(GridPane gridPane , int row, int col, Color color, String text)
    {
        Node node = getNodeByRowColumnIndex(gridPane , row, col);
        Rectangle rec = (Rectangle) node;
        rec.setFill(color);
        rec.setStroke(Color.BLACK);
        if (text != null && !text.isEmpty()) {
            Text textNode = new Text(text);
            textNode.setFont(Font.font("Arial", 12));
            textNode.setFill(Color.BLACK);
            StackPane stackPane = new StackPane(rec, textNode);
            gridPane.add(stackPane, col, row);
        }
    }

    private Node getNodeByRowColumnIndex(GridPane gridPane , final int row, final int column) {
        for (Node node : gridPane.getChildren())
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column)
                return node;
        return null;
    }

    private void finishBoard(GridPane gridPane) {
        setColorAndText(gridPane,0, 0, Color.RED, "TRIPLE\nWORD\nSCORE");
        setColorAndText(gridPane,0, 3, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,0, 7, Color.RED, "TRIPLE\nWORD\nSCORE");
        setColorAndText( gridPane,0, 11, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,0, 14, Color.RED, "TRIPLE\nWORD\nSCORE");
        setColorAndText( gridPane,1, 1, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,1, 5, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,1, 9, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,1, 13, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,2, 2, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,2, 6, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,2, 8, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,2, 12, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,3, 0, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,3, 3, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,3, 7, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,3, 14, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,4, 4, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,4, 10, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,5, 1, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,5, 5, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,5, 9, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,5, 13, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,6, 2, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,6, 6, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,6, 8, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,6, 12, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,7, 0, Color.RED, "TRIPLE\nWORD\nSCORE");
        setColorAndText( gridPane,7, 3, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,7, 7, Color.PURPLE, "");
        setColorAndText( gridPane,7, 11, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,7, 14, Color.RED, "TRIPLE\nWORD\nSCORE");
        setColorAndText( gridPane,8, 2, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,8, 6, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,8, 8, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,8, 12, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,9, 1, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,9, 5, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,9, 9, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,9, 13, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,10, 4, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,10, 10, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,11, 0, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,11, 3, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,11, 7, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,11, 11, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,11, 14, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,12, 2, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,12, 6, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,12, 8, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText( gridPane,12, 12, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,13, 1, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,13, 5, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,13, 9, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText( gridPane,13, 13, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText( gridPane,14, 0, Color.RED, "TRIPLE\nWORD\nSCORE");
        setColorAndText( gridPane,14, 3, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText(gridPane,14, 7, Color.RED, "TRIPLE\nWORD\nSCORE");
        setColorAndText(gridPane,14, 11, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText(gridPane,14, 14, Color.RED, "TRIPLE\nWORD\nSCORE");
    }
}