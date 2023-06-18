package bookscrabble.client.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
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
    public TextField nameTextField, ipTextField, portTextField, serverIpTextField, serverPortTextField;

    public void setViewModel(ViewModel vm) { // Method to set all bindings and the ViewModel
        this.vm = vm;
        if(MainApplication.getRoot().equals("ClientMode"))
            vm.isHost.bind(Bindings.when(hostButton.pressedProperty()).then(true).otherwise(false));

        if(MainApplication.getRoot().equals("HostMenu") || MainApplication.getRoot().equals("GuestMenu"))
        {
            vm.myName.bind(nameTextField.textProperty());
            vm.hostPort.bind(portTextField.textProperty());
            vm.hostIP.bind(ipTextField.textProperty());
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
    public void hostConnectButtonClicked(ActionEvent event) { // Creating the game lobby
    }

    @FXML
    public void guestConnectButtonClicked(ActionEvent event) { // connects the user to the host
    }

    @Override
    public void update(Observable o, Object arg) {} //Empty update method
}