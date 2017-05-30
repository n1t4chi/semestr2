/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package polandgis;

import java.util.ArrayList;
import layer.LayerConfig;
import layer.StyleConfigType;
import util.Config;

/**
 *
 * @author n1t4chi
 */
public class PostGISConfig extends Config {
    
    
    public static final PostGISConfig defaultPostGISConfig(){
        PostGISConfig p = new PostGISConfig();
        p.ip="localhost";
        p.port=5432;
        p.database="polska";
        p.username="postgres";
        p.password="zxcvbnm";
        p.buildingsConfig=new LayerConfig(StyleConfigType.POLYGON, "Budynki", "buildings");
        p.addressesConfig=new LayerConfig(StyleConfigType.POLYGON, "Adresy", "addresses");
        p.cadastresConfig=new LayerConfig(StyleConfigType.POLYGON, "Działki", "cadastres");
        p.municipalitiesConfig=new LayerConfig(StyleConfigType.POLYGON, "Gminy", "municipalities");
        p.powiatyConfig=new LayerConfig(StyleConfigType.POLYGON, "Powiaty", "powiaty");
        p.settlementsConfig=new LayerConfig(StyleConfigType.POLYGON, "Miejscowości", "settlements");
        p.streetsConfig=new LayerConfig(StyleConfigType.POLYGON, "Ulice", "streets");
        p.voivodeshipsConfig=new LayerConfig(StyleConfigType.POLYGON, "Województwa", "voivodeships");
        p.layersConfig=new ArrayList<>();
        return p;
    }
    
    
    String ip;
    int port;
    String database;
    String username;
    String password;

    LayerConfig buildingsConfig;
    LayerConfig addressesConfig;
    LayerConfig cadastresConfig;
    LayerConfig municipalitiesConfig;
    LayerConfig powiatyConfig;
    LayerConfig settlementsConfig;
    LayerConfig streetsConfig;
    LayerConfig voivodeshipsConfig;
    ArrayList<LayerConfig> layersConfig;

    /**
     * constructor, no field is initialised!!
     */
    public PostGISConfig() {
        ip=null;
        port=-1;
        database=null;
        username=null;
        password=null;
        buildingsConfig=null;
        addressesConfig=null;
        cadastresConfig=null;
        municipalitiesConfig=null;
        powiatyConfig=null;
        settlementsConfig=null;
        streetsConfig=null;
        voivodeshipsConfig=null;
        layersConfig = null;
    }

    public PostGISConfig(String ip, int port, String database, String username, String password, LayerConfig buildingsConfig, LayerConfig addressesConfig, LayerConfig cadastresConfig, LayerConfig municipalitiesConfig, LayerConfig powiatyConfig, LayerConfig settlementsConfig, LayerConfig streetsConfig, LayerConfig voivodeshipsConfig, ArrayList<LayerConfig> layersConfig) {
        this.ip = ip;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.buildingsConfig = buildingsConfig;
        this.addressesConfig = addressesConfig;
        this.cadastresConfig = cadastresConfig;
        this.municipalitiesConfig = municipalitiesConfig;
        this.powiatyConfig = powiatyConfig;
        this.settlementsConfig = settlementsConfig;
        this.streetsConfig = streetsConfig;
        this.voivodeshipsConfig = voivodeshipsConfig;
        this.layersConfig = layersConfig;
    }


    
    @Override
    public boolean copy(Config model) {
        if(model instanceof PostGISConfig){
            this.ip = ((PostGISConfig) model).ip;
            this.port = ((PostGISConfig) model).port;
            this.database = ((PostGISConfig) model).database;
            this.username = ((PostGISConfig) model).username;
            this.password = ((PostGISConfig) model).password;
            this.buildingsConfig = ((PostGISConfig) model).buildingsConfig;
            this.addressesConfig = ((PostGISConfig) model).addressesConfig;
            this.cadastresConfig = ((PostGISConfig) model).cadastresConfig;
            this.municipalitiesConfig = ((PostGISConfig) model).municipalitiesConfig;
            this.powiatyConfig = ((PostGISConfig) model).powiatyConfig;
            this.settlementsConfig = ((PostGISConfig) model).settlementsConfig;
            this.streetsConfig = ((PostGISConfig) model).streetsConfig;
            this.voivodeshipsConfig = ((PostGISConfig) model).voivodeshipsConfig;
            this.layersConfig = ((PostGISConfig) model).layersConfig;
            return true;
        }else{
            return false;
        }
    }
    
    @Override
    public String getFileName() {
        return "PostGIS";
    }
    
    

    public LayerConfig getAddressesConfig() {
        return addressesConfig;
    }

    public LayerConfig getBuildingsConfig() {
        return buildingsConfig;
    }

    public LayerConfig getCadastresConfig() {
        return cadastresConfig;
    }

    public String getDatabase() {
        return database;
    }

    public String getIp() {
        return ip;
    }

    public ArrayList<LayerConfig> getLayersConfig() {
        return layersConfig;
    }

    public LayerConfig getMunicipalitiesConfig() {
        return municipalitiesConfig;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public LayerConfig getPowiatyConfig() {
        return powiatyConfig;
    }

    public LayerConfig getSettlementsConfig() {
        return settlementsConfig;
    }

    public LayerConfig getStreetsConfig() {
        return streetsConfig;
    }

    public String getUsername() {
        return username;
    }

    public LayerConfig getVoivodeshipsConfig() {
        return voivodeshipsConfig;
    }

    public void setAddressesConfig(LayerConfig addressesConfig) {
        this.addressesConfig = addressesConfig;
    }

    public void setBuildingsConfig(LayerConfig buildingsConfig) {
        this.buildingsConfig = buildingsConfig;
    }

    public void setCadastresConfig(LayerConfig cadastresConfig) {
        this.cadastresConfig = cadastresConfig;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setLayersConfig(ArrayList<LayerConfig> layersConfig) {
        this.layersConfig = layersConfig;
    }

    public void setMunicipalitiesConfig(LayerConfig municipalitiesConfig) {
        this.municipalitiesConfig = municipalitiesConfig;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPowiatyConfig(LayerConfig powiatyConfig) {
        this.powiatyConfig = powiatyConfig;
    }

    public void setSettlementsConfig(LayerConfig settlementsConfig) {
        this.settlementsConfig = settlementsConfig;
    }

    public void setStreetsConfig(LayerConfig streetsConfig) {
        this.streetsConfig = streetsConfig;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setVoivodeshipsConfig(LayerConfig voivodeshipsConfig) {
        this.voivodeshipsConfig = voivodeshipsConfig;
    }
    
    
    
}
