/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.launcher;

import basicgui.IPv4Input;
import basicgui.PortInput;
import client.ConfigClient;
import client.room.GameHandler;
import game.Board;
import game.MoveNotifier;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import utilities.Socket;
import utilities.SocketException;
import utilities.messages.ReturnRoomList;
import utilities.messages.Message;
import utilities.messages.MessageType;
import utilities.messages.RequestRoomList;
import utilities.messages.RoomCreate;
import utilities.messages.data.RoomInfo;
import utilities.messages.move.Move;
/**
 *
 * @author n1t4chi
 */
public class LauncherGUI extends basicgui.BasicFrame implements MoveNotifier{
    /**
     * Suffix for configuration files.
     */
    private final static String CONFIG_SUFFIX = "Launcher";
    /**
     * Connect button
     */
    final JButton button_connect;
    /**
     * Refresh button
     */
    final JButton button_refresh;
    /**
     * Autorefresh check box
     */
    final  JCheckBox button_auto_refresh;
    /**
     * Scheduled future for autorefresh
     */
    private ScheduledFuture auto_refresh_future = null;
    
    /**
     * Join button
     */
    final JButton button_join;
    /**
     * Join button
     */
    final JButton button_create;
    /**
     * Container for room info components.
     */
    private final JPanel room_container;
    
    /**
     * Configuration.
     */
    private final ConfigClient config;
    
    /**
     * Socket for initial client server communication.
     */
    Socket Socket = null;
    
    
    
    
    /**
     * Current IP of a server.
     */
    private String ip= null;
    /**
     * Current port of a server.
     */
    private int port=-1;
    /**
     * Current name of user.
     */
    private String name= null;
    private final GridBagConstraints c;
    /**
     * Currently selected room.
     */
    private RoomInfo selected_room = null;
    
    /**
     * Current list of rooms.
     */
    ArrayList<RoomInfo> room_list = null;
    /**
     * Starts game under given IP and port.
     * @param ip 
     * @param port 
     * @param black 
     * @param name of user.
     * @param size
     */
    public void startGame(String ip,int port,boolean black,String name, Board.Size size){
        SwingUtilities.invokeLater(() -> {
            try {
                GameHandler g = new GameHandler(ip,port,black,name,size);
            } catch (IOException ex) {
                System.err.println("ERROR while connecting to game"+ex);
            }
        });
    }
    
    ScheduledExecutorService executor = null;
    
    public void switchAutoRefresh(){
        if(button_auto_refresh.isSelected()){
            if(Socket!=null){
                if(auto_refresh_future==null){
                    if(executor==null){
                        executor = Executors.newScheduledThreadPool(4);
                    }
                    auto_refresh_future = executor.scheduleWithFixedDelay(()->{
                        requestRooms();
                    },1, 5, TimeUnit.SECONDS);
                }
            }else{
                button_auto_refresh.setSelected(false);
            }
        }else{
            if(auto_refresh_future!=null){
                auto_refresh_future.cancel(true);
                auto_refresh_future=null;
            }
        }
    }
    
    /**
     * Sends request to server to create specified room.
     */
    public void createRoom(){
        if((Socket!=null)&&(Socket.isConnected())){
            JTextField room_name = new JTextField();
            JList<Board.Size> size = new JList<>(Board.Size.values());
            size.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            size.setSelectedIndex(0);
            JRadioButton ai_none = new JRadioButton("No AI players", true);
            JRadioButton ai_black = new JRadioButton("Black player AI", false);
            JRadioButton ai_white = new JRadioButton("White player AI", false);
            ButtonGroup group = new ButtonGroup();
            group.add(ai_none);
            group.add(ai_black);
            group.add(ai_white);
            Object[] obj = {
                "Room name",
                room_name,
                "Room size",
                size,
                ai_none,
                ai_black,
                ai_white
            };
            int opt = JOptionPane.showConfirmDialog(this, obj,"Create new room", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if(opt == JOptionPane.OK_OPTION){
               /* System.err.println(""
                        + "name:"+room_name.getText()
                        + "\nsize:"+size.getSelectedValue()
                        + "\n black ai?"+ai_black.isSelected() 
                        + "\n white ai?"+ai_white.isSelected()
                );*/
                notifySlaveObserversAboutMessage(new RoomCreate(room_name.getText(), size.getSelectedValue(), ai_black.isSelected(), ai_white.isSelected()));
                
            }
        }
    }
    public void disconnect(){
        /*notifySlaveObserversAboutMessage(new ServerShutdown());
        try{
            Thread.sleep(1000);
        }catch(InterruptedException ex){
            System.err.println("Will not send message to Lobby Server that client is disconnecting");
        }*/
        
        if(executor!=null)
            executor.shutdown();
        executor=null;
        button_create.setEnabled(false);
        button_refresh.setEnabled(false);
        button_join.setEnabled(false);
        button_auto_refresh.setSelected(false);
        switchAutoRefresh();
        if(auto_refresh_future!=null)
            auto_refresh_future.cancel(true);
        
        button_auto_refresh.setEnabled(false);
        
        if(room_list!=null){
            room_list.clear();
            room_list=null;
        }
        refreshRooms();
        if(Socket!=null){
            //notifySlaveObserversAboutMessage(new ServerShutdown());
            Socket.stopIO();
          /*  try{
                Thread.sleep(1000);
            }catch(InterruptedException ex){
                System.err.println("Will not give time for server to close connection");
            }*/
            try{
                Socket.Disconnect();
            }catch(SocketException ex){
                System.err.println("Failed to close socket."
                        + "");
            }
        }
        Socket=null;
        
        
        
        this.pack();
        this.repaint();
    }
    /**
     * Reset component.
     */
    public void resetRooms(){
    }
    
    /**
     * Retrieves room list from Socket.
     */
    public void refreshRooms(){
        //ArrayList<RoomComponent> al = new ArrayList<>();
        c.gridy=1;
        c.weighty=0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.gridheight=1;
        c.ipadx=5;
        c.weightx=1;
        room_container.removeAll();
        if(room_list==null){
            room_container.add(new JLabel("Connect or refresh to receive list of rooms",SwingConstants.CENTER));
        }else if(room_list.isEmpty()){
            room_container.add(new JLabel("No rooms on server",SwingConstants.CENTER));
        }else{
            for(RoomInfo info : room_list){
                //System.out.println("client.launcher.LauncherGUI.getAndPaintRooms() adding room: "+info.getRoomName());
                RoomComponent rc = new RoomComponent(info) {
                    @Override
                    public void join(int port,boolean black, Board.Size size) {
                        startGame(ip,port,black,name,size);
                    }

                    @Override
                    public void selected(RoomInfo info) {
                        selected_room = info;
                    }
                };

                //al.add(rc);
                room_container.add(rc,c);
                c.gridy++;
               // System.out.println("client.launcher.LauncherGUI.getAndPaintRooms() components: "+room_container.getComponents().length);

            }
        }
        JPanel filler = new JPanel();
        c.weighty=1;
        room_container.add(filler,c);
       // for(RoomComponent rc : al){
         //   System.out.println("client.launcher.LauncherGUI.getAndPaintRooms() rc: pos"+rc.getLocation()+" rc: size"+rc.getSize());
      // }
        this.pack();
        this.repaint();
    }
    
    /**
     * Requests new list of rooms.
     */
    public void requestRooms(){
      /*  if(Socket!=null){
            try{*/
              //  System.out.println("client.launcher.LauncherGUI.getRooms() sending message");
                notifySlaveObserversAboutMessage(new RequestRoomList());
             //   System.out.println("client.launcher.LauncherGUI.getRooms() message was send, receiving");
     /*       }catch(SocketException ex){
                System.err.println("Error on refreshing: "+ex.getLocalizedMessage());
            }
        }*/
    }
    
    private void Connect(){
        disconnect();
        IPv4Input ip_input = new IPv4Input(config.getDefaultServerIP());
        PortInput port_input = new PortInput(config.getDefaultServerPort());
        JFormattedTextField name_input = new JFormattedTextField(new DefaultFormatterFactory(new DefaultFormatter(){
            @Override
            public Object stringToValue(String string) throws ParseException {
                if( 
                    (!string.matches("[a-zA-Z0-9]?[a-zA-Z0-9 ]{0,30}[a-zA-Z0-9]?"))||
                    (!string.isEmpty()&&string.trim().isEmpty())
                )
                    throw new ParseException("", 0);
                return string;
            } 
        }), config.getClientName());
        JCheckBox save_con = new JCheckBox("Set as default");
        Object[] ip = {"Server IP:", ip_input};
        Object[] message = {
            ip,
            "Server Port:", port_input,
            "Your Nickname:",name_input,
            save_con
        };
        int opt = JOptionPane.showConfirmDialog(LauncherGUI.this,message,"Connect",JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if(opt == JOptionPane.OK_OPTION){
            this.ip = ip_input.getIP();
            this.port = port_input.getPort();
            this.name = name_input.getText();
            if(save_con.isSelected()){
                //System.out.println("changing to: "+port_input.getPort()+" , "+ip_input.getIP()+" , "+name_input.getText());
                config.set(this.port, this.ip , this.name);
            }
            boolean success = true;
            String err="";
            try{
                if(Socket!=null){
                    Socket.Disconnect();
                    resetRooms();
                    Socket = null;
                }
                Socket = new Socket(this,this.ip, this.port,50);
                button_create.setEnabled(true);
                button_refresh.setEnabled(true);
                button_join.setEnabled(true);
                button_auto_refresh.setEnabled(true);
                button_auto_refresh.setSelected(true);
                switchAutoRefresh();
                
            }catch(Exception ex){
                err="\nERROR:"+ex.getLocalizedMessage();
                if(Socket!=null){
                    Socket.Disconnect();
                    Socket = null;
                    resetRooms();
                }
                JOptionPane.showMessageDialog(this,"Could not connect to "+this.ip+":"+this.port+err,"ERROR",JOptionPane.ERROR_MESSAGE);   
            }
        }
        
    }
    
    private void joinRoom(){
        if(selected_room!=null){
            char i = 0;
            if("".equals(selected_room.getBlackPlayer()) ){
                i+= 1;
            }
            if("".equals(selected_room.getWhitePlayer()) ){
                i+= 10;
            }
            switch(i){
                case 1:
                        startGame(ip,selected_room.getRoomPortBlack(),true, name,selected_room.getRoomSize());
                    break;
                case 10:
                        startGame(ip,selected_room.getRoomPortWhite(),false, name,selected_room.getRoomSize());
                    break;
                case 11:
                        Object[] obj = {"Black","White"};
                        Object choice = JOptionPane.showInputDialog(LauncherGUI.this, "Pick which stones you want to play","Choose seat",JOptionPane.PLAIN_MESSAGE,null,obj,obj[0]);
                        if(choice == obj[0]){
                            startGame(ip,selected_room.getRoomPortBlack(),true, name,selected_room.getRoomSize());
                        }else if (choice == obj[1]){
                            startGame(ip,selected_room.getRoomPortWhite(),false, name,selected_room.getRoomSize());
                        }
                    break;
                default:
                        JOptionPane.showMessageDialog(LauncherGUI.this, "Room is full");
                    break;
            }
            
        }else{
            JOptionPane.showMessageDialog(LauncherGUI.this, "Select rooom first");
        }
    }
    

    @Override
    public boolean shouldExit() {
        disconnect();
        if(!config.save("Launcher"))
            System.err.println("COULD NOT SAVE CLIENT CONFIGURATION");
        
        return true;
    }
    
    
    /**
     * Default constructor
     * @param config config.
     * @throws HeadlessException 
     */
    public LauncherGUI(ConfigClient config) throws HeadlessException {
        super("GoGame launcher",null,"Launcher");
        this.config = config;
        
        this.addMenuItem("File",new JMenuItem(new AbstractAction("Disconnect") {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnect();
            }
        }),0);
        this.addMenuItem("File",new JMenuItem(new AbstractAction("Connect") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connect();
            }
        }),0);
        this.addMenuItem("File",new JMenuItem(new AbstractAction("Create Room") {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRoom();
            }
        }),0);
        this.addMenuItem("File",new JMenuItem(new AbstractAction("Join Room") {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinRoom();
            }
        }),0);
        JMenuItem file = getMenuItem("File");
        if(file instanceof  JMenu){
            ((JMenu)file).addMenuListener(new MenuListener(){
                @Override
                public void menuSelected(MenuEvent e) {
                    //System.out.println(".actionPerformed() not connected");
                    if(Socket==null||!LauncherGUI.this.Socket.isConnected()){
                    //    System.out.println(".actionPerformed() not connected");
                        getMenuItem("File.Disconnect").setVisible(false);
                        getMenuItem("File.Join Room").setVisible(false);
                    }else{
                    //    System.out.println(".actionPerformed() connected");
                        getMenuItem("File.Disconnect").setVisible(true);
                        if(selected_room!=null){
                            getMenuItem("File.Join Room").setVisible(true);
                        }else{
                            getMenuItem("File.Join Room").setVisible(false);
                        }
                    }
                }
                @Override
                public void menuDeselected(MenuEvent e) {}
                @Override
                public void menuCanceled(MenuEvent e) {}
            });
        }
        button_connect = new JButton(new AbstractAction("Connect") {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connect();
            }
        });
        button_refresh = new JButton(new AbstractAction("Refresh") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(Socket!=null){
                   // System.out.println(".actionPerformed()");
                    requestRooms();
                }
            }
        });
        button_auto_refresh = new JCheckBox(new AbstractAction("") {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchAutoRefresh();
            }
        });
        button_auto_refresh.setToolTipText("Auto refresh every 5 seconds");
        button_refresh.setLayout(new BorderLayout());
        button_refresh.add(button_auto_refresh,BorderLayout.EAST);
        button_auto_refresh.setOpaque(false);
        /*JPanel refresh_box = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.weightx=40;
        c.weighty=40;
        c.gridx=0;
        c.gridy=0;
        c.fill=GridBagConstraints.BOTH;
        refresh_box.add(button_refresh,c);
        
        c.fill=GridBagConstraints.VERTICAL;
        c.gridx=10;
        refresh_box.add(button_auto_refresh,c);*/
        c = new GridBagConstraints();
        
        button_join = new JButton(new AbstractAction("Join Room") {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinRoom();
            }
        });
        button_create = new JButton(new AbstractAction("Create Room") {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRoom();
            }
        });
        
        JPanel buttons = new JPanel(new GridLayout(1, 3));
        
        buttons.add(button_connect);
        buttons.add(button_refresh);
       // buttons.add(refresh_box);
        buttons.add(button_join);
        buttons.add(button_create);
        
        
        add(buttons,BorderLayout.SOUTH);
        room_container = new JPanel(new GridBagLayout());
        room_container.setVisible(true);
        //room_container.setBackground(Color.red);
        JScrollPane room_container_scroll = new JScrollPane(room_container);
        setContainer(room_container_scroll);
        disconnect();
        
        
    }
    
    
    
    
    
    @Override
    public void getHelp() {
        JEditorPane help;
        JScrollPane help_scroll;
        JPanel panel = new JPanel(new BorderLayout());
        String launcher_help =
                  "<font size=5><b>GoGame launcher instruction</b></font><br>"
                + "<b>GoGame launcher</b> allows to play Go with other players. "
                + "To start game you need to connect to <b>GoGame Server</b> first "
                + "After you successfuly connect to server you can create Game Room or join existing one.<br>"
                + "If you are newbie or you want to check some instructions click below to get online tutorial about Go."
        ;
        help = new JEditorPane("text/html", launcher_help);
        help.setEditable(false);
        help.setFocusable(false);
        help_scroll= new JScrollPane(help,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        help.setPreferredSize(new Dimension(300, 200));
        JButton site_button = new JButton(new AbstractAction("Check rules of Go online!") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JEditorPane site;
                JScrollPane site_scroll;
                try{
                    URL url = new URL("http://www.cs.cmu.edu/~wjh/go/rules/Japanese.html");
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder sb = new StringBuilder(4000);
                    String text;
                    while((text=in.readLine())!=null){
                        text+="\n";
                        if(text.contains("<img")){
                            //System.out.println("przed:"+text);
                            text=text.replaceAll("<img SRC=./", "<img SRC=http://www.cs.cmu.edu/~wjh/go/rules/");
                            text = text.substring(0,text.indexOf(".gif"))+".gif >";
                           // System.out.println("przed:"+text);
                        }
                        sb.append(text);
                    }
                    site = new JEditorPane("text/html",sb.toString());
                    //System.out.println(sb.toString());
                    //site.setMaximumSize(new Dimension(600,600));
                    site.setEditable(false);
                    site.setFocusable(false);
                    site.setPreferredSize(new Dimension(700, 800));
                    site_scroll= new JScrollPane(site,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);   
                    JOptionPane.showOptionDialog(LauncherGUI.this,site_scroll, "Online Help",JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,null,new Object[]{},null);
                 }catch(Exception ex){
                    JOptionPane.showMessageDialog(LauncherGUI.this,"Could not retrieve content of Site.<br>"+ex,"ERROR",JOptionPane.ERROR_MESSAGE);  
                }     
            }
        });
        panel.add(help_scroll,BorderLayout.CENTER);
        panel.add(site_button,BorderLayout.SOUTH);
        
        JOptionPane.showOptionDialog(this,panel, "GoGame Launcher help",JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION,null,new Object[]{},null);
    }

    
    
    
    @Override
    /**
     * Does Nothing
     */
    public void setMasterObserver(MoveNotifier observer) {}

    @Override
    /**
     * Does Nothing
     */
    public void addSlaveObserver(MoveNotifier observer) {}

    @Override
    /**
     * Does Nothing
     */
    public void notifyMasterObserverAboutMove(Move move) {}

    @Override
    /**
     * Does Nothing
     */
    public void notifySlaveObserversAboutMove(Move move) {}

    @Override
    /**
     * Does Nothing
     */
    public void notifyMasterObserverAboutMessage(Message msg) {}

    @Override
    public void notifySlaveObserversAboutMessage(Message msg) {
        if(Socket!=null){
            Socket.updateMessage(this, msg);
        }else{
            System.err.println("Socket is not initialised");
        }
    }

    @Override
    /**
     * Does Nothing
     */
    public void updateMove(MoveNotifier src, Move move) {}

    @Override
    public void updateMessage(MoveNotifier src, Message msg) {
        if(msg!=null){
            if(src == Socket){
                switch(msg.getMessageType()){
                    case SOCKET_CLOSE_ERROR:
                            JOptionPane.showMessageDialog(this, "Connection was terminated by server.", "ERROR",JOptionPane.ERROR_MESSAGE);
                            disconnect();
                    case SOCKET_INPUT_ERROR:
                    case SOCKET_OUTPUT_ERROR:
                            JOptionPane.showMessageDialog(this, "Error on communication with server:\n"+msg.getMessage(), "ERROR",JOptionPane.ERROR_MESSAGE);
                            disconnect();
                       break;
                    case SERVER_SHUTDOWN:     
                            JOptionPane.showMessageDialog(this, "Server is shutting down. Disconnecting.", "ERROR",JOptionPane.ERROR_MESSAGE);
                            disconnect();
                        break;
                    case RETURN_LIST_ROOMS:
                            if(msg instanceof ReturnRoomList){
                                room_list = ((ReturnRoomList) msg).getList();
                                refreshRooms();
                            }else{
                                System.err.println("Received illegal instance for RETURN_LIST_ROOMS type:"+msg);
                            }
                        break;
                    default:
                        break;
                }
            }else{
                if(msg instanceof RequestRoomList){
                    if(Socket!=null){
                        notifySlaveObserversAboutMessage(new RequestRoomList());
                        updateMessage(Socket, Socket.requestMessage(MessageType.RETURN_LIST_ROOMS));
                    }
                }else
                    System.err.println("Received message from illegal source:"+src);
            }
        }else{
            System.err.println("Received null message from source:"+src);
        }
    }
    
}