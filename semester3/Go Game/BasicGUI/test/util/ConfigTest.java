/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.awt.Dimension;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author n1t4chi
 */
public class ConfigTest {
    
    public ConfigTest() {
    }

    /**
     * Test of getFileName method, of class Config.
     */
    @Test
    public void testGetFileName() {
        System.out.println("getFileName");
        Config instance = new ConfigImpl();
        assertEquals(instance.getFileName(),instance.getFileName());
    }

    /**
     * Test of getPath method, of class Config.
     */
    @Test
    public void testGetPath() {
        System.out.println("getPath");
        Config instance = new ConfigImpl();
        assertEquals(instance.getPath("ConfigTest"),instance.getPath("_ConfigTest"));
    }

    /**
     * Test of getDirectory method, of class Config.
     */
    @Test
    public void testGetDirectory() {
        System.out.println("getDirectory");
        Config instance = new ConfigImpl();
        assertEquals(instance.getDirectory(),instance.getDirectory());
    }

    /**
     * Test of save and load methods, of class Config.
     */
    @Test
    public void testSaveAndLoad() {
        System.out.println("save");
        ConfigWindow save = ConfigWindow.getDefaultConfig();
        save.setWindowMinimumSize(new Dimension(1, 1));
        save.setWindowPreviousSize(new Dimension(11, 11));
        ConfigWindow load = new ConfigWindow();
        assertNull(load.getWindowPreviousLocation());
        assertNotNull(save.getWindowPreviousLocation());
        boolean result1 = save.save("ConfigTest");
        boolean result2 = load.load("ConfigTest");
        assertTrue(result1);
        assertTrue(result2);
        assertNotNull(load.getWindowPreviousLocation());
        assertEquals(save.getWindowPreviousLocation(),load.getWindowPreviousLocation());
    }

    public class ConfigImpl extends Config {

        @Override
        public boolean copy(Config model) {
            return false;
        }

        @Override
        public String getFileName() {
            return "";
        }
    }
    
}
