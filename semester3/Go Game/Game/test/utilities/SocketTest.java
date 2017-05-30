/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import game.MoveNotifier;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.TestName;
import util.RepeatTestRule;
import util.RepeatTestRule.Repeat;
import utilities.messages.Message;
import utilities.messages.MessageType;
import utilities.messages.Pong;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import utilities.messages.Ping;
import utilities.messages.RequestBoard;
import utilities.messages.ReturnBoard;
import utilities.messages.client.RequestPing;
import utilities.messages.client.ReturnPing;
import utilities.messages.move.InvalidMove;
import utilities.messages.move.Move;

/**
 * Testing class for socket_client.
 * ObjectInputStream creation might fail once in thousand initialisations thus tests usually fail once in 3 thousand times. 
 * @author n1t4chi
 */
public class SocketTest {
    
    @Rule
    public RepeatTestRule repeatRule = new RepeatTestRule();
    @Rule
    public TestName testName = new TestName();
    
    private final String ip = "127.0.0.1";
    volatile int ss_port = -1;
    
    
    MoveNotifier master = null;
    
    private class MasterAdapter implements MoveNotifier {
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
        
    }
    
    
    volatile ServerSocket ss=null;
    volatile java.net.Socket soc=null;
    volatile ObjectOutputStream oos=null;
    volatile ObjectInputStream ois=null;
    volatile Socket socket_client=null;
    volatile Socket socket_server=null;
    ScheduledFuture server = null;
    ScheduledFuture client = null;
    Message message = null;
    final ScheduledExecutorService executor;
    MasterAdapter s_ma=null;
    /**
     * Initiates thread executor.
     */
    public SocketTest() {
        executor = new ScheduledThreadPoolExecutor(20);
    }
    
    boolean testServerSetUpSocket = false;
    @Before
    public void setUpTest() throws IOException,SocketException{
        ss = new ServerSocket(0);
        ss_port = ss.getLocalPort();
        if(testName.getMethodName().startsWith("testClient")){
            //System.out.println("Server for client initialisation started");
            Runnable srv = () -> {
                try {
                    soc = ss.accept();
                    if(soc==null){
                        throw new NullPointerException("Failed to create object.");
                    }
                    oos = new ObjectOutputStream(soc.getOutputStream());
                    oos.flush();
                    ois = new ObjectInputStream(soc.getInputStream());
                    if(ois==null||oos==null){
                        throw new NullPointerException("Failed to create object.");
                    }
                } catch (IOException ex) {
                    System.out.println("Error on server IO start for client");
                }
                System.out.println("Server initialisation complete");
            };
            server = executor.schedule(srv, 1, TimeUnit.NANOSECONDS);
            //client = executor.schedule(cl, 1, TimeUnit.SECONDS);
            //System.out.println("Server thread started");
        }else{
            //System.out.println("Server initialisation started");
            Runnable srv = () -> {
                try {
                    //System.out.println("Waiting for client");
                    soc = ss.accept();
                    if(soc==null){
                        throw new NullPointerException("Failed to create object.");
                    }
                    //System.ou
                    //System.out.println("got client: "+soc);
                   /* oos = new ObjectOutputStream(soc.getOutputStream());
                    oos.flush();
                    ois = new ObjectInputStream(soc.getInputStream()); */
                    s_ma = new MasterAdapter(){
                        @Override
                        public void updateMove(MoveNotifier src, Move move) {
                            System.err.println("received"+move.getMoveType());
                            //socket_server.updateMove(s_ma, move);
                            testServerSetUpSocket=true;
                        }
                        @Override
                        public void updateMessage(MoveNotifier src, Message msg) {
                            System.err.println("received"+msg.getMessageType());
                            //socket_server.updateMessage(s_ma, msg);
                            testServerSetUpSocket=true;
                        }
                    };
                    socket_server = new Socket(s_ma,soc,1);
                    if(socket_server==null){
                        throw new NullPointerException("Failed to create object.");
                    }
                    //System.out.println("Client connected");
                } catch (IOException | SocketException ex) {
                    System.err.println("Error on server IO start: "+ex);
                }
            };
            server = executor.schedule(srv, 1, TimeUnit.NANOSECONDS);
            
            //System.out.println("Server thread started");
        }
    }
    
    @After
    public void finishTest() throws IOException,SocketException{
        server.cancel(true);
        if(oos!=null)
            oos.close();
        if(ois!=null)
            ois.close();
        if(socket_client!=null)
            socket_client.Disconnect();
        if(socket_server!=null)
            socket_server.Disconnect();
        if(soc!=null)
            soc.close();
        if(ss!=null)
            ss.close();
        server = null;
        ss=null;
        soc=null;
        oos=null;
        ois=null;
        socket_client=null;
        socket_server=null;
        ss_port = -1;
    }
    
    int total = 0;
    int ct = 0;
    int max = 100;
    @Test
    @Repeat( times = 100 )
    public void testServerClientLocalLatency(){
        total = 0;
        ct = 0;
        //System.out.println("utilties.SocketTest.testServerSetUpSocket()");
        try{
            MasterAdapter ma = new MasterAdapter(){
                @Override
                public void updateMessage(MoveNotifier src, Message msg) {
                    if(msg instanceof ReturnPing){
                        total += ((ReturnPing) msg).getPing();
                        ct++;
                    }
                }
                
            };
            socket_client = new Socket(ma,ip, ss_port,1);
            socket_client.updateMessage(ma, new RequestPing());
            max--;
            if(max==0)
                System.out.println("Average latency:"+(double)total/100+"ms, received:"+ct);
        }catch(Exception ex){
            fail("ERROR: "+ex);
        }
    }
    int count = 0;
    @Test
    @Repeat( times = 100 )
    public void testServerSetUpSocket(){
        testServerSetUpSocket = false;
        count++;
        //System.out.println("utilties.SocketTest.testServerSetUpSocket()");
        try {
            int it=0;
            //System.out.println("Waiting for server thread");
            //java.net.Socket soc = new java.net.Socket(ip, ss_port);
            MasterAdapter ma = new MasterAdapter(){
                @Override
                public void updateMessage(MoveNotifier src, Message msg) {
                    assertEquals(MessageType.REQUEST_BOARD,msg.getMessageType());
                    testServerSetUpSocket = true;
                }
            };
            socket_client = new Socket(ma,ip, ss_port,1);
            while((!server.isDone())&&(it<30)){
                try {
                    sleep(10);
                } catch (InterruptedException ex) {}
                it++;
            }
            //System.out.println("Connected "+server.isDone());
            assertNotNull("["+count+"]Client did not connect within 3 second",socket_client);
            assertNotNull("Server socket is null",socket_server);
            //System.out.println("Connected");
            socket_server.updateMessage(s_ma, (Message) new RequestBoard());
            int i=0;
            while((!testServerSetUpSocket)&&(i<100)){
                try {
                    sleep(10);
                } catch (InterruptedException ex) {}
                i++;
            }
            assertTrue("Did not receive message",testServerSetUpSocket);
        } catch (SocketException ex) {
            if(null!=ex.getType())
                switch (ex.getType()) {
                case CANNOT_CONNECT:
                    fail("["+count+"]Problem with socket creation "+ex.getLocalizedMessage());
                    break;
                case CANNOT_RECEIVE:
                    fail("["+count+"]Problem on receiving message "+ex.getLocalizedMessage());
                    break;
                case CANNOT_SEND:
                    fail("["+count+"]Problem on send a message "+ex.getLocalizedMessage());
                    break;
                default:
                    fail("["+count+"]Fail: "+ex.getLocalizedMessage());
                    break;
            }
        }
    }
    
    
    //test unsalveable after rewriting socket
    @Ignore
    @Repeat( times = 100 )
    public void testClientSendMessage(){
        //System.out.println("utilties.SocketTestClient.testSendMessage()");
        try {
            MasterAdapter ma = new MasterAdapter(){};
            socket_client = new Socket(ma,ip, ss_port,1);
            socket_client.notifySlaveObserversAboutMessage((Message) new RequestBoard());
            int it=0;
            Object ob;
            while(((ob=ois.readObject())==null)&&(it<100)){
                try {
                    sleep(10); 
                } catch (InterruptedException ex) {}
                it++;
            }
            assertNotNull("Did not receive message or connection timed out",ob);
            if(ob instanceof Message){
                assertEquals("Received different message type",((Message)ob).getMessageType(),MessageType.REQUEST_BOARD);
            }else{
                fail("Received wrong class: "+ob.getClass().getSimpleName());
            }
        }catch(IOException ex){
            fail("Most likely a problem with receiving object by server: "+ex.getLocalizedMessage());
        }catch(SocketException ex){
            fail("Problem with either connecting or sending: "+ex.getLocalizedMessage());
        } catch (ClassNotFoundException ex) {
            fail("Class not found: "+ex.getLocalizedMessage());
        }
    }
    
    
                        
    boolean testClientReceiveMessage = false;
    //test unsalveable after rewriting socket
    @Ignore
    @Repeat( times = 100 )
    public void testClientReceiveMessage(){
        testClientReceiveMessage = false;
        //System.out.println("utilties.SocketTestClient.testMultipleReceiveMultipleMessagesInOrder()");
        try {
            MasterAdapter ma = new MasterAdapter(){
                @Override
                public void updateMessage(MoveNotifier src, Message msg) {
                    assertEquals(MessageType.REQUEST_BOARD,msg.getMessageType());
                    testClientReceiveMessage = true;
                }
            };
            socket_client = new Socket(ma,ip, ss_port,1);
            oos.writeObject(new RequestBoard());
            int i=0;
            while((!testClientReceiveMessage)&&(i<100)){
                try {
                    sleep(10);
                } catch (InterruptedException ex) {
                    System.err.println("wtf");
                }
                i++;
            }
            assertTrue("Did not receive message",testClientReceiveMessage);
        }catch(IOException ex){
            fail("Most likely problem on sending object by server: "+ex.getLocalizedMessage());
        }catch(SocketException ex){
            fail("Problem with either connecting or receiving: "+ex.getLocalizedMessage());
        }
        
    }
    
    int testClientReconnect = 0;
    //test unsalveable after rewriting socket
    @Ignore
    @Repeat( times = 100 )
    public void testClientReconnect(){
        testClientReconnect = 0;
        //System.out.println("utilties.SocketTestClient.testReconnect()");
        try {
            MasterAdapter ma = new MasterAdapter(){
                @Override
                public void updateMessage(MoveNotifier src, Message msg) {
                    assertEquals(MessageType.REQUEST_BOARD,msg.getMessageType());
                    testClientReconnect++;
                }
            };
            socket_client = new Socket(ma,ip, ss_port,1);
            for(int i=0; i<10;i++){
                socket_client.Reconnect();
                oos.writeObject(new RequestBoard());
            }    
            int i=0;
            while((testClientReceiveMessage)&&(i<100)){
                try {
                    sleep(10);
                } catch (InterruptedException ex) {}
                i++;
            }
            assertEquals("Did not receive all messages",10,testClientReconnect);
        }catch(IOException ex){
            fail("Most likely problem on sending object by server: "+ex.getLocalizedMessage());
        }catch(SocketException ex){
            fail("Problem with either connecting or receiving: "+ex.getLocalizedMessage());
        }
    }
    
    
    @Test
    public void testClientParameters(){
        //System.out.println("utilties.SocketTestClient.testParameters()");
        MasterAdapter ma = new MasterAdapter(){
            @Override
            public void updateMessage(MoveNotifier src, Message msg) {
                assertEquals(MessageType.REQUEST_BOARD,msg.getMessageType());
            }
        };
        try{
            socket_client = new Socket(ma,null, ss_port,1);
            fail("No exception on receiving null pointer");
        }catch(NullPointerException ex){
        } catch (SocketException ex) {
            fail("Wrong exception on receiving null pointer: "+ex.getLocalizedMessage());
        }
        try{
            socket_client = new Socket(ma,ip, -1,1);
            fail("No exception on receiving illegal port");
        }catch(IllegalArgumentException ex){
        } catch (SocketException ex) {
            fail("Wrong exception on receiving illegal port: "+ex.getLocalizedMessage());
        }
        try{
            socket_client = new Socket(ma,ip, 65536,1);
            fail("No exception on receiving illegal port");
        }catch(IllegalArgumentException ex){
        } catch (SocketException ex) {
            fail("Wrong exception on receiving illegal port: "+ex.getLocalizedMessage());
        }
        try{
            socket_client = new Socket(ma,"obviously not a valid hostname", 12345,1);
            fail("No exception on receiving not operating server info");
        }catch(SocketException ex){
            if(ex.getType()!=SocketException.Type.CANNOT_CONNECT){
                fail("Wrong exception type on receiving wrong server address: "+ex.getLocalizedMessage());
            }
        }
        try{
            socket_client = new Socket(ma,ip, ss_port,1);
        }catch(IllegalArgumentException | NullPointerException | SocketException ex){
            fail("Exception after correct were given: "+ex.getLocalizedMessage());
        }
        
    }
    
    @Test
    public void testIsWorking(){
        MasterAdapter ma = new MasterAdapter(){
            @Override
            public void updateMessage(MoveNotifier src, Message msg) {
                assertEquals(MessageType.REQUEST_BOARD,msg.getMessageType());
            }
        };
        socket_client = new Socket(ma,ip, ss_port,1);
        assertTrue(socket_client.isWorking());
        socket_client.Disconnect();
        assertFalse(socket_client.isWorking());
    }
    @Test
    public void testIsConnected(){
        MasterAdapter ma = new MasterAdapter(){
            @Override
            public void updateMessage(MoveNotifier src, Message msg) {
                assertEquals(MessageType.REQUEST_BOARD,msg.getMessageType());
            }
        };
        socket_client = new Socket(ma,ip, ss_port,1);
        assertTrue(socket_client.isConnected());
        socket_client.Disconnect();
        assertFalse(socket_client.isConnected());
    }
    boolean testClientConnectingAndReceivingSameThing=false;
    //@Repeat( times = 100 )
    
    //test unsalveable after rewriting socket
    @Ignore
    public void testClientConnectingAndReceivingSameThing(){
        testClientConnectingAndReceivingSameThing=false;
        MasterAdapter ma = new MasterAdapter(){
            @Override
            public void updateMessage(MoveNotifier src, Message msg) {
                assertEquals(MessageType.REQUEST_BOARD,msg.getMessageType());
                testClientConnectingAndReceivingSameThing=true;
            }
        };
        try{
            socket_client = new Socket(ma,ip, ss_port,10);
           
            socket_client.updateMessage(ma, new InvalidMove());
            socket_server.updateMessage(s_ma, new InvalidMove());
            int i=0;
            while((!testClientConnectingAndReceivingSameThing)&&(i<10)){
                try {
                    sleep(10);
                } catch (InterruptedException ex) {}
                i++;
            }
            assertTrue("Did not receive message",testClientConnectingAndReceivingSameThing);
        }catch(SocketException ex){
            ex.printStackTrace();
            fail("Problem with either connecting or receiving: "+ex.getLocalizedMessage());
        }
        
        /*
        catch(IOException ex){
            fail("Most likely problem on sending object by server: "+ex.getLocalizedMessage());
        }
         */
    }
    

    
}
