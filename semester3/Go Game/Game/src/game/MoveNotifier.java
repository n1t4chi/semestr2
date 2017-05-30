/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import utilities.messages.Message;
import utilities.messages.move.Move;

/**
 * Interface providing two way notifying service for moves. 
 * Joins Observer and Observable under one interface. 
 Update() method should be called by specific notyfyXXXObserver() method to updateMove Observer with given move.
 setMasterObserver() should be called by Master observer while creating a Slave.
 * @author n1t4chi
 */
public interface MoveNotifier {
    /**
     * Changes higher rank observer which will be notified through {@link MoveNotifier#updateMove(client.Room.MoveNotifier, utilities.messages.Move) } method.
     * @param observer Master observer
     * @throws NullPointerException if move is null pointer.
     */
    public abstract void setMasterObserver(MoveNotifier observer);
    /**
     * Adds lower rank observer {@link MoveNotifier#updateMove(client.Room.MoveNotifier, utilities.messages.Move) } method.
     * There might be only one slave in implementing class thus this method should be used strictly by implementing class
     * @param observer Slave observer
     * @throws NullPointerException if move is null pointer.
     */
    public abstract void addSlaveObserver(MoveNotifier observer);
    
    /**
     * Notifies observer lover in rank with given move.
     * @param move Move to notify a master about.
     * @throws NullPointerException if move is null pointer.
     */
    public abstract void notifyMasterObserverAboutMove(Move move);
    /**
     * Notifies all observers lower in rank with given move.
     * @param move Move to notify a slaves about.
     * @throws NullPointerException if move is null pointer.
     */
    public abstract void notifySlaveObserversAboutMove(Move move);
    /**
     * Notifies observer higher in rank with given message. Moves should be strictly send by notifyXXXObserveraboutMove()
     * @param msg Message to notify a master about.
     * @throws NullPointerException if msg is null pointer.
     */
    public abstract void notifyMasterObserverAboutMessage(Message msg);
    /**
     * Notifies all observers lower in rank with given message. Moves should be strictly send by notifyXXXObserveraboutMove()
     * @param msg Message to notify a slaves about.
     * @throws NullPointerException if msg is null pointer.
     */
    public abstract void notifySlaveObserversAboutMessage(Message msg);
    
    /**
     * Method called by Master/Slave Observable strictly for passing lower/higher moves.
     * @param src Source of move
     * @param move move sent by source.
     * @throws NullPointerException if move is null pointer.
     */
    public abstract void updateMove(MoveNotifier src,Move move);
    /**
     * Method called by Master/Slave Observable for passing lower/higher various messages.
     * This method should not be used for passing Moves, rather for new board or messages.
     * @param src Source of move
     * @param msg msg sent by source.
     * @throws NullPointerException if move is null pointer.
     */
    public abstract void updateMessage(MoveNotifier src,Message msg);
    
    
    
    
}
