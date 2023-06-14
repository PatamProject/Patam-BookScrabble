module project {
    requires javafx.controls;
    requires javafx.fxml;

    opens project.client.view to Board.fxml;
    exports project.client.view;
}