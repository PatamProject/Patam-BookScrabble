package project.client.view;//package project.client.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class GuestMode {
    private Stage primaryStage;
    private String guestName , hostIP , hostPort;

    public void showGuestMode() {
        primaryStage = new Stage();
        primaryStage.setTitle("Guest Mode");

        Label errorIPLabel = new Label();
        errorIPLabel.setStyle("-fx-text-fill: red");
        Label errorPortLabel = new Label();
        errorPortLabel.setStyle("-fx-text-fill: red");

        Label nameLabel = new Label("Name:");
        TextField nameTextField = new TextField();
        nameTextField.setMinWidth(200);
        nameTextField.setMaxWidth(200);

        Label ipLabel = new Label("Enter Host IP:");
        TextField ipTextField = new TextField();
        ipTextField.setMinWidth(200);
        ipTextField.setMaxWidth(200);

        Label portLabel = new Label("Enter Host Port:");
        TextField portTextField = new TextField();
        portTextField.setMinWidth(200);
        portTextField.setMaxWidth(200);

        Button doneButton = new Button("Done");

        // Set event handler for the done button
        doneButton.setOnAction(e -> {
            // Perform actions when the "Done" button is clicked in guest mode
            guestName = nameTextField.getText();
            hostIP = ipTextField.getText();
            hostPort = portTextField.getText();

            if (!isNumeric(hostIP,false) || !isNumeric(hostPort,true)) {
                // Show an alert for an invalid port number
                showAlert(Alert.AlertType.ERROR, "Invalid Port Number", "Please enter a valid port number.");
                return;
            }

            primaryStage.close(); // Close the guest mode window
            showPlayerList(); // Show the player list
        });
        ipTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isNumeric(newValue,false)) {
                errorIPLabel.setText("Invalid IP ! Only Numbers");
            } else {
                errorIPLabel.setText("");
            }
        });
        portTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isNumeric(newValue,true)) {
                errorPortLabel.setText("Invalid Port ! Only Numbers");
            } else {
                errorPortLabel.setText("");
            }
        });
        // Create the layout for guest mode
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(nameLabel, nameTextField, ipLabel, ipTextField,portLabel,portTextField,errorIPLabel,errorPortLabel, doneButton);

        // Set the scene for guest mode
        primaryStage.setScene(new Scene(layout, 300, 200));
        primaryStage.show();
    }

    private void showPlayerList() {
        Stage playerListStage = new Stage();
        playerListStage.setTitle("Player List");

        // TODO: Retrieve the list of players from the server
        // For now, let's assume a static list of players
        ListView<String> listView = new ListView<>();
        ObservableList<String> players = FXCollections.observableArrayList("Player 1", "Player 2", "Player 3");
        listView.setItems(players);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(listView);

        playerListStage.setScene(new Scene(layout, 300, 200));
        playerListStage.show();
    }

    private boolean isNumeric(String port, boolean isPort) {
        if (port == null || port.isEmpty())
            return false;
        for (char c : port.toCharArray())
        {
            if(isPort)
            {
                if (!Character.isDigit(c))
                    return false;
            }
            else
            {
                if (!Character.isDigit(c) && (c != '.'))
                    return false;
            }
        }
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
