/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Dimension;
import java.awt.Point;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author n1t4chi
 */
public class ConfigWindowTest {
    
    public ConfigWindowTest() {
    }
    /**
     * Test of getDefaultConfig method, of class ConfigWindow.
     */
    @Test
    public void testGetDefaultConfig() {
        System.out.println("getDefaultConfig");
        ConfigWindow instance = ConfigWindow.getDefaultConfig();
        assertEquals(ConfigWindow.DEFAULT_WINDOW_SIZE_PREVIOUS, instance.getWindowPreviousSize());
        assertEquals(ConfigWindow.DEFAULT_WINDOW_SIZE_MINIMUM, instance.getWindowMinimumSize());
        assertEquals(ConfigWindow.DEFAULT_WINDOW_LOCATION_PREVIOUS, instance.getWindowPreviousLocation());
        assertEquals(ConfigWindow.DEFAULT_LANGUAGE, instance.getLanguage());
    }

    /**
     * Test of copy method, of class ConfigWindow.
     */
    @Test
    public void testCopy_ConfigWindow() {
        System.out.println("copy");
        ConfigWindow model = ConfigWindow.getDefaultConfig();
        ConfigWindow instance = new ConfigWindow();
        boolean result = instance.copy(model);
        assertEquals(true, result);
        assertEquals(((ConfigWindow)model).getWindowMinimumSize(),instance.getWindowMinimumSize()  );
        assertEquals(((ConfigWindow)model).getWindowPreviousLocation(),instance.getWindowPreviousLocation()  );
        assertEquals(((ConfigWindow)model).getWindowPreviousSize(),instance.getWindowPreviousSize()  );
        assertEquals(((ConfigWindow)model).getLanguage(),instance.getLanguage() );
    }

    /**
     * Test of set method, of class ConfigWindow.
     */
    @Test
    public void testSet() {
        System.out.println("set");
        Dimension WindowMinimumSize = null;
        Dimension WindowPreviousSize = null;
        Point WindowPreviousLocation = null;
        ConfigWindow.LanguageType Language = null;
        ConfigWindow instance = new ConfigWindow();
        instance.set(WindowMinimumSize, WindowPreviousSize, WindowPreviousLocation, Language);
        assertEquals(ConfigWindow.DEFAULT_WINDOW_SIZE_PREVIOUS, instance.getWindowPreviousSize());
        assertEquals(ConfigWindow.DEFAULT_WINDOW_SIZE_MINIMUM, instance.getWindowMinimumSize());
        assertEquals(ConfigWindow.DEFAULT_WINDOW_LOCATION_PREVIOUS, instance.getWindowPreviousLocation());
        assertEquals(ConfigWindow.DEFAULT_LANGUAGE, instance.getLanguage());
        
        WindowMinimumSize = new Dimension(100,100);
        WindowPreviousSize = new Dimension(100,100);
        WindowPreviousLocation = new Point(100,100);
        Language = ConfigWindow.LanguageType.POLISH;
        instance.set(WindowMinimumSize, WindowPreviousSize, WindowPreviousLocation, Language);
        assertEquals(WindowPreviousSize, instance.getWindowPreviousSize());
        assertEquals(WindowMinimumSize, instance.getWindowMinimumSize());
        assertEquals(WindowPreviousLocation , instance.getWindowPreviousLocation());
        assertEquals(Language, instance.getLanguage());
    }

    /**
     * Test of set/getWindowPreviousSize method, of class ConfigWindow.
     */
    @Test
    public void testSetGetWindowPreviousSize() {
        System.out.println("getWindowPreviousSize");
        ConfigWindow instance = new ConfigWindow();
        Dimension expResult = null;
        assertEquals(expResult, instance.getWindowPreviousSize());
        instance.setWindowPreviousSize(ConfigWindow.DEFAULT_WINDOW_SIZE_PREVIOUS);
        expResult = ConfigWindow.DEFAULT_WINDOW_SIZE_PREVIOUS;
        assertEquals(expResult, instance.getWindowPreviousSize());
    }

    /**
     * Test of set/getWindowMinimumSize method, of class ConfigWindow.
     */
    @Test
    public void testSetGetWindowMinimumSize() {
        System.out.println("getWindowMinimumSize");
        ConfigWindow instance = new ConfigWindow();
        Dimension expResult = null;
        assertEquals(expResult, instance.getWindowMinimumSize());
        instance.setWindowMinimumSize(ConfigWindow.DEFAULT_WINDOW_SIZE_MINIMUM);
        expResult = ConfigWindow.DEFAULT_WINDOW_SIZE_MINIMUM;
        assertEquals(expResult, instance.getWindowMinimumSize());
    }

    /**
     * Test of set/getWindowPreviousLocation method, of class ConfigWindow.
     */
    @Test
    public void testSetGetWindowPreviousLocation() {
        System.out.println("getWindowPreviousLocation");
        ConfigWindow instance = new ConfigWindow();
        Point expResult = null;
        assertEquals(expResult, instance.getWindowPreviousLocation());
        instance.setWindowPreviousLocation(ConfigWindow.DEFAULT_WINDOW_LOCATION_PREVIOUS);
        expResult = ConfigWindow.DEFAULT_WINDOW_LOCATION_PREVIOUS;
        assertEquals(expResult, instance.getWindowPreviousLocation());
    }

    /**
     * Test of set/getLanguage method, of class ConfigWindow.
     */
    @Test
    public void testSetGetLanguage() {
        System.out.println("getLanguage");
        ConfigWindow instance = new ConfigWindow();
        ConfigWindow.LanguageType expResult = null;
        assertEquals(expResult, instance.getLanguage());
        instance.setLanguage(ConfigWindow.LanguageType.ENGLISH);
        expResult = ConfigWindow.LanguageType.ENGLISH;
        assertEquals(expResult, instance.getLanguage());
    }

    /**
     * Test of copy method, of class ConfigWindow.
     */
    @Test
    public void testCopy_Config() {
        System.out.println("copy");
        ConfigWindow instance = new ConfigWindow();
        Config model = ConfigWindow.getDefaultConfig();
        boolean result = instance.copy(model);
        assertEquals(true, result);
        assertEquals(((ConfigWindow)model).getWindowMinimumSize(),instance.getWindowMinimumSize()  );
        assertEquals(((ConfigWindow)model).getWindowPreviousLocation(),instance.getWindowPreviousLocation()  );
        assertEquals(((ConfigWindow)model).getWindowPreviousSize(),instance.getWindowPreviousSize()  );
        assertEquals(((ConfigWindow)model).getLanguage(),instance.getLanguage() );
        
    }

    /**
     * Test of getFileName method, of class ConfigWindow.
     */
    @Test
    public void testGetFileName() {
        System.out.println("getFileName");
        ConfigWindow instance = new ConfigWindow();
        assertEquals("Returns the same name all the time",instance.getFileName(), instance.getFileName());
    }

    
}
