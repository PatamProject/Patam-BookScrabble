package project.client.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import project.client.view.BoardView;
import project.client.view.InfoView;
import project.client.view.RackView;
import project.client.view.TileView;

import java.util.List;

public class GameView extends Application {
    private Pane root;
    private List<String> playersName;

    public GameView(List<String> playersName) {
        this.playersName = playersName;
    }

    private VBox createPlayerInfo(String playerName) {
        VBox infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setSpacing(10);
        infoBox.setPadding(new Insets(10));

        Rectangle background = new Rectangle(200, 80);
        background.setFill(Color.GRAY.brighter());

        Text nameLabel = new Text(playerName);
        nameLabel.setFont(Font.font(16));
        nameLabel.setFill(Color.WHITE); // Set text color to white
        nameLabel.setTranslateY(-20);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(background, nameLabel);
        infoBox.getChildren().add(stackPane);

        // Add separator line between players
        if (!infoBox.getChildren().isEmpty()) {
            Region separator = new Region();
            separator.setPrefHeight(1);
            separator.setMaxWidth(Double.MAX_VALUE);
            separator.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
            infoBox.getChildren().add(separator);
            VBox.setMargin(separator, new Insets(5, 0, 5, 0));
        }

        return infoBox;
    }

    private TileView checkTileView(String letter, int score) {
        TileView tileView = new TileView(letter, score);
        return tileView;
    }

    private BoardView checkBoardView() {
        return new BoardView();
    }

    private RackView checkRackView() {
        RackView rackView = new RackView();
        return rackView;
    }

    private void createRoot() {
        root = new Pane();

        TileView tileView = checkTileView("A", 1);
        BoardView boardView = checkBoardView();
        RackView rackView = checkRackView();

        // Create player info boxes and add them to the right side of the screen
        VBox playerInfoBox = new VBox();
        playerInfoBox.setSpacing(10);
        playerInfoBox.setPadding(new Insets(10));
        playerInfoBox.setAlignment(Pos.TOP_RIGHT);
        for (String playerName : playersName) {
            VBox playerInfo = createPlayerInfo(playerName);
            playerInfoBox.getChildren().add(playerInfo);
        }
        root.getChildren().add(playerInfoBox);

        // Set the desired x and y coordinates for each view
        tileView.getView().setTranslateX(10);
        tileView.getView().setTranslateY(10);

        boardView.getView().setTranslateX(10);
        boardView.getView().setTranslateY(100);

        rackView.getView().setTranslateX(750);
        rackView.getView().setTranslateY(950);

        root.getChildren().addAll(boardView.getView(), tileView.getView(), rackView.getView());
    }

    public Pane getRoot() {
        if (root == null) {
            createRoot();
        }
        return root;
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(getRoot(), 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game View");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}