package bookscrabble.client;


import bookscrabble.client.model.ClientModel;
import bookscrabble.client.model.GameModel;
import bookscrabble.client.view.MainWindowController;
import bookscrabble.client.viewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    private static Scene scene;
    private static FXMLLoader fxmlLoader = new FXMLLoader();
    static ViewModel vm;

    public static void main(String[] args) {launch();}

    @Override
    public void start(Stage stage) throws IOException {
        GameModel gameModel = new GameModel();
        ClientModel clientModel = new ClientModel();
        vm = new ViewModel(gameModel,clientModel);
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

    private static Parent loadFXML(String fxml) {
        fxmlLoader = new FXMLLoader(MainApplication.class.getResource("/bookscrabble/fxml/" + fxml + ".fxml"));
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            MyLogger.logError("Error loading fxml file: " + e.getMessage());
        }
        return null;
    }

    public static void setRoot(String fxml) throws IOException {scene.setRoot(loadFXML(fxml));}
    public static String getRoot() {
        return fxmlLoader.getLocation().getPath();
    }
    public static FXMLLoader getFxmlLoader() {return fxmlLoader;}
}