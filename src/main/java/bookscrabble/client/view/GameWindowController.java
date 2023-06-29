package bookscrabble.client.view;

import bookscrabble.client.viewModel.ViewModel;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
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
    public Button skipTurn, done, challenge, Quit, mainMenu, exit;
    @FXML
    public Rectangle place1, place2, place3, place4, place5, place6, place7; //TODO: Ofek needs to change those names to something more understandable
    @FXML
    GridPane gridPane;
    @FXML
    HBox myRack;
    @FXML
    TableView playersTable;
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private List<Group> alreadyDrag = new ArrayList<>();
    private List<Group> dropOnBoard = new ArrayList<>();
    private Group draggedGroup=null , choosenGroup = null;
    private double initialX, initialY;
    private StringProperty myTilesProperty = new SimpleStringProperty(), myBoardProperty = new SimpleStringProperty();
    private MapProperty<String, Integer> myPlayersAndScores = new SimpleMapProperty<>();
    private String myTiles , myStringBoard , letterUpdate;
    private String[] myBoard;
    private int rowUpdate, colUpdate;

    private  List<String> nameList;
    private  List<Integer> scoreList;
    private ArrayList<Tuple<String, Integer, Integer>> tilesBuffer;
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
            //myTilesProperty.bindBidirectional(vm.myTiles);
            myBoardProperty.bindBidirectional(vm.board);
        }
        myPlayersAndScores.bindBidirectional(vm.playersAndScoresMap);
    }

    public void displayAll()
    {
        playerScreenDisplayer.completeBoard(gridPane, myRack, vm.myTiles.get());
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
//        getMyNameScore();
//        int index;
//        for(int i=2;i<8;i++)
//        {
//            index = i-2;
//            TextField textField = (TextField) playersTable.getChildren().get(i);
//            if((i%2)==0)
//                textField.setText(nameList.get(index));
//            else
//                textField.setText(Integer.toString(scoreList.get(index)));
//        }
    }

    public void getMyBoard() {
        myStringBoard = myTilesProperty.get();
        myBoard = myStringBoard.split("&");
    }

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
        if(event.getSource() instanceof Group)
        {
            Group clickedNode = (Group) event.getSource();
            if(!dropOnBoard.contains(clickedNode))
            {
                choosenGroup = clickedNode;
            }
            else {
                String id[] = clickedNode.getId().split(" ");
                int index = Integer.parseInt(id[1]);
                StackPane stackPane = (StackPane) myRack.getChildren().get(index);
                stackPane.getChildren().add(clickedNode);
                clickedNode.setManaged(true);
                //removeTileFromTuple(clickedNode);
                dropOnBoard.remove(clickedNode);
            }
        }
        else if(event.getSource() instanceof Rectangle)
        {
            if(choosenGroup == null)
                return;

            Rectangle rectangle = ( Rectangle) event.getSource();
            int row = GridPane.getRowIndex(rectangle);
            int col = GridPane.getColumnIndex(rectangle);

            choosenGroup.setManaged(false);
            copyCords(choosenGroup,rectangle);
            removeFromFather(choosenGroup);
            gridPane.add(choosenGroup,row,col);
            dropOnBoard.add(choosenGroup);

            String id[] = choosenGroup.getId().split(" ");
            tilePlacedTuple(id[1],row,col);
        }
    }

    private void removeFromFather(Group draggedGroup) // Remove the group from its father
    {
            for (int i = 0; i < 7; i++) {
                StackPane stackPane = (StackPane) myRack.getChildren().get(i);
                if (!stackPane.getChildren().isEmpty()) {
                    if (stackPane.getChildren().get(0).equals(draggedGroup)) {
                        stackPane.getChildren().remove(draggedGroup);
                        break;
                    }
                }
            }
    }

    private void removeTileFromTuple(Group clickedGroup)
    {
        Tuple<String,Integer,Integer> tuple = tupleMap.get(clickedGroup);
        tupleMap.remove(clickedGroup);
        tilesBuffer.remove(tuple);
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