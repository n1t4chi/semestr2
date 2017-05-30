/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author n1t4chi
 */
public class Layer {
    LayerConfig lc;

    final List<HashMap<String,Cell>> data_list;
    public String geom_col = "";
    
    /**
     * 
     * @param lc 
     */
    public Layer(LayerConfig lc) {
        this.data_list = Collections.synchronizedList(new ArrayList<HashMap<String,Cell>>());
        this.lc = lc;
    }
    
    public void retrieveData(Statement st) throws SQLException{
        retrieveData(st,-1,-1,-1,-1);
    }
    
    public void retrieveData(Statement st,double x_min,double y_min,double x_max,double y_max) throws SQLException{
        data_list.clear();
        String querry = "SELECT * FROM "+lc.getTableName();
        if(!((x_min==y_min)&&(y_min==x_max)&&(x_max==y_max)&&(y_max==-1))){
            querry +=" WHERE location && ST_MakeEnvelope("+x_min+","+y_min+","+x_max+","+y_max+",2180)";
        }
        ResultSet rs = st.executeQuery(querry);
        ResultSetMetaData md = rs.getMetaData();
        String[] col_nam = new String[md.getColumnCount()];
        for(int i=1;i<=md.getColumnCount();i++){
            col_nam[i-1]=md.getColumnName(i);
            //System.out.println(md.getColumnClassName(i));
            if(md.getColumnClassName(i).contains("PGgeometry"))
                geom_col = col_nam[i-1];
        }
        while(rs.next()){
            HashMap<String,Cell> hs = new HashMap<>();
            for(int i=0;i<col_nam.length;i++){
                if(!col_nam[i].isEmpty()){
                    hs.put(col_nam[i], new Cell(rs.getObject(col_nam[i])));
                }
            }
            data_list.add(hs);
        }
    }
    
    public ArrayList<Cell> getLocationData(){
        ArrayList<Cell> al = new ArrayList<>();
        for(HashMap<String,Cell> hm : data_list){
            al.add(hm.get(geom_col));
        }
        return al;
    }
    public List<HashMap<String,Cell>> getData(){
        return data_list;
    }
    
    public LayerConfig getLayerConfig(){
        return lc;
    }
}
