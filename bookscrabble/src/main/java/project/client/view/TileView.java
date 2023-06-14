package project.client.view;

import java.io.Serializable;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TileView implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int TILE_SIZE = 55;
    private static final DataFormat TILE_FORMAT = new DataFormat("project.client.view.TileView");
    private String letter;
    private int score;
    private StackPane view;
    double mouseX,mouseY;

    public TileView(String letter, int score) {
        this.letter = letter;
        this.score = score;
        this.view = createView();
    }

    private StackPane createView() {
        StackPane tilePane = new StackPane();
        tilePane.getStyleClass().add("tile");
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setPrefSize(TILE_SIZE, TILE_SIZE);

        Rectangle tileShape = new Rectangle(TILE_SIZE, TILE_SIZE);
        tileShape.setFill(Color.LIGHTBLUE);
        tileShape.setStroke(Color.BLACK);

        Text letterText = new Text(letter);
        letterText.setFont(Font.font("Arial", 20));
        letterText.setFill(Color.BLACK);

        Text scoreText = new Text(Integer.toString(score));
        scoreText.setFont(Font.font("Arial", 12));
        scoreText.setFill(Color.BLACK);
        scoreText.setTranslateY(15);
        scoreText.setTranslateX(15);

        tilePane.getChildren().addAll(tileShape, letterText, scoreText);
        tilePane.setUserData(this);
        dragAble(tilePane);
        view = tilePane;
        return tilePane;
    }

    private void dragAble(StackPane tilePane) {
        tilePane.setOnMousePressed(mouseEvent -> {
            mouseX = mouseEvent.getSceneX();
            mouseY = mouseEvent.getSceneY();
        });
        tilePane.setOnMouseDragged(mouseEvent -> {
            double deltaX = mouseEvent.getSceneX() - mouseX;
            double deltaY = mouseEvent.getSceneY() - mouseY;
            tilePane.setTranslateX(tilePane.getTranslateX() + deltaX);
            tilePane.setTranslateY(tilePane.getTranslateY() + deltaY);
            mouseX = mouseEvent.getSceneX();
            mouseY = mouseEvent.getSceneY();
        });
    }

    public static DataFormat getFormat()
    {
        return TILE_FORMAT;
    }
    public StackPane getView() {
        return view;
    }

    public String getLetter() {
        return letter;
    }

    public int getScore() {
        return score;
    }
}
