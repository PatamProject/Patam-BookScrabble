package bookscrabble.client.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
//import project.client.viewModel.ViewModel;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import bookscrabble.client.MainApplication;

public class MainWindowController implements Observer {
    //ViewModel vm;
    @FXML
    public Button startButton, exitButton, hostButton, guestButton, connectButton, goBackButton;
    @FXML
    public TextField nameTextField, ipTextField, portTextField, serverIpTextField, serverPortTextField;

//    public void setViewModel(ViewModel vm) { // Method to set all bindings and the ViewModel
//        this.vm = vm;
//        vm.myName.bind(nameTextField.textProperty());
//        vm.hostIP.bind(ipTextField.textProperty());
//        vm.BsIP.bind(serverIpTextField.textProperty());
//        //TODO: find solution for ports - binding between text to INTEGER
//        //...
//    }

    @FXML
    public void buttonToChooseModeClicked(ActionEvent event) {
        try {
            MainApplication.setRoot("ClientMode");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            e.printStackTrace();
        }
    }

    @FXML
    private void guestButtonClicked(ActionEvent event) { // Showing the guest menu bar
        try {
            MainApplication.setRoot("GuestMenu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void returnToWelcomePage(ActionEvent event) { // Showing the guest menu bar
        try {
            MainApplication.setRoot("Main");
        } catch (IOException e) {
            e.printStackTrace();
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