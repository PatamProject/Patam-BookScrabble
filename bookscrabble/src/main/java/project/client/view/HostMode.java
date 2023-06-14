package project.client.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

class HostMode {
    private Stage primaryStage;
    private String hostName, ipHost , portHost;
    private ObservableList<String> playersList;

    public void showHostMode() {
        primaryStage = new Stage();
        primaryStage.setTitle("Host Mode");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        Label nameLabel = new Label("Name:");
        TextField nameTextField = new TextField();
        nameTextField.setMinWidth(200);
        nameTextField.setMaxWidth(200);

        Label portLabel = new Label("Your Port:");
        TextField portTextField = new TextField();
        portTextField.setMinWidth(200);
        portTextField.setMaxWidth(200);

        Button doneButton = new Button("Done");

        // Set event handler for the done button
        doneButton.setOnAction(e -> {
            // Perform actions when the "Done" button is clicked in host mode
            hostName = nameTextField.getText();
            ipHost = "localhost";
            portHost = portTextField.getText();
            if (!isNumeric(portHost)) {
                // Show an alert for an invalid port number
                showAlert(Alert.AlertType.ERROR, "Invalid Port Number", "Please enter a valid port number.");
                return;
            }

            showPlayerList();

            primaryStage.close(); // Close the front page window
        });
        portTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isNumeric(newValue)) {
                errorLabel.setText("Invalid Port ! Only Numbers");
            } else {
                errorLabel.setText("");
            }
        });
        // Create the layout for host mode
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(nameLabel, nameTextField,portLabel,portTextField,errorLabel, doneButton);

        // Set the scene for host mode
        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();
    }

    private void showPlayerList() {
        Stage playerListStage = new Stage();
        playerListStage.setTitle("Player List");

        ListView<String> listView = new ListView<>();
        playersList = FXCollections.observableArrayList(hostName);
        listView.setItems(playersList);

        Button startGameButton = createStartGameButton(listView);
        if (startGameButton != null) {
            startGameButton.setOnAction(e -> showGameView());
        }

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(listView, startGameButton);

        playerListStage.setScene(new Scene(layout, 300, 300));
        playerListStage.show();

        // Update player list every 1 second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updatePlayerList(listView)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Method to update the player list
    private void updatePlayerList(ListView<String> listView) {
        // TODO: Get from server the player who connect.
        if (!playersList.contains(hostName)) {
            playersList.add(hostName);
        }

        // Ensure the number of players does not exceed 4
        while (playersList.size() > 4) {
            playersList.remove(4);
        }

        // Refresh the ListView to reflect the updated player list
        listView.refresh();
    }

    // Method to create the "Start Game" button (enabled for the host only)
    private Button createStartGameButton(ListView<String> listView) {
        if (playersList.contains(hostName)) {
            Button startGameButton = new Button("Start Game");
            return startGameButton;
        }
        return null;
    }

    // Method to show the game view
    private void showGameView() {
        Stage gameStage = new Stage();
        gameStage.setTitle("Game View");

        List<String> playersName = playersList.stream().collect(Collectors.toList());
        GameView gameView = new GameView(playersName);

        Scene scene = new Scene(gameView.getRoot(), 400, 400);
        gameStage.setScene(scene);
        gameStage.show();
    }
    private boolean isNumeric(String port) {
        if (port == null || port.isEmpty())
            return false;
        for (char c : port.toCharArray())
            if (!Character.isDigit(c))
                return false;
        return true;
    }

    private void showAlert(Alert.AlertType alertType , String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}