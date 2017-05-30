/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package figures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import main.Text;
import window.Configuration;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.PopupMenuEvent;
import window.FigurePanel;
import static window.Window.translateME;
import static java.lang.Math.abs;
import static java.lang.Math.abs;

/**
 * Class of drawable polygon.
 * @author n1t4chi
 */
public class Polygon extends Figure{
    
   //##########################fields###########################################
    
    /**
     * list of lines.
     */    
    public final ArrayList lines;  
    
    /**
     * Popup submenu. Polygon points submenu.
     */
    protected final JMenu menu_Point;
    /**
     * Popup menu item. Adds point on nearby line.
     */
    protected final JMenuItem menu_Point_Add;
    /**
     * Popup menu item. Removes nearby point.
     */
    protected final JMenuItem menu_Point_Remove;
    /**
     * closest line. For popup menu purposes.
     */
    protected PolygonLine closest_line;
    
    
    
   //#########################methods###########################################
    
    
    /**
     * Returns closest line to mouse position.
     * @param e Mouse event object.
     * @return Line or null if there is no line.
     */
    public PolygonLine getNearestLine( MouseEvent e ){
        PolygonLine rtrn = null;
        double dist=Integer.MAX_VALUE;
        generateLines(false);
        //System.out.println("Point: ["+e.getX()+"]["+e.getY()+"]");
        for (Object o : lines) {
            //System.out.println("Current line: "+rtrn+" cur dist:"+dist);
            if(o instanceof PolygonLine){          
                double d=((PolygonLine)o).ptSegDist(e.getX(), e.getY() );
                if(  dist>=d  ){
                    rtrn = (PolygonLine)o;
                    dist = d;
                }
            }
        }
        
        return rtrn;
    }

    /**
     * Returns line that mouse is nearby.
     * @param e Mouse event object.
     * @return Line or null if not one was found.
     */
    public PolygonLine getLineNearMouse( MouseEvent e ){
        PolygonLine rtrn = null;
        generateLines(false);
        for (Object o : lines) {
            if(o instanceof PolygonLine){
                if(  SEARCH_BUFFER>((PolygonLine)o).ptLineDist(e.getX(), e.getY() )  ){
                    rtrn = (PolygonLine)o;
                    break;
                }
            }
        }
        
        return rtrn;
    }
    /**
     * Checks if line list is correctly build at the moment.
     * @return True if yes, false otherwise.
     */
    public boolean checkLines(){
        boolean rtrn = false;   
        FigurePoint point_A=null;
        FigurePoint point_B=null;
        int line_index=0;
        try{
            for (Object o : points) {
                if(o instanceof FigurePoint){
                    while(!(lines.get(line_index) instanceof PolygonLine)){
                        lines.remove(lines.get(line_index));
                    }   
                    if(point_A==null){
                        point_A=((FigurePoint) o);
                    }else{                      
                        point_B=((FigurePoint) o);
                        Object line = lines.get(line_index);
                        line_index++;
                        rtrn = (((PolygonLine)line).has(point_A)&&((PolygonLine)line).has(point_B));
                        point_A=point_B;         
                    }           
                }else{
                    points.remove(o);
                }
                if(!rtrn){
                    break;
                }
            }   
        }catch(IndexOutOfBoundsException ex){
            rtrn = false;
        }
        if(rtrn &&( line_index<lines.size() )){
            lines.subList(0, line_index).clear();
        }
        return rtrn;
    }
    /**
     * (Re)Generates {@link #lines}.
     * @param forceGenerate forces to regenerate lines.
     */
    public void generateLines(boolean forceGenerate){
        if( ((forceGenerate)||(!checkLines()))&&(lines!=null) ){
            //System.out.println("figures.Polygon.generateLines() lines:"+lines);
            lines.clear();
            FigurePoint A=null;
            FigurePoint B;
            for (Object o : points) {
                if(o instanceof FigurePoint){
                    if(A==null){
                        A=((FigurePoint) o);
                    }else{
                        B=((FigurePoint) o);
                        lines.add(new PolygonLine(A,B,this));
                        A=B;
                        B=null;

                    }              
                }else{
                    points.remove(o);
                }         
            }
            if(A!=null){
                B=((FigurePoint)points.get(0));
                lines.add(new PolygonLine(A,B,this));
            }
        }
    }
    /**
     * 
     * @return True if polygon is Equilateral. 
     */
    public boolean isEquilateral(){      
        generateLines(true);
        //System.out.println("figures.Polygon.isEquilateral()");
        boolean rtrn = true;
        double side = -1337;
        for (Object o : lines) {
            if( o instanceof PolygonLine){ 
                double d = ((PolygonLine) o).getPP1().coordinates.distance(((PolygonLine) o).getPP2().coordinates);
                //System.out.println("Side:"+d);
                if(side==-1337){
                    side = d;
                }else{
                    if(Math.abs(side-d)>0.001){
                        rtrn = false;
                        break;
                    }
                }
            }
        }    
        return rtrn;
    }
    /**
     * Checks if this polygon is simple. (No side intersect with another)
     * @return True if polygon is simple, false otherwise.
     */
    public boolean isSimplePolygon(){
        generateLines(true);
        boolean rtrn = true;      
        if(lines.size()>=3){
            if(lines.size()>3){
                for(int i=0; (i<lines.size())&&(rtrn) ;i++){    
                    Object l1 = lines.get(i);
                    if( l1 instanceof PolygonLine){                   
                        for(int j=i+2; (j<(lines.size()-((i==0)?1:0))    )&&(rtrn)   ;j++){  
                            Object l2 = lines.get(j);
                            if( l2 instanceof PolygonLine){     
                                if(((PolygonLine) l1).intersectsLine((PolygonLine)l2)){
                                    /**System.out.println( ((PolygonLine) l1).getP1()
                                            +" "+((PolygonLine) l1).getP2()
                                            +" >>>> "+((PolygonLine) l2).getP1()
                                            +" "+((PolygonLine) l2).getP2()  );*/
                                    rtrn = false;
                                }                              
                            }
                        }
                    }
                }

            }else{
                rtrn = true;
            }
        }else{
            rtrn = false;
        }
        return rtrn;
    }
    
    /**
     * Method updates {@link #closest_line} with current nearby line.
     * @param e Mouse Event.
     */
    protected void updateNearbyLine(MouseEvent e){
        //System.out.println("e location:"+e.getPoint());
        closest_line = getNearestLine(e);
        //System.out.println("closest line:"+closest_line);
    }
    
    
    
   //#########################constructors######################################
    
    /**
     * Default constructor.
     * @param x X coordinates.
     * @param y Y coordinates.
     * @param border_c Border colour.
     * @param border_t Border thickness.
     * @param fill_c Fill colour.
     * @param config Configuration for Polygons.
     * @param t Text object.
     * @param container Container of this polygon.
     * @throws FigureException When:
     * <br> - list coordinates are not equal in POLYGON_POINT_SIZE
     * <br> - one of pointers is null
     */
    public Polygon(double[] x, double[] y, Color border_c, int border_t, Color fill_c, Configuration config, Text t,FigurePanel container) throws FigureException { 
        super(x,y,border_c,border_t,fill_c,config,t,container);
        this.lines = new ArrayList();
        //lines = new ArrayList();   
        generateLines(true);
        this.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MouseEvent m = getRelativeMouseEvent(e);
                setRelativeMousePosition(e);
                updateNearbyLine(m);             
                if(e.getClickCount()==2){                   
                    addPoint(new FigurePoint(
                                    new FPoint(mouse_position.getX(),mouse_position.getY())
                                    ,Polygon.this
                                )
                            ,Math.max(points.indexOf(closest_line.getPP1())
                                    ,points.indexOf(closest_line.getPP2())
                                ) 
                    );
                }
            }         
        });
        
        //menu.setVisible(false);
        
        menu_Point = new JMenu(t.getText("FIGURE_POINT"));
        menu.add(menu_Point);
        menu_Point_Add = new JMenuItem(new AbstractAction(t.getText("FIGURE_POINT_ADD")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println(".actionPerformed()");
                if((closest_line!=null)&&(mouse_position!=null)){
                    //System.out.println("adding index: max from ("+(points.indexOf(closest_line.getPP1()))+","+(points.indexOf(closest_line.getPP2()))+") " );
                    addPoint(new FigurePoint(
                                    new FPoint(mouse_position.getX(),mouse_position.getY())
                                    ,Polygon.this
                                )
                            ,Math.max(points.indexOf(closest_line.getPP1())
                                    ,points.indexOf(closest_line.getPP2())
                                ) 
                    );
                }else{
                    //System.out.println(".actionPerformed() nearby line "+closest_line);
                    //System.out.println(".actionPerformed() mouse_position "+mouse_position);
                }
            }
        });
        menu_Point_Remove = new JMenuItem(new AbstractAction(t.getText("FIGURE_POINT_REMOVE")) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nearby_point!=null){
                    if(points.size()==1){
                        container.removeFigure(Polygon.this);
                    }else{
                        removePoint(nearby_point);
                    }
                }else{
                    //System.out.println(".actionPerformed() nearby point "+nearby_point);
                }
            }
        });    
        menu_Point_Remove.setVisible(false);
        menu_Point.add(menu_Point_Add);
        menu_Point.add(menu_Point_Remove);

        resize();   
    }

    /**
     * Constructor that uses default settings.
     * @param x X coordinates within polygon.
     * @param y Y coordinates within polygon.
     * @param config Configuration for Polygons.
     * @param t Text object.
     * @param container Container of this polygon.
     * @throws FigureException when {@link #Polygon(double[], double[], java.awt.Color, int, java.awt.Color, window.Configuration, main.Text, window.FigurePanel) } does
     */
    public Polygon(double[] x, double[] y,Configuration config, Text t,FigurePanel container) throws FigureException {    
        this(x, y, config.getDefaultFigureBorderColour(), config.getDefaultFigureBorderThickness(), config.getDefaultFigureFillColour(), config,t,container);
    }     

    
    
    
    
    
    
    
    
    
   //#########################overriden methods#################################
    
     @Override
    public double getPerimeter(){
        double rtrn = 0;
        for (Object o : lines) {
            if(o instanceof PolygonLine){              
                rtrn += ((PolygonLine) o).getP1().distance(((PolygonLine) o).getP2());
            }
        }
        return rtrn;
    }
    
    
    @Override
    public double getArea(){
        FPoint A=null;
        FPoint B=null;
        double rtrn = 0;
        if(isSimplePolygon()){
            for(int i=0; i<=points.size() ; i++){
                Object o;
                if(i==points.size()){
                    o = points.get(0);
                }else{                  
                    o = points.get(i);
                }
                if(o instanceof FigurePoint){
                    if(A==null){
                        A = ((FigurePoint) o).coordinates;
                    }else{
                        if(B==null){ 
                            B = ((FigurePoint) o).coordinates;
                            rtrn+=( A.getX()*B.getY()-B.getX()*A.getY() );
                            A = B;
                            B = null;
                        }
                    }
                }
            }
            return abs(rtrn)/2;
        }else{
            return -1;
        }
    }    
    
    @Override
    public boolean addPoint(FigurePoint p){  
        boolean rtrn = super.addPoint(p);
        generateLines(true);
        repaint();
        return rtrn;
    }

    @Override
    public boolean addPoint(FigurePoint p,int index){ 
        boolean rtrn = super.addPoint(p,index);
        generateLines(true);
        resize();
        setMeAsActiveFigure();
        repaint();
        return rtrn;
    }
    

    /**
     * Removes all points without disposing this polygon. Used only to recreate polygon.
     */
    private void removeAllPoints(){
        for(int i = points.size()-1;i>=0;i--){       
            remove( (FigurePoint)points.get(i));
            
            points.remove(i);
        }
    }
    
    @Override
    public boolean removePoint(FigurePoint p){
        boolean rtrn = super.removePoint(p);
        generateLines(true);
        generateLines(true);
        resize();
        setMeAsActiveFigure();
        repaint();
        return rtrn;
    }    
    
    @Override
    public int getCornerCount(){
        return points.size();
    }
    
    @Override
    public String getFigureName(int Language){
        String rtrn = "";
        int n = this.getCornerCount();
        switch(Language){
            case Text.LANGUAGE_POLISH:
                    if(n<=20){
                        rtrn = t.getText(n+"PREFIX",Text.LANGUAGE_POLISH);
                        rtrn = rtrn + t.getText("POLYGON_SUFIX",Text.LANGUAGE_POLISH)+" ";
                    }else{
                        rtrn = t.getText("POLYGON",Text.LANGUAGE_POLISH)+" ";
                    }                 
                    if(isEquilateral()) rtrn = rtrn + t.getText("EQUILATERAL",Text.LANGUAGE_POLISH);
                break;
                
            case Text.LANGUAGE_ENGLISH: default:
                    if(isEquilateral()) rtrn = t.getText("EQUILATERAL",Text.LANGUAGE_ENGLISH)+" ";
                    if(n==1){
                        rtrn = rtrn + t.getText("MONOGON",Text.LANGUAGE_ENGLISH);
                    }else{
                        if(n<100){
                            if(n>=20){
                                if(n==20){
                                    rtrn = rtrn + t.getText("20ALONE",Text.LANGUAGE_ENGLISH);
                                }else{
                                    rtrn = rtrn + t.getText((n-n%10)+"TENS",Text.LANGUAGE_ENGLISH);
                                    if(n%10!=0){
                                        rtrn = rtrn + t.getText("POLYGON_CONJUNCTION",Text.LANGUAGE_ENGLISH);
                                    }
                                }
                            }
                            if(!((n==11)||(n==12)||(n%10==0))){
                                rtrn = rtrn + t.getText((n%10)+"SINGLES",Text.LANGUAGE_ENGLISH);   
                            }else{
                                if((n==11)||(n==12)){
                                    rtrn = rtrn + t.getText(n+"DECA_PREFIX",Text.LANGUAGE_ENGLISH);   
                                }
                            }    
                            if((n>=10)&&(n<20)){                               
                                rtrn = rtrn + t.getText("DECA_SUFIX",Text.LANGUAGE_ENGLISH);
                            }                           
                            rtrn = rtrn + t.getText("POLYGON_SUFFIX",Text.LANGUAGE_ENGLISH);          
                        }else{
                            if(n==100){
                                rtrn = t.getText("HECTOGON",Text.LANGUAGE_ENGLISH);
                            }else{
                                rtrn = t.getText("POLYGON",Text.LANGUAGE_ENGLISH);
                            }
                        }                  
                    }
                
                break;
        }
        return rtrn;
    }
    
    
    @Override
    protected void doThisWhenPopupMenuIsToBeShown(MouseEvent e){
        updateNearbyLine(e);     
    }
    @Override
    protected void doThisPopupMenuWillBecomeInvisible(PopupMenuEvent e){}
    @Override
    protected void doThisPopupMenuCanceled(PopupMenuEvent e){}
    

    @Override
    protected void doThisPopupMenuWillBecomeVisible(PopupMenuEvent e){
        //System.out.println(".popupMenuWillBecomeVisible()");
        //System.out.print("Menu point add will be ");
        if(closest_line == null){
           //.out.println("hidden");
            menu_Point_Add.setVisible(false);
        }else{
            menu_Point_Add.setVisible(true);
            //System.out.println("visible");
        }        
        //System.out.print("Menu point removeFigure will be ");
        if(nearby_point == null){
            //System.out.println("hidden");
            menu_Point_Remove.setVisible(false);
        }else{
            menu_Point_Remove.setVisible(true);
            //System.out.println("visible");
        }
    }
    
    @Override
    public void doThisWhenPointIsDragged(MouseEvent e,FigurePoint point,int dx, int dy){
        if(isResizeable()){
            FPoint p = askParentIsMouseNearPoint(translateME(e, -container.relative_Position.x+point.getLocation().x+getLocation().x, -container.relative_Position.y+point.getLocation().y+getLocation().y),point);       
                  
            if(p==null){
                point.coordinates.Translate(dx,dy);
            }else{
                point.coordinates.setLocation(p.getX(),p.getY());
            }
            //setLocation(point.coordinates.getPoint());
            resize();
            repaint();
        }
    }   
    @Override
    public void relocate(){
        //try{
        //System.out.println("figures.Polygon.relocate() points:"+points.size());
            int[][] xy = extremes();
            //System.out.println("figures.Polygon.relocate() container"+container  );
            //System.out.println("figures.Polygon.relocate() container.relative_Position"+container.relative_Position  );
            //System.out.println("figures.Polygon.relocate() xy"+xy  );
            //System.out.println("figures.Polygon.relocate() xy.length"+xy.length  );
            //System.out.println("figures.Polygon.relocate() xy[X[.length"+xy[X].length  );
            setLocation((int) container.relative_Position.getX()+xy[X][MIN]-FREE_XY_SPACE,(int)container.relative_Position.getY()+xy[Y][MIN]-FREE_XY_SPACE);
        //}catch(NullPointerException ex){}
    }
    @Override
    public void resize(){ 
        //try{
            int[][] xy = extremes();

            int curr_min_x = -relativeLocation.x+FREE_XY_SPACE;
            int curr_min_y = -relativeLocation.y+FREE_XY_SPACE;   

            int dx = curr_min_x-(int)xy[X][MIN];
            int dy = curr_min_y-(int)xy[Y][MIN];
            //setLocation((int)container.relative_Position.getX()+xy[X][MIN]-FREE_XY_SPACE,(int)container.relative_Position.getY()+xy[Y][MIN]-FREE_XY_SPACE);

            relativeLocation.translate(dx, dy);

            //setLocation(getLocation().x-dx, getLocation().y-dy);
            int new_x = (int)((xy[X][MAX]-xy[X][MIN])+2*FREE_XY_SPACE);
            int new_y = (int)((xy[Y][MAX]-xy[Y][MIN])+2*FREE_XY_SPACE);
            //+borderThickness
            setPreferredSize(new Dimension( new_x,new_y));   
            setSize(getPreferredSize());
            
            if(( Math.abs(new_x)>15000 || Math.abs(new_y)>15000  )){
                try{
                    throw new Exception();
                }catch(Exception ex){     
                    System.err.println("min x:"+xy[X][MIN]+"min y:"+xy[Y][MIN]+"man x:"+xy[X][MAX]+"man y:"+xy[Y][MAX]+" relativeLocation["+relativeLocation.x+","+relativeLocation.y+"] deltaXY["+dx+","+dy+"]");
                    System.err.println( "new_x:"+new_x+" new_y:"+new_y );
                    ex.printStackTrace();
                }
            }
            //repaint();
            //container.resize();
            //System.out.println(this);
        //}catch(NullPointerException ex){}
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        relocate();
        //System.out.println("Painting");     
        int[][] xy = getRelativeIntegerArrayOfCoordinates(relativeLocation.x,relativeLocation.y);   
        //Size();
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke((float)borderThickness ));
        g2.setColor(fillColour);
        g2.fillPolygon(xy[0], xy[1], xy[0].length);
        g2.setColor(borderColour);
        g2.drawPolygon(xy[0], xy[1], xy[0].length);
        super.paintComponent(g);
    }
    @Override
    public String toString() {
        String pts="";
        for (Object o : this.points) {
            if(o instanceof FigurePoint){
                pts+=((FigurePoint)o).toString(true);
            }
        }
        return getFigureName()+", "+pts+" n:"
                + "["+points.size()+"] "
                + "is simple: ["+isSimplePolygon()+"]"
                +( (isSimplePolygon())
                    ?(" area["+getArea()+"] perimeter["+getPerimeter()+"]  ")
                    :"" 
                );
    }

    @Override
    public void doThisWhenIAmDragged(MouseEvent e, int dx, int dy) {
        if(isMoveable()){
            translatePoints( (Math.abs(dx)<4)?dx:(int)Math.signum(dx)*4 , (Math.abs(dy)<4)?dy:(int)Math.signum(dy)*4   );
            resize();
            repaint();
           // container.resize();
           // container.repaint(new Rectangle(0,0,container.getWidth(),container.getHeight()) );
        }
    }

    @Override
    public Dimension getFigureSize() {
        return super.getFigureSize(); //To change body of generated methods, choose Tools | Templates.
    }  
    @Override
    protected void doThisWhenMouseIsPressedOnPoint(MouseEvent e,FigurePoint p) {
        setMoveable(false);
    }

    @Override
    protected void doThisWhenMouseIsReleasedOnPoint(MouseEvent e,FigurePoint p) {
        setMoveable(true);
    }

    @Override
    public String getInfoForStatusBar() {
        return "["+getCornerCount()+"]"+super.getInfoForStatusBar();
    }

    @Override
    protected String getSaveableFormatSubclass() {
        String rtrn = "";
        for (Object point : points) {
            if(point instanceof FigurePoint){
                rtrn+="("+((FigurePoint)point).coordinates.getX()+","+((FigurePoint)point).coordinates.getY()+")";
            }    
        }     
        return  rtrn;
                
    }

    @Override
    protected void recreateFromSaveSubclass(String s) throws FigureException {
        try{
           // System.out.println("figures.Polygon.recreateFromSaveSubclass() [1]size:"+points.size());
            removeAllPoints();
            //System.out.println("figures.Polygon.recreateFromSaveSubclass() [2]size:"+points.size());
            while( s.contains( ")" )  ){
                addPoint( new FigurePoint( new FPoint( 
                        Double.parseDouble( s.substring( s.indexOf("(")+1 , s.indexOf(",")  ) ),    
                        Double.parseDouble( s.substring( s.indexOf(",")+1 , s.indexOf(")")  ) )        
                ) , this));
                //System.out.println("figures.Polygon.recreateFromSaveSubclass() [3]size:"+points.size());
                
                s = s.substring( s.indexOf(")")+1 );
                
            }          
        }catch(NumberFormatException ex){
            throw new FigureException(FigureException.FIGURE_EXCEPTION_BAD_SAVE_STRING_FORMAT);
        }
    }
    
}