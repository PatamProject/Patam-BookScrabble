package bookscrabble.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import bookscrabble.client.model.ClientModel;
import bookscrabble.client.model.GameModel;
import bookscrabble.client.view.MainWindowController;
import bookscrabble.client.viewModel.ViewModel;

public class MainApplication extends Application {
    private static Scene scene;
    private static FXMLLoader fxmlLoader = new FXMLLoader();

    @Override
    public void start(Stage stage) throws IOException {
        GameModel gameModel = new GameModel();
        ClientModel clientModel = new ClientModel();
        ViewModel vm = new ViewModel(gameModel,clientModel);
        gameModel.addObserver(vm);
        clientModel.addObserver(vm);

        scene = new Scene(loadFXML("Main"));   
        stage.setTitle("SCRABBLE GAME");
        stage.setScene(scene);
        stage.setResizable(true);

        MainWindowController mwc = fxmlLoader.getController();
        mwc.setViewModel(vm);
        vm.addObserver(mwc);

        stage.show();
    }
    
    private static Parent loadFXML(String fxml) throws IOException {
        fxmlLoader = new FXMLLoader(App.class.getResource("/bookscrabble/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static String getRoot() {
        return fxmlLoader.getLocation().getPath();
    }

    public static void main(String[] args) {
        launch();
    }
}