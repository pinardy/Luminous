package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BooleanArray;
import com.mygdx.game.SocketClient;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.*;
import java.lang.Object;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by kennethlimcp on 17/Apr/2017.
 */

public class SocketTest {
    private Socket socket;

    @Before
    public void setUp() throws Exception {
        socket = SocketClient.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        socket.disconnect();
        socket = null;
    }

    @Test(timeout=5000)
    public void testConnection() throws Exception {
        assertNotNull(socket);
        assertFalse(socket.connected());

        socket.connect();
        while(!socket.connected());
        assertTrue(socket.connected());
    }

    @Test
    public void testAverageConnectionTimeParallel() throws Exception {
        int connections = 5;
        ExecutorService executor = Executors.newCachedThreadPool();
        ArrayList<Future> futureList = new ArrayList<Future>();

        Callable r = new Callable() {
            @Override
            public Object call() throws Exception {
                Socket s = SocketClient.getInstance();
                long oldTime = System.currentTimeMillis();
                s.connect();
                while (!s.connected()){Thread.yield();}
                oldTime = System.currentTimeMillis() - oldTime;
                s.disconnect();
                System.out.println(oldTime);
                return oldTime;
            }
        };

        for(int i=0; i<connections; i++) {
            Future f = executor.submit(r);
            futureList.add(f);
        }

        long totalTime = 0;

        for(Future<Long> f : futureList) {
            totalTime += f.get();
        }
        executor.shutdownNow();

        System.out.println("Average connection time is: " + totalTime/(float)futureList.size() + "ms");
        System.out.println("Number of connections made: " + futureList.size());
    }

    @Test
    public void testAverageConnectionTimeSequantial() throws Exception {
        int connections = 10;

        long totalTime = 0;
        int failures = 0;
        long initializationTime = 0;
        for (int i = 0; i < connections; i++){
            int timeout = 5000;
            boolean success = true;
            long startTIme = System.currentTimeMillis();
            socket.connect();
            while (!socket.connected()){
                if (System.currentTimeMillis() - startTIme > timeout){
                    success = false;
                    failures++;
                    break;
                }
            }
            System.out.println(System.currentTimeMillis() - startTIme);
            if (success && i > 0) totalTime += System.currentTimeMillis() - startTIme;
            else initializationTime = System.currentTimeMillis() - startTIme;
            socket.disconnect();
            try {
                // letting socket disconnect
                Thread.sleep(1000);
            }catch (Exception e){
                System.out.println("interrupted");
            }
        }
        System.out.println("Initial connection time is: "+ initializationTime);
        if (connections > 1) System.out.println("Average connection time is: " + totalTime/(float)(connections - failures -1) + "ms");
        System.out.println("Number of connections made: " + connections);
        System.out.println("NUmber of failures: " + failures);
    }

    @Test
    public void testSocketThroughput() throws Exception{
        socket.connect();
        int pipeline = 10000;
        byte[] data = new byte[512];
        final AtomicInteger packetReceived = new AtomicInteger();
        JSONObject packet = new JSONObject();
        packet.put("data", data);
        for (int i = 0; i <pipeline; i++){
            socket.emit("throughput", packet);
        }
        socket.on("throughput", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                packetReceived.getAndAdd(1);
            }
        });
        long start = System.currentTimeMillis();
        final int sec = 1000;
        while (System.currentTimeMillis() - start < sec*10){
        }
        System.out.println("Average throughput with packet size "+data.length+" is: "+packetReceived.get()/10.0);
        socket.close();
    }
}
