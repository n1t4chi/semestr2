/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities.messages.data;

import game.Board.BoardField;
import java.awt.Point;
import java.io.Serializable;

/**
 * Field info send mostly send through messages.
 * @author n1t4chi
 */
public class FieldInfo implements Serializable{
    private BoardField state;
    private final Point p;
    /**
     * Default constructor
     * @param state current field state
     * @param pos_x field X position
     * @param pos_y field Y position
     */
    public FieldInfo(BoardField state, int pos_x, int pos_y) {
        this(state, new Point(pos_x, pos_y));
    }

    /**
     * Default constructor
     * @param state current field state
     * @param pos Field position
     */
    public FieldInfo(BoardField state, Point pos) {
        this.state = state;
        this.p = pos;
    }
    
    /**
     * Returns position
     * @return position
     */
    public Point getPos() {
        return p;
    }
    /**
     * Returns X position
     * @return X position
     */
    public int getX() {
        return p.x;
    }
    /**
     * Returns Y position
     * @return Y position
     */
    public int getY() {
        return p.y;
    }
    
    public BoardField getState(){
        return state;
    }

    public void setState(BoardField state) {
        this.state = state;
    }
    
    
    
}
