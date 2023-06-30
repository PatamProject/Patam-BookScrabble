package bookscrabble.client.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import bookscrabble.client.misc.MyLogger;
import bookscrabble.client.model.ClientCommunications;
import bookscrabble.client.model.ClientModel;
import bookscrabble.client.model.GameModel;
import bookscrabble.client.model.MyHostServer;
import bookscrabble.client.view.GameWindowController;
import bookscrabble.client.view.MainWindowController;
import bookscrabble.client.view.Tuple;

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
    public boolean wasLastWordaChallenge;

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
        this.wasLastWordValid = new SimpleBooleanProperty(false);
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
            if(arg != null && arg instanceof String[]) //WordPlacement
            {
                String[] wordPlacement = (String[]) arg;
                if(wordPlacement[1].equals("true"))
                {
                    wasLastWordValid.set(true);
                    lobbyMessage.set("Word placement is legal! Score updated!");
                    wasLastWordValid.set(true);
                }
                else if(wordPlacement[1].equals("false"))
                {
                    wasLastWordValid.set(false);
                    lobbyMessage.set("Word is illegal or placement is not legal!");
                    wasLastWordValid.set(false);
                }
                else
                    MyLogger.logError("Error with wordPlacement in viewModel");
                
            }
            else 
            {

                board.set(gameModel.getBoard());
                currentPlayerName.set(gameModel.getCurrentPlayersName());
                myTiles.set(gameModel.getMyTiles());
                playersAndScoresMap.set(FXCollections.observableMap(gameModel.getPlayersAndScores()));
                myScore.set(gameModel.getMyScore().toString());
                gameErrorMessage.set(gameModel.getErrorMessage());
                if(arg != null && arg.equals("playerUpdateMessage"))
                {
                    lobbyMessage.set(gameModel.getPlayerUpdateMessage());
                    playersAndScoresMap.set(FXCollections.observableMap(gameModel.getPlayersAndScores()));
                }
                  
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
    public void sendSkipTurnRequest() {if(ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&skipTurn"));} 
    public boolean sendWordPlacementRequest(ArrayList<Tuple<String, Integer, Integer>> tiles , boolean isChallange)
    {
        final int BOARD_SIZE = GameWindowController.MAX_BOARD_SIZE;
        String[] tmpBoard = board.get().split("&");
        int tmpRow = BOARD_SIZE, tmpCol = BOARD_SIZE; //Used to find the actual row, col of the word
        boolean isVertical = false;
        boolean isOneTile = tiles.size() == 1;
        for (Tuple<String, Integer, Integer> tile : tiles) { //We want to find the first tile placed (minimum row & col). All tiles are in the same row or col
            if(tmpBoard[tile.getSecond()].charAt(tile.getThird()) != '-')
                return false; //If the tile is already occupied
            else //We insert the tile to the board tmporarily
            {
                StringBuilder sb = new StringBuilder(tmpBoard[tile.getSecond()]);
                sb.setCharAt(tile.getThird(), tile.getFirst().charAt(0));
                tmpBoard[tile.getSecond()] = sb.toString();
            }

            if(tile.getSecond() < tmpRow)
                tmpRow = tile.getSecond();
            if(tile.getThird() < tmpCol)
                tmpCol = tile.getThird();    
        }
        
        for (Tuple<String, Integer, Integer> tile : tiles) { //We want to find the orientation of the word
            if(tile.getSecond() != tmpRow)
                isVertical = true;
        }
        
        String word = null;
        int startIndex = 0, endIndex = BOARD_SIZE - 1;
        if(isVertical || isOneTile) //(col is the same for all)
        {
            for (int i = tmpRow; i >= 0 && i < BOARD_SIZE;i--) //We find the starting index of the word
            {
                if(tmpRow == 0) 
                    break;
                else
                {
                    if(tmpBoard[i].charAt(tmpCol) != '-')
                        startIndex = i;
                    else
                        break;
                }    
            }  
            
            for (int i = tmpRow; i >= 0 && i < BOARD_SIZE;i++) //We find the ending index of the word
            {
                if(tmpRow == BOARD_SIZE - 1) 
                    break;
                else
                {
                    if(tmpBoard[i].charAt(tmpCol) != '-')
                        endIndex = i;
                    else
                        break;
                }    
            }       
            //Build the word
            StringBuilder sb = new StringBuilder();
            for (int i = startIndex; i <= endIndex; i++) {
                sb.append(tmpBoard[i].charAt(tmpCol));
            }
            word = sb.toString();
            tmpRow = startIndex;
        }

        if(!isVertical || isOneTile) //(row is the same for all)
        {
            for (int i = tmpCol; i >= 0 && i < BOARD_SIZE;i--) //We find the starting index of the word
            {
                if(tmpCol == 0) 
                    break;
                else
                {
                    if(tmpBoard[tmpRow].charAt(i) != '-')
                        startIndex = i;
                    else
                        break;
                }    
            }  
            
            for (int i = tmpCol; i >= 0 && i < BOARD_SIZE;i++) //We find the ending index of the word
            {
                if(tmpCol == BOARD_SIZE - 1) 
                    break;
                else
                {
                    if(tmpBoard[tmpRow].charAt(i) != '-')
                        endIndex = i;
                    else
                        break;
                }    
            }
            word = tmpBoard[tmpRow].substring(startIndex, endIndex + 1); //Build the word
            tmpCol = startIndex;
        }
        String QorC = isChallange ? "C" : "Q";
        lastWord = word;
        row = tmpRow;
        col = tmpCol;
        this.isVertical = isVertical;
        wasLastWordaChallenge = isChallange;
        if(ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&"+ QorC +":" + word + "," + row + "," + col + "," + isVertical)); //'Q':'word,row,col,isVertical'
        return true;
    }

    private void clear()
    {
        board.set("");
        currentPlayerName.set("");
        myTiles.set("");
        myScore.set("");
        playersAndScoresMap.clear();
        gameErrorMessage.set("");
        isGameRunning.set(false);
    }

    public void close()
    {
        clientModel.disconnectedFromHost("endGame");
        gameModel.close();
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