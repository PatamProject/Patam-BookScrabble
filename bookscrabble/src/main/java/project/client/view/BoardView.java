package project.client.view;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BoardView extends Application {

    private static final int GRID_SIZE = 15;
    private static final double CELL_SIZE = 55;
    int IDrow=0,IDcol=0;

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();
        GridPane gridPane = getView();
        pane.getChildren().add(gridPane);
        primaryStage.setScene(new Scene(pane, 800, 600));
        primaryStage.setTitle("Scrabble Board");
        primaryStage.show();
    }

    public GridPane getView() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(1);
        gridPane.setVgap(1);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Rectangle cell = createCell();
                gridPane.add(cell, col, row);
            }
        }
        finishBoard(gridPane);
        return gridPane;
    }

    private Rectangle createCell() {
        Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
        cell.setFill(Color.GREEN);
        cell.setStroke(Color.BLACK);
        cell.setId("Rec:" + IDrow + ":" + IDcol);
        IDcol++;
        if (IDcol == 14) {
            IDrow++;
            IDcol = 0;
        }
        return cell;
    }

    private void finishBoard(GridPane gridPane)
    {
        setColorAndText(gridPane, 0, 0, Color.RED, "TRIPLE\nWORD\nSCORE"); setColorAndText(gridPane, 0, 3, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 0, 7, Color.RED, "TRIPLE\nWORD\nSCORE"); setColorAndText(gridPane, 0, 11, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 0, 14, Color.RED, "TRIPLE\nWORD\nSCORE");
        setColorAndText(gridPane, 1, 1, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 1, 5, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 1, 9, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 1,13, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText(gridPane, 2, 2, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 2, 6, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 2, 8, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 2, 12, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText(gridPane, 3, 0, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 3, 3, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 3, 7, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 3, 11, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 3, 14, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText(gridPane, 4, 4, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 4, 10, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText(gridPane, 5, 1, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 5, 5, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 5, 9, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 5, 13, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText(gridPane, 6, 2, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 6, 6, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 6, 8, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 6, 12, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText(gridPane, 7, 0, Color.RED, "TRIPLE\nWORD\nSCORE"); setColorAndText(gridPane, 7, 3, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 7, 7, Color.YELLOW, ""); setColorAndText(gridPane, 7, 11, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 7, 14, Color.RED, "TRIPLE\nWORD\nSCORE");
        setColorAndText(gridPane, 8, 2, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 8, 6, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 8, 8, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 8, 12, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText(gridPane, 9, 1, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 9, 5, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 9, 9, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 9, 13, Color.BLUE, "TRIPLE\nLETTER\nSCORE");
        setColorAndText(gridPane, 10, 4, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 10, 10, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText(gridPane, 11, 0, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 11, 3, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 11, 7, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 11, 11, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 11, 14, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE");
        setColorAndText(gridPane, 12, 2, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 12, 6, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 12, 8, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 12, 12, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText(gridPane, 13, 1, Color.YELLOW, "DOUBLE\nWORD\nSCORE"); setColorAndText(gridPane, 13, 5, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 13, 9, Color.BLUE, "TRIPLE\nLETTER\nSCORE"); setColorAndText(gridPane, 13,13, Color.YELLOW, "DOUBLE\nWORD\nSCORE");
        setColorAndText(gridPane, 14, 0, Color.RED, "TRIPLE\nWORD\nSCORE"); setColorAndText(gridPane, 14, 3, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 14, 7, Color.RED, "TRIPLE\nWORD\nSCORE"); setColorAndText(gridPane, 14, 11, Color.DEEPSKYBLUE, "DOUBLE\nLETTER\nSCORE"); setColorAndText(gridPane, 14, 14, Color.RED, "TRIPLE\nWORD\nSCORE");
    }

    private void setColorAndText(GridPane gridPane, int row, int col, Color color, String text) {
        Node node = getNodeByRowColumnIndex(row, col, gridPane);
        if (node instanceof Rectangle) {
            Rectangle cell = (Rectangle) node;
            cell.setFill(color);
            cell.setStroke(Color.BLACK);

            if (row == 7 && col == 7) {
                // Create star shape
                Shape star = createStar();
                star.setFill(Color.BLACK);  // Set the star color to black
                star.setStroke(null);
                cell.setFill(Color.YELLOW);  // Set the background color of the rectangle to yellow
                StackPane stackPane = new StackPane(cell, star);
                gridPane.add(stackPane, col, row);
            } else {
                if (text != null && !text.isEmpty()) {
                    Text textNode = new Text(text);
                    textNode.setFont(Font.font("Arial", 12));
                    textNode.setFill(Color.BLACK);
                    StackPane stackPane = new StackPane(cell, textNode);
                    gridPane.add(stackPane, col, row);
                } else {
                    gridPane.add(cell, col, row);
                }
            }
        }
    }

    private Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                return node;
            }
        }
        return null;
    }

    private Path createStar() {
        double centerX = CELL_SIZE / 2;
        double centerY = CELL_SIZE / 2;
        double outerRadius = CELL_SIZE / 2 - 2;
        double innerRadius = outerRadius * 0.382; // Approximate inner radius ratio for a nice star shape
        Path star = new Path();
        double angle = Math.PI / 2; // Starting angle
        double angleIncrement = Math.PI * 2 / 5; // Angle increment for each point
        double[] points = new double[10];
        for (int i = 0; i < 10; i++) {
            double radius = i % 2 == 0 ? outerRadius : innerRadius;
            points[i] = centerX + radius * Math.cos(angle);
            points[++i] = centerY - radius * Math.sin(angle);
            angle += angleIncrement;
        }
        star.getElements().addAll(new MoveTo(points[0], points[1]),
                new LineTo(points[2], points[3]),
                new LineTo(points[4], points[5]),
                new LineTo(points[6], points[7]),
                new LineTo(points[8], points[9]),
                new ClosePath());
        star.setFill(Color.YELLOW);
        star.setStroke(Color.BLACK);
        return star;
    }

    public static void main(String[] args) {
        launch(args);
    }
}