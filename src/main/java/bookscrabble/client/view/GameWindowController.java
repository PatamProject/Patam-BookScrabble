package bookscrabble.client.view;

import bookscrabble.client.misc.MyLogger;
import bookscrabble.client.viewModel.ViewModel;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

import static bookscrabble.client.view.MainWindowController.switchRoot;

public class GameWindowController implements Observer , Initializable {
    public static final int MAX_BOARD_SIZE = 15, MAX_TILE_SIZE = 7, GRID_SIZE = 15, SQUARE_SIZE_1 = 37, SQUARE_SIZE_2 = 30;
    static ViewModel vm;
    MainWindowController mwc;
    @FXML
    public Text winnerText;
    @FXML
    public Button skipTurn, done, challenge, Quit, mainMenu, exit;
    @FXML
    GridPane gridPane;
    @FXML
    HBox myRack;
    @FXML
    GridPane playersTable;
    @FXML
    TextArea textArea;
    @FXML
    TableView<PlayerAndScore> table;
    @FXML
    TableColumn<PlayerAndScore, String> nameColumn;
    @FXML
    TableColumn<PlayerAndScore, Integer> scoreColumn;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    private List<Group> dropOnBoard = new ArrayList<>();
    private Group chosenGroup = null;
    private MapProperty<String, Integer> myPlayersAndScores = new SimpleMapProperty<>();
    private Map<Group,Tuple<String,Integer,Integer>> tilesBuffer = new HashMap<>();
    public final String PICTURE_PATH = "/bookscrabble/pictures/tiles/", TILE_PNG = "tile.png";

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
            myPlayersAndScores.bind(vm.playersAndScoresMap);

            vm.playersAndScoresMap.addListener((observable, oldValue, newValue) -> {
                if(newValue != null)
                    Platform.runLater(() -> {
                        if(vm.playersAndScoresMap.getValue().size() < 2)
                        {
                            vm.lobbyMessage.setValue("Game stopped, not enough players!");
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {}
                            quitButtonClicked(new ActionEvent());
                        }
                });
            });

            vm.board.addListener((observable, oldValue, newValue) -> {
                if(newValue != null)
                    Platform.runLater(() -> {
                        displayUpdateBoard();
                });
            });

            vm.myTiles.addListener((observable, oldValue, newValue) -> { //New tiles
                if(newValue != null)
                    Platform.runLater(() -> {
                        insertImage();
                });
            });

            textArea.textProperty().set("The game has started!\n");
            vm.lobbyMessage.addListener((observable, oldValue, newValue) -> {
                if(newValue != null)
                    Platform.runLater(() -> {
                        textArea.appendText(vm.lobbyMessage.getValue());
                });
            });

            for (String player : vm.playersAndScoresMap.keySet())
                vm.lobbyMessage.set(player + " has joined the game!\n");

            vm.wasLastWordValid.addListener((observable, oldValue, newValue) -> {
                if(newValue != null)
                    Platform.runLater(() -> {
                        if(!vm.wasLastWordValid.getValue())
                        {
                            alert.setContentText("The word you entered is not valid! You can challenge it if you want!");
                            alert.showAndWait();
                        }
                });
            });
        }

        if (path.endsWith("EndGame.fxml")) {
            winnerText.setText(vm.lobbyMessage.getValue());
        }

        nameColumn.setCellValueFactory(new PropertyValueFactory<PlayerAndScore, String>("nameColumn"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<PlayerAndScore, Integer>("scoreColumn"));
        table.setItems(initialData());
    }

    private ObservableList<PlayerAndScore> initialData() {
        ArrayList<PlayerAndScore> arr = new ArrayList<>();
        for (String s : myPlayersAndScores.keySet())
            arr.add(new PlayerAndScore(s, myPlayersAndScores.get(s)));
        
        arr.sort(Comparator.comparingInt(PlayerAndScore::getScoreColumn).reversed());
        return FXCollections.observableArrayList(arr);
    }

    private String[] getBoard() {return vm.board.get().split("&");}

    public void insertImage()
    {
        String myTiles = String.join(" ",vm.myTiles.get().split(""));
        String[] tileArr = myTiles.split(" ");
        for(int i=0;i<MAX_TILE_SIZE;i++)
        {
            try {
                String imageTile = tileArr[i].concat(TILE_PNG);
                String imagePath = PICTURE_PATH + imageTile;
                Image image = new Image(getClass().getResourceAsStream(imagePath));
                StackPane stackPane = (StackPane) myRack.getChildren().get(i);
                //Tile components
                Group group;
                Rectangle rectangle;
                ImageView imageView;
                if(stackPane.getChildren().size() == 0) //if the stackPane is empty
                {
                    rectangle = new Rectangle(SQUARE_SIZE_1, SQUARE_SIZE_2);
                    rectangle.setFill(Color.DODGERBLUE);
                    imageView = new ImageView(image);
                    group = new Group(rectangle,imageView);
                    stackPane.getChildren().add(group);
                    group.onMouseClickedProperty().set(event -> onMouseClicked(event));
                }
                else //Stackpane is not empty
                {
                    group = (Group) stackPane.getChildren().get(0);
                    rectangle = (Rectangle) group.getChildren().get(0);
                    imageView = (ImageView) group.getChildren().get(1);
                }

                imageView.setImage(image);
                resizeImage(imageView, rectangle);

                imageView.setId(tileArr[i]);
                group.setId(tileArr[i] +" "+Integer.toString(i)); //ID = "letter index on rack" (L 4)
            } catch (Exception e) {
                MyLogger.logError("Error with insertImage(): " + e.getMessage());
            }
        }
    }

    private void resizeImage(ImageView imageView, Rectangle rectangle)
    {
        imageView.setPreserveRatio(true);

        double rectWidth = rectangle.getBoundsInLocal().getWidth();
        double rectHeight = rectangle.getBoundsInLocal().getHeight();

        imageView.setFitWidth(rectWidth);
        imageView.setFitHeight(rectHeight);
    }

    public void displayUpdateBoard()
    {
        for(int row=0;row<MAX_BOARD_SIZE;row++)
        {
            char[] line = getBoard()[row].toCharArray();
            for(int col=0;col<MAX_BOARD_SIZE;col++)
            {
                if((line[col]) != '-') //A new tile is placed
                {
                    putTileOnBoard(Character.toString(line[col]),row,col);
                }
            }
        }
    }

    public void onMouseClicked(MouseEvent event) // Runs when the mouse is pressed, saves which group the mouse clicked on, and saves the X Y position of the click
    {
        if(!isMyTurn()) //If it's not my turn
            return;

        if(event.getSource() instanceof Group) //If a tile is clicked
        {
            Group clickedNode = (Group) event.getSource();
            if(clickedNode.getId().equals("OnBoard")) //A tile that was already placed before my turn
                return;

            if(clickedNode.getParent() instanceof StackPane) //If the tile is on the rack
            {
               chosenGroup = clickedNode;
            }
            else //Tile is on the board (now we check that the tile is mine)
            {
                if(dropOnBoard.contains(clickedNode)) //The tile is mine
                    clearMyTilesFromBoard(clickedNode,false);
                else //The tile is not mine   
                    return;
            }
        }
        else if(event.getSource() instanceof Rectangle) //If a rectangle on the board is clicked
        {
            if(chosenGroup == null)
                return;

            Rectangle rectangle = (Rectangle) event.getSource();
            if(rectangle.getId() != null) //Used rectangle and should not be pressed
                return;

            int row = GridPane.getRowIndex(rectangle);
            int col = GridPane.getColumnIndex(rectangle);

            chosenGroup.setManaged(false);
            copyCords(chosenGroup,rectangle);
            removeGroup(chosenGroup);
            gridPane.add(chosenGroup,row,col);
            dropOnBoard.add(chosenGroup);
            String id[] = chosenGroup.getId().split(" ");
            tilePlaced(id[0],row,col);
            chosenGroup = null;
        }
    }

    private void removeGroup(final Group group)
    {
        for (int i = 0; i < 7; i++) {
            StackPane stackPane = (StackPane) myRack.getChildren().get(i);
            if (!stackPane.getChildren().isEmpty()) {
                if (stackPane.getChildren().get(0).equals(group)) { 
                    stackPane.getChildren().remove(group);
                    break;
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
        }
        return null;
    }

    private void putTileOnBoard(String letter, int row, int col)
    {
        try {
            Node node = getNode(row,col);
            if(node == null) //If there is a tile on the board (not empty
                return;

            Rectangle rectangle = (Rectangle) node;
            gridPane.getChildren().remove(rectangle);
            rectangle.setVisible(false);
            rectangle.toBack();
            rectangle.setId("OnBoard");
            String imageTile = letter.concat(TILE_PNG);
            String imagePath = PICTURE_PATH + imageTile;
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            Rectangle newRectangle = new Rectangle(SQUARE_SIZE_1,SQUARE_SIZE_2);
            newRectangle.setFill(Color.DODGERBLUE);
            Group group = new Group(newRectangle,imageView);
            group.setManaged(false);
            group.setId("OnBoard"); 
            copyCords(group, rectangle);
            gridPane.add(group,row,col);
            resizeImage(imageView, rectangle);
        } catch (Exception e) {
            MyLogger.logError("Error with putTileOnBoard(): " + e.getMessage());
        }
    }

    public void tilePlaced(String letter, Integer row, Integer col)
    {
        if(letter == null || letter.length() != 1 || !letter.matches("[a-zA-Z]")) //only one valid tile
            return;
        else if(row < 0 || row > MAX_BOARD_SIZE - 1 || col < 0 || col > MAX_BOARD_SIZE - 1) //valid row and col
            return;

        tilesBuffer.put(chosenGroup,new Tuple<String,Integer,Integer>(letter, row, col));
    }

    private void clearMyTilesFromBoard(Group tile, boolean deleteAll)
    {
        if(tile != null) //Used to clear a specific tile from the board
        {
            String id[] = tile.getId().split(" ");
            int index = Integer.parseInt(id[1]);
            StackPane stackPane = (StackPane) myRack.getChildren().get(index);
            stackPane.getChildren().add(tile);
            tile.setManaged(true);
            tilesBuffer.remove(tile);
            dropOnBoard.remove(tile);
        }
        else //Used to clear all the tiles from the board
        {
            for (Group group : dropOnBoard) {
                String id[] = group.getId().split(" ");
                int index = Integer.parseInt(id[1]);
                StackPane stackPane = (StackPane) myRack.getChildren().get(index);
                stackPane.getChildren().add(group);
                group.setManaged(true);
                tilesBuffer.remove(group);
            }
        }
        if(deleteAll)
            dropOnBoard.clear();
    }

    private boolean AreTileInTheSameRowCol()
    {
        if(tilesBuffer.isEmpty())
            return false;
        else if(tilesBuffer.size() == 1)
            return true;

        ArrayList<Tuple<String,Integer,Integer>> tiles = new ArrayList<Tuple<String,Integer,Integer>>(tilesBuffer.values());
        boolean sameRow = true;
        boolean sameCol = true;
        int row = tiles.get(0).getSecond();
        int col = tiles.get(0).getThird();

        for(int i = 1; i < tiles.size(); i++)
        {
            if(tiles.get(i).getSecond() != row)
                sameRow = false;
            if(tiles.get(i).getThird() != col)
                sameCol = false;
        }

        return sameRow || sameCol;
    }

    private boolean isMyTurn()
    {
        boolean res = vm.currentPlayerName.get().equals(vm.myName.get());
        if(res == false)
        {
            alert.setTitle("Not Your Turn");
            alert.setHeaderText(null);
            alert.setContentText("It's not your turn, please wait");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    public void quitButtonClicked(ActionEvent event) { // Quits the game and showing final scores
        if (vm.isHost.get())
            vm.sendEndgameRequest();
        else
            vm.sendLeaveRequest();
        switchRoot("EndGame");
    }

    @FXML
    public void doneButtonClicked(ActionEvent event) { // Sends user attempt for word placement
        if(isMyTurn() == false)
            return;
        
        ArrayList<Tuple<String,Integer,Integer>> tilesBufferList = new ArrayList<>(this.tilesBuffer.values());
        if(vm.sendWordPlacementRequest(tilesBufferList, false) == false || AreTileInTheSameRowCol() == false)
        {
            alert.setTitle("Illegal Word Placement");
            alert.setHeaderText(null);
            alert.setContentText("The tiles you placed are invalid, please try again");
            alert.showAndWait();
        }
    }

    @FXML
    public void challengeButtonClicked(ActionEvent event) { // Challenge
        if(isMyTurn() == false)
            return;

        ArrayList<Tuple<String,Integer,Integer>> tilesBufferList = new ArrayList<>(this.tilesBuffer.values());
        if(vm.sendWordPlacementRequest(tilesBufferList, true) == false || AreTileInTheSameRowCol() == false)
        {
            alert.setTitle("Illegal Word Placement");
            alert.setHeaderText(null);
            alert.setContentText("The tiles you placed are invalid, please try again");
            alert.showAndWait();
        }
    }

    @FXML
    public void skipTurnButtonClicked(ActionEvent event) { // Skip the user turn
        if(isMyTurn() == false)
            return;
        clearMyTilesFromBoard(null,true);
        vm.sendSkipTurnRequest();
    }

    @FXML
    public void mainMenuButtonClicked(ActionEvent event) { // Returns the user to the main menu
        if (vm.isHost.get())
               vm.sendEndgameRequest();
           else
               vm.sendLeaveRequest();
        switchRoot("Main");
        mwc = MainApplication.getFxmlLoader().getController();
    }

    @FXML // Closing the app
    public void exitButtonClicked(ActionEvent event) {
        if (vm.isHost.get())
            vm.sendEndgameRequest();
        else
            vm.sendLeaveRequest();
        Stage stage = (Stage) exit.getScene().getWindow();
        stage.close();
    }
}