/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import window.Configuration;

/**
 * Class containing text values to be displayed in program.
 * @author n1t4chi
 */
public class Text { 
    
   //###########################Static Context##################################
    /**
     * Name of application.
     */
    public static final String APPLICATION_NAME = "Figury Editor 2016";
    /**
     * Returns Information about this application
     * @param Language Language.
     * @return Information.
     */
    public static final  String getInfo(int Language){
        String application="Figury Editor 2016";
        String author="";
        String version="";
        String usage="";
        switch(Language){
            case LANGUAGE_POLISH:
                author = "Autor";
                version = "Wersja";
                usage ="Graficzny edytor figur 2D.";
            break;
            case LANGUAGE_ENGLISH: default:
                author = "Author";
                version = "Version";
                usage ="2D graphic figure editor.";
                break;
        }   
        return APPLICATION_NAME+"\n"+author+": Piotr Olejarz\n"+version+": 2.1 \n"+usage;
    }
    /**
     * Returns Helpful information for this application.
     * @param Language Language.
     * @return Help text.
     */
    public static final  String getHelp(int Language){
        String rtrn = "";
        switch(Language){
            case LANGUAGE_POLISH:
                    rtrn= "Przy pomocy tego programu możesz rysować figury i potem je edytować.\n"
                            + "Aby stworzyć figurę należy wybrać z paska narzędzi porządaną figurę. Można także ustawić styl nowej figury przed jej stworzeniem. \n"
                            + "Następnie należy kliknąć w wybranych miejscach by postawić punkty.\n"
                            + "Minimalna i maksymalna ilość punktów do postawienia różni się w zależności of wybranej figury:\n"
                            + "\t- W przypadku czworokątów dowolnych należy postawić 4 punkty.\n"
                            + "\t- Elipsy i koła wymagają wybrania 2 punktów, z czego pierwszy to środek figury a drugi punkt ustala półosie.\n"
                            + "\t- Wielokąt wymaga przynajmiej jednego punktu.\n"
                            + "\t- Równoległoboki i jego specyficzne przypadki wymagają postawienia 3 punktów:\n"
                            + "\t  Pierwsze dwa punkty ustalą podstawę czworokąta na bazie której będą prowadzone obliczenia, pozostanie ona niezmiona po utworzeniu figury.\n"
                            + "\t  Trzeci punkt [może ulec zmianie] ustali kierunek pozostałych boków, ich długości [jeżeli nie wybrano równoboku], oraz kąt między bokami [jeżeli nie wybrano prostokąta] .\n"
                            + "Po wprowadzeniu odpowiedniej ilości punktów lub po spełnieniu warunków i kliknięciu prawym przyciskiem myszy zostanie utworzona nowa figura.\n"
                            + "Gdy wyświetli się ona na ekranie można mastepnie edytować wierzchołki i  skalować kółkiem myszy.\n"
                            + "Po kliknięciu prawym przyciskiem na figurę można edytować różne opcje figury takie jak kolor czy też zablokowanie rozmiaru.\n"
                            + "Inne opcje związane z wyświetlaniem lub domyślne ustawienia są dostępne w innych kategoriach na pasku menu.";
                break;
            case LANGUAGE_ENGLISH: default:
                rtrn= "With this application you can draw various figures and edit them afterward.\n"
                        + "To create new figure, you can pick desired one from toolbar and pick desired style beforehand.\n"
                        + "Next you should click on screen to place few points.\n "
                        + "Minimal and maximal amount of points varies between types of figures:\n"
                        + "\t- In case of quadrilaterals, you will be re required to place only 4 points.\n"
                        + "\t- Ellipses and circles need 2 points. First is the centre of this figure and next one will inform about axises.\n"
                        + "\t- Polygons require atleast 1 point, maximal amount is (almost) infinite\n"
                        + "\t- Parallelograms and its special cases requires 3 points:\n"
                        + "\t\t  First two points determine base side of figure. Most calculations to determine final shape are based on them, but they do not change location.\n"
                        + "\t\t  Third which might [and probably will] be relocated determines direction of all other sidesd. Also determines length [if its not equilateral] and angle [if not perpendicular] .\n"
                        + "After placing maximal amount of points or clicking right mouse button after placing minimal amount of points, new figure will be created.\n"
                        + "After newly made figure will become visible you can start editing its vertices and scale with mouse wheel.\n "           
                        + "After clicking with right mouse button, popup menu will showup that allows to change various setting of current figure.\n"
                        + "Other options that are related to application are under other options and submenus.";
                        
                break;
        }
        return rtrn;    
    }
    
    /**
     * Constant for English language.
     */
    public final static int LANGUAGE_ENGLISH = 0;
    /**
     * Constant for Polish language.
     */
    public final static int LANGUAGE_POLISH = 1;
    /**
     * Returns if given language is a proper one.
     * @param language Language ID to check
     * @return True if given language ID is a proper one.
     */
    public static boolean isProperLanguage(int language){
        if((language==LANGUAGE_ENGLISH)||(language==LANGUAGE_POLISH)){
            return true;
        }else{
            return false;
        }
    }    
    
   //##################################fields###################################
    
    
    /**
     * List of Polish texts.
     */
    private final ArrayList text_list_PL;
    /**
     * List of English texts;
     */
    private final ArrayList text_list_ENG;
    
    /**
     * Configuration object.
     */
    private final Configuration config;
    
    
    
    
    
   //#########################methods###########################################
    
    
    /**
     * Loads specified language test file and saves it to proper test list.
     * @param language Language ID.
     * @see #text_list_ENG
     * @see #text_list_PL
     */
    private void LoadText(int language){
        if(isProperLanguage(language)){
            String lang = (language==LANGUAGE_POLISH)?"Polish":((language==LANGUAGE_ENGLISH)?"English":"");
            if(!lang.isEmpty()){
                InputStreamReader isr = new InputStreamReader(getClass().getResourceAsStream("/text/"+lang));
                BufferedReader reader=new BufferedReader(isr);
                String lane;
                try {
                    while((lane=reader.readLine())!=null){
                        if((!lane.startsWith("//"))&&(lane.contains("###"))){
                            try{
                                String id = lane.substring(0, lane.indexOf("#"));
                                String text = lane.substring(lane.lastIndexOf("#")+1,lane.length() );
                                TextLine tl = new TextLine(text, id);
                                switch(language){
                                    case LANGUAGE_POLISH:
                                            text_list_PL.add(tl);
                                        break;
                                    case LANGUAGE_ENGLISH:
                                            text_list_ENG.add(tl);
                                        break;
                                }
                            }catch(IndexOutOfBoundsException ex){}
                        }
                    }
                } catch (IOException ex) {
                    System.err.println("Something went wrong, could not load text files.");
                }
            }else{
                System.err.println("Wrong language. Language texts have not been loaded");
            }
        }else{
            System.err.println("Wrong language. Language texts have not been loaded");
        }
    }
    /**
     * Gets text line from proper text list.
     * @param ID ID of text line.
     * @param language Language of text line.
     * @return Text string if ID was matched with existing one, null otherwise.
     */
    private String getTextFromWithin(String ID,int language){
        String rtrn = null;
        ArrayList list = null;
        if(!isProperLanguage(language)){
            System.err.println("Wrong language. Returning text from defeault languge");    
            language = Configuration.DEFAULT_VALUE_LANGUAGE;
        }
        switch(language){          
            case LANGUAGE_POLISH: 
                    list = text_list_PL;
                break;           
            case LANGUAGE_ENGLISH: default: 
                    list = text_list_ENG;
                break;
        }  
        if(list!=null){
            for (Object object : list) {   
                if(object instanceof TextLine){
                    if (((TextLine) object).isIt(ID)){
                        rtrn = ((TextLine) object).getText();
                        break;
                    }
                }
            }     
        }
        return rtrn;
    }
    /**
     * Gets text line from proper text list.
     * @param ID ID of text line.
     * @param language Language of text line.
     * @return Text string if ID was matched with existing one, null otherwise.
     */
    public String getText(String ID,int language){
        return getTextFromWithin(ID,language);
    }
    /**
     * Gets text line from proper text list.
     * @param ID ID of text line.
     * @return Text string if ID was matched with existing one, null otherwise.
     */
    public String getText(String ID){
        return getTextFromWithin(ID,config.getLanguage());
    }
    
    /**
     * Returns Information about this application
     * @return Information.
     */
    public final  String getInfo(){
        return getInfo(config.getLanguage());  
    }
    /**
     * Returns Helpful information for this application.
     * @return Help text.
     */
    public final  String getHelp(){
        return getHelp(config.getLanguage());    
    }
    //#######################constructors#######################################
    
    /**
     * Default constructor.
     * @param config Configuration object
     */
    public Text(Configuration config) {   
        this.config = config;
        text_list_PL = new ArrayList();
        text_list_ENG = new ArrayList();
        LoadText(LANGUAGE_POLISH);
        LoadText(LANGUAGE_ENGLISH);
    }
}