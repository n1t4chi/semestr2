/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicgui;

import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 * JtextField with enabled checking for IP input.
 * @author n1t4chi
 */
public class PortInput extends JFormattedTextField{
    
    /**
     * Returns integer value of typed Port.
     * Might return default value if there was some problem with parsing typed value, although most unlikely
     * @return valid port.
     */
    public int getPort(){
        int rtrn = Integer.getInteger(getText(),default_value);
        if(rtrn <0||rtrn >65535)
            rtrn = default_value;
        return rtrn;
    }
    
    private final int default_value;
    /**
     * Default constructor, Sets default value to be displayed.
     * @param default_value default value to be displayed.
     */
    public PortInput(int default_value) {
        super(new DefaultFormatterFactory(new DefaultFormatter(){
            @Override
            public Object stringToValue(String string) throws ParseException {
                if(!string.isEmpty()){
                    int i = Integer.getInteger(string,default_value);
                    if(i<0||i>65535)
                        throw new ParseException("", 0);
                }
                return string;
            } 
        }), ""+default_value);
        this.default_value = default_value;
    }
}
