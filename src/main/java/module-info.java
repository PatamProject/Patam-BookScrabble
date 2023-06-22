module bookscrabble {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens bookscrabble.client.view to javafx.fxml;
    exports bookscrabble.client;
    exports bookscrabble.client.view;
    exports bookscrabble.client.viewModel;
    opens bookscrabble.client to javafx.fxml;
}
