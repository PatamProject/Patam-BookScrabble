package bookscrabble.client.view;

import bookscrabble.client.viewModel.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import static bookscrabble.client.view.MainWindowController.switchRoot;

public class GameWindowController implements Observer , Initializable {
    public static final int MAX_BOARD_SIZE = 15;
    static ViewModel vm;
    MainWindowController mwc;
    private GameWindowDisplayer playerScreenDisplayer = new GameWindowDisplayer();
    @FXML
    public Text winnerText;
    @FXML
    public Button skipTurn, done, challenge, Quit, mainMenu, exit;
    @FXML
    public Rectangle place1, place2, place3, place4, place5, place6, place7; //TODO: Ofek needs to change those names to something more understandable
    @FXML
    GridPane gridPane;
    @FXML
    HBox hBox;

    @FXML
    GridPane playersTable;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private List<Group> alreadyDrag = new ArrayList<>();
    private Group draggedGroup=null;
    private double initialX, initialY;
    private StringProperty myTilesProperty = new SimpleStringProperty(), myBoardProperty = new SimpleStringProperty();
    private MapProperty<String, Integer> myPlayersAndScores = new SimpleMapProperty<>();
    private String myTiles , myStringBoard , letterUpdate;
    private String[] myBoard;
    private int rowUpdate, colUpdate;
    private ArrayList<Tuple<String,Integer,Integer>> tilesBuffer = new ArrayList<>(); 
    private  List<String> nameList;
    private  List<Integer> scoreList;

    private Map<Group,Tuple<String, Integer, Integer>> tupleMap = new HashMap<>();

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

            vm.playersAndScoresMap.addListener((observable, oldValue, newValue) -> {
                if(newValue != null)
                    Platform.runLater(() -> {
                        if(vm.playersAndScoresMap.getValue().size() < 2)
                            vm.lobbyMessage.setValue("Game stopped, not enough players!");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {}
                            quitButtonClicked(new ActionEvent());
                    });
            });
        }

        if (path.endsWith("EndGame.fxml")) {
            winnerText.setText(vm.lobbyMessage.getValue());
        }

        myPlayersAndScores.bindBidirectional(vm.playersAndScoresMap);
    }

    public void displayAll()
    {
        playerScreenDisplayer.completeBoard(gridPane,hBox,myTilesProperty.get());
    }

    public void updateDisplayAll() // Called all the update function to show the player the update screen.
    {
        displayUpdateList();
        displayUpdateBoard();
    }

    private void UpdateStringBoard(String letter, int row, int col) // Send the update to all players
    {
        boolean stop=false;
        for(int i=0;i<15&& !stop;i++)
        {
            char line[] = myBoard[i].toCharArray();
            for(int j=0;j<15 && !stop;j++)
            {
                if(row == i && col == j)
                {
                    line[j] = letter.charAt(0);
                    stop=true;
                }
                myBoard[i] = String.valueOf(line);
            }
        }
    }

    public void displayUpdateList() // display on screen the new lists.
    {
        getMyNameScore();
        int index;
        for(int i=2;i<8;i++)
        {
            index = i-2;
            TextField textField = (TextField) playersTable.getChildren().get(i);
            if((i%2)==0)
                textField.setText(nameList.get(index));
            else
                textField.setText(Integer.toString(scoreList.get(index)));
        }
    }

    public void getMyBoard() {
        myStringBoard = myTilesProperty.get();
        myBoard = myStringBoard.split("&");
    }

    public void getMyTiles() {  myTiles = myTilesProperty.get(); } // Prints the current value of myTilesProperty

    private void getMyNameScore() // Update the list of name & score.
    {
        Map<String,Integer> mapNameScore = myPlayersAndScores.get();
        nameList = new ArrayList<>(mapNameScore.keySet());
        scoreList = new ArrayList<>(mapNameScore.values());
        //List<String> stringScoreList = scoreList.stream().map(String::valueOf).collect(Collectors.toList());
    }

    public void displayUpdateBoard()
    {
        getMyBoard();
        for(int row=0;row<15;row++)
        {
            char line[] = myBoard[row].toCharArray();
            for(int col=0;col<15;col++)
            {
                if(!Character.toString(line[col]).equals("-"))
                {
                    putTileOnBoardToOthers(Character.toString(line[col]),row,col);
                }
            }
        }
    }

    public void tilePlacedTuple(String letter , int row , int col)
    {
        if(letter == null || letter.length() != 1 || !letter.matches("[a-zA-Z]")) //only one valid tile
        return;
        else if(row < 0 || row > MAX_BOARD_SIZE - 1 || col < 0 || col > MAX_BOARD_SIZE - 1) //valid row and col
        return;
        Tuple<String,Integer,Integer> tuple = new Tuple<>(letter, row, col);
        tilesBuffer.add(tuple);
        tupleMap.put(draggedGroup , tuple);
    }


//    public void updateNewTile() // Put new tile inside the rack.
//    {
//        String oldTiles = myTiles;
//        getMyTiles(); // Update myTiles to new Tiles
//        for(int i=0;i<7;i++)
//        {
//            StackPane stackPane = (StackPane) hBox.getChildren().get(i);
//            if(stackPane.getChildren().isEmpty())
//            {
//                Rectangle rectangle = new Rectangle(55,55);
//                String imageTile = letter.concat("tile.png");
//                String imagePath = "bookscrabble/resources/ImageTile/"+ imageTile;
//                ClassLoader classLoader = getClass().getClassLoader();
//                Image image = new Image(classLoader.getResourceAsStream(imagePath));
//                ImageView imageView = new ImageView(image);
//                Group group = new Group(rectangle,imageView);
//                stackPane.getChildren().add(group);
//            }
//        }
//    }

    public void onMouseClicked(MouseEvent event) // Runs when the mouse is pressed, saves which group the mouse clicked on, and saves the X Y position of the click
    {
        if(event.getSource() instanceof Group) {
            Group clickedGroup = (Group) event.getSource();
            alreadyDrag.remove(clickedGroup);
            String id[] = clickedGroup.getId().split(" ");
            int index = Integer.parseInt(id[1]);
            StackPane stackPane = (StackPane) hBox.getChildren().get(index);
            stackPane.getChildren().add(clickedGroup);
            clickedGroup.setManaged(true);
            removeTileFromTuple(clickedGroup);
        }
    }

    private void removeTileFromTuple(Group clickedGroup)
    {
        Tuple<String,Integer,Integer> tuple = tupleMap.get(clickedGroup);
        tupleMap.remove(clickedGroup);
        tilesBuffer.remove(tuple);
    }

    public void onMousePressed(MouseEvent event) // Runs when the mouse is pressed, saves which group the mouse clicked on, and saves the X Y position of the click
    {
        draggedGroup = (Group) event.getSource();
        initialX = event.getSceneX();
        initialY = event.getSceneY();
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
                    copyCords(draggedGroup,removeNode);
                    removeFromFather(draggedGroup , indexRow , indexCol);
                    letterUpdate = draggedGroup.getId();
                    rowUpdate = indexRow;
                    colUpdate = indexCol;
                    ImageView imageView = (ImageView) draggedGroup.getChildren().get(1);
                    //UpdateStringBoard(imageView.getId() , indexRow , indexCol);
                    String id[] = draggedGroup.getId().split(" ");
                    tilePlacedTuple(id[1],indexRow,indexCol);
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

    private void copyCords(Group destination, Node source) // Copy the cords from the removed rectangle the dropped group.
    {
        destination.setTranslateX(source.getTranslateX());
        destination.setTranslateY(source.getTranslateY());
        destination.setLayoutX(source.getLayoutX());
        destination.setLayoutY(source.getLayoutY());
        destination.setScaleX(source.getScaleX());
        destination.setScaleY(source.getScaleY());
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

    private void putTileOnBoardToOthers(String letter, int row, int col)
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

    public void tilePlaced(String letter, Integer row, Integer col)
    {
        if(letter == null || letter.length() != 1 || !letter.matches("[a-zA-Z]")) //only one valid tile
            return;
        else if(row < 0 || row > MAX_BOARD_SIZE - 1 || col < 0 || col > MAX_BOARD_SIZE - 1) //valid row and col
            return;

        tilesBuffer.add(new Tuple<String,Integer,Integer>(letter, row, col));
    }

    private boolean AreTileInTheSameRowCol()
    {
        if(tilesBuffer.isEmpty())
            return false;
        else if(tilesBuffer.size() == 1)
            return true;

        boolean sameRow = true;
        boolean sameCol = true;
        int row = tilesBuffer.get(0).getSecond();
        int col = tilesBuffer.get(0).getThird();

        for(int i = 1; i < tilesBuffer.size(); i++)
        {
            if(tilesBuffer.get(i).getSecond() != row)
                sameRow = false;
            if(tilesBuffer.get(i).getThird() != col)
                sameCol = false;
        }

        return sameRow || sameCol;
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
        if(vm.sendWordPlacementRequest(tilesBuffer, false) == false || AreTileInTheSameRowCol() == false)
        {
            alert.setTitle("Illegal Word Placement");
            alert.setHeaderText(null);
            alert.setContentText("The tiles you placed are invalid, please try again");
            alert.showAndWait();
        }
    }

    @FXML
    public void challengeButtonClicked(ActionEvent event) { // Challenge
        if(vm.sendWordPlacementRequest(tilesBuffer, true) == false || AreTileInTheSameRowCol() == false)
        {
            alert.setTitle("Illegal Word Placement");
            alert.setHeaderText(null);
            alert.setContentText("The tiles you placed are invalid, please try again");
            alert.showAndWait();
        }
    }

    @FXML
    public void skipTurnButtonClicked(ActionEvent event) { // Skip the user turn
        vm.sendSkipTurnRequest();
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