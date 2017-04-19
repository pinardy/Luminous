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
import java.util.concurrent.ThreadPoolExecutor;
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
        int connections = 1000;
        long totalTime = 0;

        ExecutorService executor = Executors.newCachedThreadPool();
        ArrayList<Future> futureList = new ArrayList<Future>();

        Callable r = new Callable() {
            @Override
            public Object call() throws Exception {
                Socket s = SocketClient.getTestingInstance();
                long oldTime = System.currentTimeMillis();
                s.connect();
                while (!s.connected()){Thread.yield();}
                oldTime = System.currentTimeMillis() - oldTime;
                s.disconnect();
                s = null;
                return oldTime;
            }
        };


        for(int i=0; i<connections; i++) {
            Future f = executor.submit(r);
            futureList.add(f);
        }

        for(Future<Long> f : futureList) {
            totalTime += f.get();
        }

            System.out.println("Number of connections made: " + futureList.size());
            System.out.println("Average connection time is: " + totalTime/(float)futureList.size() + "ms");

        executor.shutdownNow();


    }

    @Test
    public void testAverageConnectionTimeSequential() throws Exception {
        int connections = 100;

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
            if (success) totalTime += System.currentTimeMillis() - startTIme;
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
        System.out.println("Number of failures: " + failures);
    }

    @Test(timeout=60000)
    public void testSocketThroughput() throws Exception{
        int totalBytes = 512*1000;
        final AtomicInteger packetReceived = new AtomicInteger();
        socket.on("throughput", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                packetReceived.getAndIncrement();
            }
        });

        for(int i=1; i<256; i*=2) {
            System.out.println("\n");


            int byteSize = 128*i;
            int pipeline = totalBytes/byteSize;

            byte[] data = new byte[byteSize];

            socket.connect();
            while(!socket.connected());

            JSONObject packet = new JSONObject();
            ThroughputTask task = new ThroughputTask(pipeline, packet, socket);
            packet.put("data", data);

            task.start();
            while (!task.started);

            long start = System.currentTimeMillis();
            while (packetReceived.get() < pipeline){
                Thread.yield();
            }

            start = System.currentTimeMillis() - start;

            System.out.println("Number of packets sent  : " + packetReceived.get());
            System.out.println("Size of each packet sent: " + byteSize + " bytes");
            System.out.println("Average throughput is " + (pipeline*data.length)/start + " Kbytes/sec");

            task.interrupt();
            socket.close();
            while (socket.connected());
            packetReceived.set(0);
        }
    }

    private class ThroughputTask extends Thread{
        volatile boolean started = false;
        int pipeline;
        Socket socket;
        JSONObject toSend;
        ThroughputTask(int pipeline, JSONObject toSend, Socket socket){
            this.pipeline = pipeline;
            this.toSend = toSend;
            this.socket = socket;
        }
        @Override
        public void run() {
            for (int i = 0; i <pipeline; i++){
                if (!Thread.interrupted()) {
                    started = true;
                    socket.emit("throughput", toSend);
                }else {
                    break;
                }
            }
        }
    }
}
