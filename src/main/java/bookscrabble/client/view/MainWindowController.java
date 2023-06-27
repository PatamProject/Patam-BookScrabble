package bookscrabble.client.view;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import bookscrabble.client.misc.MyLogger;
import bookscrabble.client.viewModel.ViewModel;

public class MainWindowController implements Observer, Initializable {
    static ViewModel vm;
    GameWindowController gwc;
    @FXML
    public Button startButton, exitButton, hostButton, guestButton, connectButton, goBackButton;
    @FXML
    public TextField nameTextField, hostIpTextField, hostPortTextField, serverIpTextField, serverPortTextField;
    @FXML
    public Label modelErrorLabel, viewErrorLabel, messageLabel;
    @FXML
    public TextArea playersTextArea;
    public BooleanProperty isConnectedToGame = new SimpleBooleanProperty(false);

    private final String playerJoinedMsg = " has joined the lobby!\n";

    @Override
    public void update(Observable o, Object arg) 
    {
        if(arg != null && arg.equals("playerUpdateMessage"))
        {
            if(playersTextArea != null)
                Platform.runLater(() -> playersTextArea.appendText(vm.lobbyMessage.get() + playerJoinedMsg));
        }
        else if(arg != null && arg.equals("endGame"))
        {
            if(playersTextArea != null)
                Platform.runLater(() -> playersTextArea.appendText("Game ended!\n"));
            if(vm.isHost.get())
            {
                switchRoot("HostMenu");
            }
            else
                switchRoot("GuestMenu"); 
        }
        else if(arg != null && arg.equals("gameStarted")) 
        {
            Platform.runLater(() -> gameStarted());
        }
    }

    public void setViewModel(ViewModel vm) { //  Setter for the ViewModel
        if(vm != null)
            MainWindowController.vm = vm;    
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) { // Method to set all bindings
        String path = location.getFile();
        if(path.endsWith("HostMenu.fxml") || path.endsWith("GuestMenu.fxml")) // If FXML file is HostMenu.fxml or GuestMenu.fxml
        {
            vm.myName.bind(nameTextField.textProperty());
            vm.hostPort.bind(hostPortTextField.textProperty());
            vm.hostIP.bind(hostIpTextField.textProperty());
            modelErrorLabel.textProperty().bind(vm.gameErrorMessage);
            modelErrorLabel.textProperty().bind(vm.clientErrorMessage);
            isConnectedToGame.bind(vm.isConnectedToHost);
        }

        if(path.endsWith("HostMenu.fxml")) // If FXML file is HostMenu.fxml
        {
            vm.BsIP.bind(serverIpTextField.textProperty());
            vm.BsPort.bind(serverPortTextField.textProperty());  
        }

        if(path.endsWith("GuestGameLobby.fxml") || path.endsWith("HostGameLobby.fxml"))
        {
            for (String player : vm.playersAndScoresMap.keySet()) {
                vm.lobbyMessage.set(player);
                this.update(vm, "playerUpdateMessage");
                //playersTextArea.appendText(player + playerJoinedMsg);  
            }
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
    private void hostButtonClicked(ActionEvent event) {switchRoot("HostMenu"); vm.isHost.set(true);}
    @FXML // Showing the GuestMenu
    private void guestButtonClicked(ActionEvent event) {switchRoot("GuestMenu"); vm.isHost.set(false);}
    @FXML // Showing MainMenu
    private void returnToWelcomePage(ActionEvent event) {switchRoot("Main");}

    @FXML // Creating the game lobby
    public void creatingGameLobby(ActionEvent event) {
        viewErrorLabel.setText("");
        messageLabel.setText("");

        // If the user input is legal (for host)
        if(validateUserInput(true))
        {
            hostIpTextField.setText("localhost");
            messageLabel.setText("Creating game lobby...");
            vm.setBsIP();
            vm.setBsPort();
            sendInitialInfoToModel();
            if(tryToConnect("Failed to create game lobby. Please try again.", "Game lobby created successfully.")) // If the connection is established
            {
                switchRoot("HostGameLobby");
            }
        }
    }

    @FXML // Connects the user to the host
    public void connectToHostButtonClicked(ActionEvent event) {
        viewErrorLabel.setText("");
        messageLabel.setText("");
        
        // If the user input is legal (for guest)
        if(validateUserInput(false))
        {
            messageLabel.setText("Connecting to host...");
            sendInitialInfoToModel();
            if(tryToConnect("Failed to connect to host. Please try again.", "Connected to host successfully.")) // If the connection is established
            {
                switchRoot("GuestGameLobby");
            }
        }
    }

    private boolean tryToConnect(String failedMessage, String successMessage) { // Method to establish a connection with a host or with myHostServer
        vm.startHostServer(); // Starting the host server (only works if the user is a host)
        vm.createClient(); // Cre ating a client and sending a join request to host or server
        int timeOutCounter = 0;
        while(!vm.isConnectedToHost.get())
        {
            if(timeOutCounter == 50) // If the connection times out (5 seconds)
            {
                modelErrorLabel.getText(); 
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
        while(vm.playersAndScoresMap.size() == 0) // Waiting for the host to send the player list
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        } 
        return true;
    }

    @FXML // Leaving the gameLobby and disconnecting from the game
    public void returnToGuestMenu(ActionEvent event) {
        vm.sendLeaveRequest();
        guestButtonClicked(event);
    }

    @FXML // Leaving the gameLobby and closing the game
    public void returnToHostMenu(ActionEvent event) {
        vm.sendEndgameRequest();
        hostButtonClicked(event);
    }

    @FXML // A method for the host to start the game with
    public void startGameButtonClicked(ActionEvent event) {
        if(vm.playersAndScoresMap.size() < 2)
        {
            viewErrorLabel.setText("There must be at least 2 players in the game lobby to start the game.");
            return;
        }
        playersTextArea.setText("Starting game...\n");
        //Waiting for an update from viewModel that the game has started successfully
    }

    private void gameStarted()
    {
        vm.sendStartGameRequest();
        switchRoot("GameWindow");
        gwc = MainApplication.getFxmlLoader().getController();
        gwc.displayAll();
        gwc.setViewModel(vm);
        vm.addObserver(gwc);
    }

    public static void switchRoot(String r) { // Switching between different roots
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
    }

    private boolean validateUserInput(boolean isHost)
    {
        String nameRegex = "^.{0,10}$", portRegex = "^\\d{1,5}$", ipRegex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
        if(isHost)
            if(isTextFieldLegal(nameRegex, 15 ,nameTextField) && 
            isTextFieldLegal(portRegex, 5 ,hostPortTextField, serverPortTextField) && 
            isTextFieldLegal(ipRegex, 15 , serverIpTextField))
                return true;
            else
                return false;
        else
            if(isTextFieldLegal(nameRegex, 15 ,nameTextField) && 
            isTextFieldLegal(portRegex, 5 ,hostPortTextField) && 
            isTextFieldLegal(ipRegex, 15 , hostIpTextField))
                return true;
            else
                return false;
    }

    private boolean isTextFieldLegal(String regex, int maxInputLength ,TextField...inputs)
    {
        for(TextField input : inputs)
        {
            if(input.getText().isEmpty())
            {
                viewErrorLabel.setText("Please fill in all fields.");
                return false;
            }

            if(input.getText().length() > maxInputLength)
            {
                viewErrorLabel.setText("Please only write up to " + maxInputLength + "characters.");
                return false;
            }

            if(!input.getText().matches(regex))
            {
                if(input.equals(serverIpTextField) || input.equals(hostIpTextField))
                {
                    if(input.getText().equals("localhost"))
                        return true;
                }
                viewErrorLabel.setText("Please fill in all fields correctly.");
                return false;
            }
        }
        return true;
    }
}