/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Window class that displays fields.
 * @author n1t4chi
 */
public class Window extends JFrame{
    /**
     * Value divider for {@link #slider_chance}.
     */
    final static int SLIDER_DIVIDER = 10000;
    
    
    /**
     * List of fields
     */
    final Field[][] fields;
    /**
     * RNG
     */
    final Random rand;
    /**
     * Lock for locking other fields.
     */
    final ReentrantLock super_lock;
    /**
     * Chance of colour change
     */
    double colour_change_chance;
    /**
     * Speed of changing colour.
     */
    int speed;
    /**
     * Toolbar.
     */
    final JToolBar toolbar;
    /**
     * Speed slider.
     */
    final JSlider slider_speed;
    /**
     * Random colour change chance slider.
     */
    final JSlider slider_chance;
    final JPanel Field_Container;
    
    /**
     * Constructor
     * @param rows amount of rows. [must be positive]
     * @param columns amount of columns.[must be positive]
     * @param speed speed of colour changing.[must be positive]
     * @param colour_change_chance chance of field changing its colour randomly. [must be within [0,1] range]
     * @throws HeadlessException if system does not support GUI application.
     * @throws IllegalArgumentException if given arguments are not correct.
     */
    public Window(int rows,int columns,int speed,double colour_change_chance) throws HeadlessException,IllegalArgumentException {
        if(!(
            ( (colour_change_chance>=0)&&(colour_change_chance<=1)  )&& 
            (rows>0)&&      
            (columns>0)&&      
            (speed>0)     
        )){
            throw new IllegalArgumentException("Wrong parameters");
        }
        fields = new Field[rows][columns];
        rand = new Random();
        
        super_lock = new ReentrantLock(true);
        this.colour_change_chance = colour_change_chance;    
        this.speed = speed;
        this.setVisible(true);
        this.setTitle("Simulation");    
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        Field_Container = new JPanel();
        Field_Container.setLayout(new GridLayout(rows, columns));   
        add(Field_Container,BorderLayout.CENTER);    
        slider_chance = new JSlider(0, SLIDER_DIVIDER, (int)(this.colour_change_chance*SLIDER_DIVIDER));
        slider_chance.setToolTipText("Field random colour change chance");  
        
        Hashtable lTable = new Hashtable();
        lTable.put( 0, new JLabel("0%") );
        lTable.put( SLIDER_DIVIDER, new JLabel("100%") );
        
        
        
        
        slider_chance.setLabelTable( lTable );       
        slider_chance.setPaintLabels(true);
        slider_chance.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super_lock.lock();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                try{
                    for(int x=0;x<fields.length;x++){
                        for(int y=0;y<fields[x].length;y++){
                            fields[x][y].setColourChangeChance( Window.this.colour_change_chance );
                        }
                    }    
                }finally{
                    super_lock.unlock();
                }
            }           
        });
        slider_chance.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Window.this.colour_change_chance = (double)slider_chance.getValue()/SLIDER_DIVIDER;
                //System.out.println(".stateChanged() changing chance to"+Window.this.colour_change_chance);
            }           
        });
        
        
        
        
        slider_speed = new JSlider(1, 2000,speed);
        slider_speed.setMajorTickSpacing(1999);
        slider_speed.setPaintLabels(true);
        slider_speed.setPaintTrack(true);
        
        slider_speed.setToolTipText("Field speed");
        
        
        slider_speed.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                super_lock.lock();
                for(int x=0;x<fields.length;x++){
                    for(int y=0;y<fields[x].length;y++){
                        fields[x][y].StopField();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                try{
                    for(int x=0;x<fields.length;x++){
                        for(int y=0;y<fields[x].length;y++){
                            fields[x][y].setSpeed(Window.this.speed);
                            fields[x][y].ScheduleField();
                        }
                    }    
                }finally{
                    super_lock.unlock();
                }
            }           
        });
        
        slider_speed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Window.this.speed = slider_speed.getValue();
                //System.out.println(".stateChanged() changing speed to"+Window.this.speed);
            }
            
        });
        toolbar = new JToolBar();
        toolbar.add(slider_chance);
        toolbar.add(slider_speed);
        toolbar.setFloatable(false);
        add(toolbar,BorderLayout.PAGE_START);
        int wid = (int)(30/Math.sqrt(rows));
        int hei = (int)(30/Math.sqrt(columns));
        //System.out.print(  "height:"+toolbar.getPreferredSize().height+" "+hei+" "+this.getInsets().top+" "+this.getInsets().bottom+" = "+(toolbar.getPreferredSize().height+hei+this.getInsets().top+this.getInsets().bottom)  );
        //System.out.print(  "\nwidth:"+wid+" "+this.getInsets().left+this.getInsets().right+" = "+(wid+this.getInsets().left+this.getInsets().right)  );
        this.setMinimumSize(new Dimension(rows*wid+this.getInsets().left+this.getInsets().right,toolbar.getPreferredSize().height+columns*hei+this.getInsets().top+this.getInsets().bottom));       
        this.setPreferredSize(new Dimension(rows*wid*5+this.getInsets().left+this.getInsets().right,toolbar.getPreferredSize().height+columns*hei*5+this.getInsets().top+this.getInsets().bottom));   
        //creating fields.   
        for(int x=0;x<fields.length;x++){
            for(int y=0;y<fields.length;y++){
                fields[x][y] = new Field(rand,this.colour_change_chance, this.speed,super_lock);  
                fields[x][y].setName("F["+x+"]["+y+"]");                        
                Field_Container.add(fields[x][y]);
                //System.out.println(fields[x][y].getBounds());          
            }      
        }    
        //adding neighbours.
        for(int x=0;x<fields.length;x++){
            for(int y=0;y<fields[x].length;y++){
                if(x>0){
                    fields[x][y].setNeighbour(fields[x-1][y]);          
                }
                if(x<fields.length-1){
                    fields[x][y].setNeighbour(fields[x+1][y]);          
                }
                if(y<fields[x].length-1){
                    fields[x][y].setNeighbour(fields[x][y+1]);          
                }
                if(y>0){
                    fields[x][y].setNeighbour(fields[x][y-1]);
                }          
            }      
        }    
        
        
        super_lock.lock();
        try{
            //execution of tasks[field colour changing] at specified speed.
            //ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor(); //Single thread version
            for(int x=0;x<fields.length;x++){
                for(int y=0;y<fields[x].length;y++){
                    fields[x][y].ScheduleField();
                }
            }
        }finally{
            super_lock.unlock();
        }
        pack();      
    }
}
