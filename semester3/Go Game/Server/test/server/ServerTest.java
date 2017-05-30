/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author n1t4chi
 */
public class ServerTest {
    private final static int PORT = 6666;
    private final static int VAL = 100;
    public ServerTest() {
    }

    Server instance = null;
    @After
    public void after(){
        if(instance!=null)
            try {
                instance.dispose();
            } catch (Exception ex) {}
    }
    public Socket testConnect() throws IOException{
        Socket soc = new Socket("localhost", PORT);
        assertEquals(VAL, soc.getInputStream().read()); 
        return soc;
    }
    


    /**
     * Test of init method, of class Server.
     */
    @Test
    public void testInit() throws Exception {
        System.out.println("init");
        instance = new ServerImpl();
        instance.init();
        try{
            testConnect();
        }catch(IOException ex){
            fail("Cannot connect after init:"+ex.getLocalizedMessage());
        }
    }

    /**
     * Test of restart method, of class Server.
     */
    @Test
    public void testRestart() throws Exception {
        System.out.println("restart");
        instance = new ServerImpl();
        instance.init();
        Socket soc = null;
        try{
            soc = testConnect();
        }catch(IOException ex){
            fail("Cannot connect after init:"+ex.getLocalizedMessage());
        }
        assertNotNull(soc);
        try{
            instance.restart();
        }catch(IOException ex){
            fail("Dispose threw an exception"+ex.getLocalizedMessage());
        }
        
        try{
            instance.interpret("write", "to","client");
            assertEquals(0,soc.getInputStream().available());
        }catch(IOException ex){}
        
        try{
            instance.interpret("write", "to","client");
            testConnect();
        }catch(IOException ex){
            fail("Cannot connect after restart:"+ex.getLocalizedMessage());
        }
    }

    /**
     * Test of dispose method, of class Server.
     */
    @Test
    public void testDispose() throws Exception {
        System.out.println("dispose");
        instance = new ServerImpl();
        instance.init();
        Socket soc = null;
        try{
            soc = testConnect();
        }catch(IOException ex){
            fail("Cannot connect after init:"+ex.getLocalizedMessage());
        }
        assertNotNull(soc);
        try{
            instance.dispose();
        }catch(IOException ex){
            fail("Dispose threw an exception"+ex.getLocalizedMessage());
        }
        
        try{
            instance.interpret("write", "to","client");
            assertEquals(0,soc.getInputStream().available());
        }catch(IOException ex){}
        
        try{
            testConnect();
            fail("Can connect after Dispose()");
        }catch(IOException ex){}
        
    }


    public class ServerImpl extends Server {
        ServerSocket soc;
        public ServerImpl() {
            super("sys", "err");
            
        }
        
        @Override
        public boolean isWorking() {
            return soc.isBound();
        }

        @Override
        public void initSubclass() throws Exception {
            soc = new ServerSocket(PORT);
            interpret("test");
        }

        @Override
        public void restartSubclass() throws Exception {}

        @Override
        public void disposeSubclass() throws Exception {
            if(os!=null)
                os.close();
            if(s!=null)
                s.close();
            if(soc!=null)
                soc.close();
            soc = null;
            s = null;
            os = null;
        }

        @Override
        public void interpret(String command, String... parameters) {
            try{
                if(os!=null){
                    os.write(VAL);
                }
            }catch(Exception ex){
                fail("Exception while writing to host:"+ex);
            }
        }
        Socket s=null;
        OutputStream os=null;
        @Override
        public void interpret(String input) {    
            if(soc!=null)
                Executors.newSingleThreadExecutor().execute(() -> { 
                    try{
                         s = soc.accept();
                         os = s.getOutputStream();
                         interpret("test","value");
                    }catch(Exception ex){
                        fail("Exception while accepting host connection:"+ex);
                    }
                });
        }
    }
    
}
