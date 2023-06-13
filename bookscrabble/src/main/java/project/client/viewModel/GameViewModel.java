package project.client.viewModel;

import javafx.beans.property.*;
import project.client.model.GameModel;
import java.util.Observable;
import java.util.Observer;

public class GameViewModel implements Observer {
    GameModel gameModel;
    public StringProperty board, currentPlayerName, myTiles, myName;
    public BooleanProperty isMyTurn;
    public IntegerProperty myScore;

    public GameViewModel(GameModel gameModel) {
        this.gameModel = gameModel;
        this.board = new SimpleStringProperty();
        this.currentPlayerName = new SimpleStringProperty();
        this.isMyTurn = new SimpleBooleanProperty();
        this.myScore = new SimpleIntegerProperty();
        this.myTiles = new SimpleStringProperty();
        this.myName = new SimpleStringProperty();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o.equals(gameModel)) {
            myScore.set(gameModel.getMyScore());
            myTiles.set(gameModel.getMyTiles());
            board.set(gameModel.getBoard());
            currentPlayerName.set(gameModel.getCurrentPlayersName());
            isMyTurn.set(gameModel.getIsMyTurn());
        }
    }
/* 
    //Updates from View -> Model//

    - Set my own name
    - Set ifHost
    //Host only options
    - Send BSS ip and port to connect
    - Set lobby password?
    - Kick player?
    - Send start game request
    - Send end game request
    //Player options
    - Send join request(ip + port + password?)? (Inside ClientModel ctor)
    - send leave request
    - send word placement
    - send skipTurn
    - send challenge option

*/

}