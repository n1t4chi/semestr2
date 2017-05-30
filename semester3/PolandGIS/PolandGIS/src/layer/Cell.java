/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layer;

import org.postgis.Geometry;
import org.postgis.LineString;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.postgis.Polygon;

/**
 * Used for containing cell Data
 * @author n1t4chi
 */
public class Cell {
    final Object data;
    final CellType type;
    
    
    public enum CellType {
        INTEGER,
        DOUBLE,
        BOOLEAN,
        TEXT,
        LINE,
        POLYGON,
        POINT,
        UNSUPPORTED_GEOMETRY,
        UNSUPPORTED,
        NULL
    }
    
    
    /**
     * Constructor, Supports Integer, Double, String, Polygon, 
     * @param data 
     * @throws IllegalArgumentException on invalid data
     */
    public Cell(Object data) {
        if(data instanceof PGgeometry){
            Geometry g = ((PGgeometry)data).getGeometry();
            if(g instanceof Polygon){
                type = CellType.POLYGON;
            }else if(data instanceof LineString){
                type = CellType.LINE;
            }else if(data instanceof Point){
                type = CellType.POINT;
            }else {
                type = CellType.UNSUPPORTED_GEOMETRY;
            }
            this.data = g;
        }else{     
            this.data = data;
            if(data instanceof Integer){
                type = CellType.INTEGER;
            }else if(data instanceof Double){
                type = CellType.DOUBLE;
            }else if(data instanceof String){
                type = CellType.TEXT;
            }else if(data instanceof Boolean){
                type = CellType.BOOLEAN;
            }else if(data == null) {
                type = CellType.NULL;
            }else{
                type = CellType.UNSUPPORTED;
            }
        }
    }

    
    
    public Object getData() {
        return data;
    }
    
}
