module bookscrabble {
    requires javafx.controls;
    requires javafx.fxml;

    opens bookscrabble.client.view to javafx.fxml;
    exports bookscrabble.client;
    exports bookscrabble.client.view;
}
