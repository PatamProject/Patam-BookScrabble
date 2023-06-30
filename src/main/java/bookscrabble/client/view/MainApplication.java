package bookscrabble.client.view;


import bookscrabble.client.misc.MyLogger;
import bookscrabble.client.model.ClientModel;
import bookscrabble.client.model.GameModel;
import bookscrabble.client.viewModel.ViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application { //Used to run the game in a GUI
    private static Scene scene;
    private static FXMLLoader fxmlLoader = new FXMLLoader();
    private static ViewModel vm;
    private static ClientModel cm;
    private static GameModel gm;
    private static MainWindowController mwc;

    public static void main(String[] args) {launch();}

    @Override
    public void start(Stage stage) throws IOException {
        gm = GameModel.getGameModel();
        cm = ClientModel.getClientModel();
        vm = new ViewModel(gm,cm);
        gm.addObserver(vm);
        cm.addObserver(vm);

        scene = new Scene(loadFXML("Main"));
        stage.setTitle("SCRABBLE GAME");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(1200);
        stage.setMinHeight(800);
        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        mwc = fxmlLoader.getController();
        mwc.setViewModel(vm);
        vm.addObserver(mwc);
        new Thread(()-> mwc.getMyIPAddress()).start();

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
    public static String getRoot() {return fxmlLoader.getLocation().getPath();}
    public static FXMLLoader getFxmlLoader() {return fxmlLoader;}
}