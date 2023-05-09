module project {
    requires javafx.controls;
    requires javafx.fxml;

    opens project.client to javafx.fxml;
    exports project.client;
}
