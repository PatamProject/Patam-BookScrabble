package bookscrabble.client.view;

import bookscrabble.client.misc.MyLogger;
import bookscrabble.client.model.ClientModel;
import bookscrabble.client.viewModel.ViewModel;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public Label modelErrorLabel, viewErrorLabel, messageLabel, myPort, myIP;
    @FXML
    public TextArea playersTextArea;

    public BooleanProperty isConnectedToGame = new SimpleBooleanProperty(false);
    public static volatile StringBuilder externalIP = new StringBuilder();
    //private static volatile AtomicBoolean isGameStarted = new AtomicBoolean(false);
    @Override
    public void update(Observable o, Object arg) 
    {
        if(arg != null && arg.equals("endGame"))
        {
            try {
                Thread.sleep(1500); // Allow user to read message
                switchRoot(vm.isHost.get() ? "HostMenu" : "GuestMenu");
            } catch (Exception e) {}
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
            if(viewErrorLabel != null)
                viewErrorLabel.setText("");
        }

        if(path.endsWith("HostMenu.fxml")) // If FXML file is HostMenu.fxml
        {
            vm.BsIP.bind(serverIpTextField.textProperty());
            vm.BsPort.bind(serverPortTextField.textProperty());  
        }

        if(path.endsWith("GuestGameLobby.fxml") || path.endsWith("HostGameLobby.fxml"))
        {
            //Binding vm.isGameRunning to gameStarted() method
            vm.isGameRunning.addListener((observable, oldValue, newValue) -> {
                if(newValue)
                    Platform.runLater(() -> {
                        //isGameStarted.set(true); 
                        gameStarted();
                    });
            });
            //Whenever vm.lobbyMessage is changed, the playersTextArea is appended with the new message
            playersTextArea.textProperty().set("Welcome to the lobby!\n");
            vm.lobbyMessage.addListener((observable, oldValue, newValue) -> {
                if(newValue != null && newValue != oldValue)
                    Platform.runLater(() -> {
                        if(playersTextArea != null)
                            playersTextArea.appendText(newValue);
                    });
            });

            for (String player : vm.playersAndScoresMap.keySet())
                vm.lobbyMessage.set(player + " joined the lobby!\n");  
        }

        if (path.endsWith("HostGameLobby.fxml")) {
            //externalIP = getMyIPAddress();
            myIP.setText("Your IP is: " + externalIP);
            myPort.setText("Your port is: " + vm.hostPort.get());
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
    private void hostButtonClicked(ActionEvent event) {vm.isHost.set(true); switchRoot("HostMenu"); }
    @FXML // Showing the GuestMenu
    private void guestButtonClicked(ActionEvent event) { vm.isHost.set(false); switchRoot("GuestMenu");}
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
                switchRoot("HostGameLobby");
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
                switchRoot("GuestGameLobby");
        }
    }

    private boolean tryToConnect(String failedMessage, String successMessage) { // Method to establish a connection with a host or with myHostServer
        vm.startHostServer(); // Starting the host server (only works if the user is a host)
        vm.createClient(); // Creating a client and sending a join request to host or server
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
        while(vm.playersAndScoresMap != null && vm.playersAndScoresMap.size() == 0) // Waiting for the host to send the player list
        {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        } 
        return true;
    }

    public void getMyIPAddress() { // Method to get the external IP of the user on a seperated thread
        int counter = 0;
        while (externalIP.length() == 0 && counter < 100)
        {
            URL url;
            BufferedReader reader;
            try {
                url = new URL("https://api.ipify.org/?format=text");
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                externalIP.append(reader.readLine());
                reader.close();
            } catch (IOException e) {
                counter++;
                if (counter == 99) {
                    MyLogger.logError("Problem with returning my IP address: " + e.getMessage());
                    break;
                }
            }
        }
    }

    @FXML // Leaving the gameLobby and disconnecting from the game
    public void returnToGuestMenu(ActionEvent event) {
        if(playersTextArea != null)
            playersTextArea.clear();
        vm.sendLeaveRequest();
        guestButtonClicked(event);
    }

    @FXML // Leaving the gameLobby and closing the game
    public void returnToHostMenu(ActionEvent event) {
        if(playersTextArea != null)
            playersTextArea.clear();
        ClientModel.isGameRunning = true; // Closes the lobby for all other players
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
        vm.sendStartGameRequest();
    }

    private void gameStarted()
    {
        ClientModel.isGameRunning = true;
        switchRoot("GameWindow");
        gwc = MainApplication.getFxmlLoader().getController();
        //gwc.setViewModel(vm); //Happens when loading the fxml
        vm.addObserver(gwc);
        vm.isGameRunning.set(true);
        gwc.insertImage(); //Inserts tiles to the rack for the first time
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
            return isTextFieldLegal(nameRegex, 15, nameTextField) &&
                    isTextFieldLegal(portRegex, 5, hostPortTextField, serverPortTextField) &&
                    isTextFieldLegal(ipRegex, 15, serverIpTextField);
        else
            return isTextFieldLegal(nameRegex, 15, nameTextField) &&
                    isTextFieldLegal(portRegex, 5, hostPortTextField) &&
                    isTextFieldLegal(ipRegex, 15, hostIpTextField);
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
                viewErrorLabel.setText("Please only write up to " + maxInputLength + " characters.");
                return false;
            }

            if(!input.getText().matches(regex))
            {
                if(input.equals(serverIpTextField) || input.equals(hostIpTextField))
                    if(input.getText().equals("localhost"))
                        return true;
                viewErrorLabel.setText("Please fill in all fields correctly.");
                return false;
            }
        }
        return true;
    }
}