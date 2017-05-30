/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import static com.sun.corba.se.impl.util.Utility.printStackTrace;
import game.AIPlayer;
import game.Board;
import game.HumanPlayer;
import game.MoveNotifier;
import game.Player;
import game.playerstate.PlayerState;
import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.game.PlayerGameState;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.Socket;
import utilities.SocketException;
import utilities.messages.Message;
import utilities.messages.MessageType;
import utilities.messages.PlayerConnected;
import utilities.messages.ReturnBoard;
import utilities.messages.ReturnOpponentName;
import utilities.messages.ServerShutdown;
import utilities.messages.client.ReturnGameState;
import utilities.messages.data.FieldInfo;
import utilities.messages.data.RoomInfo;
import utilities.messages.move.ChooseTerritories;
import utilities.messages.move.InvalidMove;
import utilities.messages.move.Lose;
import utilities.messages.move.MakeMove;
import utilities.messages.move.Move;
import utilities.messages.move.PlaceStone;
import utilities.messages.move.TerritoriesAgree;
import utilities.messages.move.TerritoriesChosen;
import utilities.messages.move.TerritoriesDisagree;
import utilities.messages.move.ValidMove;
import utilities.messages.move.Win;

/**
 * Server room handler. Controls the game state and player movements.
 * checks if they do not cheat and sends over messages.
 * @author n1t4chi
 */
public class RoomServer extends Server implements MoveNotifier{
    /**
     * Board
     */
    private final Board board;
    /**
     * Black player
     */
    private Player black_player=null;
    
    /**
     * White player
     */
    private Player white_player=null;
    
    /**
     * Server socket for black player
     */
    ServerSocket socket_black_player=null;
    /**
     * Server socket for white player
     */
    ServerSocket socket_white_player=null;
    /**
     * Name of this room.
     */
    private final String RoomName;
    /**
     * Executor for waiting for player.
     */
    private ScheduledExecutorService ses;
    
    /**
     * ScheduledFuture for room destroyer which disposes this room after 10 minutes of not having any human players.
     */
    private ScheduledFuture RoomDestroyer = null;
    
    /**
     * Whether the black recently passed his move.
     */
    private boolean black_pass = false;
    /**
     * Whether the white passed his move.
     */
    private boolean white_pass = false;
    
    /**
     * Last received list of territories.
     */
    private ArrayList<FieldInfo> territories_list=null;
    
    private final PlayerState player_state_black;
    private final PlayerState player_state_white;
    
    
    /**
     * Returns info of this room
     * @return info of this room
     */
    public RoomInfo getRoomInfo(){
        
        if(isWorking()){
            return new RoomInfo(
                    (socket_black_player==null)?-1:socket_black_player.getLocalPort(),
                    (socket_white_player==null)?-1:socket_white_player.getLocalPort(),
                    board.getSize(), 
                    RoomName, 
                    (black_player==null)?"":black_player.getName(),
                    (white_player==null)?"":white_player.getName()
            );
        }else{
            return null;
        }
    }
    
    
    private void createAI(boolean black){
        if(black){
            black_player = new AIPlayer(this,player_state_black, true,board.getSize());
            printSys("AI is set as black player");
            updateMessage(black_player, new PlayerConnected("Player"));
        }else{    
            white_player = new AIPlayer(this,player_state_white, false,board.getSize());
            printSys("AI is set as white player");
            updateMessage(white_player, new PlayerConnected("Player"));
        }
    }
    
    /**
     * Creates thread that waits for player and initiates connection. 
     * @param black whether to wait for black player
     */
    private boolean  connectPlayer(boolean black){
        if( (ses!=null)&&(!ses.isShutdown()) ){
            ses.schedule(()->{
                connectPlayer(black,0);
            }, 1, TimeUnit.MICROSECONDS);
            return true;
        }else
            return false;
    }
    
    /**
     * Waits for player and initiates connection.
     * @param black whether to wait for black player
     * @param interator how many times it tried to establish connection with player, after 5 fails the AI player is created.
     */
    private void connectPlayer(boolean black,int iterator){
        try {
            Socket soc = new Socket(this,(black)?socket_black_player.accept():socket_white_player.accept(),50);
            if(black){
                black_player = new HumanPlayer(this, "Player", soc,player_state_black);
                //soc.addObserver((HumanPlayer)black_player);
                printSys("Black player connected");
                //
                //updateMessage(black_player, new PlayerConnected("Player"));
            }else{
                white_player = new HumanPlayer(this, "Player", soc,player_state_white);
                //soc.addObserver((HumanPlayer)white_player);
                printSys("White player connected");
                //updateMessage(black_player, new PlayerConnected("Player"));
            }
        } catch (IOException ex) {
            if(iterator<5){
                connectPlayer(black, iterator++);
            }else{
                printErr("Cannot set up socket for player. Creating AI player instead. \nError:"+ex.getLocalizedMessage());
                if(black){
                    try {
                        socket_black_player.close();
                    } catch (IOException ex1) {
                        printErr("Error:"+ex1.getLocalizedMessage());
                    }
                    socket_black_player=null;
                    createAI(true);
                }else{
                    try {
                        socket_white_player.close();
                    } catch (IOException ex1) {
                        printErr("Error:"+ex1.getLocalizedMessage());
                    }
                    socket_white_player=null;
                    createAI(false);
                }
            }
            
        }
     /*   if(RoomDestroyer!=null){
            RoomDestroyer.cancel(true);
        }*/
        
    }

    
    
    private boolean GameStarted;
    
    /**
     * Default Constructor
     * @param master master of this component. 
     * Should be used as a way to close Room Server with ability to inform Users. 
     * Also RoomServer will notify Master when the game ends and it can be disposed naturally.
     * @param size Size of board
     * @param BlackAI Whether black player is AI
     * @param WhiteAI Whether white player is AI
     * @param RoomName Name of room
     * @throws NullPointerException If info is a null pointer
     */
    public RoomServer(MoveNotifier master, Board.Size size, boolean BlackAI,boolean WhiteAI,String RoomName) {
        super("System["+RoomName+"]:","ERROR["+RoomName+"]:");
        GameStarted = false;
        this.board = new Board(size);
        this.RoomName=RoomName;
        ses = new ScheduledThreadPoolExecutor(4);
        player_state_black = new PlayerState(PlayerState.PlayerType.AI);
        player_state_black.setConnectionState(PlayerConnectionState.ConnectionState.CONNECTED);
        player_state_black.forceSetGameState(PlayerGameState.GameState.WAIT);
        player_state_white = new PlayerState(PlayerState.PlayerType.AI);
        player_state_white.setConnectionState(PlayerConnectionState.ConnectionState.CONNECTED);
        player_state_white.forceSetGameState(PlayerGameState.GameState.WAIT);
        setMasterObserver(master);
        if(BlackAI){
            createAI(true);
        }else{
            try {
                socket_black_player = new ServerSocket(0);
                connectPlayer(true);
                RoomDestroyer = null;
            } catch (IOException ex) {
                printErr("Couldn't make Black player:"+ex.getLocalizedMessage());
                socket_black_player=null;
                createAI(true);
            }
        }
        if(WhiteAI){
            createAI(false);
        }else{
            try {
                socket_white_player = new ServerSocket(0);
                connectPlayer(false);
                RoomDestroyer = null;
            } catch (IOException ex) {
                printErr("Couldn't make white player:"+ex.getLocalizedMessage());
                socket_white_player=null;
                createAI(false);
            }
        }
        scheduleRoomDestroyer();
    }
    boolean dispose_on_next_cycle = false;
    private void scheduleRoomDestroyer(){
        if((ses!=null)&&(RoomDestroyer==null)){
            printSys("Room will be disposed in 10 minutes if noone joins.");
            RoomDestroyer = ses.scheduleAtFixedRate(()->{
                if(((socket_black_player==null)||(black_player==null))&&((socket_white_player==null)||(white_player==null))){
                    if(dispose_on_next_cycle){
                        try {
                            
                            dispose();
                        } catch (Exception ex) {
                            printErr("Couldn't dispose after inactivity");
                        }
                    }
                    dispose_on_next_cycle = true;
                    printSys("Room will be disposed in 5 minutes if no player is detected then.");
                }else{
                    printSys("Room will not be disposed in 5 minutes.");
                    dispose_on_next_cycle = false;
                }
            },5, 5, TimeUnit.MINUTES);
        }
        
    }
    
    @Override
    public synchronized void updateMove(MoveNotifier src, Move move) {
        if((src!=null)&&(move!=null)){
            printSys("Received move from "+((src == black_player)?"black":"white")+" player, move:"+move.getMoveType());
            //printStackTrace();
            if(src == black_player)
                black_pass=false;
            else if (src == white_player)
                white_pass=false;
            switch(move.getMoveType()){
                case PASS:
                        if(src == black_player){
                            black_pass=true;
                        }else{
                            white_pass=true;
                        }
                        if(white_pass&&black_pass){
                            black_pass=white_pass=false;
                            notifySlaveObserversAboutMove(new ChooseTerritories());
                        }else{
                            notifySlaveObserverAboutMove_SendToOppositePlayer(src, new MakeMove());
                        }
                    break;  
                case PLACE_STONE:
                        if(move instanceof PlaceStone){
                            if(board.isLegalMove((PlaceStone)move)){
                                board.changeField((PlaceStone)move);
                                notifySlaveObserverAboutMove_SendToTarget(src, new ValidMove());
                                notifySlaveObserverAboutMove_SendToOppositePlayer(src, move);
                                notifySlaveObserverAboutMove_SendToOppositePlayer(src, new MakeMove());
                            }else{
                                notifySlaveObserverAboutMove_SendToTarget(src, new InvalidMove() );
                                notifySlaveObserverAboutMove_SendToOppositePlayer(src, new MakeMove());
                            }
                        }
                    break;
                case RETURN_TO_STONE_PLACING:
                        notifySlaveObserverAboutMove_SendToOppositePlayer(src,move);
                        notifySlaveObserverAboutMove_SendToOppositePlayer(src, new MakeMove());
                    break;
                case SURRENDER:
                        notifySlaveObserverAboutMove_SendToOppositePlayer(src, new Win());
                        try {
                            Thread.sleep(1000);
                            dispose();
                        } catch (Exception ex) {
                            printErr("Couldn't dispose room after game ended:"+ex);
                        }
                    break;

                case TERRITORIES_CHOOSEN:
                        if(move instanceof TerritoriesChosen){
                            territories_list = ((TerritoriesChosen)move).getTerritoriesList();
                            notifySlaveObserverAboutMove_SendToOppositePlayer(src, move);
                        }else{
                            printErr("Received illegal instance for TERRITORIES_CHOOSEN from "+src);
                        }
                    break;
                case TERRITORIES_DISAGREE:
                        notifySlaveObserverAboutMove_SendToOppositePlayer(src, move);
                        territories_list = null;
                        //notifySlaveObserverAboutMove_SendToTarget(src, (new ChooseTerritories()));
                    break;
                case TERRITORIES_AGREE:
                        if(territories_list!=null){
                            notifySlaveObserverAboutMove_SendToOppositePlayer(src, new TerritoriesAgree());
                            double b_score = board.calculateScore(true,territories_list);
                            double w_score = board.calculateScore(false,territories_list);
                            if(b_score > w_score ){
                                notifySlaveObserverAboutMove_SendToWhitePlayer(new Lose());
                                notifySlaveObserverAboutMove_SendToBlackPlayer(new Win());
                            } else if ( b_score < w_score){
                                notifySlaveObserverAboutMove_SendToWhitePlayer(new Win());
                                notifySlaveObserverAboutMove_SendToBlackPlayer(new Lose());
                            }else{
                                notifySlaveObserverAboutMove_SendToWhitePlayer(new Win());
                                notifySlaveObserverAboutMove_SendToBlackPlayer(new Win());
                                //throw new UnsupportedOperationException("to do");   //won't ever happen //They said Trump won't ever become President
                            }
                            try {
                                Thread.sleep(1000);
                                dispose();
                            } catch (Exception ex) {
                                printErr("Couldn't dispose room after game ended:"+ex);
                            }
                        }   

                    break;
                case CHOOSE_TERRITORIES:
                case INVALID_MOVE:
                case LOSE:
                case MAKE_MOVE:
                case VALID_MOVE:
                case WIN:
                default:
                        printErr("Received illegal move type ["+move.getMoveType()+"] from "+src);
                    break;    
            }
        }
    }

    @Override
    public synchronized void updateMessage(MoveNotifier src, Message msg) {
        if((src!=null)&&(msg!=null)){
            if(src instanceof LobbyServer){
                if(msg.getMessageType() == MessageType.SERVER_SHUTDOWN){
                    try {
                        dispose();
                    } catch (Exception ex) {
                        printErr("Cannot dispose Room Server:"+RoomName);
                    }
                }
            }else if((src == black_player) || (src == white_player)){
                printSys("Received message from "+((src == black_player)?"black":"white")+" player, message:"+msg.getMessageType());
                switch(msg.getMessageType()){
                    case PLAYER_DISCONNECTED:
                            try{
                                notifySlaveObserverAboutMessage_SendToOppositePlayer(src, msg);
                          /*  }catch(Exception ex){
                                printErr("Cannot wait for players to receive messages:"+ex);*/
                            }finally{
                                if(src == black_player){
                                    printSys("Black player disconnected");
                                    //black_player.setName("");
                                    black_player = null;
                                    connectPlayer(true);
                                    printSys("waiting for new black player");
                                }else if (src == white_player){
                                    printSys("White player disconnected");
                                    //white_player.setName("");
                                    white_player = null;
                                    connectPlayer(false);
                                    printSys("waiting for new white player");
                                } 
                            }
                          /*  try {
                                dispose();
                            } catch (Exception ex) {
                                printErr("Couldn't dispose room after game ended");
                            }
                            printSys("disposed ");
                            //there are no breaks on this message case
                        */
                        break; //NO BREAK HERE
                    case RETURN_OPPONENT_NAME:
                            notifySlaveObserverAboutMessage_SendToOppositePlayer(src, msg);
                        break;
                    case PLAYER_CONNECTED:
                            if(msg instanceof PlayerConnected){
                                if(src == black_player){
                                    black_player.setName(((PlayerConnected) msg).getName());
                                }else if (src == white_player)
                                    white_player.setName(((PlayerConnected) msg).getName());
                                if((black_player!=null)&&(white_player!=null)&&(!GameStarted)){
                                    GameStarted=true;
                                    notifySlaveObserverAboutMove_SendToBlackPlayer(new MakeMove());
                                }
                            }//the ride never ends
                            notifySlaveObserverAboutMessage_SendToOppositePlayer(src, msg);
                        break;//yes it does
                    case CHAT:
                            notifySlaveObserverAboutMessage_SendToOppositePlayer(src, msg);
                        break;
                    case REQUEST_GAME_STATE:
                            if(src == black_player)
                                notifySlaveObserverAboutMessage_SendToBlackPlayer(new ReturnGameState(black_player.getPlayerGameState()));
                            else if(src == black_player)
                                notifySlaveObserverAboutMessage_SendToWhitePlayer(new ReturnGameState(white_player.getPlayerGameState()));
                        break;
                    case REQUEST_BOARD:
                            notifySlaveObserverAboutMessage_SendToTarget(src,new ReturnBoard(board));
                        break;
                    case REQUEST_OPPONENT_NAME:
                            notifySlaveObserverAboutMessage_SendToTarget(src,new ReturnOpponentName( 
                                (src==white_player)?
                                        ((black_player == null)?"":black_player.getName()):
                                        ((white_player == null)?"":white_player.getName())
                            ));
                        break;  
                    case MOVE:
                            if(msg instanceof Move){
                                updateMove(src,(Move)msg);
                            }else
                                printErr("Received illegal instance for Move type from "+src+" move:"+msg);
                        break;
                    default:
                            printErr("Received illegal message type ["+(msg.getMessageType())+"] from "+src);
                        break;
                }
            }else{
                printErr("Received message from illegal source: "+src);
            }
        }else{
            printErr("Received illegal update: source["+src+"] msg["+msg+"]");
        }
    }
    
    @Override
    public synchronized void notifySlaveObserversAboutMove(Move move) {
        notifySlaveObserverAboutMove_SendToTarget(black_player, move);
        notifySlaveObserverAboutMove_SendToTarget(white_player, move);
    }
    /**
     * Notifies given player about move
     * @param target player to send message to
     * @param move  move to send
     */
    public synchronized void notifySlaveObserverAboutMove_SendToTarget(MoveNotifier target,Move move) {
        if((move !=null)&&(target!=null)){
            printSys("Sending move to "+((target == black_player)?"black":"white")+" player, move:"+move.getMoveType());
            target.updateMove(this, move);
        }else{
           // printErr("Null pointer["+target+"]["+move+"]");
        }
    }
    /**
     * Notifies black player about message
     * @param target player to send message to
     * @param msg message to send
     */
    public synchronized void notifySlaveObserverAboutMessage_SendToTarget(MoveNotifier target,Message msg) {
        if((msg !=null)&&(target!=null)){ 
            printSys("Sending message to "+((target == black_player)?"black":"white")+" player, message:"+msg.getMessageType());
            target.updateMessage(this, msg);
        }else{
           //printErr("Null pointer ["+target+"]["+msg+"]");
        }
    }
    /**
     * Notifies black player about move
     * @param move  move to send
     */
    public synchronized void notifySlaveObserverAboutMove_SendToBlackPlayer(Move move) {
        notifySlaveObserverAboutMove_SendToTarget(black_player, move);
    }
    /**
     * Notifies black player about message
     * @param msg message to send
     */
    public synchronized void notifySlaveObserverAboutMessage_SendToBlackPlayer(Message msg) {
        notifySlaveObserverAboutMessage_SendToTarget(black_player, msg);
    }
    /**
     * Notifies white player about move
     * @param move  move to send
     */
    public synchronized void notifySlaveObserverAboutMove_SendToWhitePlayer(Move move) {
        notifySlaveObserverAboutMove_SendToTarget(white_player, move);
    }
    /**
     * Notifies white player about message
     * @param msg message to send
     */
    public synchronized void notifySlaveObserverAboutMessage_SendToWhitePlayer(Message msg) {
        notifySlaveObserverAboutMessage_SendToTarget(white_player, msg);
    }
    /**
     * Notifies opposite player to given player about move
     * @param player player
     * @param move  move to send
     */
    public synchronized void notifySlaveObserverAboutMove_SendToOppositePlayer(MoveNotifier player, Move move) {
        if(player!=null){
            if(player==white_player)
                notifySlaveObserverAboutMove_SendToTarget(black_player, move);
            else if(player==black_player)
                notifySlaveObserverAboutMove_SendToTarget(white_player, move);
            else{
                printErr("Illegal player:"+player);
            }
        }else{
            printErr("Null source");
        }
    }
    /**
     * Notifies opposite player about message
     * @param player Source of message, null pointer does nothing.
     * @param msg message to send
     */
    public synchronized void notifySlaveObserverAboutMessage_SendToOppositePlayer(MoveNotifier player, Message msg) {
        if(player!=null){
            if(player==white_player)
                notifySlaveObserverAboutMessage_SendToTarget(black_player, msg);
            else if(player==black_player)
                notifySlaveObserverAboutMessage_SendToTarget(white_player, msg);
            else{
                printErr("Illegal player:"+player);
            }
        }else{
            printErr("Null source");
        }
    }

    @Override
    public synchronized void notifySlaveObserversAboutMessage(Message msg) {
        notifySlaveObserverAboutMessage_SendToTarget(black_player, msg);
        notifySlaveObserverAboutMessage_SendToTarget(white_player, msg);
    }
    
    
    
    
    /**
     * Master of this component.
     */
    private MoveNotifier master;
    @Override
    public void setMasterObserver(MoveNotifier observer) {
        this.master = observer;
    }
    @Override
    public void notifyMasterObserverAboutMessage(Message msg) {
        if(master!=null)
            master.updateMessage(this, msg);
        else
            printErr("RoomServer has no master");
    }

    @Override
    /**
     * Returns true at all times.
     * @param 
     */
    public boolean isWorking() {
        return ((white_player!=null)||(socket_white_player!=null))&&((black_player!=null)||(socket_black_player!=null));
    }

    @Override
    protected void disposeSubclass() throws Exception {
        notifyMasterObserverAboutMessage(new ServerShutdown());
        if(black_player instanceof HumanPlayer){
            ((HumanPlayer)black_player).close();
        }
        if(white_player instanceof HumanPlayer){
            ((HumanPlayer)white_player).close();
        }
        if(RoomDestroyer!=null)
            RoomDestroyer.cancel(true);
        RoomDestroyer=null;
        if(ses!=null)
            ses.shutdownNow();
        ses = null;
        
        try{
            //notifySlaveObserversAboutMessage(new ServerShutdown());

            if(socket_black_player!=null)
                socket_black_player.close();
            if(socket_white_player!=null)
                socket_white_player.close();
            socket_black_player=null;
            socket_white_player=null;
            black_player = null;
            white_player = null;
        }catch(IOException | SocketException ex){
            printErr("Error on closing the sockets: "+ex.getLocalizedMessage());
            
        }
    }

    @Override
    public String toString() {
        return "Room["+(RoomName)+"]";
    }

    
    
    @Override
    public void interpret(String command, String... parameters) {}

    @Override
    public void interpret(String input) {}


    @Override
    /**
     * does nothing
     */
    protected void initSubclass() throws Exception {}

    @Override
    /**
     * does nothing
     */
    protected void restartSubclass() throws Exception {}

    @Override
    /**
     * does nothing
     */
    public void addSlaveObserver(MoveNotifier observer) {}
    @Override
    /**
     * does nothing
     */
    public void notifyMasterObserverAboutMove(Move move) {}
}
