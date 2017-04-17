package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
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
    public void testAverageConnectionTime() throws Exception {
        int connections = 1000;
        ExecutorService executor = Executors.newCachedThreadPool();
        ArrayList<Future> futureList = new ArrayList<Future>();

        Callable r = new Callable() {
            @Override
            public Object call() throws Exception {
                Socket s = SocketClient.getInstance();
                long oldTime = System.currentTimeMillis();
                s.connect();
                oldTime = System.currentTimeMillis() - oldTime;
                s.disconnect();

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
    public void testSocketThroughput() {
       
    }
}
