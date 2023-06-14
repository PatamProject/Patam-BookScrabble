package project.client.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.client.view.GameView;

public class FrontPage extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("BookScrabble");

        // Create headline label
        Label headlineLabel = new Label("Hello");
        headlineLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Create buttons
        Button hostButton = new Button("Host Mode");
        Button guestButton = new Button("Guest Mode");

        // Set event handlers for buttons
        hostButton.setOnAction(e -> {
            HostMode hostMode = new HostMode();
            hostMode.showHostMode();
            primaryStage.close();
        });
        guestButton.setOnAction(e -> {
            GuestMode guestMode = new GuestMode();
            guestMode.showGuestMode();
            primaryStage.close();
        });

        // Create the layout for the front page
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(headlineLabel, hostButton, guestButton);

        // Set the scene on the primary stage
        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}