package project.client.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import project.client.viewModel.ViewModel;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class MainWindowController implements Observer {
    ViewModel vm;
    @FXML
    public Button hostButton, guestButton, connectButton, goBackButton;
    @FXML
    public TextField nameTextField, ipTextField, portTextField, serverIpTextField, serverPortTextField;

    public void setViewModel(ViewModel vm) { // Method to set all bindings and the ViewModel
        this.vm = vm;
        vm.myName.bind(nameTextField.textProperty());
        vm.hostIP.bind(ipTextField.textProperty());
        vm.BsIP.bind(serverIpTextField.textProperty());
        //TODO: find solution for ports - binding between text to INTEGER
        //...
    }

    @FXML
    private void hostButtonClicked(ActionEvent event) { // Showing the host menu bar
        //ipTextField.setText("localhost"); //TODO: why it's null?
        try { // Load and display HostMenu.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HostMenu.fxml"));
            Parent hostMenu = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Get the main stage

            // Show HostMenu.fxml in a new scene
            Scene scene = new Scene(hostMenu);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void hostConnectButtonClicked(ActionEvent actionEvent) { // Creating the game lobby
    }

    @FXML
    private void guestButtonClicked(ActionEvent event) { // Showing the guest menu bar
        try { // Load and display GuestMenu.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GuestMenu.fxml"));
            Parent guestMenu = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Get the main stage

            // Show GuestMenu.fxml in a new scene
            Scene scene = new Scene(guestMenu);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void guestConnectButtonClicked(ActionEvent event) { // connects the user to the host
    }

    @FXML
    public void goBackButtonClicked(ActionEvent event) { // Returns the user to the guest/host menu
        try { // Load and display ClientMode.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientMode.fxml"));
            Parent clientMode = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // Get the main stage

            // Show ClientMode.fxml in a new scene
            Scene scene = new Scene(clientMode);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Observable o, Object arg) {} //Empty update method
}