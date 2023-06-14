package project.client.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class InfoView {

    private String playerName;
    private int playerScore;

    public InfoView(String playerName, int playerScore) {
        this.playerName = playerName;
        this.playerScore = playerScore;
    }

    public VBox getView() {
        VBox infoBox = new VBox();
        infoBox.setAlignment(Pos.CENTER);
        infoBox.setSpacing(10);
        infoBox.setPadding(new Insets(10));

        // Create a rectangle with red background
        Rectangle background = new Rectangle(200, 80);
        background.setFill(Color.RED);

        Text nameLabel = new Text("Name: " + playerName);
        nameLabel.setFont(Font.font(16));
        nameLabel.setFill(Color.WHITE); // Set text color to white
        nameLabel.setTranslateY(-20);
        nameLabel.setTranslateX(0);
        Text scoreLabel = new Text("Score: " + playerScore);
        scoreLabel.setFont(Font.font(16));
        scoreLabel.setFill(Color.WHITE); // Set text color to white
        scoreLabel.setTranslateY(10);
        // Add text labels to the background rectangle
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(background, nameLabel, scoreLabel);

        infoBox.getChildren().add(stackPane);

        return infoBox;
    }

    /*
    public InfoView checkInfoView() {
        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Player Name");
        nameDialog.setHeaderText("Enter your name:");
        nameDialog.setContentText("Name:");

        TextInputDialog scoreDialog = new TextInputDialog();
        scoreDialog.setTitle("Player Score");
        scoreDialog.setHeaderText("Enter your score:");
        scoreDialog.setContentText("Score:");

        String playerName;
        int playerScore;

        // Capture player name
        nameDialog.showAndWait();
        playerName = nameDialog.getResult();

        // Capture player score
        scoreDialog.showAndWait();
        String scoreInput = scoreDialog.getResult();
        playerScore = Integer.parseInt(scoreInput);

        // Create InfoView instance with captured data
        return new InfoView(playerName, playerScore);
    }
     */
}