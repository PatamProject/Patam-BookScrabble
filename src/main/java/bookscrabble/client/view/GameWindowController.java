package bookscrabble.client.view;

import bookscrabble.client.viewModel.ViewModel;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import static bookscrabble.client.view.MainWindowController.switchRoot;

public class GameWindowController implements Observer , Initializable {
    static ViewModel vm;
    MainWindowController mwc;
    private GameWindowDisplayer playerScreenDisplayer = new GameWindowDisplayer();
    @FXML
    public TableView scoreTable;
    @FXML
    public Button skipTurn, done, challenge, Quit, mainMenu, exit;
    @FXML
    public Rectangle place1, place2, place3, place4, place5, place6, place7; //TODO: Ofek needs to change those names to something more understandable
    @FXML
    GridPane gridPane;
    @FXML
    HBox hBox;
    //    @FXML
//    TableView<?> tableView;
//    @FXML
//    TableColumn<String,String> nameList;
//    @FXML
//    TableColumn<String,Integer> scoreList;
    @FXML
    private TableView<?> tableView;
    @FXML
    private TableColumn<?,?> nameList;
    @FXML
    private TableColumn<?,?> scoreList;

    private List<Group> alreadyDrag = new ArrayList<>();
    private Group draggedGroup=null;
    private double initialX, initialY;
    private StringProperty myTilesProperty = new SimpleStringProperty(), myBoardProperty = new SimpleStringProperty();
    private MapProperty<String, Integer> myPlayersAndScores = new SimpleMapProperty<>();
    private String myStringBoard , letterUpdate;
    private String[] myBoard;
    private int rowUpdate, colUpdate;

    @Override
    public void update(Observable o, Object arg) {} // Empty update method

    public void setViewModel(ViewModel vm) { //  Setter for the ViewModel
        if(vm != null)
            MainWindowController.vm = vm;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) { // Method to set all bindings
        if(vm == null)
            vm = MainWindowController.vm;
        String path = location.getFile();
        if (path.endsWith("GameWindow.fxml")) {
            myTilesProperty.bindBidirectional(vm.myTiles);
            myBoardProperty.bindBidirectional(vm.board);
        }
        myPlayersAndScores.bindBidirectional(vm.playersAndScoresMap);

//        nameList.setCellValueFactory(new PropertyValueFactory<>("key"));
//        scoreList.setCellValueFactory(new PropertyValueFactory<>("value"));
//        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(vm.playersAndScoresMap.entrySet());
//        tableView.setItems(FXCollections.observableArrayList(entryList));
    }

    public void displayAll()
    {
        playerScreenDisplayer.completeBoard(gridPane,hBox,myTilesProperty.get());
    }

    public void displayUpdateBoard()
    {
        getMyBoard();
        for(int row=0;row<15;row++)
        {
            String[] line = myBoard[row].split(" ");
            for(int col=0;col<15;col++)
            {
                if(!line[col].equals("-"))
                {
                    putTileOnBoard(line[col],row,col);
                }
            }
        }
    }

    public void getMyBoard() {
        myStringBoard = myTilesProperty.get();
        myBoard = myStringBoard.split("&");
    }

    public void onMouseClicked(MouseEvent event) // Runs when the mouse is clicked, saves which group the mouse clicked on, and saves the X Y position of the click
    {
        initialX = event.getSceneX();
        initialY = event.getSceneY();
        draggedGroup = (Group) event.getSource();
        if(!alreadyDrag.contains(draggedGroup))
            draggedGroup.setManaged(false);
    }

    public void onMouseDragged(MouseEvent event) // Runs when the mouse is dragged, drag the group on the map , and saves the X Y position of the drag
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

    public void onMouseReleased(MouseEvent event) { // רRuns when the mouse is released, drops the group on the board and places it in the appropriate position according to X Y
        if (draggedGroup != null && !alreadyDrag.contains(draggedGroup))
        {
            initialX = event.getSceneX();
            initialY = event.getSceneY();

            double releaseX = event.getSceneX();
            double releaseY = event.getSceneY();

            dropRectangle(releaseX, releaseY);
        }
    }

    private void dropRectangle(double releaseX , double releaseY) // Put the group on the board.
    {
        boolean stop=false;
        int indexRow = 0 , indexCol = 0 ;
        for(int y = 4 ; y < 830 && !stop ; y+=55)
        {
            for(int x = 547 ; x < 1373 && !stop ; x+=55)
            {
                if((releaseX >= x && releaseX < x+55) && (releaseY >= y && releaseY < y+55))
                {
                    final int row = indexRow;
                    final int col = indexCol;
                    Node removeNode = getNode(indexRow, indexCol);
                    if(removeNode instanceof Rectangle)
                    {
                        if(!checkRemoveRectangle(removeNode))
                        {
                            System.out.println("Cant remove this rectangle");
                            break;
                        }
                    }
                    copyCords(draggedGroup,removeNode);
                    removeFromFather(draggedGroup , indexRow , indexCol);
                    letterUpdate = draggedGroup.getId();
                    rowUpdate = indexRow;
                    colUpdate = indexCol;
                    draggedGroup.setId("group:"+indexRow+":"+indexCol);
                    gridPane.add(draggedGroup,row,col);
                    stop = true;
                }
                indexCol++;
            }
            indexRow++;
            indexCol=0;
        }
        alreadyDrag.add(draggedGroup);
        draggedGroup = null;
    }

    private boolean checkRemoveRectangle(Node removeNode)
    {
        Rectangle rectangle = (Rectangle) removeNode;
        return !removeNode.getId().equals("UnRemovable");
    }

    private void removeFromFather(Group draggedGroup , int row , int col) // Remove the group from its father
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

    private void copyCords(Group draggedGroup, Node infoSquare) // Copy the cords from the removed rectangle the dropped group.
    {
        draggedGroup.setTranslateX(infoSquare.getTranslateX());
        draggedGroup.setTranslateY(infoSquare.getTranslateY());
        draggedGroup.setLayoutX(infoSquare.getLayoutX());
        draggedGroup.setLayoutY(infoSquare.getLayoutY());
        draggedGroup.setScaleX(infoSquare.getScaleX());
        draggedGroup.setScaleY(infoSquare.getScaleY());
    }

    private Node getNode(int rowIndex, int columnIndex) { // Return the Node that need to be removed.
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

    private void UpdateBoard(String letter, int row, int col)
    {
        getMyBoard();
        boolean stop=false;
        String newBoard = "";
        for(int i=0;i<15&& !stop;i++)
        {
            String[] line = myBoard[i].split(" ");
            for(int j=0;j<15 && !stop;j++)
            {
                if(row == i && col == j)
                {
                    line[j] = letter;
                    stop=true;
                }
                newBoard.concat(line[j]);
                newBoard.concat(" ");
            }
            newBoard.concat("&");
        }
        myBoardProperty.set(newBoard);
    }

    private void putTileOnBoard(String letter, int row, int col)
    {
        Rectangle rectangle = (Rectangle) getNode(row,col);
        String imageTile = letter.concat("tile.png");
        String imagePath = "bookscrabble/resources/ImageTile/"+ imageTile;
        Image image = new Image(imagePath);
        ImagePattern imagePattern = new ImagePattern(image);
        if (rectangle != null) {
            rectangle.setFill(imagePattern);
            rectangle.setId("UnRemovable");
        }
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
    public void doneButtonClicked(ActionEvent event) { // Sends user attempt for word placement
        UpdateBoard(letterUpdate,rowUpdate,colUpdate);
        //vm.sendWordPlacementRequest(); //TODO: Commented section for communication with the model
    }

    @FXML
    public void challengeButtonClicked(ActionEvent event) { // Challenge
        //vm.sendChallengeRequest(); //TODO: Commented section for communication with the model
    }

    @FXML
    public void skipTurnButtonClicked(ActionEvent event) { // Skip the user turn
        //vm.sendSkipTurnRequest(); //TODO: Commented section for communication with the model
    }

    @FXML
    public void mainMenuButtonClicked(ActionEvent event) { // Returns the user to the main menu
        switchRoot("Main");
        mwc = MainApplication.getFxmlLoader().getController();
    }

    @FXML // Closing the app
    public void exitButtonClicked(ActionEvent event) {
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
    }
}