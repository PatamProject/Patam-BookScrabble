package bookscrabble.client.view;

import bookscrabble.client.MainApplication;
import bookscrabble.client.viewModel.ViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static bookscrabble.client.view.MainWindowController.switchRoot;

public class GameWindowController implements Observer {
    static ViewModel vm;
    MainWindowController mwc;
    @FXML
    public TableView scoreTable;
    @FXML
    public Button skipTurn, done, challenge, Quit, mainMenu;
    @FXML
    public Rectangle place1, place2, place3, place4, place5, place6, place7; //TODO: Ofek needs to change those names to something more understandable
    @FXML
    GridPane gridPane;
    @FXML
    HBox hBox;
    private GameWindowDisplayer playerScreenDisplayer = new GameWindowDisplayer();
    private List<Group> alreadyDrag = new ArrayList<>();
    private Group draggedGroup=null;
    private double initialX, initialY;

    @Override
    public void update(Observable o, Object arg) {} // Empty update method

    public void setViewModel(ViewModel vm) { //  Setter for the ViewModel
        if(vm != null)
            MainWindowController.vm = vm;
        //TODO: Add bindings to update the game
    }

    //TODO: Ofek needs to add comments to all of his functions

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

    @FXML
    public void quitButtonClicked(ActionEvent event) { // Quits the game and showing final scores
        //TODO: Commented section for communication with the model
//        if (vm.isHost.get())
//            vm.sendEndgameRequest();
//        else
//            vm.sendLeaveRequest();
        switchRoot("EndGame");
    }

    @FXML
    public void doneButtonClicked(ActionEvent event) {
        //vm.sendWordPlacementRequest(); //TODO: Commented section for communication with the model
    }

    @FXML
    public void challengeButtonClicked(ActionEvent event) {
        //vm.sendChallengeRequest(); //TODO: Commented section for communication with the model
    }

    @FXML
    public void skipTurnButtonClicked(ActionEvent event) {
        //vm.sendSkipTurnRequest(); //TODO: Commented section for communication with the model
    }

    @FXML
    public void mainMenuButtonClicked(ActionEvent event) { // Returns the user to the main menu
        switchRoot("Main");
        mwc = MainApplication.getFxmlLoader().getController();
    }
}