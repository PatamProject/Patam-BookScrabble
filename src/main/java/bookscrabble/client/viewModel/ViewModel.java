package bookscrabble.client.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.Observable;
import java.util.Observer;

import bookscrabble.client.model.ClientCommunications;
import bookscrabble.client.model.ClientModel;
import bookscrabble.client.model.GameModel;
import bookscrabble.client.model.MyHostServer;
import bookscrabble.client.view.MainWindowController;

public class ViewModel extends Observable implements Observer {
    ClientModel clientModel; //Model representation for client info
    GameModel gameModel; // Model representation for game info
    //Game info
    public BooleanProperty isConnectedToHost, isGameRunning;
    public StringProperty board, currentPlayerName, myTiles, gameErrorMessage;
    public StringProperty myScore;
    public MapProperty<String, Integer> playersAndScoresMap;
    //Client info
    public BooleanProperty isHost;
    public StringProperty myName, hostIP, BsIP, clientErrorMessage;
    public StringProperty hostPort, BsPort;

    public StringProperty lobbyMessage;

    //Related to word placement
    public BooleanProperty wasLastWordValid;
    public String lastWord;
    public Integer row = 0;
    public Integer col = 0;
    public boolean isVertical;

    public ViewModel(GameModel gameModel, ClientModel clientModel) { //Ctor
        this.clientModel = clientModel;
        this.gameModel = gameModel;
        this.board = new SimpleStringProperty(); // AB-C----&D--E----&----T.... (A,B,C,D,E,T are tiles, - is empty, & is new line)
        this.currentPlayerName = new SimpleStringProperty();
        this.myScore = new SimpleStringProperty();
        this.myTiles = new SimpleStringProperty();
        this.myName = new SimpleStringProperty();
        this.isHost = new SimpleBooleanProperty();
        this.hostIP = new SimpleStringProperty();
        this.hostPort = new SimpleStringProperty();
        this.BsIP = new SimpleStringProperty();
        this.BsPort = new SimpleStringProperty();
        this.wasLastWordValid = new SimpleBooleanProperty();
        this.isConnectedToHost = new SimpleBooleanProperty();
        this.gameErrorMessage = new SimpleStringProperty();
        clientErrorMessage = new SimpleStringProperty();
        playersAndScoresMap = new SimpleMapProperty<>();
        isGameRunning = new SimpleBooleanProperty();
        lobbyMessage = new SimpleStringProperty();
    }

    @Override
    public void update(Observable o, Object arg) { //Receiving updates from the class observables
        if (o.equals(gameModel)) {
            if(arg != null && arg.equals("isLegal")) //failedWordPlacement
                wasLastWordValid.set(false);
            else // True / null
            {
                if(arg != null && arg.equals("isLegal")) //successfulWordPlacement
                    wasLastWordValid.set(true);

                board.set(gameModel.getBoard());
                currentPlayerName.set(gameModel.getCurrentPlayersName());
                myTiles.set(gameModel.getMyTiles());
                playersAndScoresMap.set(FXCollections.observableMap(gameModel.getPlayersAndScores()));
                myScore.set(gameModel.getMyScore().toString());
                gameErrorMessage.set(gameModel.getErrorMessage());
                if(arg != null && arg.equals("playerUpdateMessage"))
                    lobbyMessage.set(gameModel.getPlayerUpdateMessage());
                  
            }
        }
        if (o.equals(clientModel)) {
            if(arg != null && arg.equals("endGame"))
            {
                isConnectedToHost.set(false);
                clientErrorMessage.set("Game ended!");
                lobbyMessage.set("Game ended!");
                setChanged();
                notifyObservers("endGame");
            }
            isConnectedToHost.set(clientModel.isConnectedToHost);
            clientErrorMessage.set(clientModel.getErrorMessage());
        }

        if(arg != null && arg.equals("gameStarted"))
            isGameRunning.set(true);  
    }

    //Updates from View -> Model//
    public void setIfHost() {clientModel.setIfHost(isHost.get());}
    public void setMyName() {clientModel.setMyName(myName.get());}    
    public void setHostIP() {clientModel.setHostIP(hostIP.get());}
    public void setHostPort() {clientModel.setHostPort(Integer.parseInt(hostPort.get()));}
    public void setBsIP() {clientModel.setBsIP(BsIP.get());}
    public void setBsPort() {clientModel.setBsPort(Integer.parseInt(BsPort.get()));}
    public void createClient() {clientModel.createClient(isHost.get(), hostIP.get(), Integer.parseInt(hostPort.get()), myName.get());}

    //Host only options
    public void startHostServer()  
    {
        if(isHost.get()) //If I am the host
            MyHostServer.getHostServer().start(Integer.parseInt(hostPort.get()), Integer.parseInt(BsPort.get()), BsIP.get());
    }
    public void sendStartGameRequest() {if(ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&startGame"));}
    public void sendEndgameRequest() {if(ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&endGame")); clear();}

    //Player options
    public void sendLeaveRequest() {if(ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&leave")); clear();}
    public void sendSkipTurnRequest() {if(ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&skipTurn"));} //'Q':'word,row,col,isVertical'
    public void sendWordPlacementRequest(String playerSentBoard, boolean isChallange)
    {
        final int BOARD_SIZE = 15;
        StringBuilder word = new StringBuilder();
        String[] oldBoard = board.get().split("&");
        String[] newBoard = playerSentBoard.split("&");
        int tmpRow = -1, tmpCol = -1;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if(oldBoard[i].charAt(j) != newBoard[i].charAt(j))
                {
                    if(tmpRow == -1 && tmpCol == -1) //First new tile is found
                    {
                        tmpRow = i;
                        tmpCol = j;
                    }
                    word.append(newBoard[i].charAt(j));
                }
            }
        }

        /*
         public Word fromStringToWord(String pName, final String tiles, final int row,final int col,final boolean vertical)
    { //A word is created from the tiles taken from the player and from the board respectively 
        Player p = players.get(pName);
        if(p == null)
            return null;
        
        int tmpRow = row, tmpCol = col;
        Tile tile;
        ArrayList<Tile> tilesArr = new ArrayList<>();
        Tile[][] tilesOnBoard = board.getTiles(); //copy of board tiles. Be careful to not create extra tiles!
        for (int i = 0; i < tiles.length(); i++)
        {
            if(tilesOnBoard[tmpRow][tmpCol] != null && tiles.charAt(i) == tilesOnBoard[tmpRow][tmpCol].letter) //Tile is on the board
            {
                tilesArr.add(null); //Word on board, do not take
                //tilesOnBoard[tmpRow][tmpCol] if doesnt work?
            }
            else
            {
                tile = p.getRack().takeTileFromRack(tiles.charAt(i)); 
                if(tilesOnBoard[tmpRow][tmpCol] == null && tile != null) //Tile is on the rack
                    tilesArr.add(tile);
                else //Can't find tile / tile placed on another tile
                    return null; 
            }
                
            //Adjust tmpRow and tmpCol according to vertical
            if(vertical)
                tmpRow++;
            else
                tmpCol++;
        }
        Tile[] wordTiles = tilesArr.toArray(new Tile[tilesArr.size()]);
        return new Word(wordTiles, row, col, vertical);
    }
          
         */
        // lastWord = word;
        // this.row = row;
        // this.col = col;
        // this.isVertical = isVertical;
        //ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&Q:" + word + "," + row + "," + col + "," + isVertical);
    }

    public void sendChallengeRequest()
    {
        //ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&C:" + lastWord + "," + row + "," + col + "," + isVertical);
    }

    private void clear()
    {
        board.set("");
        currentPlayerName.set("");
        myTiles.set("");
        myScore.set("");
        playersAndScoresMap.set(null);
        gameErrorMessage.set("");
        isGameRunning.set(false);
    }

/* 
    //Updates from View -> Model//

    - Set my own name ✓
    - Set ifHost ✓
    //Host only options
    - Send BSS ip and port to connect ✓
    - Set lobby password? 
    - Kick player?
    - Send start game request ✓
    - Send end game request ✓
    //Player options
    - Send join request(ip + port + password?) (Already inside ClientModel ctor) ✓
    - send leave request ✓
    - send skipTurn ✓
    - send word placement ✓
    - send challenge option ✓
*/
}