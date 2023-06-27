module bookscrabble {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;

    opens bookscrabble.client.view to javafx.fxml;
    exports bookscrabble.client;
    exports bookscrabble.client.model;
    exports bookscrabble.client.view;
    exports bookscrabble.client.viewModel;
    exports bookscrabble.server;
    opens bookscrabble.client to javafx.fxml;
}
