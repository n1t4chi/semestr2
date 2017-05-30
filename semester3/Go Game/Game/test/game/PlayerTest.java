/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.playerstate.PlayerState;
import game.playerstate.connection.PlayerConnectionState;
import game.playerstate.game.PlayerGameState;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import utilities.messages.Message;
import utilities.messages.move.ChooseTerritories;
import utilities.messages.move.InvalidMove;
import utilities.messages.move.Lose;
import utilities.messages.move.MakeMove;
import utilities.messages.move.Move;
import utilities.messages.move.Pass;
import utilities.messages.move.PlaceStone;
import utilities.messages.move.ReturnToStonePlacing;
import utilities.messages.move.Surrender;
import utilities.messages.move.TerritoriesAgree;
import utilities.messages.move.TerritoriesChosen;
import utilities.messages.move.TerritoriesDisagree;
import utilities.messages.move.ValidMove;
import utilities.messages.move.Win;

/**
 * Longest test ever as in amount of lines
 * @author n1t4chi
 */
public class PlayerTest {
    
    public PlayerTest() {
    }
    MoveNotifier m = new MoveNotifier() {
        @Override
        public void setMasterObserver(MoveNotifier observer) {}
        @Override
        public void addSlaveObserver(MoveNotifier observer) {}
        @Override
        public void notifyMasterObserverAboutMove(Move move) {}
        @Override
        public void notifySlaveObserversAboutMove(Move move) {}
        @Override
        public void notifyMasterObserverAboutMessage(Message msg) {}
        @Override
        public void notifySlaveObserversAboutMessage(Message msg) {}
        @Override
        public void updateMove(MoveNotifier src, Move move) {}
        @Override
        public void updateMessage(MoveNotifier src, Message msg) {}
    };
    MoveNotifier s = new MoveNotifier() {
        @Override
        public void setMasterObserver(MoveNotifier observer) {}
        @Override
        public void addSlaveObserver(MoveNotifier observer) {}
        @Override
        public void notifyMasterObserverAboutMove(Move move) {}
        @Override
        public void notifySlaveObserversAboutMove(Move move) {}
        @Override
        public void notifyMasterObserverAboutMessage(Message msg) {}
        @Override
        public void notifySlaveObserversAboutMessage(Message msg) {}
        @Override
        public void updateMove(MoveNotifier src, Move move) {}
        @Override
        public void updateMessage(MoveNotifier src, Message msg) {}
    };



    @Test
    public void testUpdateMove_AGREE() {
        Player i= new PlayerImpl();
        PlayerGameState.GameState gs = PlayerGameState.GameState.AGREE;
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ChooseTerritories());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new InvalidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Lose());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new MakeMove());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Pass());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new PlaceStone(true,0,0));
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ReturnToStonePlacing());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Surrender());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesAgree());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesChosen());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesDisagree());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ValidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Win());
        assertTrue(subclass_receives_message);
        
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ChooseTerritories());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new InvalidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Lose());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new MakeMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Pass());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new PlaceStone(true,0,0));
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ReturnToStonePlacing());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Surrender());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesAgree());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesChosen());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesDisagree());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ValidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Win());
        assertFalse(master_receives_message);
    }
    @Test
    public void testUpdateMove_CHOOSE() {
        Player i= new PlayerImpl();
        PlayerGameState.GameState gs = PlayerGameState.GameState.CHOOSE;
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ChooseTerritories());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new InvalidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Lose());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new MakeMove());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Pass());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new PlaceStone(true,0,0));
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ReturnToStonePlacing());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Surrender());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesAgree());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesChosen());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesDisagree());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ValidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Win());
        assertTrue(subclass_receives_message);
        
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ChooseTerritories());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new InvalidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Lose());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new MakeMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Pass());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new PlaceStone(true,0,0));
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ReturnToStonePlacing());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Surrender());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesAgree());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesChosen());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesDisagree());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ValidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Win());
        assertFalse(master_receives_message);
    }
    @Ignore
    public void testUpdateMove_DISAGREE() {
        Player i= new PlayerImpl();
        PlayerGameState.GameState gs = PlayerGameState.GameState.DISAGREE;
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ChooseTerritories());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new InvalidMove());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Lose());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new MakeMove());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Pass());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new PlaceStone(true,0,0));
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ReturnToStonePlacing());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Surrender());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesAgree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesChosen());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesDisagree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ValidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Win());
        assertTrue(subclass_receives_message);
        
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ChooseTerritories());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new InvalidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Lose());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new MakeMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Pass());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new PlaceStone(true,0,0));
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ReturnToStonePlacing());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Surrender());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesAgree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesChosen());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesDisagree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ValidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Win());
        assertFalse(master_receives_message);
    }
    @Test
    public void testUpdateMove_LOST() {
        Player i= new PlayerImpl();
        PlayerGameState.GameState gs = PlayerGameState.GameState.LOST;
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ChooseTerritories());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new InvalidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Lose());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new MakeMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Pass());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new PlaceStone(true,0,0));
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ReturnToStonePlacing());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Surrender());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesAgree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesChosen());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesDisagree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ValidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Win());
        assertFalse(subclass_receives_message);
        
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ChooseTerritories());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new InvalidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Lose());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new MakeMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Pass());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new PlaceStone(true,0,0));
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ReturnToStonePlacing());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Surrender());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesAgree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesChosen());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesDisagree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ValidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Win());
        assertFalse(master_receives_message);
    }
    @Test
    public void testUpdateMove_PASS() {
        Player i= new PlayerImpl();
        PlayerGameState.GameState gs = PlayerGameState.GameState.PASS;
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ChooseTerritories());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new InvalidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Lose());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new MakeMove());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Pass());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new PlaceStone(true,0,0));
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ReturnToStonePlacing());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Surrender());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesAgree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesChosen());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesDisagree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ValidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Win());
        assertTrue(subclass_receives_message);
        
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ChooseTerritories());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new InvalidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Lose());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new MakeMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Pass());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new PlaceStone(true,0,0));
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ReturnToStonePlacing());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Surrender());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesAgree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesChosen());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesDisagree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ValidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Win());
        assertFalse(master_receives_message);
    }
    @Test
    public void testUpdateMove_PLACE() {
        Player i= new PlayerImpl();
        PlayerGameState.GameState gs = PlayerGameState.GameState.PLACE;
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ChooseTerritories());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new InvalidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Lose());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new MakeMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Pass());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new PlaceStone(true,0,0));
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ReturnToStonePlacing());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Surrender());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesAgree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesChosen());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesDisagree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ValidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Win());
        assertTrue(subclass_receives_message);
        
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ChooseTerritories());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new InvalidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Lose());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new MakeMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Pass());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new PlaceStone(true,0,0));
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ReturnToStonePlacing());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Surrender());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesAgree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesChosen());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesDisagree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ValidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Win());
        assertFalse(master_receives_message);
    }
    @Ignore
    public void testUpdateMove_STOPPED() {
        Player i= new PlayerImpl();
        PlayerGameState.GameState gs = PlayerGameState.GameState.STOPPED;
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ChooseTerritories());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new InvalidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Lose());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new MakeMove());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Pass());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new PlaceStone(true,0,0));
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ReturnToStonePlacing());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Surrender());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesAgree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesChosen());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesDisagree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ValidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Win());
        assertTrue(subclass_receives_message);
        
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ChooseTerritories());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new InvalidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Lose());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new MakeMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Pass());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new PlaceStone(true,0,0));
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ReturnToStonePlacing());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Surrender());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesAgree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesChosen());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesDisagree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ValidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Win());
        assertFalse(master_receives_message);
    }
    @Test
    public void testUpdateMove_WAIT() {
        Player i= new PlayerImpl();
        PlayerGameState.GameState gs = PlayerGameState.GameState.WAIT;
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ChooseTerritories());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new InvalidMove());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Lose());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new MakeMove());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Pass());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new PlaceStone(true,0,0));
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ReturnToStonePlacing());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Surrender());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesAgree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesChosen());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesDisagree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ValidMove());
        assertTrue(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Win());
        assertTrue(subclass_receives_message);
        
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ChooseTerritories());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new InvalidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Lose());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new MakeMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Pass());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new PlaceStone(true,0,0));
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ReturnToStonePlacing());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Surrender());
        assertTrue(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesAgree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesChosen());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesDisagree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ValidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Win());
        assertFalse(master_receives_message);
    }
    @Test
    public void testUpdateMove_WON() {
        Player i= new PlayerImpl();
        PlayerGameState.GameState gs = PlayerGameState.GameState.WON;
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ChooseTerritories());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new InvalidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Lose());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new MakeMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Pass());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new PlaceStone(true,0,0));
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ReturnToStonePlacing());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Surrender());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesAgree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesChosen());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new TerritoriesDisagree());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new ValidMove());
        assertFalse(subclass_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(m,new Win());
        assertFalse(subclass_receives_message);
        
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ChooseTerritories());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new InvalidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Lose());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new MakeMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Pass());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new PlaceStone(true,0,0));
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ReturnToStonePlacing());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Surrender());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesAgree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesChosen());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new TerritoriesDisagree());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new ValidMove());
        assertFalse(master_receives_message);
        
        i.playerState.forceSetGameState(gs);
        i.updateMove(s,new Win());
        assertFalse(master_receives_message);
    }


    boolean subclass_receives_message = false;
    boolean master_receives_message = false;
    
    public class PlayerImpl extends Player {

        @Override
        protected MoveNotifier getSlave() {
            return s;
        }
        
        
        
        public PlayerImpl() {
            super(m, "",new PlayerState(PlayerState.PlayerType.AI));
        }

        @Override
        public void updateMove(MoveNotifier src, Move move) {
            subclass_receives_message = false;
            master_receives_message = false;
            super.updateMove(src, move); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void notifyMasterObserverAboutMove(Move move) {
            master_receives_message = true;
        }

        
        
        @Override
        public void updateMove_Subclass(MoveNotifier src, Move move) {
            subclass_receives_message = true;
        }

        @Override
        public void notifySlaveObserversAboutMove(Move move) {}

        @Override
        public void notifySlaveObserversAboutMessage(Message msg) {}

        @Override
        public void updateMessage(MoveNotifier src, Message msg) {}
    }
    
}
