/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.room;

import game.MoveNotifier;
import game.Board;
import game.playerstate.PlayerState;
import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.game.PlayerGameState;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import utilities.Socket;
import utilities.SocketException;
import utilities.messages.ChatMessage;
import utilities.messages.Message;
import utilities.messages.MessageType;
import static utilities.messages.MessageType.REQUEST_GAME_STATE;
import static utilities.messages.MessageType.REQUEST_PING;
import static utilities.messages.MessageType.REQUEST_SCORE;
import utilities.messages.PlayerConnected;
import utilities.messages.move.Move;
import utilities.messages.RequestBoard;
import utilities.messages.RequestOpponentName;
import utilities.messages.ReturnBoard;
import utilities.messages.ReturnOpponentName;
import utilities.messages.client.RequestGameState;
import utilities.messages.client.ReturnConnectionState;
import utilities.messages.client.ReturnGameState;
import utilities.messages.client.ReturnLegalPlaces;
import utilities.messages.client.ReturnScore;
import utilities.messages.data.FieldInfo;
import utilities.messages.move.ChooseTerritories;
import utilities.messages.move.MakeMove;
import utilities.messages.move.PlaceStone;
import utilities.messages.move.Surrender;
import utilities.messages.move.TerritoriesChosen;

/**
 * GameHandler handler class.
 * @author n1t4chi
 */
public class GameHandler implements MoveNotifier{
    /**
     * Socket for communication.
     */
    private Socket socket=null;
    /**
     * Master notifier. Same as socket.
     */
    private MoveNotifier master;
    /**
     * Board for game.
     */
    private final Board board;
    /**
     * GameHandler and connection state handler.
     */
    private final PlayerState state;
    
    /**
     * GUI
     */
    private RoomGUI gui;
    /**
     * Slave notifier. Same as gui.
     */
    private MoveNotifier slave;
    
    /**
     * Whether the player plays black stones or not.
     */
    private final boolean isBlack;
    /**
     * Name of player.
     */
    private final String name;
    /**
     * IP of a server.
     */
    private final String ip;
    /**
     * port of server.
     */
    private final int port;   
    
    /**
     * Interval on socket checking.
     */
    private final static int INTERVAL = 10;
    
    private PlaceStone lastMove;
    
    /**
     * Default constructor
     * @param ip IP of a server
     * @param port Port of a room
     * @param black Whether this player plays black stones
     * @param Name Name of player.
     * @param size Size of a board.
     * @throws java.io.IOException If connecting to room failed.
     */
    public GameHandler(String ip,int port,boolean black,String Name,Board.Size size) throws IOException {
        if((ip==null)||(Name==null)){
            throw new NullPointerException("One of arguments is null pointer");
        }
        if(port<0||port>65535){
            throw new IllegalArgumentException("Invalid port");
        }
        this.ip=ip;
        this.port=port;
        this.name = Name;
        this.board = new Board(size);
        isBlack = black;
        state = new PlayerState(PlayerState.PlayerType.HUMAN);
        state.setConnectionState(PlayerConnectionState.ConnectionState.CONNECTED);
        state.setGameState(PlayerGameState.GameState.WAIT);
        if(connect(false)){
            for(int i=0;i<5&&(socket==null);i++){
                try{
                    socket = new Socket(this,ip,port,INTERVAL);    
                    setMasterObserver(socket);
                }catch(SocketException ex){
                    System.err.println("ERROR could not connect"+ex.getLocalizedMessage()+"\nAttemp "+(i+1)+" out of 5");
                }
            }
            if(socket==null){
                throw new IOException("Could not connect to server");
            }

            SwingUtilities.invokeLater(() -> {
                try{
                    gui=new RoomGUI(this,Name,board,black, size);
                    addSlaveObserver(gui);
                    gui.setVisible(true);
                    gui.setEnabled(true);
                    boolean updated = updateWholeBoard(false);
                    notifyMasterObserverAboutMessage(new ReturnOpponentName(Name));
                }catch(Exception ex){
                    System.err.println("Exception on initialising GUI:"+ex);
                }
            });
        }else{
            throw new IOException("Failed to connect to server");
        }
    }
    
    /**
     * Returns board.
     * @return board.
     */
    public Board.BoardField[][] getBoard(){
        return board.getBoard();
    }
    
    
 /*   /**
     * Checks if it is legal move.
     * @param x
     * @param y
     * @return 
     */
  /*  public boolean legalMove(int x, int y){
        return board.isLegalMove(Board.BoardField.BLACK, x, y);
    }*/

    

    /**
     * Tries to reconnect to server. 
     * @return whether reconnection and board retrieving was successful or not.
     */
    private boolean reconnect(){
        boolean rtrn = false;
        if(socket!=null){
            for(int i=0;i<5&&(!socket.isConnected());i++ ){
                try{ 
                    rtrn = socket.Reconnect();
                }catch(SocketException ex){}
            }
        }else{
            return connect();
        }
        if(socket.isConnected()){
            state.setConnectionState(PlayerConnectionState.ConnectionState.RECONNECTED);
            if(checkConnection()){
                if(updateWholeBoard(true)){
                    rtrn = true;
                    state.setConnectionState(PlayerConnectionState.ConnectionState.CONNECTED);
                }else{
                    rtrn = false;
                }
            }
        }
        return rtrn;
    }
    
    /**
     * Checks whether socket is properly working and does not fail ping 10 times.
     * @return true if working connection was established
     */
    private boolean checkConnection(){
        if((socket!=null)&&(socket.isConnected())){
            int failures = 0;
            for(int i=0 ; i<10 ; i++){
                if(socket.checkPing()==-1){
                    failures++;
                }
            }
            return failures<10;      
        }else{
                return false;
        }
    }
    /**
     * Connects socket to server.
     * @return true if connection was established.
     */
    private boolean connect(boolean loadBoard){
        if(socket!=null)
            socket.Disconnect();
        for(int i=0;i<5&&(socket==null);i++ ){
            try{ 
                socket = new Socket(this,ip, port,INTERVAL);
                setMasterObserver(socket);
            }catch(SocketException ex){}
        }
        if(checkConnection()&&(!loadBoard||updateWholeBoard(true))){
            state.setConnectionState(PlayerConnectionState.ConnectionState.CONNECTED);
            return true;
        }else{
            state.setConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED);
            socket.Disconnect();
            socket = null;
            return false;
        }
    };
    /**
     * Connects socket to server.
     * @return true if connection was established.
     */
    private boolean connect(){
        return connect(true);
    }
    /**
     * Updates whole board,retrieves opponent name from server and sends again client nickname.
     * @return Whether it was successful or not.
     */
    private boolean updateWholeBoard(boolean force){
        boolean got_board = false,got_state = false;
        if((socket!=null)&&(socket.isConnected())){
            notifyMasterObserverAboutMessage(new PlayerConnected(name));
            notifyMasterObserverAboutMessage(new RequestOpponentName());
            
            notifyMasterObserverAboutMessage(new RequestBoard());
            if(force){
                Message msg =socket.requestMessage(MessageType.RETURN_BOARD);
                if(msg!=null){
                    if(msg instanceof ReturnBoard){
                        updateMessage(socket, msg);
                        got_board = true;
                    }else{
                        System.err.println("Received illegal instance for RETURN_BOARD type from socket:"+msg);
                    }
                }else{
                    System.err.println("Did not receive Board from server");
                }
            }
            notifyMasterObserverAboutMessage(new RequestGameState());
            if(force){
                Message msg =socket.requestMessage(MessageType.RETURN_GAME_STATE);
                if(msg!=null){
                    if(msg instanceof ReturnGameState){
                        updateMessage(socket, msg);
                        got_board = true;
                    }else{
                        System.err.println("Received illegal instance for RETURN_BOARD type from socket:"+msg);
                    }
                }else{
                    System.err.println("Did not receive Board from server");
                }
            }
        }
        return force||(got_board&&got_state);
    }
    /* *
     * Waits for message on socket
     * @param type
     * @return 
     */
   /* private Message receive(MessageType type){
        Message msg=null;
        try{
            msg = socket.receive(10000, MessageType.MOVE);
            if(msg instanceof Move){
                notifySlaveObserversAboutMove((Move)msg);
            }
        }catch(SocketException ex){
            System.err.println("Exception on receiving message:"+ex);
        }
        return msg;
    }*/
    
    
 /*   @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof Socket.SocketStatus){
        //if(arg==Socket.SocketStatus.IO_ERROR){
            switch((Socket.SocketStatus)arg){
                case CLOSE_ERROR:
                case IO_ERROR:
                    break;
            }
        }else if(arg instanceof MessageType){
            Message msg;
            switch((MessageType)arg){
                case SOCKET_CLOSE_ERROR:
                case SOCKET_INPUT_ERROR:
                case SOCKET_OUTPUT_ERROR:
                        state.setConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED);
                        boolean rtrn = reconnect();
                    break;
                case MOVE:
                        if( (msg = receive(MessageType.MOVE)) instanceof Move){
                            notifySlaveObserversAboutMove( (Move)msg);
                        }else{
                            System.err.println("Received NULL message on update, type["+arg+"]");
                        }   
                    break;
                case RETURN_BOARD:
                        if(!lockReturnBoard) 
                            break; //break only if it is locked.
                        else
                            if( (msg = receive((MessageType)arg))!=null){
                                if(msg instanceof ReturnBoard){
                                    board.setBoard(((ReturnBoard) msg).getBoard());
                                    notifySlaveObserversAboutMessage(msg);
                                }else
                                    System.err.println("Received invalid instance of message on update, type["+arg+"]");
                            }else{
                                System.err.println("Received NULL message on update, type["+arg+"]");
                            }   
                    break;
                case CHAT:case RETURN_OPPONENT_NAME:case RETURN_SCORE:
                        if( (msg = receive((MessageType)arg))!=null){
                            notifySlaveObserversAboutMessage(msg);
                            }else{
                                System.err.println("Received NULL message on update, type["+arg+"]");
                            }   
                    break;
                case SERVER_SHUTDOWN:
                        state.setConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED);
                        state.setGameState(PlayerGameState.GameState.WAIT);
                        notifySlaveObserversAboutMessage(new ChatMessage(ChatMessage.ChatSource.SERVER, "Server is shutting down. Connection will be terminated"));
                        notifySlaveObserversAboutMessage(new ReturnConnectionState(state.getConnectionState()));
                        notifySlaveObserversAboutMessage(new ReturnGameState(state.getGameState()));
                        socket.Disconnect();
                        
                    break;
                default:
                        //throw new UnsupportedOperationException("todo");
                    break;
            }
        
        }
    }*/
    /**
     * Closes socket connection.
     */
    public void disconnect(){
        //gui.dispose();
        state.setConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED);
        state.setGameState(PlayerGameState.GameState.WAIT);
        notifySlaveObserversAboutMessage(new ReturnConnectionState(state.getConnectionState()));
        notifySlaveObserversAboutMessage(new ReturnGameState(state.getGameState()));
        try{
            if(socket!=null)
                socket.Disconnect();
        }catch(SocketException ex){
            System.err.println("Error on closing socket.");
        }
        socket=null;
        setMasterObserver(null);
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
    public void notifyMasterObserverAboutMove(Move move) {
        if(master!=null)
            master.updateMove(this, move);
        else
            System.err.println("Game Handler has no active socket");
    }

    @Override
    public void notifySlaveObserversAboutMove(Move move) {
        if(slave!=null)
            slave.updateMove(this, move);
        else
            System.err.println("Game Handler has no Room GUI");
    }
    @Override
    public void updateMove(MoveNotifier src, Move move) {
        if(move != null){
            if(src==slave){
                System.out.println("Received move from BoardGUI: "+move.getMoveType());
                switch(move.getMoveType()){  
                    case PASS:
                        state.setGameState(PlayerGameState.GameState.PASS);
                        notifyMasterObserverAboutMove(move);
                        break;
                    case PLACE_STONE:
                        lastMove=(PlaceStone)move;
                        state.setGameState(PlayerGameState.GameState.WAIT);
                        notifyMasterObserverAboutMove(move);
                        break;  
                    case RETURN_TO_STONE_PLACING:
                        state.setGameState(PlayerGameState.GameState.WAIT);
                        notifyMasterObserverAboutMove(move);
                        break;  
                    case SURRENDER:
                        notifyMasterObserverAboutMove(move);
                        state.setGameState(PlayerGameState.GameState.LOST);
                        notifySlaveObserversAboutMove(move);//?
                        break;  
                    case TERRITORIES_AGREE:
                        state.setGameState(PlayerGameState.GameState.AGREE);
                        notifyMasterObserverAboutMove(move);
                        break;
                    case TERRITORIES_CHOOSEN:
                        if(move instanceof TerritoriesChosen){
                            state.setGameState(PlayerGameState.GameState.AGREE);
                            territories = ((TerritoriesChosen) move).getTerritoriesList();
                            notifyMasterObserverAboutMove(move);
                        }else{
                            System.err.println("Received illegal instance for TerritoriesChosen type from Socket");
                        }
                        //notifySlaveObserversAboutMessage(new ReturnGameState(state.getGameState()));
                        break;
                    case TERRITORIES_DISAGREE:
                        //state.setGameState(PlayerGameState.GameState.CHOOSE); //disagree state seems pointless
                        territories = null;
                        notifyMasterObserverAboutMove(move);
                        break;   
                    
                    case INVALID_MOVE:
                        notifyMasterObserverAboutMessage( new RequestBoard() ); //???
                        //System.err.println("Wrong source of INVALID_MOVE move"+move.getMessageType());
                        break;    
                        
                    //te są raczej tylko od serwera, można walnać komunikat że źródło wiadomości jest złe, zawsze to info podczas debugowania

                        
                        
                    case CHOOSE_TERRITORIES:
                    case MAKE_MOVE:
                    case LOSE:
                    case VALID_MOVE:
                    case WIN:
                            System.err.println("Received illegal move type ["+move.getMessageType()+"] from RoomGUI");
                        break;  
                }
            }else if(src == master){
                System.out.println("Received move from Socket: "+move.getMoveType());
                switch(move.getMoveType()){                    
                    case CHOOSE_TERRITORIES:
                        state.setGameState(PlayerGameState.GameState.CHOOSE);
                        notifySlaveObserversAboutMove(move);
                        break;
                    case INVALID_MOVE:
                       // notifyMasterObserverAboutMessage( new RequestBoard() );
                       // notifyMasterObserverAboutMessage( new RequestGameState() );
                        notifySlaveObserversAboutMessage(new ChatMessage(ChatMessage.ChatSource.ERROR, "Last move was checked as invalid by server."));
                        notifySlaveObserversAboutMove(move);
                        break;
                    case MAKE_MOVE:
                        state.setGameState(PlayerGameState.GameState.PLACE);
                        notifySlaveObserversAboutMove(move);
                        break;
                    case LOSE:
                        state.setGameState(PlayerGameState.GameState.LOST);
                        notifySlaveObserversAboutMove(move);
                        break;
                    case VALID_MOVE:
                        //System.err.println("Changing last move:["+lastMove.getMessage()+"]");
                        state.setGameState(PlayerGameState.GameState.WAIT);
                        board.changeField(lastMove);
                        notifySlaveObserversAboutMessage(new ReturnBoard(board));
                        break;  
                    case WIN:
                        state.setGameState(PlayerGameState.GameState.WON);
                        notifySlaveObserversAboutMove(move);
                        break;
                    case TERRITORIES_AGREE:
                        state.setGameState(PlayerGameState.GameState.AGREE);
                        break; 
                    case TERRITORIES_DISAGREE:
                        state.setGameState(PlayerGameState.GameState.CHOOSE);
                        territories = null;
                        notifySlaveObserversAboutMove(move);
                        break;  
                    case TERRITORIES_CHOOSEN:
                        state.setGameState(PlayerGameState.GameState.CHOOSE);
                        if(move instanceof TerritoriesChosen){
                            territories = ((TerritoriesChosen) move).getTerritoriesList();
                            notifySlaveObserversAboutMove(move);     
                        }else{
                            System.err.println("Received illegal instance for TerritoriesChosen type from Socket");
                        }
                        break; //This train needs some breaks
                    case RETURN_TO_STONE_PLACING:
                        state.setGameState(PlayerGameState.GameState.PLACE);
                        notifySlaveObserversAboutMove(move);
                        break;  
                    case PLACE_STONE:
                        if(move instanceof PlaceStone){
                            //System.err.println("Changing last move:["+((PlaceStone)move).getMessage()+"]");
                            state.setGameState(PlayerGameState.GameState.WAIT);
                            board.changeField((PlaceStone)move);
                            notifySlaveObserversAboutMessage(new ReturnBoard(board));
                        }else{
                            System.err.println("Received illegal instance for Place Stone type from socket:"+move.getMessage());
                        }
                        //state.setGameState(PlayerGameState.GameState.PLACE);                        
                        break;
                        
                        
                    //te powinny być jedynie od klienta,  można walnać komunikat że źródło wiadomości jest złe, zawsze to info podczas debugowania
                    case PASS:
                    case SURRENDER:
                            System.err.println("Received illegal move type ["+move.getMessageType()+"] from Server");
                        break;   
                        
                }
            }else{
                System.err.println("Received move ["+move+"] from illegal source:"+src);
            }      
        }else{
            System.err.println("Received null move from:"+src);
        }
    }
    
    
    
    
    @Override
    public void updateMessage(MoveNotifier src, Message msg) {
        if(msg!=null) {
            if(src == master){
                if(msg.getMessageType()!=MessageType.RETURN_PING)
                    System.out.println("Received message from Socket: "+msg.getMessageType());
                switch(msg.getMessageType()){
                    
                    case SOCKET_CLOSE_ERROR:
                    case SOCKET_INPUT_ERROR:
                    case SOCKET_OUTPUT_ERROR:
                            state.setConnectionState(PlayerConnectionState.ConnectionState.DISCONNECTED);
                            boolean rtrn = reconnect();
                            if(!rtrn){
                                disconnect();
                            }
                        break;
                    case MOVE:
                            System.err.println("Received Move message through updateMessage from socket");
                            if(msg instanceof Move){
                                updateMove(src,(Move)msg);
                            }else{
                                System.err.println("Received nvalid instance of message for MOVE type from socket:"+msg.getMessage());
                            }   
                        break;
                    case RETURN_BOARD:
                            if(msg instanceof ReturnBoard){
                                System.out.println(board);
                                board.setBoard(((ReturnBoard) msg).getBoard());
                                System.out.println(board);
                                notifySlaveObserversAboutMessage(msg);
                            }else
                                System.err.println("Received invalid instance of message for ReturnBoard type:"+msg.getMessage());
                        break;
                    case RETURN_GAME_STATE:
                            if(msg instanceof ReturnGameState){
                                if(((ReturnGameState) msg).getState()!=state.getGameState()){
                                    state.forceSetGameState(((ReturnGameState) msg).getState());
                                    switch(state.getGameState()){
                                        case STOPPED:
                                            break;
                                        case PLACE:
                                            notifySlaveObserversAboutMessage(new MakeMove());
                                            break;
                                        case WAIT:
                                            break;
                                        case PASS:
                                            break;
                                        case CHOOSE:
                                            notifySlaveObserversAboutMessage(new ChooseTerritories());
                                            break;
                                        case AGREE:
                                            break;
                                        case DISAGREE:
                                            break;
                                        case WON:
                                            break;
                                        case LOST:
                                            break;
                                    }
                                    notifySlaveObserversAboutMessage(msg);
                                    //throw new UnsupportedOperationException("TODO what happpends when game states were in mismatch with server. ");
                                }
                            }else
                                System.err.println("Received invalid instance of message for ReturnBoard type:"+msg.getMessage());
                        break;
                    case RETURN_PING:case CHAT:case RETURN_OPPONENT_NAME: case PLAYER_CONNECTED: case PLAYER_DISCONNECTED:
                            notifySlaveObserversAboutMessage(msg); 
                        break;
                    case SERVER_SHUTDOWN:
                            notifySlaveObserversAboutMessage(new ChatMessage(ChatMessage.ChatSource.SERVER, "Server is shutting down. Connection will be terminated"));
                            disconnect();
                        break;
                    default:
                            System.err.println("Received illegal message type from socket"+msg.getMessageType());
                        break;
                }
            }else if(src == slave){
                if(
                        (msg.getMessageType()!=REQUEST_PING)
                        &&(msg.getMessageType()!=REQUEST_GAME_STATE)
                        &&(msg.getMessageType()!=MessageType.REQUEST_CONNECTION_STATE)
                        &&(msg.getMessageType()!=REQUEST_SCORE)
                )
                    System.out.println("Received message from BoardGUI: "+msg.getMessageType());
                switch(msg.getMessageType()){
                    
                    
                    case MOVE:
                            System.err.println("Received Move message through updateMessage from RoomGUI");
                            if(msg instanceof Move){
                                updateMove(src,(Move)msg);
                            }else{
                                System.err.println("Received object with illegal class for MOVE type from RoomGUI:"+msg.getMessage());
                            }   
                        break;
                    case REQUEST_LEGAL_PLACES:
                            ArrayList<FieldInfo> list = board.getPossibleMoves((isBlack)?Board.BoardField.BLACK:Board.BoardField.WHITE );
                            notifySlaveObserversAboutMessage(new ReturnLegalPlaces(list));
                        break;
                    case CHAT:case REQUEST_OPPONENT_NAME:case REQUEST_PING:
                            notifyMasterObserverAboutMessage(msg);
                        break;
                    case REQUEST_BOARD:
                            notifySlaveObserversAboutMessage(new ReturnBoard(board));
                        break;
                    case QUIT_GAME:
                            notifyMasterObserverAboutMove(new Surrender());
                            disconnect();
                            gui.dispose();
                        break;

                    case REQUEST_CONNECTION_STATE:
                            notifySlaveObserversAboutMessage(new ReturnConnectionState(state.getConnectionState()));
                        break;
                    case REQUEST_GAME_STATE:
                            notifySlaveObserversAboutMessage(new ReturnGameState(state.getGameState()));
                        break;
                    case REQUEST_SCORE:
                            //System.out.println("Black:["+board.calculateScore(true,territories)+"] white:["+board.calculateScore(false, territories)+"]" );
                            notifySlaveObserversAboutMessage(new ReturnScore(board.calculateScore(true,territories),board.calculateScore(false, territories)));
                        break;
                    case REQUEST_DEFAULT_TERRITORIES:
                            notifySlaveObserversAboutMessage(board.getDefaultTerritories());
                        break;
                    default:
                            System.err.println("Received illegal message type from RoomGUI"+msg.getMessageType());
                        break;
                }
            }else{
                System.err.println("Received message from illegal source"+src);
            }
        }else{
            System.err.println("Received NULL message from:"+src);
        }
    }

    private ArrayList<FieldInfo> territories = null;
    
    @Override
    public void notifyMasterObserverAboutMessage(Message msg) {
        if(master!=null)
            master.updateMessage(this, msg);
        else
            System.err.println("Game Handler has no socket");
    }



    @Override
    public void notifySlaveObserversAboutMessage(Message msg) {
        if(slave!=null)
            slave.updateMessage(this, msg);
        else
            System.err.println("Game Handler has no slave component");
    }


    
}
