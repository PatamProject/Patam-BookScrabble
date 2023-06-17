module project.client.view {
    requires javafx.controls;
    requires javafx.fxml;

    opens project.client.view to javafx.fxml;
    exports project.client.view;
}