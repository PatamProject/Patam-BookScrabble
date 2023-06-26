package bookscrabble.client.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.Observable;
import java.util.Observer;

import bookscrabble.client.model.ClientCommunications;
import bookscrabble.client.model.ClientModel;
import bookscrabble.client.model.GameModel;
import bookscrabble.client.model.MyHostServer;

public class ViewModel extends Observable implements Observer {
    ClientModel clientModel; //Model representation for client info
    GameModel gameModel; // Model representation for game info
    //Game info
    public BooleanProperty isConnectedToHost;
    public StringProperty board, currentPlayerName, myTiles, gameErrorMessage;
    public StringProperty myScore;
    public MapProperty<String, Integer> playersAndScoresMap;
    //Client info
    public BooleanProperty isHost;
    public StringProperty myName, hostIP, BsIP, clientErrorMessage;
    public StringProperty hostPort, BsPort;

    //Lobby info
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
                {
                    lobbyMessage.set(gameModel.getPlayerUpdateMessage());
                    setChanged();
                    notifyObservers("playerUpdateMessage");
                }
            }
        }
        if (o.equals(clientModel)) {
            isConnectedToHost.set(clientModel.isConnectedToHost);
            clientErrorMessage.set(clientModel.getErrorMessage());
        }
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
    public void sendStartGameRequest() {ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&startGame");}
    public void sendEndgameRequest() {ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&endGame");}

    //Player options
    public void sendLeaveRequest() {ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&leave");}
    public void sendSkipTurnRequest() {ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&skipTurn");} //'Q':'word,row,col,isVertical'
    public void sendWordPlacementRequest(String word, int row, int col, boolean isVertical)
    {
        lastWord = word;
        this.row = row;
        this.col = col;
        this.isVertical = isVertical;
        ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&Q:" + word + "," + row + "," + col + "," + isVertical);
    }

    public void sendChallengeRequest()
    {
        ClientCommunications.sendAMessage(clientModel.getMyConnectionToHost().getMyID(),myName.get() + "&C:" + lastWord + "," + row + "," + col + "," + isVertical);
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