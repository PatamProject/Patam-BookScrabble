package bookscrabble.client.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import bookscrabble.client.MainApplication;
import bookscrabble.client.MyLogger;
import bookscrabble.client.viewModel.ViewModel;

public class MainWindowController implements Observer {
    static ViewModel vm;
    @FXML
    public Button startButton, exitButton, hostButton, guestButton, connectButton, goBackButton;
    @FXML
    public TextField nameTextField, hostIpTextField, hostPortTextField, serverIpTextField, serverPortTextField;
    @FXML
    public Label errorLabel;

    public void setViewModel(ViewModel vm) { // Method to set all bindings and the ViewModel
        if(vm != null)
            MainWindowController.vm = vm;
            
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

    @FXML // Closing the app
    public void exitButtonClicked(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML // Showing the ModeMenu
    public void chooseModeMenu(ActionEvent event) {switchRoot("ClientMode"); setViewModel(this.vm);}
    @FXML // Showing the HostMenu
    private void hostButtonClicked(ActionEvent event) {switchRoot("HostMenu"); setViewModel(this.vm);}
    @FXML // Showing the GuestMenu
    private void guestButtonClicked(ActionEvent event) {switchRoot("GuestMenu"); setViewModel(this.vm);}
    @FXML // Showing MainMenu
    private void returnToWelcomePage(ActionEvent event) {switchRoot("Main");}

    @FXML // Creating the game lobby
    public void creatingGameLobby(ActionEvent event) {
        if (nameTextField.getText().isEmpty() || hostPortTextField.getText().isEmpty() || serverIpTextField.getText().isEmpty() || serverPortTextField.getText().isEmpty())
            errorLabel.setText("Please fill in all fields."); // Preventing empty field
        else {
            vm.setBsIP();
            vm.setBsPort();
            sendInitialInfoToModel();
        }
    }

    @FXML // Connects the user to the host
    public void connectToHostButtonClicked(ActionEvent event) {
        if (nameTextField.getText().isEmpty() || hostIpTextField.getText().isEmpty() || hostPortTextField.getText().isEmpty())
            errorLabel.setText("Please fill in all fields."); // Preventing empty field
        else {
            hostIpTextField.setText("localhost");
            sendInitialInfoToModel();
        }
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