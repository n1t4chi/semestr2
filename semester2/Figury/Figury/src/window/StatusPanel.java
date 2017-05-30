/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package window;

import figures.Figure;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import main.Text;

/**
     * Status panel class
 * @author n1t4chi
 */
public class StatusPanel extends JPanel{

   //##########################fields###########################################
    /**
     * Label for displaying info about active polygon.
     */
    private final JLabel Curr_Polygon_Info;
    /**
     * Label for displaying mouse info.
     */
    private final JLabel Mouse_Info;
    /**
     * Configuration object.
     */
    private final Configuration config;
    /**
     * Text object.
     */
    private final Text t;





   //#########################methods###########################################
    
    
    
    /**
     * Changes label sizes so they fit perfectly.
     */
    private void labelSize(){
        try{              
            Dimension s = this.getParent().getAccessibleContext().getAccessibleComponent().getSize();
            this.setPreferredSize(new Dimension(s.width,config.getFont().getSize()*2));
            this.setMinimumSize(new Dimension(100,config.getFont().getSize()*2));
            this.setMaximumSize(new Dimension(s.width,config.getFont().getSize()*5/2));
            this.setSize(this.getPreferredSize());
            Mouse_Info.setLocation(this.getSize().width*3/4, 0);
            Mouse_Info.setPreferredSize(new Dimension(this.getSize().width/4, this.getSize().height));
            Mouse_Info.setSize(Mouse_Info.getPreferredSize());
            Mouse_Info.setMinimumSize(Mouse_Info.getPreferredSize());
            Curr_Polygon_Info.setPreferredSize(new Dimension(this.getSize().width*3/4, this.getSize().height));
            Curr_Polygon_Info.setSize(Curr_Polygon_Info.getPreferredSize());
            Curr_Polygon_Info.setMinimumSize(Curr_Polygon_Info.getPreferredSize());
        }catch(NullPointerException ex){}
    }
    /**
     * Updates mouse info
     * @param x Current mouse X coordinate.
     * @param y Current mouse Y coordinate.
     */
    public void updateMouseInfo(int x, int y){
        Mouse_Info.setText("X:"+x+" Y:"+y);
    }
    /**
     * Updates polygon info.
     * @param p Current active Polygon.
     */
    public void updateFigureInfo(Figure p){      
        //System.out.println("window.Window.StatusPanel.updateFigureInfo()");
        Curr_Polygon_Info.setText(p.getInfoForStatusBar());
    }

    /**
     * Updates labels so they would suit new font configuration of style.
     */
    public void updateFontStyle(){
        Curr_Polygon_Info.setFont(config.getFont());
        Mouse_Info.setFont(config.getFont());
        Mouse_Info.setForeground(config.getFontColour());
        Curr_Polygon_Info.setForeground(config.getFontColour());      
    
    }
    
    /**
     * Updates labels so they would suit new policy of borders.
     */
    public void updateBorderStyle(){
        if(config.shouldDrawBorder()){
            Curr_Polygon_Info.setBorder(new LineBorder( new Color(Curr_Polygon_Info.hashCode()) ,2));
            Mouse_Info.setBorder(new LineBorder( new Color(Mouse_Info.hashCode()) ,2));
        }
    }



   //#########################constructors######################################
    
    
    /**
     * Constructor
     * @param t Text object.
     * @param config Configuration object.
     */
    public StatusPanel(Text t, Configuration config) {
        this.t = t;
        this.config = config;
        this.setVisible(true);
        this.setEnabled(true);
        this.setFocusable(false);    
        this.setLayout(null);
        this.setMinimumSize(new Dimension(100,config.getFont().getSize()*2));
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                //System.out.println("err");
                labelSize();
                super.componentResized(e);
            }

        });
        Curr_Polygon_Info = new JLabel();
        Curr_Polygon_Info.setVisible(true);
        Curr_Polygon_Info.setEnabled(true);
        //Curr_Polygon_Info.setBorder(new LineBorder(new Color(Curr_Polygon_Info.hashCode())));
        Mouse_Info = new JLabel();
        Mouse_Info.setVisible(true);
        Mouse_Info.setEnabled(true);
        //Mouse_Info.setBorder(new LineBorder(new Color(Mouse_Info.hashCode())));
        Mouse_Info.setLocation(this.getSize().width*3/4, 0);
        Curr_Polygon_Info.setLocation(0, 0);
        add(Curr_Polygon_Info);
        add( Mouse_Info);
    }


}
