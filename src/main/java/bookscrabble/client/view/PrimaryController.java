package bookscrabble.client.view;

import java.io.IOException;

import bookscrabble.client.App;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}
