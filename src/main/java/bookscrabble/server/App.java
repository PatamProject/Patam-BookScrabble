package bookscrabble.server;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import bookscrabble.server.serverHandler.BookScrabbleHandler;
import bookscrabble.server.serverHandler.MyServer;

public class App extends Application {
    MyServer myServer;
    public static TextArea textArea;
    private TextField portTextField;
    private Button startButton;
    private Button stopButton;

    public static void main(String[] args) { //Used to run the server with a GUI
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create UI controls
        Label portLabel = new Label("Server Port:");
        portTextField = new TextField();
        portTextField.setText("5555");
        
        textArea = new TextArea();
        textArea.setEditable(false);
        startButton = new Button("Start");
        stopButton = new Button("Stop");
        stopButton.setDisable(true); // Initially disable the stop button

        // Set event handlers
        startButton.setOnAction(event -> startServer());
        stopButton.setOnAction(event -> stopServer());

        // Arrange UI controls in layout
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.add(portLabel, 0, 0);
        gridPane.add(portTextField, 1, 0);
        gridPane.add(startButton, 0, 1);
        gridPane.add(stopButton, 1, 1);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(gridPane, textArea);

        // Create the scene and set it on the stage
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.setTitle("BookScrabble Server");
        primaryStage.setOnCloseRequest(event -> stopServer()); // Stop the server when the application is closed
        primaryStage.show();
    }

    private void startServer() {
        int port = 0;
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        String portText = portTextField.getText();
        if(portText.isEmpty()) {
            alert.setContentText("Port number cannot be empty");
            alert.showAndWait();
            return;
        }

        if(!portText.matches("^\\d{1,5}$")) { //Only allow 5 digits
            alert.setContentText("Port number cannot contain letters or is too long");
            alert.showAndWait();
            return;
        }

        port = Integer.parseInt(portText);
        if(port <= 0 || port > 65535) {
            alert.setContentText("Invalid port number, must be between 0 and 65535");
            alert.showAndWait();
            return;
        }
        myServer = new MyServer(port,new BookScrabbleHandler()); //Local server
        App.write("Lunching server...");
        myServer.start();

        // Update UI controls
        portTextField.setDisable(true);
        startButton.setDisable(true);
        try {
            Thread.sleep(1500); // Wait for 1.5 seconds to make sure the server is running
        } catch (InterruptedException e) {}
        stopButton.setDisable(false);
    }

    public static void write(String text){textArea.appendText(text + "\n");}

    private void stopServer() {
        textArea.appendText("Stopping server...\n");
        if(myServer != null)
        {
            myServer.close();
            myServer = null;
        }

        // Update UI controls
        portTextField.setDisable(false);
        stopButton.setDisable(true);
        try {
            Thread.sleep(1500); // Wait for 1.5 seconds to make sure the server is closed
        } catch (InterruptedException e) {}
        startButton.setDisable(false);
    }
}
