package com.mygdx.game;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by polarvenezia on 28/3/17.
 */

public class SocketClient {
    private static Socket ourInstance;

    public static Socket getInstance() {
        if (ourInstance == null){
            try {
                ourInstance = IO.socket("http://128.199.74.49:8008");
//			    ourInstance = IO.socket("http://localhost:8008");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return ourInstance;
    }

    private SocketClient() {
    }
}
