/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.swing.JComponent;

/**
 * Class of field that changes its colour depending on neighbours or at random. To Start chaning it needs to be executed at fixed rate, best at {@link #getSpeed() } speed.
 * @author n1t4chi
 */
public class Field extends JComponent implements Runnable {
    /**
     * Chance of changing colour at random.
     */
    private /*final*/ double colour_change_chance;
    /**
     * Speed of changing colour.
     */
    private /*final*/ int speed;
    /**
     * Array of fields that are nearby.
     */
    private Field[] neighbour_fields;  
    /**
     * Current colour.
     */
    private volatile Color current_colour;
    /**
     * RNG.
     */
    private final Random rand;
    
    /**
     * 
     * Lock for reading/writing.
     */
    public ReadWriteLock lock;
    
    
    
    
    
    
    
    /**
     * Super lock for locking field when setting are being changed by window.
     */
    final ReentrantLock super_lock;
    
    /**
     * Sets new random speed of this field within range of [s/2 , 3/2*s], where s is given average speed.
     * @param speed Average speed.
     */
    public void setSpeed(int speed){
        //                          0,5k          +   rand from 0 to k
        this.speed = Math.round((float)speed/2    +   rand.nextInt(speed+1));
    }
    /**
     * Sets new random colour change chance.
     * @param chance New random colour change chance.
     */
    public void setColourChangeChance(double chance){
        this.colour_change_chance = chance;
        
    }
    
    
    /**
     * Returns speed of this field [in miliseconds].
     * @return speed of this field.
     */
    public int getSpeed(){
        return speed;
    }
    /**
     * Adds new neighbour.
     * @param neighbour New neighbour.
     */
    public void setNeighbour(Field neighbour){
        neighbour_fields = Arrays.copyOf(neighbour_fields, neighbour_fields.length+1);
        neighbour_fields[neighbour_fields.length-1]= neighbour;
    }
    
    /**
     * Returns current colour.
     * @return Current colour.
     */
    public Color getColour() {
        Color rtrn=null;
        //wait for unlock
        //Run.println(getName()+" Waiting to read colour");
        lock.readLock().lock();
        //Run.println(getName()+" continuing reading colour.");
        try{
            rtrn = current_colour;
        }finally{
            lock.readLock().unlock();
            //notifyAll();
        }    
        //Run.println(getName()+" finished reading colour.");
        //return current_colour;
        return rtrn;
    }
    /**
     * Returns random colour.
     * @return Random colour.
     */
    Color getRandomColour(){
        return new Color(Math.abs(this.rand.nextInt(256)),Math.abs(this.rand.nextInt(256)),Math.abs(this.rand.nextInt(256)));
    }
    /**
     * Returns colour based on neighbours.
     * @return Colour.
     */
    Color getNeighbourColour(){
        int r=0;
        int g=0;
        float b=0;
        for(int i=0;i<neighbour_fields.length;i++){  
            Run.println(getName()+" Starting reading "+neighbour_fields[i].getName() +" colour");
            Color c = neighbour_fields[i].getColour();      
            Run.println(getName()+" Finished reading "+neighbour_fields[i].getName() +" colour");       
            r+=c.getRed();
            g+=c.getGreen();
            b+=c.getBlue();
        }      
        return new Color( Math.round((float)r/neighbour_fields.length), Math.round((float)g/neighbour_fields.length), Math.round((float)b/neighbour_fields.length));
    }
    
   /* void changeColourHeighbour(){
        super_lock.lock();
        try{
            try{
                for(Field n : neighbour_fields){
                    n.lock.readLock().lock();
                }
            }finally{
                super_lock.unlock();
            }
            Run.println(getName()+" Changing colour based on ["+neighbour_fields.length+"] neighbours.");
            for(int i=0;i<neighbour_fields.length;i++){     
                Run.println(getName()+" Starting reading "+neighbour_fields[i].getName() +" colour");
                Color c = neighbour_fields[i].getColour();      
                Run.println(getName()+" Finished reading "+neighbour_fields[i].getName() +" colour");       
                r+=c.getRed();
                g+=c.getGreen();
                b+=c.getBlue();
            }     
            Run.println(getName()+"RGB: ("+r+","+g+","+b+")"+" divider:"+neighbour_fields.length);
            current_colour = 
            Run.println(getName()+"Changing colour based on neighbours to "+current_colour);  
        }finally{
            for(Field n : neighbour_fields){
                n.lock.readLock().unlock();
            }
        } 
    }*/
    
    /**
     * Changes colour of this field, either randomly or from average neighbour colour.
     */
    public void changeColour(){
        //waiting for unlock 
        Color c; 
        if(rand.nextDouble()<colour_change_chance){   
            c = getRandomColour();
        }else{         
            c = getNeighbourColour();
        }
                
        //lock.writeLock().lock();
        try{
            current_colour = c;
        }finally{ 
          //  lock.writeLock().unlock();
        }
    }
    /**
     * Executor of this field.
     */
    ScheduledExecutorService executor;
    /**
     * A ScheduledFuture that can be used to extract result or cancel.
     */
    ScheduledFuture SF;
    /**
     * Schedules this thread for repeated execution
     */
    public void ScheduleField(){   
        SF = executor.scheduleAtFixedRate(this, 0, getSpeed(), TimeUnit.MILLISECONDS);    
    }
    /**
     * Stops this field.
     */
    public void StopField(){
        SF.cancel(false);
    }
    
    
    /**
     * Constructor.
     * @param rand RNG. [must not be null pointer]
     * @param colour_change_chance chance of field changing its colour randomly. [must be positive]
     * @param speed Speed of changing colour.  [must be within [0,1] range]
     * @param super_lock Super lock when all fields properties are being changed.
     * @throws IllegalArgumentException if given arguments are not correct.
     */
    public Field(Random rand,double colour_change_chance, int speed ,ReentrantLock super_lock) throws IllegalArgumentException{
        
        if(!(
            ( (colour_change_chance>=0)&&(colour_change_chance<=1)  )&&      
            (speed>0)&&
            ( rand!=null )    
        )){
            throw new IllegalArgumentException("Wrong parameters.");
        }
        executor = Executors.newSingleThreadScheduledExecutor();
        this.rand = rand;        
        neighbour_fields=new Field[0];
        this.super_lock=super_lock;
        this.lock =  new ReentrantReadWriteLock(true);
        setSpeed(speed);
        setColourChangeChance(colour_change_chance);
        current_colour = new Color(this.rand.nextInt(256),this.rand.nextInt(256),this.rand.nextInt(256));
        this.setMinimumSize(new Dimension(10,10));
        this.setPreferredSize(new Dimension(20,20));
        this.setSize(new Dimension(20,20));
        this.setVisible(true);
        this.setEnabled(true);
        this.setDoubleBuffered(true);
    }

    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(current_colour);
        g2.fillRect(0,0, this.getWidth(), this.getHeight());
    }
    
    @Override
    public void run() {
        if(!this.super_lock.isLocked()){
            changeColour();
        }
        repaint();
        
    }
    
}
