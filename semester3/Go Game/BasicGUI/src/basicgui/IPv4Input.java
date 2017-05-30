/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicgui;

import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;

/**
 * JtextField with enabled checking for IPv4 input.
 * @author n1t4chi
 */
public class IPv4Input extends JFormattedTextField{
    /**
     * I believe it works.
     */
    private static final String regex = "("
                + "([0-9])"
                + "|([1-9][0-9])"
                + "|(1([0-9]{2}))"
                + "|(25[0-5])"
                + "|(2[0-4][0-9])"
            + ")"
            + "("
                + "("
                    + "[.]("
                        + "[0-9]|"
            + "([1-9][0-9])|"
            + "(1([0-9]{2}))|"
            + "(25[0-5])|"
            + "(2[0-4][0-9])|"
                    + ")"
                + "){0,3}"
            + ")";

    /**
     * Returns typed IP address.
     * Might return default value if there was some problem with typed value, although mostly unlikely
     * @return 
     */
    public String getIP(){
        String rtrn = getText();
        if(!rtrn.matches(regex))
            rtrn = default_value;
        return rtrn;
    }
    
    private final String default_value;
    
    /**
     * Default constructor, Sets default value to be displayed.
     * @param default_value default value to be displayed.
     */
    public IPv4Input(String default_value) {
        super(new DefaultFormatterFactory(new DefaultFormatter(){
            @Override
            public Object stringToValue(String string) throws ParseException {
                if((string.matches(regex)&&!string.contains(".."))||string.isEmpty()){
                    return string;
                }else{
                    throw new ParseException("Not IP", 0);
                }
            } 
        }), default_value);
        this.default_value = default_value;
    }
    
}
