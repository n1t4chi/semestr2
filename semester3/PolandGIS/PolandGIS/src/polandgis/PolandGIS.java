/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package polandgis;

import basicgui.BasicFrame;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import layer.Cell;
import layer.Layer;
import layer.LayerConfig;
import layer.StyleConfigType;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;
import org.postgresql.util.PGobject;

/**
 *
 * @author n1t4chi
 */
public class PolandGIS {
    
    Connection c;
    BasicFrame gui;
    final PostGISConfig conf;
            
    public PolandGIS() {
        conf = PostGISConfig.defaultPostGISConfig();
        conf.load("");
        
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://"+conf.getIp()+":"+conf.getPort()+"/"+conf.getDatabase(),conf.getUsername(), conf.getPassword());
            c.setAutoCommit(false);
            ((org.postgresql.PGConnection)c).addDataType("geometry", (Class<? extends PGobject>) Class.forName("org.postgis.PGgeometry"));
        } catch (ClassNotFoundException | SQLException e) {
           System.err.println(e.getClass().getName()+": "+e.getMessage());
           System.exit(0);
        }
        //System.out.println("Opened database successfully");
        
        SwingUtilities.invokeLater(()->{
            this.gui = new BasicFrame("PolandGIS",null,"PolandGIS"){
                
                @Override
                public boolean shouldExit(){
                    try{
                        if(c!=null)
                            c.close();
                        conf.save("");
                    }catch(SQLException ex){
                        System.err.println("Error on closing connection:"+ex.getMessage());
                    }
                    return true;
                }
                
            };
            this.gui.setVisible(true);   
        });
        
        LayerConfig conf = new LayerConfig(StyleConfigType.POLYGON, "Budynki", "Buildings");
        Layer layer = new Layer(conf);
        try{
            Statement st = c.createStatement();
            layer.retrieveData(st, 0, 0, 7, 7);
            ArrayList<Cell> al = layer.getLocationData();
            for(Cell c:al){
                Polygon pl = (Polygon)c.getData();
                for(int i=0; i<pl.numPoints();i++){
                    Point p = pl.getPoint(i);
                    System.out.print("( "+p.x+" , "+p.y+" )");
                }
                System.out.println("");
            }
                System.out.println("");
                System.out.println("");
            layer.retrieveData(st);
            al = layer.getLocationData();
            for(Cell c:al){
                Polygon pl = (Polygon)c.getData();
                for(int i=0; i<pl.numPoints();i++){
                    Point p = pl.getPoint(i);
                    System.out.print("( "+p.x+" , "+p.y+" )");
                }
                System.out.println("");
            }
        } catch (SQLException e) {
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        } 
        
        
    }

    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PolandGIS pg = new PolandGIS();
    }
    
}


            //receiving table
                /*
                ResultSet rs = st.executeQuery("SELECT * FROM Buildings");
                ResultSetMetaData md = rs.getMetaData();
                System.out.println("Available columns:");
                for(int i=1;i<=md.getColumnCount();i++){
                    System.out.print(md.getColumnName(i)+"("+md.getColumnClassName(i)+")\t");
                }
                System.out.println();
                while(rs.next()){
                    int gid = rs.getInt("gid");
                    
                    int street_gid = rs.getInt("street_gid");
                    PGgeometry geom = rs.getObject("location", PGgeometry.class);
                    System.out.print("Received object: ["+gid+"]["+street_gid+"] location: ");
                    Polygon pl = (Polygon)geom.getGeometry();
                    for(int i=0; i<pl.numPoints();i++){
                        Point p = pl.getPoint(i);
                        System.out.print("( "+p.x+" , "+p.y+" )");
                    }
                    System.out.println();
                }*/