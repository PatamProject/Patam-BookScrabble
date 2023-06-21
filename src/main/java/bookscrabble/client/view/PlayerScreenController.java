package bookscrabble.client.view;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class PlayerScreenController {
    private PlayerScreenDisplayer playerScreenDisplayer = new PlayerScreenDisplayer();
    @FXML
    GridPane gridPane;
    @FXML
    HBox hBox;
    private List<Group> alreadyDrag = new ArrayList<>();
    private Group draggedGroup=null;
    private double initialX, initialY;

    public void displayAll() {playerScreenDisplayer.completeBoard(gridPane,hBox);}

    public void onMouseClicked(MouseEvent event)
    {
        initialX = event.getSceneX();
        initialY = event.getSceneY();
        draggedGroup = (Group) event.getSource();
        if(!alreadyDrag.contains(draggedGroup))
            draggedGroup.setManaged(false);
    }

    public void onMouseDragged(MouseEvent event)
    {
        if(draggedGroup != null && !alreadyDrag.contains(draggedGroup))
        {
            double offsetX = event.getSceneX() - initialX;
            double offsetY = event.getSceneY() - initialY;

            draggedGroup.setTranslateX(draggedGroup.getTranslateX() + offsetX);
            draggedGroup.setTranslateY(draggedGroup.getTranslateY() + offsetY);

            initialX = event.getSceneX();
            initialY = event.getSceneY();
        }
    }

    public void onMouseReleased(MouseEvent event) {
        if (draggedGroup != null && !alreadyDrag.contains(draggedGroup))
        {
            initialX = event.getSceneX();
            initialY = event.getSceneY();

            double releaseX = event.getSceneX();
            double releaseY = event.getSceneY();

            dropRectangle(releaseX, releaseY);
        }
    }

    private void dropRectangle(double releaseX , double releaseY)
    {
        boolean flag=false;
        int indexRow = 0 , indexCol = 0 ;
        for(int y = 4 ; y < 830 ; y+=55)
        {
            for(int x = 547 ; x < 1373 ; x+=55)
            {
                if((releaseX >= x && releaseX < x+55) && (releaseY >= y && releaseY < y+55))
                {
                    final int row = indexRow;
                    final int col = indexCol;
                    Node removeNode = getRectangle(indexRow, indexCol);
                    copyInfo(draggedGroup,removeNode);
                    removeFromFather(draggedGroup , indexRow , indexCol);
                    gridPane.add(draggedGroup,row,col);

                    flag = true;
                    break;
                }
                indexCol++;
            }
            if(flag)
                break;
            indexRow++;
            indexCol=0;
        }
        alreadyDrag.add(draggedGroup);
        draggedGroup = null;
    }

    private void removeFromFather(Group draggedGroup , int row , int col)
    {
        if(draggedGroup.getParent() instanceof HBox) {
            for (int i = 0; i < 7; i++) {
                StackPane stackPane = (StackPane) hBox.getChildren().get(i);
                if (!stackPane.getChildren().isEmpty()) {
                    if (stackPane.getChildren().get(0).equals(draggedGroup)) {
                        stackPane.getChildren().remove(draggedGroup);
                        break;
                    }
                }
            }
        }
    }

    private void copyInfo(Group draggedGroup, Node infoSquare)
    {
        draggedGroup.setTranslateX(infoSquare.getTranslateX());
        draggedGroup.setTranslateY(infoSquare.getTranslateY());
        draggedGroup.setLayoutX(infoSquare.getLayoutX());
        draggedGroup.setLayoutY(infoSquare.getLayoutY());
        draggedGroup.setScaleX(infoSquare.getScaleX());
        draggedGroup.setScaleY(infoSquare.getScaleY());
    }

    private Node getRectangle(int rowIndex, int columnIndex) {
        for (Node node : gridPane.getChildren()) {
            if(node instanceof Rectangle) {
                Integer nodeColumnIndex = GridPane.getColumnIndex(node);
                Integer nodeRowIndex = GridPane.getRowIndex(node);
                if (nodeColumnIndex != null && nodeRowIndex != null && nodeColumnIndex == columnIndex && nodeRowIndex == rowIndex)
                    return node;
            }
            else if (node instanceof StackPane)
            {
                StackPane stackPane = (StackPane) node;
                Node nodeRec = stackPane.getChildren().get(0);
                Integer nodeColumnIndex = GridPane.getColumnIndex(nodeRec);
                Integer nodeRowIndex = GridPane.getRowIndex(nodeRec);
                if (nodeColumnIndex != null && nodeRowIndex != null && nodeColumnIndex == columnIndex && nodeRowIndex == rowIndex)
                    return stackPane;
            }
        }
        return null;
    }
}