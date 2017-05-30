/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basicgui;

import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import util.ConfigWindow;

/**
 * Test class for BasicFrame.
 * @author n1t4chi
 */
public class BasicFrameTest {
   
    
    /**
     * Returns invisible simple BasicFrame.
     * @return invisible simple BasicFrame.
     */
    private BasicFrame getBasicFrame(){
        BasicFrame rtrn = new BasicFrame("",null,ConfigWindow.getDefaultConfig(),"BASIC_FRAME_TEST");
        rtrn.setVisible(false);
        return rtrn;   
    }

    /**
     * Test of getAbout method, of class BasicFrame.
     */
    @Test
    public void testGetHelp() {
        System.out.println("getHelp");
        BasicFrame instance = getBasicFrame();
        try{
        instance.getHelp();
        }catch(Exception e){
            fail("getHelp() threw exception");
        }
    }
    
    /**
     * Test of getAbout method, of class BasicFrame.
     */
    @Test
    public void testGetAbout() {
        System.out.println("getAbout");
        BasicFrame instance = getBasicFrame();
        String result = instance.getAbout();
        // TODO review the generated test code and remove the default call to fail.
        assertNotNull(result);
    }

    /**
     * Test of shouldExit method, of class BasicFrame.
     */
    @Test
    public void testShouldExit() {
        System.out.println("shouldExit");
        BasicFrame instance = getBasicFrame();
        boolean expResult = true;
        boolean result = instance.shouldExit();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of getContainer method, of class BasicFrame.
     */
    @Test
    public void testGetContainer() {
        System.out.println("getContainer");
        BasicFrame instance = getBasicFrame();
        JComponent result = instance.getContainer();
        assertNotNull(result);
    }

    /**
     * Test of addSeparator method, of class BasicFrame.
     */
    @Test
    public void testAddSeparator() {
        System.out.println("addSeparator");
        String menu = "";
        BasicFrame instance = getBasicFrame();
        try{
            instance.addSeparator(menu);
            fail("No exception while trying to add separator to menu bar");
        }catch(IllegalArgumentException e){}
        menu = "NotReallyMenu";
        try{
            instance.addSeparator(menu);
            fail("No exception while trying to add separator to non existing menu");
        }catch(IllegalArgumentException e){}
        menu = null;
        try{
            instance.addSeparator(menu);
            fail("No exception after passing null argument");
        }catch(NullPointerException e){}
        menu = "File";
        try{
            instance.addSeparator(menu);
        }catch(Exception e){
            fail("Exception on adding valid menu: "+e.getLocalizedMessage());
        }
        
    }

    /**
     * Test of addMenu method, of class BasicFrame.
     */
    @Test
    public void testAddMenu_JMenu() {
        System.out.println("addMenu");
        BasicFrame instance = getBasicFrame();
        JMenu menu = new JMenu("");
        try{
            instance.addMenu(menu);
            fail("No exception after passing menu with empty name");
        }catch(IllegalArgumentException e){}
        menu = new JMenu("File");
        try{
            instance.addMenu(menu);
            fail("No exception after passing menu with same name");
        }catch(IllegalArgumentException e){}
        menu = null;
        try{
            instance.addMenu(menu);
            fail("No exception after passing null argument");
        }catch(NullPointerException e){}  
        menu = new JMenu("Testing Testing");
        try{
            instance.addMenu(menu);
        }catch(Exception e){
            fail("Exception on adding valid menu: "+e.getLocalizedMessage());
        }     
    }

    /**
     * Test of addMenu method, of class BasicFrame.
     */
    @Test
    public void testAddMenu_String() {
        System.out.println("addMenu");
        BasicFrame instance = getBasicFrame();
        String menu = "";
        try{
            instance.addMenu(menu);
            fail("No exception after passing menu with empty name");
        }catch(IllegalArgumentException e){}
        menu = "File";
        try{
            instance.addMenu(menu);
            fail("No exception after passing menu with same name");
        }catch(IllegalArgumentException e){}
        menu = null;
        try{
            instance.addMenu(menu);
            fail("No exception after passing null argument");
        }catch(IllegalArgumentException e){}   
        menu = "Testing Testing";
        try{
            instance.addMenu(menu);
        }catch(Exception e){
            fail("Exception on adding valid menu: "+e.getLocalizedMessage());
        }     
    }

    /**
     * Test of addSubMenu method, of class BasicFrame.
     */
    @Test
    public void testAddSubMenu_String_String() {
        System.out.println("addSubMenu");
        String menu = "";
        String menu_name = "";
        BasicFrame instance = getBasicFrame();
        try{
            instance.addSubMenu(menu, menu_name);
            fail("No exception after passing submenu with empty name");
        }catch(IllegalArgumentException e){}
        menu_name = "File";
        try{
            instance.addSubMenu(menu, menu_name);
            fail("No exception after passing submenu with same name");
        }catch(IllegalArgumentException e){}
        menu = "obviouslyfakemenuname";
        menu_name = "TestingTesting"; 
        try{
            instance.addSubMenu(menu, menu_name);
            fail("No exception after passing non existing menu name");
        }catch(IllegalArgumentException e){}
        menu = null;
        try{
            instance.addSubMenu(menu, menu_name);
            fail("No exception after passing null argument");
        }catch(NullPointerException e){}  
        menu = "File"; 
        menu_name = "Testing Testing";
        try{
            instance.addSubMenu(menu, menu_name);
        }catch(Exception e){
            fail("Exception on adding valid  menu: "+e.getLocalizedMessage());
        }     
    }

    /**
     * Test of addSubMenu method, of class BasicFrame.
     */
    @Test
    public void testAddSubMenu_String_JMenu() {
        System.out.println("addSubMenu");
        String menu = "";
        JMenu submenu = new JMenu("");
        BasicFrame instance = getBasicFrame();
        try{
            instance.addSubMenu(menu, menu);
            fail("No exception after passing submenu with empty name");
        }catch(IllegalArgumentException e){}
        submenu = new JMenu("File");
        try{
            instance.addSubMenu(menu, menu);
            fail("No exception after passing submenu with same name");
        }catch(IllegalArgumentException e){}
        menu = "obviouslyfakemenuname";
        submenu = new JMenu("TestingTesting");
        try{
            instance.addSubMenu(menu, menu);
            fail("No exception after passing non existing menu name");
        }catch(IllegalArgumentException e){}
        menu = null;
        try{
            instance.addSubMenu(menu, menu);
            fail("No exception after passing null argument");
        }catch(NullPointerException e){}   
        menu = "File";
        submenu = new JMenu("TestingTesting");
        try{
            instance.addSubMenu(menu, submenu);
        }catch(Exception e){
            fail("Exception on adding valid menu: "+e.getLocalizedMessage());
        }    
    }

    /**
     * Test of addMenu method, of class BasicFrame.
     */
    @Test
    public void testAddMenu_JMenu_int() {
        System.out.println("addMenu");
        BasicFrame instance = getBasicFrame();
        JMenu menu = new JMenu("");
        int index = -1;
        try{
            instance.addMenu(menu, index);
            fail("No exception after passing submenu with empty name");
        }catch(IllegalArgumentException e){}
        menu = new JMenu("File");
        try{
            instance.addMenu(menu, index);
            fail("No exception after passing submenu with same name");
        }catch(IllegalArgumentException e){}
        menu = null;
        try{
            instance.addMenu(menu, index);
            fail("No exception after passing null argument");
        }catch(NullPointerException e){}   
        index = 2352235;
        menu = new JMenu("Legit Name");
        try{
            instance.addMenu(menu, index);
            fail("No exception after passing illegal index");
        }catch(Exception e){
        }    
        index = -1;
        menu = new JMenu("Legit");
        try{
            instance.addMenu(menu, index);
        }catch(Exception e){
            fail("Exception on adding valid menu: "+e.getLocalizedMessage());
        }    
    }

    /**
     * Test of addMenu method, of class BasicFrame.
     */
    @Test
    public void testAddMenu_String_int() {
        System.out.println("addMenu");
        String menu_name = "";
        int index = -1;
        BasicFrame instance = getBasicFrame();
        try{
            instance.addMenu(menu_name, index);
            fail("No exception after passing submenu with empty name");
        }catch(IllegalArgumentException e){}
        menu_name = "File";
        try{
            instance.addMenu(menu_name, index);
            fail("No exception after passing submenu with same name");
        }catch(IllegalArgumentException e){}
        menu_name = null;
        try{
            instance.addMenu(menu_name, index);
            fail("No exception after passing null argument");
        }catch(IllegalArgumentException e){}   
        index = 2352235;
        menu_name = "";
        try{
            instance.addMenu(menu_name, index);
            fail("No exception after passing illegal index");
        }catch(Exception e){    
        }
        index = -1;
        menu_name = "Legit Name";
        try{
            instance.addMenu(menu_name, index);
        }catch(Exception e){
            fail("Exception on adding valid menu: "+e.getLocalizedMessage());
        }   
    }

    /**
     * Test of addSubMenu method, of class BasicFrame.
     */
    @Test
    public void testAddSubMenu_3args_1() {
        System.out.println("addSubMenu");
        String menu = "";
        String menu_name = "";
        int index = 0;
        BasicFrame instance = getBasicFrame();
        try{
            instance.addSubMenu(menu,menu_name,index);
            fail("No exception after passing submenu with empty name");
        }catch(IllegalArgumentException e){}
        menu_name = "File";
        try{
            instance.addSubMenu(menu,menu_name,index);
            fail("No exception after passing submenu with same name");
        }catch(IllegalArgumentException e){}
        menu = null;
        try{
            instance.addSubMenu(menu,menu_name,index);
            fail("No exception after passing null argument");
        }catch(NullPointerException e){}   
        menu = "obviouslyfakemenuname";
        menu_name = "TestingTesting";
        try{
            instance.addSubMenu(menu,menu_name,index);
            fail("No exception after passing non existing menu name");
        }catch(IllegalArgumentException e){}
        menu = "";
        index = 2352235;
        menu_name = "Legit Name";
        try{
            instance.addSubMenu(menu,menu_name,index);
            fail("No exception after passing illegal index");
        }catch(IllegalArgumentException e){    
        }
        index = -1;
        menu = "";
        menu_name = "Legit";
        try{
            instance.addSubMenu(menu,menu_name,index);
        }catch(Exception e){
            fail("Exception on adding valid menu: "+e.getLocalizedMessage());
        }   
    }

    /**
     * Test of addSubMenu method, of class BasicFrame.
     */
    @Test
    public void testAddSubMenu_3args_2() {
        System.out.println("addSubMenu");
        String menu = "";
        JMenu submenu = new JMenu("");
        int index = -1;
        BasicFrame instance = getBasicFrame();
        try{
            instance.addSubMenu(menu, submenu, index);
            fail("No exception after passing submenu with empty name");
        }catch(IllegalArgumentException e){}
        submenu = new JMenu("File");
        try{
            instance.addSubMenu(menu, submenu, index);
            fail("No exception after passing submenu with same name");
        }catch(IllegalArgumentException e){}
        menu = "obviouslyfakemenuname";
        submenu = new JMenu("TestingTesting");
        try{
            instance.addSubMenu(menu, submenu, index);
            fail("No exception after passing non existing menu name");
        }catch(IllegalArgumentException e){}
        menu = null;
        try{
            instance.addSubMenu(menu, submenu, index);
            fail("No exception after passing null argument");
        }catch(NullPointerException e){} 
        
        menu = "";
        index = 2352235;
        submenu = new JMenu("Legit Name");
        try{
            instance.addSubMenu(menu,submenu,index);
            fail("No exception after passing illegal index");
        }catch(Exception e){    
        }
        menu = "File";
        submenu = new JMenu("TestingTesting");
        index = -1;
        try{
            instance.addSubMenu(menu, submenu, index);
        }catch(Exception e){
            fail("Exception on adding valid separator: "+e.getLocalizedMessage());
        }    
    }

    /**
     * Test of addMenuItem method, of class BasicFrame.
     */
    @Test
    public void testAddMenuItem_String_JMenuItem() {
        System.out.println("addMenuItem");
        String menu = "File";
        JMenuItem item = new JMenuItem("");
        BasicFrame instance = getBasicFrame();
        try{
            instance.addMenuItem(menu, item);
            fail("No exception after passing submenu with empty name");
        }catch(IllegalArgumentException e){}
        item = new JMenuItem("Exit");
        try{
            instance.addMenuItem(menu, item);
            fail("No exception after passing submenu with same name");
        }catch(IllegalArgumentException e){}
        menu = "obviouslyfakemenuname";
        try{
            instance.addMenuItem(menu, item);
            fail("No exception after passing non existing menu name");
        }catch(IllegalArgumentException e){}
        menu = "File";
        item = null;
        try{
            instance.addMenuItem(menu, item);
            fail("No exception after passing null argument");
        }catch(NullPointerException e){}   
        menu = "File";
        item = new JMenuItem("Legit");
        try{
            instance.addMenuItem(menu, item);
        }catch(Exception e){
            fail("Exception on adding valid separator: "+e.getLocalizedMessage());
        }    
    }

    /**
     * Test of addMenuItem method, of class BasicFrame.
     */
    @Test
    public void testAddMenuItem_3args() {
        System.out.println("addMenuItem");
        String menu = "File";
        JMenuItem item = new JMenuItem("");
        int index = -1;
        BasicFrame instance = getBasicFrame();
        try{
            instance.addMenuItem(menu, item, index);
            fail("No exception after passing submenu with empty name");
        }catch(IllegalArgumentException e){}
        item = new JMenuItem("Exit");
        try{
            instance.addMenuItem(menu, item, index);
            fail("No exception after passing submenu with same name");
        }catch(IllegalArgumentException e){}
        menu = "obviouslyfakemenuname";
        try{
            instance.addMenuItem(menu, item, index);
            fail("No exception after passing non existing menu name");
        }catch(IllegalArgumentException e){}
        menu = "File";
        item = null;
        try{
            instance.addMenuItem(menu, item, index);
            fail("No exception after passing null argument");
        }catch(NullPointerException e){} 
        menu = "File";
        item = new JMenuItem("Legit");
        index = 23534;
        try{
            instance.addMenuItem(menu, item, index);
            fail("No exception after passing illegal index");
        }catch(IllegalArgumentException e){}  
        menu = "File";
        item = new JMenuItem("Legit2");
        index = -1;
        try{
            instance.addMenuItem(menu, item, index);
        }catch(Exception e){
            fail("Exception on adding valid item: "+e.getLocalizedMessage());
        }      
    }
    
}
