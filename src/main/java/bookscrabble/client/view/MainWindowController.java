package bookscrabble.client.view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import bookscrabble.client.MainApplication;
import bookscrabble.client.MyLogger;
import bookscrabble.client.viewModel.ViewModel;

public class MainWindowController implements Observer, Initializable {
    static ViewModel vm;
    @FXML
    public Button startButton, exitButton, hostButton, guestButton, connectButton, goBackButton;
    @FXML
    public TextField nameTextField, hostIpTextField, hostPortTextField, serverIpTextField, serverPortTextField;
    @FXML
    public Label modelErrorLabel, viewErrorLabel, messageLabel;
    public BooleanProperty isConnectedToGame = new SimpleBooleanProperty(false);

    public void setViewModel(ViewModel vm) { // Method to set all bindings and the ViewModel
        if(vm != null)
            MainWindowController.vm = vm;    
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String path = location.getFile();
        if(path.endsWith("ClientMode.fxml")) // if FXML file is ClientMode.fxml
            vm.isHost.bind(Bindings.when(hostButton.pressedProperty()).then(true).otherwise(false)); //TODO: find why isHost is false when I am the host
        if(path.endsWith("HostMenu.fxml") || path.endsWith("GuestMenu.fxml")) // if FXML file is HostMenu.fxml or GuestMenu.fxml
        {
            vm.myName.bind(nameTextField.textProperty());
            vm.hostPort.bind(hostPortTextField.textProperty());
            vm.hostIP.bind(hostIpTextField.textProperty());
            modelErrorLabel.textProperty().bind(vm.errorMessage);
            isConnectedToGame.bind(vm.isConnectedToHost);
        }

        if(path.endsWith("HostMenu.fxml")) // if FXML file is HostMenu.fxml
        {
            vm.BsIP.bind(serverIpTextField.textProperty());
            vm.BsPort.bind(serverPortTextField.textProperty());  
        }
    }

    @FXML // Closing the app
    public void exitButtonClicked(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML // Showing the ModeMenu
    public void chooseModeMenu(ActionEvent event) {switchRoot("ClientMode");}
    @FXML // Showing the HostMenu
    private void hostButtonClicked(ActionEvent event) {switchRoot("HostMenu");}
    @FXML // Showing the GuestMenu
    private void guestButtonClicked(ActionEvent event) {switchRoot("GuestMenu");}
    @FXML // Showing MainMenu
    private void returnToWelcomePage(ActionEvent event) {switchRoot("Main");}

    @FXML // Creating the game lobby
    public void creatingGameLobby(ActionEvent event) {
        viewErrorLabel.setText("");
        messageLabel.setText("");
        if (nameTextField.getText().isEmpty() || hostPortTextField.getText().isEmpty() || serverIpTextField.getText().isEmpty() || serverPortTextField.getText().isEmpty())
            viewErrorLabel.setText("Please fill in all fields."); // Preventing empty field
        else {
            hostIpTextField.setText("localhost");
            messageLabel.setText("Creating game lobby...");
            vm.setBsIP();
            vm.setBsPort();
            sendInitialInfoToModel();
            if(tryToConnect("Failed to create game lobby. Please try again.", "Game lobby created successfully.")) // If the connection is established
                switchRoot("GameLobby");
        }
    }

    @FXML // Connects the user to the host
    public void connectToHostButtonClicked(ActionEvent event) {
        viewErrorLabel.setText("");
        messageLabel.setText("");
        if (nameTextField.getText().isEmpty() || hostIpTextField.getText().isEmpty() || hostPortTextField.getText().isEmpty())
            viewErrorLabel.setText("Please fill in all fields."); // Preventing empty field
        else {
            messageLabel.setText("Connecting to host...");
            sendInitialInfoToModel();
            if(tryToConnect("Failed to connect to host. Please try again.", "Connected to host successfully.")) // If the connection is established
                switchRoot("GameLobby");
        }
    }

    private boolean tryToConnect(String failedMessage, String successMessage) {
        int timeOutCounter = 0;
        while(!vm.isConnectedToHost.get())
        { //TODO: find why timeOutCounter reaches to 50 and connection is not established
            if(timeOutCounter == 50) // If the connection times out (5 seconds)
            {
                modelErrorLabel.getText(); //TODO: why it's null?
                viewErrorLabel.setText(failedMessage);
                return false;
            }

            try {
                timeOutCounter++;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                MyLogger.logError(e.getMessage());
            }
        } // Waiting for the connection to be established
        viewErrorLabel.setText(successMessage);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
        return true;
    }

    @Override
    public void update(Observable o, Object arg) {} // Empty update method

    private void switchRoot(String r) { // Switching between different roots
        try {
            MainApplication.setRoot(r);
        } catch (IOException e) {
            MyLogger.logError(e.getMessage());
        }
    }

    private void sendInitialInfoToModel() { // Method to update the model with the user input
        vm.setMyName();
        vm.setIfHost();
        vm.setHostIP();
        vm.setHostPort();
        vm.createClient();
    }
}