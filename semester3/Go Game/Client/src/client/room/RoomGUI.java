/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.room;

import game.Board;
import game.MoveNotifier;
import game.playerstate.game.PlayerGameState.GameState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import util.Config;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import utilities.messages.ChatMessage;
import utilities.messages.Message;
import utilities.messages.client.QuitGame;
import utilities.messages.client.RequestConnectionState;
import utilities.messages.client.RequestGameState;
import utilities.messages.client.RequestPing;
import utilities.messages.client.RequestScore;
import utilities.messages.client.ReturnGameState;
import utilities.messages.client.ReturnPing;
import utilities.messages.client.ReturnScore;
import utilities.messages.move.Move;

/**
 * Class used for displaying board in window.
 * @author n1t4chi
 */
public class RoomGUI extends basicgui.BasicFrame implements MoveNotifier {
    
    
    /**
     * Image displayed when there is no player or player is disconnected
     */
    private final Image img_dc;
    /**
     * Image displayed when player can place a stone.
     */
    private final Image img_move;
    /**
     * Image displayed when player waits for opponent
     */
    private final Image img_wait;
    /**
     * Image displayed when player chooses territories
     */
    private final Image img_choose;
    /**
     * Image displayed when player decides if he agrees with opponent chosen territories
     */
    private final Image img_decide;
    /**
     * Image displayed when player accepts or submits territory.
     */
    private final Image img_accept;
    
    
    /**
     * BoardGUI on which game is played.
     */
    private final BoardGUI board_gui;
    /**
     * Name of user.
     */
    private final String name;
    /**
     * Name of user.
     */
    private String opponent="";
    /**
     * Status panel for players
     */
    private final JPanel status_player_panel;
    /**
     * Status label for black player. Name.
     */
    private final JLabel status_player_black_name;
    /**
     * Status label for white player. Name.
     */
    private final JLabel status_player_white_name;
    /**
     * Status label for black player. Score.
     */
    private final JLabel status_player_black_score;
    /**
     * Status label for white player. Score.
     */
    private final JLabel status_player_white_score;
    /**
     * Status panel for black player. Score.
     */
    private final JPanel status_player_black;
    /**
     * Status panel for white player. Score.
     */
    private final JPanel status_player_white;
    /**
     * Status bar
     */
    private final JPanel status_bar;
    /**
     * Label for ping
     */
    private final JLabel status_ping;
    /**
     * Label for connection status.
     */
    private final JLabel status_connection;
    /**
     * Label for game status.
     */
    private final JLabel status_game;
    
    /**
     * Chat panel containing input and output text components.
     */
    private final JPanel chat_panel;
    /**
     * Chat input field.
     */
    private final JTextField chat_input;
    /**
     * Chat output field.
     */
    private final JEditorPane chat_output;
    
    /**
     * ScheduledFuture for status updater
     */
    private final ScheduledFuture status_updater_future;
    /**
     * Executor for status updater
     */
    private final ScheduledExecutorService ses;
    /**
     * Whether this player is black
     */
    private final boolean isBlack;
    /**
     * Current game state
     */
    private GameState game_state=null;
    /**
     * HTML editor kit
     */
    private final HTMLEditorKit kit;
    
    /**
     * Adds given text at the end of the chat output. Adds new line after text
     * Error message type
     * @param text text to print
     */
    public void printErr(String text){
        println(ChatMessage.ChatSource.ERROR, text);
    }
    /**
     * Adds given text at the end of the chat output. Adds new line after text
     * System message type
     * @param text text to print
     */
    public void printSys(String text){
        println(ChatMessage.ChatSource.SYSTEM, text);
    }
    /**
     * Adds given text at the end of the chat output. Adds new line after text
     * @param opponent whether message is from opponent or not
     * @param text text to print
     */
    public void println(boolean opponent,String text){
        println((opponent)?ChatMessage.ChatSource.OPPONENT:ChatMessage.ChatSource.PLAYER, text);
    }
    /**
     * Adds given text at the end of the chat output. Adds new line after text
     * @param type type of message
     * @param text text to print
     */
    public void println(ChatMessage.ChatSource type,String text){
        try {
            String font_col,source;
            switch(type){
                case OPPONENT: font_col = "#aa2222"; source=opponent; break;
                case PLAYER: font_col = "#2222aa"; source=name; break;
                case ERROR: font_col = "#ff0000"; source="ERROR"; break;
                case SERVER: font_col = "#000000"; source="SYSTEM"; break;
                default :  font_col = "#000000"; source="SYSTEM"; break;
            }
            text = "<font color =\""+font_col+"\">["+source+"]</font>:"+text+"<br>";
            HTMLDocument doc = (HTMLDocument)chat_output.getDocument();
            kit.insertHTML(doc,doc.getLength(), text, 0,0,null);
            //kit.write(out, doc, ERROR, WIDTH);
            //doc.insertString(doc.getLength()  ,text, null);
        } catch (Exception ex) {
            System.err.println("Failed to insert message: "+ex);
        }
    }
    
    
    /**
     * Default constructor 
     * @param master Master of this component
     * @param name Name of user.
     * @param board Board object
     * @param black Whether the player is black or not.
     * @throws HeadlessException 
     */
    public RoomGUI(MoveNotifier master,String name,Board board,boolean black, Board.Size size) throws HeadlessException {
        super("GoGame client",null,"Room");
        this.name = name;
        this.isBlack = black;
        this.master = master;
        this.addMenuItem("File",new JMenuItem(new AbstractAction("Reconnect") {
            @Override
            public void actionPerformed(ActionEvent e) {
                
            }
        }),0);
        img_dc = util.ImageEditor.getImage("status/dc.png");
        img_move = util.ImageEditor.getImage("status/move.png");
        img_wait = util.ImageEditor.getImage("status/wait.png");
        img_choose = util.ImageEditor.getImage("status/choose.png");
        img_decide = util.ImageEditor.getImage("status/decide.png");
        img_accept = util.ImageEditor.getImage("status/accept.png");
        board_gui = new BoardGUI(this,black,size);
        this.addSlaveObserver(board_gui);
        
        //System.out.println("client.room.RoomGUI.<init>() pre container count:"+this.getContainer().getComponentCount());
        //this.setContainer(new JPanel(new BorderLayout()));
        this.getContainer().add(board_gui,BorderLayout.CENTER);
        //System.out.println("client.room.RoomGUI.<init>() post container count:"+this.getContainer().getComponentCount());

        
        status_bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        status_connection = new JLabel("dc");
        status_game = new JLabel("game");
        status_ping = new JLabel("ping");
        
        
        
        
        status_bar.add(status_game);
        status_bar.add(status_connection);
        status_bar.add(status_ping);
        
        status_player_panel = new JPanel(new GridLayout(0,1));
        status_player_black = new JPanel(new GridLayout(0,1)){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(game_state!=null){
                    int h = getHeight();
                    int h2 = h/2;
                    int w = getWidth();
                    int w2 = w/2;
                    Graphics2D g2 = (Graphics2D)g;
                    g2.setColor(Color.BLACK);
                    g2.fillRect(w-h2, h2, h2, h2);
                    Image img=null;
                    switch(game_state){
                        case AGREE:img = (isBlack)?img_accept:img_decide;break;
                        case PASS:case WAIT:img = (isBlack)?img_wait:img_move;break;
                        case PLACE:img = (isBlack)?img_move:img_wait;break;
                        case CHOOSE:img = (isBlack)?img_choose:img_choose;break;
                        case WON:case LOST:img =img_accept;break;
                    }
                    if((!isBlack)&&("".equals(opponent)))
                        img = img_dc;

                    g2.drawImage(img,w-h2, 0, h2, h2, null);
                    //g2.dispose();
                }
            }
        };
        status_player_black_name = new JLabel("Player:"+((isBlack)?name:""));
        status_player_black.add(status_player_black_name);
        status_player_black_score = new JLabel("Score:");
        status_player_black.add(status_player_black_score);
        status_player_white = new JPanel(new GridLayout(0,1)){
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int h = getHeight();
                int h2 = h/2;
                int w = getWidth();
                int w2 = w/2;
                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(Color.WHITE);
                g2.fillRect(w-h2, h2, h2, h2);
                g2.setColor(Color.black);
                g2.drawRect(w-h2, h2, h2, h2);
                if(game_state!=null){
                    Image img=null;
                    switch(game_state){
                        case AGREE:img = (!isBlack)?img_accept:img_decide;break;
                        case PASS:case WAIT:img = (!isBlack)?img_wait:img_move;break;
                        case PLACE:img = (!isBlack)?img_move:img_wait;break;
                        case CHOOSE:img = (!isBlack)?img_choose:img_choose;break;
                        case WON:case LOST:img =img_accept;break;
                    }
                    if((isBlack)&&("".equals(opponent)))
                        img = img_dc;

                    g2.drawImage(img,w-h2, 0, h2, h2, null);
                    //g2.dispose();
                }
            }
        };
        status_player_white_name = new JLabel("Player:"+((!isBlack)?name:""));
        status_player_white.add(status_player_white_name);
        status_player_white_score = new JLabel("Score:");
        status_player_white.add(status_player_white_score);
        
        status_player_black.setBorder(new EmptyBorder(1, 1,1,1));
        status_player_white.setBorder(new EmptyBorder(1, 1,1,1));
        status_player_white.setPreferredSize(new Dimension(200, 50));
        status_player_panel.setPreferredSize(new Dimension(200, 100));
        status_player_black.setPreferredSize(new Dimension(200, 50));
        status_player_white.setPreferredSize(new Dimension(200, 50));
        status_player_panel.add(status_player_black);
        status_player_panel.add(status_player_white);
        
        chat_panel = new JPanel(new BorderLayout(3,3));
        chat_output = new JEditorPane("text/html", "<html></html>");
        chat_output.setEditable(false);
        JScrollPane chat_output_scroll = new JScrollPane(chat_output,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chat_input = new JTextField("");
        chat_input.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                if(!chat_input.getText().trim().isEmpty()){
                    //System.err.println("Input:"+chat_input.getText()); 
                    notifyMasterObserverAboutMessage(new ChatMessage(ChatMessage.ChatSource.OPPONENT,chat_input.getText()));
                    println(false,chat_input.getText());
                }
                chat_input.setText("");
            }
        });
        kit = (HTMLEditorKit)chat_output.getEditorKit();
        chat_panel.setPreferredSize(new Dimension(200, 0));
        chat_panel.add(chat_output_scroll,BorderLayout.CENTER);
        chat_panel.add(chat_input,BorderLayout.SOUTH);
        chat_panel.add(status_player_panel,BorderLayout.NORTH);
        
        this.add(status_bar,BorderLayout.SOUTH);
        this.add(chat_panel,BorderLayout.EAST);
        
        
        ses = new ScheduledThreadPoolExecutor(2);
        status_updater_future = ses.scheduleAtFixedRate(() -> {
            ping_request_counter++;
            if(ping_request_counter==10){
                notifyMasterObserverAboutMessage(new RequestPing());
                ping_request_counter=0;
            }
            notifyMasterObserverAboutMessage(new RequestConnectionState());
            notifyMasterObserverAboutMessage(new RequestGameState());
            notifyMasterObserverAboutMessage(new RequestScore());
        },100 , 100, TimeUnit.MILLISECONDS
        );
        this.validate();
        this.repaint();
        //notifyMasterObserverAboutMessage(new RequestOpponentName());
    }
    private volatile int ping_request_counter = 0;
    @Override
    public boolean shouldExit() {
        int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to quit?\n","Quitting", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(opt == JOptionPane.YES_OPTION){
            status_updater_future.cancel(true);
            ses.shutdown();
            notifyMasterObserverAboutMessage(new QuitGame());
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if(status_updater_future!=null)
            status_updater_future.cancel(true);
        if(ses!=null)
            ses.shutdown();
    }

    
    
    
    
    private MoveNotifier master = null;
    private MoveNotifier slave = null;
    
    @Override
    public void setMasterObserver(MoveNotifier observer) {
        master = observer;
    }

    @Override
    public void addSlaveObserver(MoveNotifier observer) {
        slave = observer;
    }


    @Override
    public void updateMove(MoveNotifier src, Move move) {
        if(src==slave){
            notifyMasterObserverAboutMove(move);
        }else if(src==master){
            switch(move.getMoveType()){
                case RETURN_TO_STONE_PLACING:
                    JOptionPane.showMessageDialog(this, "Opponent wants to go back to placing stones. \nYou will make first move.");
                    break;
                case TERRITORIES_DISAGREE:
                    printSys("Opponent disagreed with your territories.");
                    break;            
                case CHOOSE_TERRITORIES:
                    JOptionPane.showMessageDialog(this, "Choose territories now.\nLeft click on the field to add black territorie,\nright click on the field to add white territorie\nclick twice on the territorie to remove it.");
                    break;
            /*    case INVALID_MOVE:
                    printSys("Server declined your last move.");
                    break;*/
                case TERRITORIES_CHOOSEN:
                    JOptionPane.showMessageDialog(this, "Your opponent chosen territories first!\nYou can disagree or agree with him.");
                    break;
                case LOSE:
                    JOptionPane.showMessageDialog(this, "You lost!");
                    break;
                case WIN:
                    JOptionPane.showMessageDialog(this, "You won!");
                    break;
            }
            
            notifySlaveObserversAboutMove(move);
        }else{
            System.err.println("Received move from illegal source:"+src);
        }
    }

    @Override
    public void updateMessage(MoveNotifier src, Message msg) {
        if(src == master){
            if(msg!=null) switch(msg.getMessageType()){
                case RETURN_OPPONENT_NAME:
                        String new_opponent =  msg.getMessage().toString();
                        new_opponent=(new_opponent.isEmpty())?"Opponent":new_opponent;
                        opponent = new_opponent;
                        if(isBlack)
                            status_player_white_name.setText("Player:"+opponent);
                        else    
                            status_player_black_name.setText("Player:"+opponent);
                        
                        //repaint();
                    break;
                case PLAYER_CONNECTED:
                        String new_name =  msg.getMessage().toString();
                        new_name=(new_name.isEmpty())?"Opponent":new_name;
                        printSys("Player ["+new_name+"] connected to game");   
                        opponent = new_name;
                        if(isBlack)
                            status_player_white_name.setText("Player:"+opponent);
                        else    
                            status_player_black_name.setText("Player:"+opponent);
                        //repaint();
                    break;
                case PLAYER_DISCONNECTED:
                        printSys("Player ["+opponent+"] disconnected from the game");   
                        opponent = "";
                    break;
                case RETURN_BOARD: case RETURN_LEGAL_PLACES: case RETURN_DEFAULT_TERRITORIES:
                        notifySlaveObserversAboutMessage(msg);
                    break;
                case RETURN_GAME_STATE:
                        if(msg instanceof ReturnGameState){
                            //notifySlaveObserversAboutMessage(msg); needed?
                            game_state = ((ReturnGameState)msg).getState();
                            status_game.setText(""+msg.getMessage());
                            //repaint();
                        }else
                            System.err.println("Received illegal instance for RETURN_GAME_STATE from GameHandler:"+msg);      
                break;   
                case RETURN_PING:
                        if(msg instanceof ReturnPing){
                            status_ping.setText(((ReturnPing)msg).getPing()+"ms");
                            //repaint();
                        }else
                            System.err.println("Received illegal instance for RETURN_PING from GameHandler:"+msg);
                    break;
                case RETURN_SCORE:
                        if(msg instanceof ReturnScore){
                            status_player_black_score.setText("Score:"+((ReturnScore) msg).getScoreBlack());
                            status_player_white_score.setText("Score:"+((ReturnScore) msg).getScoreWhite());
                            repaint();
                        }
                    break;
                case RETURN_CONNECTION_STATE:
                        status_connection.setText(""+msg.getMessage());
                        repaint();
                    break;
                case CHAT:
                        if(msg instanceof ChatMessage){
                            if(((ChatMessage)msg).getSource() == ChatMessage.ChatSource.PLAYER){
                                println(ChatMessage.ChatSource.OPPONENT, ((ChatMessage) msg).getChatMessage()); //in case when opponent sends message to server with player type.
                            }else{
                                println(((ChatMessage) msg).getSource(), ((ChatMessage) msg).getChatMessage());
                            }
                        }
                    break;
                default:
                        System.err.println("Received illegal message from GameHandler:"+msg);
                    break;
            }
        }else if (src == slave){
            notifyMasterObserverAboutMessage(msg);
        }else {
            System.err.println("Received message from illegal source:"+src);
        }
    }  

    @Override
    public void notifyMasterObserverAboutMove(Move move) {
        if(master!=null)
            master.updateMove(this, move);
        else
            System.err.println("RoomGUI has no master");
    }

    @Override
    public void notifySlaveObserversAboutMove(Move move) {
        if(slave!=null)
            slave.updateMove(this, move);
        else
            System.err.println("RoomGUI has no slave");
    }
    
    @Override
    public void notifyMasterObserverAboutMessage(Message msg) {
        if(master!=null)
            master.updateMessage(this, msg);
        else
            System.err.println("RoomGUI has no master");
    }


    @Override
    public void notifySlaveObserversAboutMessage(Message msg) {
        if(slave!=null){
            slave.updateMessage(this, msg);
        }else
            System.err.println("RoomGUI has no slave");
    }
}
