package bookscrabble.client;

import bookscrabble.client.view.GameWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class PlayerScreenApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader();
        BorderPane root = fxmlLoader.load(getClass().getResource("PlayerScreen.fxml").openStream());
        GameWindowController playerScreenController = fxmlLoader.getController();
        playerScreenController.displayAll();
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Scrabble Game");
        stage.setScene(scene);
        stage.show();
    }
}