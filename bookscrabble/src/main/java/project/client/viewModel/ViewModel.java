package project.client.viewModel;

import javafx.beans.property.*;
import project.client.model.ClientCommunications;
import project.client.model.ClientModel;
import project.client.model.GameModel;

import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {
    ClientModel clientModel; //Model representation for client info
    GameModel gameModel; // Model representation for game info
    //Game info
    public StringProperty board, currentPlayerName, myTiles, playersAndScores;
    public IntegerProperty myScore;
    //Client info
    public BooleanProperty isHost;
    public StringProperty myName, hostIP, BsIP;
    public IntegerProperty hostPort, BsPort;

    //Related to word placement
    public BooleanProperty wasLastWordValid;
    public String lastWord;
    public int row = 0;
    public int col = 0;
    public boolean isVertical;

    public ViewModel(GameModel gameModel, ClientModel clientModel) { //Ctor
        this.clientModel = clientModel;
        this.gameModel = gameModel;
        this.board = new SimpleStringProperty(); // AB-C----&D--E----&----T.... (A,B,C,D,E,T are tiles, - is empty, & is new line)
        this.currentPlayerName = new SimpleStringProperty();
        this.myScore = new SimpleIntegerProperty();
        this.myTiles = new SimpleStringProperty();
        this.myName = new SimpleStringProperty();
        this.playersAndScores = new SimpleStringProperty(); // name1,score1-name2,score2-... 
        this.isHost = new SimpleBooleanProperty();
        this.hostIP = new SimpleStringProperty();
        this.hostPort = new SimpleIntegerProperty();
        this.BsIP = new SimpleStringProperty();
        this.BsPort = new SimpleIntegerProperty();
        this.wasLastWordValid = new SimpleBooleanProperty();
    }

    @Override
    public void update(Observable o, Object arg) { //Receiving updates from the class observables
        if (o.equals(gameModel)) {
            if(arg.equals(false)) //failedWordPlacement
                wasLastWordValid.set(false);
            else // True / null
            {
                if(arg.equals(true)) //successfulWordPlacement
                    wasLastWordValid.set(true);

                board.set(gameModel.getBoard());
                currentPlayerName.set(gameModel.getCurrentPlayersName());
                myTiles.set(gameModel.getMyTiles());
                playersAndScores.set(gameModel.getPlayersAndScoresAsString());
                myScore.set(gameModel.getMyScore());
            }
        }
        if (o.equals(clientModel)) {
            isHost.set(clientModel.isHost());
            myName.set(ClientModel.getName());
            hostIP.set(clientModel.getHostIP());
            BsIP.set(clientModel.getBsIP());
            hostPort.set(clientModel.getHostPort());
            BsPort.set(clientModel.getBsPort());
        }
    }

    //Updates from View -> Model//
    public void setIfHost() {clientModel.setIfHost(isHost.get());}
    public void setMyName() {clientModel.setMyName(myName.get());}    
    public void setHostIP() {clientModel.setHostIP(hostIP.get());}
    public void setHostPort() {clientModel.setHostPort(hostPort.get());}
    public void setBsIP() {clientModel.setBsIP(BsIP.get());}
    public void setBsPort() {clientModel.setBsPort(BsPort.get());}
    public void createClient() {clientModel.createClient(isHost.get(), hostIP.get(), hostPort.get(), myName.get());}

    //Host only options
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