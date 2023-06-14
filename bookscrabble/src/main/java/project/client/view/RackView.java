package project.client.view;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RackView implements Serializable {

    private static final int SQUARE_SIZE = 55;
    private static final int RACK_SIZE = SQUARE_SIZE * 7;
    private static final Color SQUARE_COLOR = Color.LIGHTBLUE;
    private static final Color RACK_COLOR = Color.BLACK;

    private static final DataFormat RACK_FORMAT = new DataFormat("project.client.view.RackView");

    private List<StackPane> squarePanes;
    private StackPane rackPane;
    double mouseX,mouseY;

    public RackView() {

        createRack();
    }

    private void createRack() {
        squarePanes = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            StackPane squarePane = createSquare(i);
            squarePanes.add(squarePane);

        }

        HBox squaresContainer = new HBox();
        squaresContainer.setAlignment(Pos.CENTER);
        squaresContainer.setSpacing(5);
        squaresContainer.getChildren().addAll(squarePanes);

        rackPane = new StackPane(squaresContainer);
        rackPane.setAlignment(Pos.CENTER);
        rackPane.setPrefSize(RACK_SIZE, SQUARE_SIZE);
        rackPane.setStyle("-fx-background-color: " + colorRGB(RACK_COLOR) + ";");
        makeDraggable(rackPane);
    }

    private StackPane createSquare(int id) {
        StackPane squarePane = new StackPane();
        squarePane.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);

        Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
        square.setFill(SQUARE_COLOR);
        square.setStroke(Color.BLACK);
        square.setId(Integer.toString(id + 1));
        squarePane.getChildren().add(square);

        squarePane.setOnDragEntered(event -> {
            if (event.getGestureSource() instanceof RackView && event.getDragboard().hasContent(TileView.getFormat())) {
                squarePane.setStyle("-fx-background-color: transparent;");
            }
            event.consume();
        });

        squarePane.setOnDragExited(event -> {
            if (event.getGestureSource() instanceof RackView && event.getDragboard().hasContent(TileView.getFormat())) {
                squarePane.setStyle("");
            }
            event.consume();
        });

        // Enable dragging and dropping on the square pane
        makeDraggable(squarePane);

        return squarePane;
    }

    private void makeDraggable(Node node) {
        node.setOnDragOver(event -> {
            if (event.getGestureSource() instanceof RackView && event.getDragboard().hasContent(TileView.getFormat())) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        node.setOnDragEntered(event -> {
            if (event.getGestureSource() instanceof RackView && event.getDragboard().hasContent(TileView.getFormat())) {
                StackPane targetPane = (StackPane) node;
                targetPane.setStyle("-fx-background-color: transparent;");
            }
            event.consume();
        });

        node.setOnDragExited(event -> {
            if (event.getGestureSource() instanceof RackView && event.getDragboard().hasContent(TileView.getFormat())) {
                StackPane targetPane = (StackPane) node;
                targetPane.setStyle("");
            }
            event.consume();
        });

        node.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasContent(TileView.getFormat())) {
                TileView draggedTileView = (TileView) dragboard.getContent(TileView.getFormat());
                StackPane targetPane = (StackPane) node;

                // Remove the TileView from its current square (if any)
                Pane sourcePane = (Pane) draggedTileView.getView().getParent();
                if (sourcePane != null) {
                    sourcePane.getChildren().remove(draggedTileView.getView());
                }

                // Add the TileView to the target square
                targetPane.getChildren().add(draggedTileView.getView());

                // Set the square ID for the TileView
                draggedTileView.getView().setId(targetPane.getChildren().size() + "");

                // Set the alignment of the TileView within the square
                StackPane.setAlignment(draggedTileView.getView(), Pos.CENTER);

                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    public List<StackPane> getSquarePanes() {
        return squarePanes;
    }

    public StackPane getView() {
        return rackPane;
    }

    private String colorRGB(Color color) {
        return String.format("rgb(%d, %d, %d)", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }

    public static DataFormat getFormat() {
        return RACK_FORMAT;
    }

}
