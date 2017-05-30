/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.Board.BoardField;
import game.playerstate.PlayerState;
import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.game.PlayerGameState;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import utilities.messages.Message;
import utilities.messages.RequestBoard;
import utilities.messages.ReturnBoard;
import utilities.messages.client.ReturnConnectionState;
import utilities.messages.client.ReturnGameState;
import utilities.messages.client.ReturnScore;
import utilities.messages.data.FieldInfo;
import utilities.messages.move.ChooseTerritories;
import utilities.messages.move.MakeMove;
import utilities.messages.move.Move;
import utilities.messages.move.Pass;
import utilities.messages.move.PlaceStone;
import utilities.messages.move.TerritoriesAgree;
import utilities.messages.move.TerritoriesChosen;

/**
 * Class used to make AI player.
 * @author Kingaa
 */
public class AIPlayer extends Player {
    
    private final Board board;
    private PlaceStone lastMove;
    private final boolean black;
    
    /**
     * Default constructor
     * @param master Master of this component
     * @param state State pointer
     * @param black if it plays with black stones
     * @param size Size of a board
      */
    public AIPlayer(MoveNotifier master,PlayerState state, boolean black, Board.Size size){
        super(master, "Computer",state);
        this.black=black;
        board = new Board(size);
        slave = this;
    }


    @Override
    public void addSlaveObserver(MoveNotifier observer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public void notifyMasterObserverAboutMove_asSlave(Move move) {
        updateMove(slave, move);
    }

    

    @Override
    public void updateMove_Subclass(MoveNotifier src, Move move) {
        
        if(move!=null){
            if(src == getMaster() ){
                    switch(move.getMoveType()){
                        case TERRITORIES_CHOOSEN:
                            notifyMasterObserverAboutMove_asSlave(new TerritoriesAgree());
                            break;
                            
                        case CHOOSE_TERRITORIES:
                            TerritoriesChosen msg = new TerritoriesChosen();
                            ArrayList<Point> territories = (ArrayList<Point>) board.getDefaultTerritories().getBlackTerritories();
                            for(Point p: territories)
                                msg.addLegalPlace(new FieldInfo(BoardField.BLACK, p));
                            territories = (ArrayList<Point>) board.getDefaultTerritories().getWhiteTerritories();
                            for(Point p: territories)
                                msg.addLegalPlace(new FieldInfo(BoardField.WHITE, p));
                            notifyMasterObserverAboutMove_asSlave(msg);
                            break;
                        case MAKE_MOVE:
                            ArrayList<FieldInfo> list;  //list of all the possible moves
                            if(black)
                                list = board.getPossibleMoves(Board.BoardField.BLACK);
                            else
                                list = board.getPossibleMoves(Board.BoardField.WHITE);
                            if(!list.isEmpty()){
                                FieldInfo goodMove=null;    //a move which capture opponent stones
                                for(FieldInfo fi: list){
                                    if(black)
                                        if(board.canCaptureStones(BoardField.BLACK, fi.getX(), fi.getY()))
                                            goodMove=fi;
                                    else
                                        if(board.canCaptureStones(BoardField.WHITE, fi.getX(), fi.getY()))
                                            goodMove=fi;

                                }
                                if(goodMove==null){ //if there is no move which capture opponent stones get random legal move
                                    int index = new Random().nextInt(list.size());
                                    lastMove = new PlaceStone(black, list.get(index).getX(), list.get(index).getY());
                                }
                                else
                                    lastMove = new PlaceStone(black, goodMove.getX(), goodMove.getY());
                                notifyMasterObserverAboutMove_asSlave(lastMove);
                            }else{
                                notifyMasterObserverAboutMove_asSlave(new Pass());  //pass if there is no legal moves
                            }
                            break;
                        case INVALID_MOVE: 
                                notifyMasterObserverAboutMessage(new RequestBoard());
                            break;
                        case PLACE_STONE:
                            board.changeField((PlaceStone)move);
                            break;
                        case VALID_MOVE: 
                            board.changeField(lastMove);
                            
                            
                        case SURRENDER:  
                        case PASS:
                        case TERRITORIES_DISAGREE:
                        case RETURN_TO_STONE_PLACING:
                        case WIN:
                        case LOSE:
                        case TERRITORIES_AGREE:
                            break;
                    }
            }
        }
        
    }

    @Override
    public void updateMessage(MoveNotifier src, Message msg) {
        
        if(msg!=null){
            if(src == getMaster()){
                switch(msg.getMessageType()){
                    case RETURN_BOARD:
                            if(msg instanceof ReturnBoard){
                                board.setBoard(((ReturnBoard) msg).getBoard());
                            }else{
                                System.err.println("Server send back illegal instance for RETURN_BOARD type");
                            }
                        break;
                }
            }
        }
        
    }

    
    
    @Override
    public void notifySlaveObserversAboutMove(Move move) {
        updateMove(slave, move);
    }

    @Override
    public void notifySlaveObserversAboutMessage(Message msg) {
        updateMessage(slave, msg);
    }
    
}
