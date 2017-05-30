/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.launcher;

import client.ConfigClient;
import game.Board;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.mockito.Mockito;
import utilities.Socket;
import utilities.messages.MessageType;
import utilities.messages.RequestRoomList;
import utilities.messages.ReturnRoomList;
import utilities.messages.data.RoomInfo;

/**
 * This class provides basic possibility to test Laumcher GUI. It must be run with Run File since it is not a JUnit test class.
 * @author n1t4chi
 */
public class LauncherGUITest {
  
        
    public LauncherGUITest() {
        testLauncherGUI_ServerConnectionAndRoomSelection();
        //testRoomComponent();
    }

    public static void main(String[] args) {
        LauncherGUITest t = new LauncherGUITest();
    }

    
    public void testRoomComponent() {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setMinimumSize(new Dimension(450,200));
            f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            f.setVisible(true);
            JPanel room_container = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.weighty=0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.FIRST_LINE_START;
            c.gridheight=1;
            c.ipadx=5;
            c.weightx=1;
            c.gridx=1;
            room_container.setVisible(true);
            //room_container.setBackground(Color.red);
            JScrollPane room_container_scroll = new JScrollPane(room_container);
            f.setContentPane(room_container_scroll);
            String[] black = {"","b1","b2",""};
            String[] white = {"","","w2","w3"};
            for(int i=0;i<4;i++){
                c.gridy=i;
                
                RoomComponent rc = new RoomComponent(new RoomInfo(0, 0,(i==0)?Board.Size.SMALL:(i==1)?Board.Size.MODERATE:Board.Size.BIG, "test"+i, black[i], white[i] )) {
                    @Override
                    public void join(int port,boolean black, Board.Size size) {}
                    @Override
                    public void selected(RoomInfo info) {}

                };

                room_container.add(rc,c);
            }
            
            JPanel filler = new JPanel();
            c.gridy=5;
            c.weighty=1;
            room_container.add(filler,c);
            
            
            
        });
    }
    
    LauncherGUI gui;
    public void testLauncherGUI_ServerConnectionAndRoomSelection() {
        ConfigClient cc = ConfigClient.getDefaultConfigClient();
        gui=null;
      //  System.out.println("client.launcher.LauncherGUITest.testLauncherGUI_ServerConnectionAndRoomSelection() 1");
        SwingUtilities.invokeLater(() -> {
            try{
                gui = new LauncherGUI(cc);
                gui.setVisible(true);
                gui.setEnabled(true);
            }catch(Exception ex){
                System.err.println("Exception on creating GUI:"+ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        });
    //    System.out.println("client.launcher.LauncherGUITest.testLauncherGUI_ServerConnectionAndRoomSelection() 2");
        Socket soc = Mockito.mock(Socket.class);
        ArrayList<RoomInfo> list = new ArrayList();
        list.add(new RoomInfo(0, 1, Board.Size.SMALL,"Test Full Room", "Black player 1" , "White player 1"));
        list.add(new RoomInfo(2, 111111, Board.Size.MODERATE, "Test Mixed Room", "Black player 2" , ""));
        list.add(new RoomInfo(111112, 111113, Board.Size.BIG, "Test Empty Room", "" , ""));
        Mockito.when(soc.requestMessage(MessageType.RETURN_LIST_ROOMS)).thenReturn(new ReturnRoomList(list));
        Mockito.when(soc.isConnected()).thenReturn(true);
        Mockito.when(soc.isWorking()).thenReturn(true);
        while(gui==null){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(LauncherGUITest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        gui.Socket = soc;
        gui.button_create.setEnabled(true);
        gui.button_refresh.setEnabled(true);
        gui.button_join.setEnabled(true);
        gui.button_auto_refresh.setEnabled(true);
        gui.updateMessage(null, new RequestRoomList());
       // System.err.println("scoket:"+gui.Socket);
        while(gui.Socket!=null){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(LauncherGUITest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    
}
