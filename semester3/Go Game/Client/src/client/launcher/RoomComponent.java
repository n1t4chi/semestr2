/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.launcher;

import game.Board;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import utilities.messages.data.RoomInfo;

/**
 * Class used for displaying room info in Launcher.
 * @author n1t4chi
 */
public abstract class RoomComponent extends JComponent implements FocusListener,MouseListener{
    /**
     * Room info
     */
    private RoomInfo info;
    /**
     * Label displaying room name
     */
    private final JLabel label_room_name;
    /**
     * Label displaying black player text
     */
    private final JLabel label_black;
    /**
     * Label displaying white player text
     */
    private final JLabel label_white;
    /**
     * Component for displaying black user name or button for joining.
     */
    private JComponent component_black;
    /**
     * Component for displaying white user name or button for joining.
     */
    private JComponent component_white;
    
    /**
     * Performs task necessary to connect to room under given port. 
     * It's called from within Room Component after Join button was clicked.
     * @param port connection port with server.
     * @param black Whether joined black player slot 
     * @param size Size of a board.
     */
    public abstract void join(int port,boolean black, Board.Size size);
    /**
     * It's called when this Room Component is selected.
     * @param info RoomInfo held by calling component.
     */
    public abstract void selected(RoomInfo info);
    /**
     * GridBagConstraints
     */
    private final GridBagConstraints c = new GridBagConstraints();
    
    /**
     * 
     * @param info 
     *  @throws NullPointerException if info is null pointer
     */
    public final void updateInfo(RoomInfo info){
        if(info == null)
            throw new NullPointerException("Null pointer");
        this.info = info;
        label_room_name.setText(info.getRoomName()+" ["+((info.getRoomSize()==Board.Size.BIG)?"19x19":(info.getRoomSize()==Board.Size.MODERATE)?"13x13":"9x9")+"]");
        if(component_black!=null){
            this.remove(component_black);
        }
        if("".equalsIgnoreCase(info.getBlackPlayer())){
            component_black = new JButton(new AbstractAction("Join as Black") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    join(info.getRoomPortBlack(),true,info.getRoomSize());
                }
            });
        }else{
            component_black = new JLabel(info.getBlackPlayer());
        }
        if(component_white!=null){
            this.remove(component_white);
        }
        if("".equalsIgnoreCase(info.getWhitePlayer())){
            component_white = new JButton(new AbstractAction("Join as White") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    join(info.getRoomPortWhite(),false,info.getRoomSize());
                }
            });
        }else{
            component_white = new JLabel(info.getWhitePlayer());
        }
        component_black.setBorder(new EmptyBorder(1, 1 ,1 ,1));
        component_white.setBorder(new EmptyBorder(1, 1 ,1 ,1));
        c.gridx=1;
        c.gridy=1;
        add(component_black,c);
        
        c.gridx=1;
        c.gridy=2;
        add(component_white,c);
        
        
        
    }
    
    Border raised = new BevelBorder(BevelBorder.RAISED);
    Border lovered = new BevelBorder(BevelBorder.LOWERED);
    
    
    public RoomComponent(RoomInfo info) {
        label_room_name = new JLabel();
        label_room_name.setBorder(new EmptyBorder(1, 1 ,1 ,1));
        label_black = new JLabel("Black:");
        label_black.setBorder(new EmptyBorder(1, 1 ,1 ,1));
        label_white = new JLabel("White:");
        label_white.setBorder(new EmptyBorder(1, 1 ,1 ,1));
        this.setMinimumSize(new Dimension(100, 60));
        this.setPreferredSize(new Dimension(150, 60));
        this.setMaximumSize(new Dimension(200, 60));
        this.setFocusable(true);
        this.addFocusListener(this);
        this.addMouseListener(this);
        this.setVisible(true);
        this.setLayout(new GridBagLayout());
        //c.gridheight=3;
        //c.gridwidth=2;
        c.fill = GridBagConstraints.BOTH;
        c.ipadx=10;
        //c.ipady=10;
        c.gridheight=1;
        c.gridwidth=4;
        c.weighty=0.5;
        c.weightx=1;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx=0;
        c.gridy=0;
        this.add(label_room_name,c);
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx=0;
        c.gridwidth=1;
        c.gridx=0;
        c.gridy=1;
        this.add(label_black,c);
        c.gridx=0;
        c.gridy=2;
        this.add(label_white,c);
        c.weightx=0.5;
        
        
        
        this.setBorder(raised);
        updateInfo(info);
    }

    @Override
    public void focusGained(FocusEvent e) {
        this.setBorder(lovered);
        selected(info);
    }

    @Override
    public void focusLost(FocusEvent e) {
        this.setBorder(raised);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.requestFocusInWindow();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    
    
}
