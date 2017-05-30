package game;

import game.playerstate.PlayerState;
import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.game.PlayerGameState;
import game.playerstate.game.PlayerGameState.GameState;
import java.util.Random;
import utilities.messages.Message;
import utilities.messages.PlayerConnected;
import utilities.messages.move.InvalidMove;
import utilities.messages.move.Move;
import utilities.messages.move.ReturnToStonePlacing;

/**
 * Interface for players. It can be a human player or AI.
 * Uses notifyMasterObserverAboutXXX methods to pass moves and messages to RoomServer component which governs over this object.
 * Receives messages through updateMessage/Move() from RoomServer to process.
 * @author Kinga
 */
public abstract class Player implements MoveNotifier{
    /**
     * Master of this component which can be notified about moves and will notify this object about moves.
     */
    protected MoveNotifier master = null;
    /**
     * Master of this component which can be notified about moves and will notify this object about moves.
     */
    protected MoveNotifier slave = null;
    /**
     * Player state. 
     */
    final PlayerState playerState;
    /**
     * Name of player.
     */
    public String Name;
    
    /**
     * Returns master of this object
     * @return master
     */
    protected MoveNotifier getMaster() {
        return master;
    }
    /**
     * Returns slave of this component
     * @return slave
     */
    protected MoveNotifier getSlave() {
        return slave;
    }

    
    
    
    
    /**
     * Default constructor
     * @param master Master of this component
     * @param name Name of player
     * @param state State pointer
     */
    public Player(MoveNotifier master,String name,PlayerState state) {
        setMasterObserver(master);
        this.Name = name;
        this.playerState = state;
        //notifyMasterObserverAboutMessage(new PlayerConnected(name));
    }

    
    
    
    
    @Override
    public void setMasterObserver(MoveNotifier observer) {
        master = observer;
    }
    @Override
    public void addSlaveObserver(MoveNotifier observer) {
        slave = observer;
    }
   

    @Override
    public void notifyMasterObserverAboutMessage(Message msg) {
        if(master!=null)
            master.updateMessage(this, msg);
        else
            System.err.println("Player has no master");
    }

    @Override
    public void notifyMasterObserverAboutMove(Move move) {
        if(master!=null)
            master.updateMove(this, move);
        else
            System.err.println("Player has no master");
    }
    

    /**
     * Changes name of Player.
     * @param Name name of Player.
     */
    public void setName(String Name) {
        if(Name.trim().isEmpty())
            Name = "Player#"+(new Random()).nextInt();
        this.Name = Name;
    }
    /**
     * Returns name of Player
     * @return name of Player
     */
    public String getName() {
        return Name;
    }
    
     /**
     * Changes player game state
     * @param state player game state
     * @return whether it was successful or not [If not then change to given state was illegal]
     */
    public boolean setPlayerGameState(PlayerGameState.GameState state) {
        return this.playerState.setGameState(state);
    }
    /**
     * Changes player Connection state
     * @param state player Connection state
     * @return whether it was successful or not [If not then change to given state was illegal]
     */
    public boolean setPlayerConnectionState(PlayerConnectionState.ConnectionState state) {
        return this.playerState.setConnectionState(state);
    }
    
    /**
     * Returns player Game state
     * @return player Game state
     */
    public PlayerGameState.GameState getPlayerGameState(){
        return playerState.getGameState();
    }
    /**
     * Returns player connection state
     * @return player connection state
     */
    public PlayerConnectionState.ConnectionState getPlayerConnectionState(){
        return playerState.getConnectionState();
    }

    @Override
    public void updateMove(MoveNotifier src, Move move) {
        if(move!=null){
            if(src == getMaster() ){
                if(
                        (getPlayerGameState()!=GameState.LOST)&&
                        (getPlayerGameState()!=GameState.WON)
                ){
                    switch(move.getMoveType()){
                        case SURRENDER:  
                        case PASS:  
                                System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType());
                            break;

                        case TERRITORIES_DISAGREE:
                        case TERRITORIES_CHOOSEN:
                                if((
                                        (getPlayerGameState() == GameState.CHOOSE)||
                                        (getPlayerGameState() == GameState.DISAGREE)||
                                        (getPlayerGameState() == GameState.AGREE)
                                    )    
                                       &&(setPlayerGameState(PlayerGameState.GameState.CHOOSE))
                                )
                                    updateMove_Subclass(src, move);
                                else{
                                    System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                }
                            break;
                        case CHOOSE_TERRITORIES:
                                if((
                                        (getPlayerGameState() == GameState.CHOOSE)||
                                        (getPlayerGameState() == GameState.PASS)||
                                        (getPlayerGameState() == GameState.DISAGREE)||
                                        (getPlayerGameState() == GameState.AGREE)
                                    )    
                                       &&setPlayerGameState(PlayerGameState.GameState.CHOOSE)
                                )
                                    updateMove_Subclass(src, move);
                                else{
                                    System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                }
                            break;
                        case RETURN_TO_STONE_PLACING:
                                if(
                                    (
                                        (getPlayerGameState() == GameState.CHOOSE)||
                                        (getPlayerGameState() == GameState.DISAGREE)||
                                        (getPlayerGameState() == GameState.AGREE)
                                    )    
                                       &&setPlayerGameState(PlayerGameState.GameState.PLACE)
                                )
                                    updateMove_Subclass(src, move);
                                else{
                                    System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                }
                            break;
                        case MAKE_MOVE:
                                if(         
                                        ((getPlayerGameState() == GameState.WAIT)|| 
                                        (getPlayerGameState() == GameState.CHOOSE)|| 
                                        (getPlayerGameState() == GameState.DISAGREE)|| 
                                        (getPlayerGameState() == GameState.AGREE)|| 
                                        (getPlayerGameState() == GameState.PASS)
                                    )
                                    &&setPlayerGameState(PlayerGameState.GameState.PLACE)
                                )
                                    updateMove_Subclass(src, move);
                                else{
                                    System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                }
                            break;
                        case INVALID_MOVE:
                                if(         (getPlayerGameState() == GameState.WAIT)
                                            &&setPlayerGameState(PlayerGameState.GameState.PLACE))
                                    updateMove_Subclass(src, move);
                                else{
                                    System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                }
                            break;     
                        case PLACE_STONE: 
                                if(        
                                        (getPlayerGameState() == GameState.WAIT)||
                                        (getPlayerGameState() == GameState.PASS)
                                )
                                    updateMove_Subclass(src, move);
                                else{
                                    System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                }
                            break;  
                        case VALID_MOVE:
                                if(         (getPlayerGameState() == GameState.WAIT)
                                        &&setPlayerGameState(PlayerGameState.GameState.WAIT)
                                )
                                    updateMove_Subclass(src, move);
                                else{
                                    System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                }
                            break;  
                        case WIN:
                                if(setPlayerGameState(PlayerGameState.GameState.WON))
                                    updateMove_Subclass(src, move);
                                else{
                                    System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                }
                            break; 
                        case LOSE:
                                    if(
                                            (getPlayerGameState() == GameState.AGREE)
                                            &&(setPlayerGameState(PlayerGameState.GameState.LOST))
                                    )
                                        updateMove_Subclass(src, move);
                                    else{
                                        System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                    }
                            break; 
                        case TERRITORIES_AGREE:
                                if(setPlayerGameState(PlayerGameState.GameState.AGREE))
                                    updateMove_Subclass(src, move);
                                else{
                                    System.err.println("[PLAYER]Got illegal move from Server:"+move.getMoveType()+" for current state:"+getPlayerGameState());
                                }
                            break;     
                    }
                }
            }else if(src == getSlave()){
                GameState gs = getPlayerGameState();
                if(
                        (getPlayerGameState()!=GameState.LOST)&&
                        (getPlayerGameState()!=GameState.WON)
                ){
                    switch(move.getMoveType()){
                        //illegal moves from socket:
                        case CHOOSE_TERRITORIES:
                        case INVALID_MOVE:
                        case MAKE_MOVE:
                        case VALID_MOVE:
                        case LOSE:
                        case WIN:
                                System.err.println("[PLAYER]Got illegal move from Socket:"+move.getMoveType());
                                notifySlaveObserversAboutMove(new InvalidMove());
                            break;

                        case TERRITORIES_DISAGREE:
                                if((
                                        (getPlayerGameState() == GameState.CHOOSE)||
                                        (getPlayerGameState() == GameState.DISAGREE)||
                                        (getPlayerGameState() == GameState.AGREE)
                                    )    
                                       &&setPlayerGameState(PlayerGameState.GameState.CHOOSE)
                                )
                                    notifyMasterObserverAboutMove(move);
                                else{
                                    notifySlaveObserversAboutMove(new InvalidMove());
                                }
                            break;
                        case RETURN_TO_STONE_PLACING:
                                if(
                                    (
                                        (getPlayerGameState() == GameState.CHOOSE)||
                                        (getPlayerGameState() == GameState.DISAGREE)||
                                        (getPlayerGameState() == GameState.AGREE)
                                    )    
                                       &&(setPlayerGameState(PlayerGameState.GameState.WAIT))
                                )
                                    notifyMasterObserverAboutMove(move);
                                else{
                                    notifySlaveObserversAboutMove(new InvalidMove());
                                }
                            break;
                        case PLACE_STONE: 
                                if((
                                        (getPlayerGameState() == GameState.PLACE)
                                    )    
                                       &&setPlayerGameState(PlayerGameState.GameState.WAIT)
                                )
                                    notifyMasterObserverAboutMove(move);
                                else{
                                    notifySlaveObserversAboutMove(new InvalidMove());
                                }
                            break;
                        case PASS:     
                                if(
                                        (getPlayerGameState()==GameState.PLACE)
                                        &&(setPlayerGameState(PlayerGameState.GameState.PASS))
                                        
                                )
                                    notifyMasterObserverAboutMove(move);
                                else{
                                    notifySlaveObserversAboutMove(new InvalidMove());
                                }
                            break;
                        case SURRENDER:
                                if(setPlayerGameState(PlayerGameState.GameState.LOST))
                                    notifyMasterObserverAboutMove(move);
                                else{
                                    notifySlaveObserversAboutMove(new InvalidMove());
                                }
                            break;   
                        case TERRITORIES_CHOOSEN:
                        case TERRITORIES_AGREE:
                                if(setPlayerGameState(PlayerGameState.GameState.AGREE))
                                    notifyMasterObserverAboutMove(move);
                                else{
                                    notifySlaveObserversAboutMove(new InvalidMove());
                                }
                            break;   
                    }
                }
            }else{
                System.err.println("[PLAYER]Received move from illegal source: "+src+"  M["+master+"] S["+slave+"]");
            }
        }else{
            System.err.println("[PLAYER]Received NULL move from"+src);
        }
    }
    /**
     * Method called after super class checks Messages and changes states.
     * @param src
     * @param move
     */
    public abstract void updateMove_Subclass(MoveNotifier src, Move move);
    
    
    
}
