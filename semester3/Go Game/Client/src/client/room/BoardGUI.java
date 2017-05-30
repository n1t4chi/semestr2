 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.room;


import game.MoveNotifier;
import game.Board;
import game.Board.BoardField;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import util.Config;
import util.ImageEditor;
import utilities.messages.Message;
import utilities.messages.MessageType;
import utilities.messages.RequestBoard;
import utilities.messages.ReturnBoard;
import utilities.messages.client.RequestDefaultTerritories;
import utilities.messages.client.RequestLegalPlaces;
import utilities.messages.client.ReturnDefaultTerritories;
import utilities.messages.client.ReturnLegalPlaces;
import utilities.messages.data.FieldInfo;
import utilities.messages.move.Move;
import utilities.messages.move.Pass;
import utilities.messages.move.PlaceStone;
import utilities.messages.move.ReturnToStonePlacing;
import utilities.messages.move.Surrender;
import utilities.messages.move.TerritoriesAgree;
import utilities.messages.move.TerritoriesChosen;
import utilities.messages.move.TerritoriesDisagree;
import utilities.messages.move.ValidMove;

/**
 * Class for displaying the board
 * @author Kingaa
 */
public class BoardGUI extends JPanel implements MoveNotifier{
 
    private final BoardPanel boardPanel;
    private final PlayButtonsPanel playButtonsPanel;
    //private final JPanel territoriesButtonsPanel;
    
    //private Board board;
    
    private boolean black = false;
    
    private MoveNotifier master;
    
    /** enum for BoardGUI state */
    public enum BoardGUIstate { WAIT, PLAY, TERRITORIES, OPPONENTS_TERRITORIES, CHANGE_OPPONENTS_TERRITORIES }
    private BoardGUIstate state;
    
    /**
     * Constructor
     * @param master Master of this component
     * @param black if the player plays with black stones
     * @param size size of the board
     */
    public BoardGUI(MoveNotifier master, boolean black, Board.Size size){
        super(new BorderLayout());
        this.master = master;
        //this.board=board;

        this.black=black;
        
        state = BoardGUIstate.WAIT;
        
        playButtonsPanel = new PlayButtonsPanel();
        add(playButtonsPanel, BorderLayout.SOUTH);
        
        int siz=(size==Board.Size.SMALL)?9:((size==Board.Size.MODERATE)?13:19  );
        boardPanel = new BoardPanel(siz);
        add(boardPanel, BorderLayout.CENTER);
    }
    
    /**
     * Returns the boardGUI state
     * @return boardGUI state
     */
    public BoardGUIstate getBoardGUIstate(){
        return state;
    }
    
    /**
     * Changes boardGUI state
     * @param state 
     */
    public void setBoardGUIstate(BoardGUIstate state){
        this.state=state;
    }
    
    /**
     * Repaints the board
     */
    public void repaintBoard(){
        boardPanel.repaint();
    }

    @Override
    public void updateMove(MoveNotifier src, Move move) {
        repaintBoard();
        switch(move.getMoveType()){
                case CHOOSE_TERRITORIES:
                    playButtonsPanel.setTerritoriesMode();
                    //notifyMasterObserverAboutMessage(new RequestBoard());
                    notifyMasterObserverAboutMessage(new RequestDefaultTerritories());
                    setBoardGUIstate( BoardGUIstate.TERRITORIES );
                    playButtonsPanel.setTerritoriesMode();
                    break;
                case MAKE_MOVE:
                    setBoardGUIstate( BoardGUIstate.PLAY );
                   /* playButtonsPanel.setPlayMode();
                    notifyMasterObserverAboutMessage(new RequestLegalPlaces());
                    break;*/
                case RETURN_TO_STONE_PLACING:
                    notifyMasterObserverAboutMessage(new RequestLegalPlaces());
                    playButtonsPanel.setPlayMode();
                    boardPanel.clearTerritories();
                    boardPanel.clearOpponentsTerritories();
                    break;
                case TERRITORIES_CHOOSEN:
                    setBoardGUIstate(BoardGUIstate.OPPONENTS_TERRITORIES);
                    boardPanel.addOpponentsTerritories(((TerritoriesChosen)move).getTerritoriesList());
                    playButtonsPanel.setOpponentsTerritoriesMode();
                    break;
                case TERRITORIES_DISAGREE:
                    setBoardGUIstate(BoardGUIstate.TERRITORIES);
                    break;
        }
    }

    @Override
    public void notifyMasterObserverAboutMove(Move move) {
        if(master!=null){
            try{
                master.updateMove(this, move);
            }catch(Exception ex){
                System.err.println("Error on updating move: "+ex);
                ex.printStackTrace();
                throw ex;
            }
        }else
            System.err.println("BoardGUI has no master");
    }
    @Override
    public void updateMessage(MoveNotifier src, Message msg) {
        if(src == master){
            switch(msg.getMessageType()){
                case RETURN_DEFAULT_TERRITORIES:
                    ReturnDefaultTerritories returnDefaultTerritories = (ReturnDefaultTerritories) msg;
                    boardPanel.addTerritories((ArrayList<Point>)returnDefaultTerritories.getBlackTerritories(), (ArrayList<Point>)returnDefaultTerritories.getWhiteTerritories());
                    break;
                case RETURN_LEGAL_PLACES:
                    boardPanel.addLegalPlaces((ReturnLegalPlaces)msg);
                    setBoardGUIstate(BoardGUIstate.PLAY);
                    break;
                case RETURN_BOARD:
                    boardPanel.updateBoard(((ReturnBoard)msg).getBoard());
                    break;
                default:
                        System.err.println("Received illegal message from RoomGUI:"+msg.getMessageType());
                    break;
            }
        }
    }

    /** Panel for the buttons  */
    private class PlayButtonsPanel extends JPanel{
        private final JButton button1;
        private final JButton button2;
        
        /** constructor */
        public PlayButtonsPanel(){
            super(new GridLayout(1,2));
            
            button1 = new JButton("Surrender");
            button1.addActionListener((ActionEvent ae) -> {
                if(getBoardGUIstate() == BoardGUIstate.PLAY){   //Surrender
                    notifyMasterObserverAboutMove(new Surrender());
                    setBoardGUIstate(BoardGUIstate.WAIT);
                }
                if(getBoardGUIstate() == BoardGUIstate.TERRITORIES){    //Send
                    notifyMasterObserverAboutMove(boardPanel.getTerritoriesChosenMessage());
                    //setBoardGUIstate(BoardGUIstate.TERRITORIES);
                }
                if(getBoardGUIstate() == BoardGUIstate.OPPONENTS_TERRITORIES){    //Agree
                    notifyMasterObserverAboutMove(new TerritoriesAgree());
                    //setBoardGUIstate(BoardGUIstate.WAIT);
                }
                if(getBoardGUIstate() == BoardGUIstate.CHANGE_OPPONENTS_TERRITORIES){   //Change this territories
                    boardPanel.clearTerritories();
                    boardPanel.addBlackTerritories(boardPanel.opponentsTerritoriesBlack);
                    boardPanel.addWhiteTerritories(boardPanel.opponentsTerritoriesWhite);
                    boardPanel.clearOpponentsTerritories();
                    setBoardGUIstate(BoardGUIstate.TERRITORIES);
                    setTerritoriesMode();
                    //notifyMasterObserverAboutMove(new TerritoriesDisagree());
                }
            });
            
            button2 = new JButton("Pass");
            button2.addActionListener((ActionEvent ae) -> {
                if(getBoardGUIstate() == BoardGUIstate.PLAY){   //Pass
                    notifyMasterObserverAboutMove(new Pass());
                    setBoardGUIstate(BoardGUIstate.WAIT);
                }
                if(getBoardGUIstate() == BoardGUIstate.TERRITORIES){    //Back to stone placing
                    notifyMasterObserverAboutMove(new ReturnToStonePlacing());
                    setBoardGUIstate(BoardGUIstate.WAIT);
                }
                if(getBoardGUIstate() == BoardGUIstate.OPPONENTS_TERRITORIES){    //Disagree
                    setBoardGUIstate(BoardGUIstate.CHANGE_OPPONENTS_TERRITORIES);
                    setChangeOpponentsTerritoriesMode();
                    notifyMasterObserverAboutMove(new TerritoriesDisagree());
                }
                if(getBoardGUIstate() == BoardGUIstate.CHANGE_OPPONENTS_TERRITORIES){    //Return to the previous board
                    boardPanel.clearOpponentsTerritories();
                    setBoardGUIstate(BoardGUIstate.TERRITORIES);
                    setTerritoriesMode();
                    //notifyMasterObserverAboutMove(new TerritoriesDisagree());
                }
            });
            
            add(button1);
            add(button2);
        }
        
        /** changing the names of the buttons */
        public void changeButtons(String text1, String text2){
            button1.setText(text1);
            button2.setText(text2);
        }
        
        /** sets the territories mode*/
        public void setTerritoriesMode(){
            changeButtons("Send", "Return to placing stones");
        }

        /** sets the play mode*/
        private void setPlayMode() {
            changeButtons("Surrender", "Pass");
        }
        
        /** sets the opponents territories mode*/
        private void setOpponentsTerritoriesMode() {
            changeButtons("Agree", "Disagree");
        }
        
        /** sets the opponents territories mode*/
        private void setChangeOpponentsTerritoriesMode() {
            changeButtons("Change this territories", "Return to the previous board");
        }
    }
    
    /** Panel for the board */
    private class BoardPanel extends JPanel{
        
        private Image blackStone= null;
        private Image whiteStone= null;  
        private Image bg= null;

        private final Field[][] fields;
        
        private BoardField[][] board;
        
        private ArrayList<String> legalPlaces;

        private ArrayList<Field> territoriesWhite;
        private ArrayList<Field> territoriesBlack;
        
        private ArrayList<Field> opponentsTerritoriesWhite;
        private ArrayList<Field> opponentsTerritoriesBlack;
        
        /** constructor */
        public BoardPanel(int size){
            super(new GridLayout(size, size));
            
            legalPlaces = new ArrayList<>();
            territoriesWhite = new ArrayList<>();
            territoriesBlack = new ArrayList<>();
            opponentsTerritoriesWhite = new ArrayList<>();
            opponentsTerritoriesBlack = new ArrayList<>();

            try{
                blackStone = ImageIO.read(getClass().getResourceAsStream("/images/board/black9x9.png"));
                whiteStone = ImageIO.read(getClass().getResourceAsStream("/images/board/white9x9.png"));
                switch (size) {
                    case 9: bg = ImageIO.read(getClass().getResourceAsStream("/images/board/GameGo9x9.png")); break;
                    case 13:bg = ImageIO.read(getClass().getResourceAsStream("/images/board/GameGo13x13.png")); break;
                    default: 
                        size=19;
                        bg = ImageIO.read(getClass().getResourceAsStream("/images/board/GameGo19x19.png")); 
                    break;
                }
            }catch(IOException ex){
                System.err.println("PROBLEM WHILE LOADING BOARD IMAGES: "+ex);
            }
            if(blackStone == null){
                blackStone = ImageEditor.getEmptyImage(100, 100);
                Graphics2D g = (Graphics2D) blackStone.getGraphics();
                g.setPaint(new Color(32, 32, 32));
                g.fillOval(0, 0, 100,100);
                g.dispose();
            }
            if(whiteStone == null){
                whiteStone = ImageEditor.getEmptyImage(100, 100);
                Graphics2D g = (Graphics2D) whiteStone.getGraphics();
                g.setPaint(new Color(224, 224, 224));
                g.fillOval(0, 0, 100,100);
                g.dispose();
            }
            if(bg == null){
                int c_size = 30;
                int c_size_h = 15;
                int i_size = (size)*c_size;
                bg = ImageEditor.getEmptyImage(i_size,i_size);
                Graphics2D g = (Graphics2D) bg.getGraphics();
                g.setPaint(new Color(255, 178, 102));
                g.fillRect(0, 0, i_size,i_size);
                g.setPaint(new Color(1, 1, 1));
                g.setStroke(new BasicStroke(2));
                for(int i=0;i<size;i++){
                    g.drawLine(c_size_h+(c_size*i), c_size_h, c_size_h+(c_size*i), i_size-c_size_h);
                    g.drawLine(c_size_h, c_size_h+(c_size*i), i_size-c_size_h, c_size_h+(c_size*i));
                }
            }
            fields = new Field[size][size];

            for(int i=0; i<fields.length; i++)
                for(int j=0; j<fields.length; j++)
                    fields[i][j]=new Field(j, i);

            for(int i=0; i<fields.length; i++)
                for(int j=0; j<fields.length; j++)
                    add(fields[i][j]);
        }

        /**
         * Returns the Stone componet of provided coordinates
         * @param x x
         * @param y y
         * @return the Stone componet of provided coordinates
         */
        public Field getField(int x, int y){
            return fields[x][y];
        }
        
        /**
         * Returns message about choosen territories
         * @return TerritoriesChosen message
         */
        public TerritoriesChosen getTerritoriesChosenMessage(){
            TerritoriesChosen msg = new TerritoriesChosen();
            for(Field field: territoriesWhite)
                msg.addLegalPlace(new FieldInfo(BoardField.WHITE, field.y, field.x));
            for(Field field: territoriesBlack)
                msg.addLegalPlace(new FieldInfo(BoardField.BLACK, field.y, field.x));
            return msg;
        }
        
        /**
         * Updates territories list
         * @param territories message about territories
         */
        public void addTerritories(ArrayList<Point> newBlackTerritories, ArrayList<Point> newWhiteTerritories){
            territoriesBlack.clear();
            for(Point p: newBlackTerritories)
                territoriesBlack.add(getField(p.x, p.y));
            
            territoriesWhite.clear();
            for(Point p: newWhiteTerritories)
                territoriesWhite.add(getField(p.x, p.y));            
        }
        /**
         * Updates territories list
         * @param newBlackTerritories list of new black territories
         */
        public void addBlackTerritories(ArrayList<Field> newBlackTerritories){
            territoriesBlack=newBlackTerritories;
        }
        
        /**
         * Updates territories list
         * @param newWhiteTerritories list of new black territories
         */
        public void addWhiteTerritories(ArrayList<Field> newWhiteTerritories){
            territoriesWhite=newWhiteTerritories;
        }
        
        /** clears the lists of territories */
        public void clearTerritories(){
            territoriesBlack.clear();
            territoriesWhite.clear();
        }
        
        /** adding opponents territories */
        public void addOpponentsTerritories(ArrayList<FieldInfo> list){
            for(FieldInfo fieldInfo: list){
                if(fieldInfo.getState() == BoardField.BLACK)
                    opponentsTerritoriesBlack.add(getField(fieldInfo.getX(), fieldInfo.getY()));
                else if (fieldInfo.getState() == BoardField.WHITE)
                    opponentsTerritoriesWhite.add(getField(fieldInfo.getX(), fieldInfo.getY()));                   
            }
        }
        
        /** clear the lists of territories choosen by the opponent */
        public void clearOpponentsTerritories(){
            opponentsTerritoriesWhite.clear();
            opponentsTerritoriesBlack.clear();
        }
        
        /**
         * updates the list of legal places on board
         * @param msg ReturnLegalPlaces message
         */
        public void addLegalPlaces(ReturnLegalPlaces msg){
            if(legalPlaces!=null){
                legalPlaces.clear();
                for(FieldInfo fieldInfo: msg.getLegalPlaces())
                    legalPlaces.add(fieldInfo.getX()+":"+fieldInfo.getY());
            }
        }
        
        /**
         * updates the board
         * @param board board
         */
        private void updateBoard(BoardField[][] board) {
            this.board=board;
            repaint();
        }
        
        /**
        * Paints the board
        * @param g 
        */
        @Override
        public void paint(Graphics g){
            super.paint(g);
            g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);

            for(int i=0; i<fields.length; i++)
                for(int j=0; j<fields.length; j++){
                    if(board!=null)
                        
                    if(board[i][j] == BoardField.BLACK)
                        g.drawImage(blackStone, fields[j][i].getX()+1, fields[j][i].getY(), fields[j][i].getWidth(), fields[j][i].getHeight(), null);
                    else if( board[i][j] == BoardField.WHITE )
                        g.drawImage(whiteStone, fields[j][i].getX()+1, fields[j][i].getY(), fields[j][i].getWidth(), fields[j][i].getHeight(), null);
                    switch(getBoardGUIstate()){
                        case TERRITORIES:
                            if( territoriesWhite.contains(fields[i][j]) ){
                                g.setColor(Color.WHITE);
                                g.fillRect(fields[j][i].getX() + fields[j][i].getWidth()/4, fields[j][i].getY() + fields[j][i].getHeight()/4, fields[j][i].getWidth()/2, fields[j][i].getHeight()/2);
                            }
                            else if( territoriesBlack.contains(fields[i][j]) ){
                                g.setColor(Color.BLACK);
                                g.fillRect(fields[j][i].getX() + fields[j][i].getWidth()/4, fields[j][i].getY() + fields[j][i].getHeight()/4, fields[j][i].getWidth()/2, fields[j][i].getHeight()/2);
                            }
                            break;
                        case OPPONENTS_TERRITORIES:
                            if( opponentsTerritoriesWhite.contains(fields[i][j]) ){
                                g.setColor(Color.WHITE);
                                g.fillRect(fields[j][i].getX() + fields[j][i].getWidth()/4, fields[j][i].getY() + fields[j][i].getHeight()/4, fields[j][i].getWidth()/2, fields[j][i].getHeight()/2);
                            }
                            else if( opponentsTerritoriesBlack.contains(fields[i][j]) ){
                                g.setColor(Color.BLACK);
                                g.fillRect(fields[j][i].getX() + fields[j][i].getWidth()/4, fields[j][i].getY() + fields[j][i].getHeight()/4, fields[j][i].getWidth()/2, fields[j][i].getHeight()/2);
                            }
                            break;
                        case CHANGE_OPPONENTS_TERRITORIES:
                            if( opponentsTerritoriesWhite.contains(fields[i][j]) ){
                                g.setColor(Color.WHITE);
                                g.fillRect(fields[j][i].getX() + fields[j][i].getWidth()/4, fields[j][i].getY() + fields[j][i].getHeight()/4, fields[j][i].getWidth()/2, fields[j][i].getHeight()/2);
                            }
                            else if( opponentsTerritoriesBlack.contains(fields[i][j]) ){
                                g.setColor(Color.BLACK);
                                g.fillRect(fields[j][i].getX() + fields[j][i].getWidth()/4, fields[j][i].getY() + fields[j][i].getHeight()/4, fields[j][i].getWidth()/2, fields[j][i].getHeight()/2);
                            }
                            break;
                    }
                }
        }
        
        /**
        * Helps to place the stones in appropriate field
        */
        private class Field extends Component {
            private final int x;
            private final int y;

            /**
            * Constructor
            * @param x x
            * @param y y
            */
            public Field(int x, int y){
                this.x=x;
                this.y=y;

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        switch (getBoardGUIstate()){
                            case PLAY:  //if in the list of legal places, change local board, request updating board, change boardGUIstate
                                if(legalPlaces!=null){
                                    System.out.println(x+":"+y);
                                    if(legalPlaces.contains(x+":"+y)){
                                        notifyMasterObserverAboutMove(new PlaceStone(black, x, y));
                                        /*if(black)
                                            board[x][y]=BoardField.BLACK;
                                        else
                                            board[x][y]=BoardField.WHITE;*/
                                        //notifyMasterObserverAboutMessage(new RequestBoard());
                                        setBoardGUIstate(BoardGUIstate.WAIT);
                                    }
                                }                             
                                break;
                            case TERRITORIES:
                                if(SwingUtilities.isLeftMouseButton(e)){    //left click adding/removing black territories                            
                                    if(territoriesWhite.contains(fields[x][y]))
                                        return;
                                    if(territoriesBlack.contains(fields[x][y]))
                                        territoriesBlack.remove(fields[x][y]);
                                    else if( board[x][y] == BoardField.EMPTY )
                                        territoriesBlack.add(fields[x][y]);
                                }
                                else if(SwingUtilities.isRightMouseButton(e)){  //right click adding/removing white territories                                   
                                    if(territoriesBlack.contains(fields[x][y]))
                                        return;
                                    if(territoriesWhite.contains(fields[x][y]))
                                        territoriesWhite.remove(fields[x][y]);
                                    else if( board[x][y] == BoardField.EMPTY )
                                        territoriesWhite.add(fields[x][y]);
                                }
                                break;
                        }
                        repaintBoard();
                    }          
                });
            }   
        }
    }

    @Override
    public void notifyMasterObserverAboutMessage(Message msg) {
        if(master!=null){
            try{
                master.updateMessage(this, msg);
            }catch(Exception ex){
                System.err.println("Error on updating message: "+ex);
                ex.printStackTrace();
                throw ex;
            }
        }else
            System.err.println("BoardGUI has no master");
    }

    @Override
    public void setMasterObserver(MoveNotifier observer) {
        this.master=observer;
    }

    @Override
    /**
     * Does nothing
     */
    public void addSlaveObserver(MoveNotifier observer) {}
    
    @Override
    /**
     * Does nothing
     */
    public void notifySlaveObserversAboutMove(Move move) {}

    @Override
    /**
     * Does nothing
     */
    public void notifySlaveObserversAboutMessage(Message msg) {}
}

