package project.client.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import project.client.model.ClientModel;
import project.client.model.GameModel;
import project.client.viewModel.ViewModel;

import java.io.IOException;
import java.util.Objects;

public class ClientModeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        GameModel gameModel = new GameModel();
        ClientModel clientModel = new ClientModel();
        ViewModel vm = new ViewModel(gameModel, clientModel);
        gameModel.addObserver(vm);
        clientModel.addObserver(vm);

        FXMLLoader fxmlLoader = new FXMLLoader(ClientModeApplication.class.getResource("ClientMode.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("SCRABBLE GAME");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }



//    //Model
//    GameModel gameModel = new GameModel();
//    ClientModel clientModel = new ClientModel();
//
//    ViewModel vm = new ViewModel(gameModel, clientModel); //ViewModel
//        gameModel.addObserver(vm);
//        clientModel.addObserver(vm);
//
//    FXMLLoader fxmlLoader = new FXMLLoader();
//    BorderPane root = fxmlLoader.load(Objects.requireNonNull(getClass().getResource("ClientMode.fxml")).openStream());
//    MainWindowController mwc = fxmlLoader.getController(); // View
//        mwc.setViewModel(vm);
//        vm.addObserver(mwc);
//
//    Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        stage.setTitle("SCRABBLE GAME");
//        stage.setScene(scene);
//        stage.show();
}