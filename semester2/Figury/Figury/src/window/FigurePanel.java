/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package window;

import figures.Ellipse;
import figures.Figure;
import figures.FigureException;
import figures.FigurePoint;
import figures.Polygon;
import figures.Quadrilateral;
import figures.FPoint;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import main.Text;
import static window.Window.X;
import static window.Window.Y;
import static window.Window.translateME;

/**
     * Container for figures.
 * @author n1t4chi
 */
public class FigurePanel extends JLayeredPane{

    
   //##########################static context###################################
    /**
     * free space on edges.
     */
    public static final int FREE_XY_SPACE=100;
    /**
     * Multiplier for grid where FigurePoints are dragged onto within specified radius.
     */
    public static final int GRID_MULTIPLIER = 50;

    /**
     * Constant for figure type. Used when creating new figures. General polygon.
     */
    public static final int FIGURE_TYPE_POLYGON = 0;
    /**
     * Constant for figure type. Used when creating new figures. Quadrilateral polygon.
     */
    public static final int FIGURE_TYPE_QUADRILATERAL = 1;
    /**
     * Constant for figure type. Used when creating new figures. Ellipse.
     */
    public static final int FIGURE_TYPE_ELLIPSE = 2;
    /**
     * Constant for figure type. Used when creating new figures. Parallelogram.
     */
    public static final int FIGURE_TYPE_QUADRILATERAL_PARALLELOGRAM = 10;
    /**
     * Constant for figure type. Used when creating new figures. Rhomb.
     */
    public static final int FIGURE_TYPE_QUADRILATERAL_RHOMB = 11;
    /**
     * Constant for figure type. Used when creating new figures. Rectangle.
     */
    public static final int FIGURE_TYPE_QUADRILATERAL_RECTANGLE = 12;
    /**
     * Constant for figure type. Used when creating new figures. Square.
     */
    public static final int FIGURE_TYPE_QUADRILATERAL_SQUARE = 13;
    /**
     * Constant for figure type. Used when creating new figures. Circle.
     */
    public static final int FIGURE_TYPE_ELLIPSE_CIRCLE = 20;
    
    
   //##########################fields###########################################
    
    
    
    /**
     * relative position of (0,0) point in relation of figures to (0,0) point of this.
     */
    public final Point relative_Position;
    /**
     * Text object.
     */
    private final Text t;
    
    /**
     * StatusPanel.
     */
    public final StatusPanel status_bar;
    
    /**
     * Window that contains FigurePanel.
     */
    public final Window window;
    /**
     * Mouse listener object.
     */
    private final MouseAdapter ma;
    /**
     * Figure list.
     */
    private final ArrayList figure_list;
    /**
     * Configuration object.
     */
    public final Configuration config;
    
    
    
    
    
    
    
    
    /**
     * Whether the figure is being created or not.
     */
    public boolean figure_Creation;
    /**
     * Table of points for {@link #createFigure(int) } purpose.
     */
    int[][] point_tab;
    /**
     * Figure type to create.
     */
    int figure_type;
    /**
     * Minimal amount of point required to create new figure.
     */
    int min_figure_pt;
    /**
     * Maximal amount of point required to create new figure.
     */
    int max_figure_pt;
    /**
     * Current Mouse position on glass panel. For figure creation.
     */
    final Point curr_mouse;
    /**
     * Glass panel object.
     */
    private final JComponent glassPanel;
    

   //#########################methods###########################################
    
    /**
     * Initiates fields for figure creation.
     */
    public void initFigureCreation(){
        figure_Creation = true;
        point_tab = new int[2][0];
        switch(figure_type){
            case FIGURE_TYPE_QUADRILATERAL_PARALLELOGRAM:
            case FIGURE_TYPE_QUADRILATERAL_RECTANGLE:
            case FIGURE_TYPE_QUADRILATERAL_RHOMB:
            case FIGURE_TYPE_QUADRILATERAL_SQUARE:
                    min_figure_pt=3;
                    max_figure_pt=3;
                break;
            case FIGURE_TYPE_QUADRILATERAL:
                    min_figure_pt=4;
                    max_figure_pt=4;
                break;
            case FIGURE_TYPE_ELLIPSE:
            case FIGURE_TYPE_ELLIPSE_CIRCLE: 
                    min_figure_pt=2;
                    max_figure_pt=2;
                break;
            case FIGURE_TYPE_POLYGON: default:
                    min_figure_pt=1;
                    max_figure_pt=Integer.MAX_VALUE;
                break;                      
        }
        glassPanel.setSize(getSize());
        glassPanel.setVisible(true);  
    }
    
    
    
    
    
    /**
     * Finalizes figure creation.
     * @param scrap_figure If true, the figure creation will be cancelled, otherwise Figure will be placed onto this.
     */
    public void finalizeFigureCreation(boolean scrap_figure){
        if(!scrap_figure){
            try{
                Figure f;
                //System.out.println("window.FigurePanel.finalizeFigureCreation() point tab length ["+point_tab.length+"]["+point_tab[0].length+"]");
                double[][] p_tab = new double[2][point_tab[0].length];
                for(int i=0; i<p_tab[0].length;i++){
                    p_tab[0][i] = point_tab[0][i];
                    p_tab[1][i] = point_tab[1][i];
                }
                switch(figure_type){
                    case FIGURE_TYPE_QUADRILATERAL_PARALLELOGRAM:
                    case FIGURE_TYPE_QUADRILATERAL_RHOMB:
                    case FIGURE_TYPE_QUADRILATERAL_RECTANGLE:
                    case FIGURE_TYPE_QUADRILATERAL_SQUARE:
                            boolean parallel = true;
                            boolean right = (figure_type==FIGURE_TYPE_QUADRILATERAL_RECTANGLE)||(figure_type==FIGURE_TYPE_QUADRILATERAL_SQUARE);
                            boolean equilateral = (figure_type==FIGURE_TYPE_QUADRILATERAL_RHOMB)||(figure_type==FIGURE_TYPE_QUADRILATERAL_SQUARE);                               
                            f = new Quadrilateral(p_tab[X],p_tab[Y],parallel,equilateral,right,config,t,this);                               
                        break;
                    case FIGURE_TYPE_QUADRILATERAL:
                            f = new Quadrilateral(p_tab[X],p_tab[Y],config,t,this);
                        break;
                    case FIGURE_TYPE_ELLIPSE_CIRCLE:
                            double r = Point.distance(p_tab[X][0], p_tab[Y][0], p_tab[X][1], p_tab[Y][1]);
                            f = new Ellipse(p_tab[X][0],p_tab[Y][0],r,config,t,this);
                        break;          
                    case FIGURE_TYPE_ELLIPSE:    
                            double rx = Math.abs(p_tab[X][0]-p_tab[X][1]);
                            double ry = Math.abs(p_tab[Y][0]-p_tab[Y][1]);
                            f = new Ellipse(p_tab[X][0],p_tab[Y][0],rx,ry,config,t,this);
                        break;
                    case FIGURE_TYPE_POLYGON: default: //POLYGON
                            f = new Polygon(p_tab[X],p_tab[Y],config,t,this);
                        break;
                }
                this.addFigure(f);
                this.repaint();
            }catch(FigureException ex){
                window.showErrorDialog(t.getText("NEW_FIGURE_ERROR")+"\n"+ex.getMessage() );
            }
        }
        figure_Creation=false;
        point_tab = new int[2][0];
        glassPanel.setVisible(false);  
    }
    
    /**
     * Begins creation of new figure.
     * @param figure_type Type of figure to create.
     */
    public void createFigure(int figure_type){
        this.figure_type = figure_type;
        initFigureCreation();
        
    }
    
    
    /**
     * Adds figure to this and from {@link #figure_list}. 
     * @param fig figure to be added.
     * @return the component argument.
     * #add(java.awt.Component)
     */
    public Component addFigure(Figure fig) {
        //System.out.println("window.Window.FigurePanel.add()");
        Component rtrn = add(fig);
        figure_list.add(fig);
        return rtrn; 
    }      
    /**
     * Sets given polygon as current active polygon on {@link Window#status_bar}
     * @param p Figure to be set as active.
     */
    public void setActiveFigure(Figure p){ 
        //System.out.println("window.Window.FigurePanel.setActiveFigure()");
        setPosition(p, 0);
        p.setPointVisibility(true);
        for (Object o : figure_list) {
            if(o instanceof Figure){
                ((Figure) o).setPointVisibility(  p.equals(o) );
            }
        }       
        status_bar.updateFigureInfo(p);
    }

    /**
     * Removes figure from this and from {@link #figure_list}. Does not ask to confirm.
     * @param fig figure to be removed.
     * @see #remove(java.awt.Component) 
     */
    public void removeFigureNoWarning(Figure fig) {
        super.remove(fig);
        figure_list.remove(fig);    
        repaint();
    }
    /**
     * Removes figure from this and from {@link #figure_list}.
     * @param fig figure to be removed.
     * @see #remove(java.awt.Component) 
     */
    public void removeFigure(Figure fig) {
        int i=JOptionPane.showConfirmDialog(window, t.getText("DELETE_FIGURE_MESSAGE"), t.getText("DELETE_FIGURE_TITLE"),JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(i == JOptionPane.YES_OPTION){
            super.remove(fig);
            figure_list.remove(fig);    
            repaint();
        }else{
            window.showCancelDialog();
        }
    }
    
    
    /**
     * Checks if mouse is near other Point within all polygons inside.
     * @param e mouse event
     * @param current FigurePoint that should be ignored. Can be null.
     * @return Point that mouse is nearby.
     */
    public FPoint isMouseNearOtherPoint(MouseEvent e,FigurePoint current){
        FPoint rtrn = null;
        for (Object o : figure_list) {
            if(o instanceof Figure){
                rtrn = ((Figure) o).isMouseNearPoint(e,current);
                if(rtrn != null){
                    break;
                }
            }
        }
       /* if(rtrn == null){
            if(e.getX()%100<3 && e.getY()%100<3 ){
                double x = Math.signum(e.getX())*(Math.abs(e.getX()) - e.getX()%100);
                double y = Math.signum(e.getY())*(Math.abs(e.getY()) - e.getY()%100);             
                rtrn =new FigurePoint(new FPoint(x,y),f);
            }
            
        }*/
        if(rtrn == null){
            int x_c = e.getX();
            int y_c = e.getY();
            
            //for(int i =-150;i<=230;i++){
                // System.out.println( "i:"+i+"  (|i|+5)%100 = "+(Math.abs(i)+5)%100+"  <= 10 ?"+ (((Math.abs(i)+5)%100)<=10) + (?("  final i:"+ :"" ) );
            //}                       
            if(((((Math.abs(x_c)+Figure.SEARCH_BUFFER/2)%GRID_MULTIPLIER )<=Figure.SEARCH_BUFFER))&&((((Math.abs(y_c)+Figure.SEARCH_BUFFER/2)%GRID_MULTIPLIER )<=Figure.SEARCH_BUFFER))){
                //System.out.println("window.FigurePanel.isMouseNearOtherPoint() inner");
                rtrn = new FPoint(    ((Math.signum(x_c))*((Math.abs(x_c)+Figure.SEARCH_BUFFER/2)-(Math.abs(x_c)+Figure.SEARCH_BUFFER/2)%GRID_MULTIPLIER )) , ((Math.signum(y_c))*((Math.abs(y_c)+Figure.SEARCH_BUFFER/2)-(Math.abs(y_c)+Figure.SEARCH_BUFFER/2)%GRID_MULTIPLIER ))    );
            }
        }
        return rtrn;
    }

    /**
     * Changes size and location that it fits perfectly with figures within.
     */
    public void resize(){
        Dimension min = window.getAccessibleContext().getAccessibleComponent().getSize();
        int min_x=0;
        int min_y=0;
        int max_x=0;
        int max_y=0;        
        for (Object o : figure_list) {
            if(o instanceof Figure){
                Rectangle r = ((Figure) o).getFigureBounds();
                if(min_x>r.x){
                    min_x = r.x;
                }
                if(max_x<r.x+r.width){
                    max_x = r.x+r.width;
                }
                if(min_y>r.y){
                    min_y = r.y;
                }
                if(max_y<r.y+r.height){
                    max_y = r.y+r.height;
                }                    
            }
        }
        
        
       // Point old_relative = relative_Position.getLocation();
        int x = min_x-FREE_XY_SPACE;
        int y = min_y-FREE_XY_SPACE;       
        int w = max_x-min_x+2*FREE_XY_SPACE;
        int h = max_y-min_y+2*FREE_XY_SPACE;
        //relative_Position.setLocation(-x , -y );          
        
        setPreferredSize(new Dimension(((w>min.width)?w:min.width),((h>min.height)?h:min.height)));     


        //System.out.println(" x="+x+" y="+y+"  curr relative pos = ("+relative_Position.x+","+relative_Position.y+")");  
        Rectangle rec = getVisibleRect();
        //System.out.println("rectangle: "+rec);
     //   rec.translate( -(old_relative.x+x),-(old_relative.y+y)  );
        //System.out.println("rectangle after translation: "+rec);
        scrollRectToVisible(rec); 
        repaint();
        //System.out.println("[2]window.Window.FigurePanel.resize() chaging visible rect to "+getVisibleRect());   
        if((min_x<-15000)||(min_y<-15000)){
            try{
                throw new Exception();
            }catch(Exception ex){
                System.err.println(" min x:"+min_x+" max x:"+max_x+" min y:"+min_y+" max y:"+max_y);
               // System.err.print("relative pos pre:"+old_relative);
                System.err.println(" || post:"+relative_Position);
                ex.printStackTrace();
            }
        }       
    }      

    /**
     * Removes all figures.
     */
    public void removeAllFigures(){
        for(int i=figure_list.size()-1; i>=0 ; i-- ){
            removeFigureNoWarning( (Figure) figure_list.get(i) );
        }
    }

   //#########################constructors######################################
    /**
     * Constructor.
     * @param figure_list List of figures.
     * @param config Configuration.
     * @param window Current window.
     * @param status_bar status bar within window.
     * @param t text object.
     */
    public FigurePanel(ArrayList figure_list, Configuration config,Window window,StatusPanel status_bar,Text t) {      
        //super();
        this.figure_list = figure_list;
        this.config = config;
        this.window = window;
        this.relative_Position = new Point(0,0);
        this.status_bar = status_bar;
        this.t = t;
        setOpaque(true);
        //setAutoscrolls(true);
        curr_mouse = new Point(0,0);
        glassPanel = new JComponent() {    
            
            @Override
            protected void paintComponent(Graphics graphic) {   
                Graphics2D g = (Graphics2D)graphic;
                try{
                    if((figure_Creation)&&(point_tab[0].length>0 )){
                        g.setStroke(new BasicStroke(config.getDefaultFigureBorderThickness()));
                        g.setColor(config.defaultFigureFillColour);  
                        //System.out.println(".paintComponent() its working");
                        if((figure_type==FIGURE_TYPE_ELLIPSE_CIRCLE)||(figure_type==FIGURE_TYPE_ELLIPSE)){
                            int dx;
                            int dy;                            
                            if(figure_type==FIGURE_TYPE_ELLIPSE_CIRCLE){
                                int r = (int) Point.distance(point_tab[0][0], point_tab[1][0],curr_mouse.x,curr_mouse.y);  
                                dx = r;
                                dy = r;
                            }else{
                                dx=(int)Math.abs( point_tab[0][0]-curr_mouse.x );
                                dy=(int)Math.abs( point_tab[1][0]-curr_mouse.y );
                            }
                            g.fillOval((int)point_tab[0][0]-dx, (int)point_tab[1][0]-dy, 2*dx, 2*dy);  
                            g.setColor(config.defaultFigureBorderColour); 
                            g.drawOval((int)point_tab[0][0]-dx, (int)point_tab[1][0]-dy, 2*dx, 2*dy);                            
                        }else{
                            if((figure_type!=FIGURE_TYPE_POLYGON && (figure_type!=FIGURE_TYPE_QUADRILATERAL) )){
                                if(point_tab[0].length>1){
                                    g.setColor(config.defaultFigureBorderColour);
                                    for (int i = 2; i < point_tab[0].length ; i++) {                                 
                                        g.drawLine(point_tab[0][i], point_tab[1][i], point_tab[0][i-1], point_tab[1][i-1]);
                                    }                           
                                    g.setColor(Color.RED);
                                    g.drawLine(point_tab[0][0], point_tab[1][0], point_tab[0][1], point_tab[1][1]);
                                    g.setColor(config.defaultFigureBorderColour);
                                }else{
                                    g.setColor(Color.RED);
                                }
                                g.drawLine(point_tab[0][point_tab[0].length-1], point_tab[1][point_tab[1].length-1], curr_mouse.x,curr_mouse.y);
                                
                            }else{
                                int[] posx;
                                int[] posy;
                                if((figure_type==FIGURE_TYPE_QUADRILATERAL)&&(point_tab[0].length == max_figure_pt)){
                                    posx = point_tab[0];
                                    posy = point_tab[1];
                                }else{
                                    posx = Arrays.copyOf(point_tab[0], point_tab[0].length+1);
                                    posy = Arrays.copyOf(point_tab[1], point_tab[1].length+1);
                                    posx[posx.length-1]=curr_mouse.x;
                                    posy[posx.length-1]=curr_mouse.y;
                                }
                                g.fillPolygon(posx, posy, posx.length);                        
                                g.setColor(config.defaultFigureBorderColour);
                                g.drawPolygon(posx, posy, posx.length);
                            }                          
                        } 
                                
                                
                    }
                }catch(NullPointerException | ArrayIndexOutOfBoundsException ex){
                    window.showErrorDialog(t.getText("ERROR_FIGURE_CREATION"));
                    finalizeFigureCreation(true);
                }
            }
            
        };        
        glassPanel.setSize(getSize());
        glassPanel.setVisible(false);
        KeyAdapter k = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(".keyPressed()");
                switch(e.getKeyCode()){
                    case KeyEvent.VK_ESCAPE:
                            finalizeFigureCreation(true);
                        break;
                    case KeyEvent.VK_ENTER:
                            finalizeFigureCreation(false);
                        break;
                }
            }
            
        };
        addKeyListener(k);
        glassPanel.addKeyListener(k);
        MouseAdapter glass_ma = new MouseAdapter(){    
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
                if(e.getButton()==MouseEvent.BUTTON1){
                    if(point_tab[0].length<=max_figure_pt-1){
                        point_tab[0] = Arrays.copyOf(point_tab[0], point_tab[0].length+1);
                        point_tab[1] = Arrays.copyOf(point_tab[1], point_tab[1].length+1);
                        point_tab[0][point_tab[0].length-1] = e.getX();
                        point_tab[1][point_tab[1].length-1] = e.getY();
                        //System.out.println("new point ["+point_tab[0].length+"] out of ["+max_figure_pt+"] ("+(point_tab[0][point_tab[0].length-1])+","+(point_tab[1][point_tab[1].length-1])+")");
                        glassPanel.repaint();
                    }
                }
                if( (
                        (e.getButton()!=MouseEvent.BUTTON1)
                        &&(point_tab[0].length>=min_figure_pt)
                    )
                    ||(point_tab[0].length==max_figure_pt)  
                ){
                    finalizeFigureCreation(false);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                curr_mouse.setLocation(e.getPoint());
                glassPanel.repaint();
                //System.out.println("curr_mouse"+curr_mouse);
            }
            
        };
        glassPanel.addMouseListener(glass_ma);
        glassPanel.addMouseMotionListener(glass_ma);
        
        
        add(glassPanel,DRAG_LAYER);
        ma = new MouseAdapter(){    
            Point prev_point;
            boolean drag = false;
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //drag = true;
                prev_point  = new Point(e.getX(),e.getY());
                super.mouseEntered(e); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //drag = false;
                super.mouseExited(e); //To change body of generated methods, choose Tools | Templates.
            }



            @Override
            public void mousePressed(MouseEvent e) {
                drag = true;
                prev_point = new Point(e.getX(),e.getY());
                resize();
                super.mouseClicked(e); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                drag = false;
                super.mouseReleased(e); //To change body of generated methods, choose Tools | Templates.
            }

            
            
            @Override
            public void mouseMoved(MouseEvent e) {
                getParent().dispatchEvent(translateME(e, getLocation().x, getLocation().y));
                //this.getLocation().x  this.getLocation().y
                //System.out.println((-relative_Position.x+getLocation().x+e.getX())+","+(-relative_Position.y+getLocation().y+e.getY()));
                //System.out.println("(-"+relative_Position.x+"+"+getLocation().x+"+"+e.getX()+"),+(-"+relative_Position.y+"+"+getLocation().y+"+"+e.getY()+")");
                status_bar.updateMouseInfo(-relative_Position.x+getLocation().x +e.getX(),-relative_Position.y+getLocation().y+e.getY());
                super.mouseMoved(e);            

            }
            @Override
            public void mouseDragged(MouseEvent e) {
                getParent().dispatchEvent(translateME(e, getLocation().x, getLocation().y));

                /*
                System.out.println((-relative_Position.x+getLocation().x+e.getX())+","+(-relative_Position.y+getLocation().y+e.getY()));
                System.out.println("(-"+relative_Position.x+"+"+getLocation().x+"+"+e.getX()+"),+(-"+relative_Position.y+"+"+getLocation().y+"+"+e.getY()+")");
                status_bar.updateMouseInfo(-relative_Position.x+getLocation().x+e.getX(),-relative_Position.y+getLocation().y+e.getY());      
                if((Math.abs(e.getX())+Math.abs(e.getY()))>20000){
                    try{
                        throw new Exception();
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                }*/
                
                if(drag){
                    if(prev_point!=null){
                        Rectangle rec = getVisibleRect();

                        rec.translate( e.getX()-prev_point.x, e.getY()-prev_point.y);

                        prev_point.setLocation(e.getX(),e.getY()); 
                        scrollRectToVisible(rec);    
                    }else{
                        prev_point = new Point(e.getX(),e.getY());
                    }    
                }
                //System.out.println(".mouseDragged() by ("+(e.getX()-prev_point.x)+","+(e.getY()-prev_point.y)+") changing visible rectangle to "+getVisibleRect()  );            

                super.mouseDragged(e);             
            }       
        };

        addMouseListener(ma);
        addMouseMotionListener(ma);
        addMouseWheelListener(ma);
        
        
        
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                glassPanel.setPreferredSize(getSize());
                //resize();
            }

        });   
        resize();
    }   
    
    





   //#########################overriden methods#################################
    @Override
    protected void paintComponent(Graphics g) {     
        //System.out.println("window.FigurePanel.paintComponent() pre if");
        if(config.getShowGrid()){
            //System.out.println("window.FigurePanel.paintComponent()post if");
            Graphics2D g2 = (Graphics2D)g;
            int width = getSize().width;
            int height = getSize().height;
            g2.setStroke(new BasicStroke(1));
            g2.setColor(Color.gray);
            //System.out.println("window.FigurePanel.paintComponent() amount of lines: "+width/GRID_MULTIPLIER+"x"+height/GRID_MULTIPLIER+" " );
            for(int x=0; x<width ; x+=GRID_MULTIPLIER){  
                //System.out.println("window.FigurePanel.paintComponent() drawLine("+x+","+0+", "+x+", "+height+")" );
                g2.drawLine(x,0, x, height);
            }
            for(int y=0; y<height ; y+=GRID_MULTIPLIER){;
                //System.out.println("window.FigurePanel.paintComponent() drawLine("+0+","+y+", "+width+", "+0+")" );
                g2.drawLine(0,y, width, y);
            }  
        }
        super.paintComponent(g);
    }
}
