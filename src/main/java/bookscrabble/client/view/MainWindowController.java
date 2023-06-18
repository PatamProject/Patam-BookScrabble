package bookscrabble.client.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import bookscrabble.client.MainApplication;
import bookscrabble.client.MyLogger;
import bookscrabble.client.viewModel.ViewModel;

public class MainWindowController implements Observer {
    ViewModel vm;
    @FXML
    public Button startButton, exitButton, hostButton, guestButton, connectButton, goBackButton;
    @FXML
    public TextField nameTextField, hostIpTextField, hostPortTextField, serverIpTextField, serverPortTextField;
    @FXML
    public Label errorLabel;

    public void setViewModel(ViewModel vm) { // Method to set all bindings and the ViewModel
        if(vm != null)
            this.vm = vm;
            
        if(MainApplication.getRoot().equals("ClientMode"))
            vm.isHost.bind(Bindings.when(hostButton.pressedProperty()).then(true).otherwise(false));

        if(MainApplication.getRoot().equals("HostMenu") || MainApplication.getRoot().equals("GuestMenu"))
        {
            vm.myName.bind(nameTextField.textProperty());
            vm.hostPort.bind(hostPortTextField.textProperty());
            vm.hostIP.bind(hostIpTextField.textProperty());
        }

        if(MainApplication.getRoot().equals("HostMenu"))
        {
            vm.BsIP.bind(serverIpTextField.textProperty());
            vm.BsPort.bind(serverPortTextField.textProperty());  
        }
    }

    @FXML
    public void buttonToChooseModeClicked(ActionEvent event) {
        try {
            MainApplication.setRoot("ClientMode");
        } catch (IOException e) {
            MyLogger.logError(e.getMessage());
        }
        setViewModel(this.vm);
    }

    @FXML
    public void exitButtonClicked(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void hostButtonClicked(ActionEvent event) { // Showing the host menu bar
        try {
            MainApplication.setRoot("HostMenu");
        } catch (IOException e) {
            MyLogger.logError(e.getMessage());
        }
        setViewModel(this.vm);
    }

    @FXML
    private void guestButtonClicked(ActionEvent event) { // Showing the guest menu bar
        try {
            MainApplication.setRoot("GuestMenu");
        } catch (IOException e) {
            MyLogger.logError(e.getMessage());
        }
        setViewModel(this.vm);
    }

    @FXML
    private void returnToWelcomePage(ActionEvent event) { // Showing the guest menu bar
        try {
            MainApplication.setRoot("Main");
        } catch (IOException e) {
            MyLogger.logError(e.getMessage());
        }
    }

    @FXML
    public void creatingGameLobby(ActionEvent event) { // Creating the game lobby
        if (nameTextField.getText().isEmpty() || hostPortTextField.getText().isEmpty() || serverIpTextField.getText().isEmpty() || serverPortTextField.getText().isEmpty())
            errorLabel.setText("Please fill in all fields.");
        else {
            vm.setBsIP();
            vm.setBsPort();
            sendInitialInfoToModel();
        }
    }

    @FXML
    public void connectToHostButtonClicked(ActionEvent event) { // connects the user to the host
        if (nameTextField.getText().isEmpty() || hostIpTextField.getText().isEmpty() || hostPortTextField.getText().isEmpty())
            errorLabel.setText("Please fill in all fields.");
        else {
            hostIpTextField.setText("localhost");
            sendInitialInfoToModel();
        }
    }

    @Override
    public void update(Observable o, Object arg) {} //Empty update method

    private void sendInitialInfoToModel() {
        vm.setMyName();
        vm.setIfHost();
        vm.setHostIP();
        vm.setHostPort();
        vm.createClient();
    }
}