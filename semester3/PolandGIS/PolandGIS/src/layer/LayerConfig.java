/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layer;

import java.awt.Color;
import java.util.Random;

/**
 * Layer configuration
 * @author n1t4chi
 */
public class LayerConfig {
    private LabelConfig labelConfig;
    private StyleConfig styleConfig;
    private String layerName;
    private String tableName;
    
    
    
    /**
     * Constructor, sets label and style as null pointers!!.
     */
    public LayerConfig() {
        this.labelConfig = null;
        this.styleConfig = null;
        layerName = null;
        tableName = null;
    }

    /**
     * Default constructor. Randomizes type at the start.
     * @param type 
     * @param layerName 
     * @param tableName 
     * @throws NullPointerException if one of pointers is null
     */
    public LayerConfig(StyleConfigType type, String layerName, String tableName) {
        if((type == null)||(layerName == null)||(tableName == null))
            throw new NullPointerException("null pointer");
        this.layerName = layerName;
        this.tableName = tableName;
        this.labelConfig = new LabelConfig();
        switch(type){
            case LINE:
                this.styleConfig = new StyleConfigLine(StrokeType.LINE, new Color( (new Random()).nextInt()), 1, false);
                break;
            case POINT:
                this.styleConfig = new StyleConfigPoint(1, StyleConfigPoint.figureType.SQUARE,new Color( (new Random()).nextInt()), false);
                break;
            case POLYGON:
                this.styleConfig = new StyleConfigPolygon(new Color( (new Random()).nextInt()), StrokeType.LINE, new Color( (new Random()).nextInt()), 1);
                break;     
             default:
                    throw new UnsupportedClassVersionError("Unsupported type");
        }
    }

    
    public String getLayerName() {
        return layerName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setLabelConfig(LabelConfig labelConfig) {
        this.labelConfig = labelConfig;
    }

    public void setStyleConfig(StyleConfig styleConfig) {
        this.styleConfig = styleConfig;
    }

    
    
    /**
     * Returns label config
     * @return 
     */
    public LabelConfig getLabelConfig() {
        return labelConfig;
    }
    /**
     * Returns style config
     * @return 
     */
    public StyleConfig getStyleConfig() {
        return styleConfig;
    }
    
    
}
