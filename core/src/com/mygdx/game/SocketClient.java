package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

import org.json.JSONArray;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by polarvenezia on 28/3/17.
 */

public class SocketClient {
    private static Socket ourInstance;
    public static String myID;
    public static JSONArray shadows;
    public static JSONArray orbs;
    public static JSONObject status;
    public static JSONArray players;
    public static JSONArray pillars;

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

    public static Socket getTestingInstance(){
        Socket socket = null;
        try {
            socket = IO.socket("http://128.199.74.49:8008");
//			    ourInstance = IO.socket("http://localhost:8008");
        }catch (Exception e){
            e.printStackTrace();
        }
        return socket;
    }

    private SocketClient() {
    }

    public static boolean isConnected(){
        return getInstance().connected();
    }
}

class ServerShadow{
    Vector2 position;
    int serverTime;
    ServerShadow(Vector2 position, int time){
        this.position = position;
        this.serverTime = time;
    }

    public int getServerTime() {
        return serverTime;
    }
}
