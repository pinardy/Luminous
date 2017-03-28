package com.mygdx.game.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.ShadowManagement;
import com.mygdx.game.SocketClient;
import com.mygdx.game.Sprites.Orb;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.Tools.B2WorldCreator;
import com.mygdx.game.Tools.Controller;
import com.mygdx.game.Tools.WorldContactListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Pin on 04-Feb-17.
 */

public class PlayScreen implements Screen {

    private Socket socket;
    private String myID;
    HashMap<String, Vector2> clientPrediction;
    private HashMap<String, Player> players;
    private boolean keyPressed;
    private boolean multiplayer = false;

    private MultiplayerGame game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;

    private Hud hud;


    // Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    // Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr; // graphical representation of fixtures in box2d world
    private B2WorldCreator creator;

    // Sprites
    public static Player player;
    private Orb orb;

    // List of Orbs
    public static ArrayList<Orb> listOfOrbs = new ArrayList<Orb>();

    // Music
    private Music music;

    // Controller
    public static Controller controller;


    private ShadowManagement sm = null;

    public PlayScreen(MultiplayerGame game) {
        players = new HashMap<String, Player>();
        clientPrediction = new HashMap<String, Vector2>();
        keyPressed = false;
        multiplayer = true;

        this.game = game;

        // create cam to follow player throughout world
        gameCam = new OrthographicCamera();

        // create a FitViewPort to maintain virtual aspect ratio
        gamePort = new FitViewport(MultiplayerGame.V_WIDTH, MultiplayerGame.V_HEIGHT, gameCam);

        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map_easy.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);

        // initially set our gamcam to be centered correctly at the start of of map
        gameCam.position.set((gamePort.getWorldWidth() / 2), (gamePort.getWorldHeight() / 2), 0);

        // World(Vector2 gravity, boolean doSleep)
        world = new World(new Vector2(0, 0), true);

        // allows for debug lines of our box2d world
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);

        // create a player in our game world
        player = new Player(world);

        // create an orb in our game world
        orb = new Orb(this, .32f, .32f);

        // play music
        music = MultiplayerGame.manager.get("audio/music/dungeon_peace.mp3", Music.class);
        music.setLooping(true);
        music.play();

        // controller
        controller = new Controller();

        world.setContactListener(new WorldContactListener());

        sm = new ShadowManagement(game);
        sm.start();

        if (multiplayer) connectSocket();
    }

    public void update(float dt) {
        // handle input first
        handleInput(dt);
        world.step(1 / 60f, 6, 2);

        player.update(dt);
        orb.update(dt);

        for (int i = 0; i < listOfOrbs.size(); i++){
            listOfOrbs.get(i).update(dt);
        }

        sm.update(dt);

        // track movement of player
        gameCam.position.x = player.b2body.getPosition().x;
        gameCam.position.y = player.b2body.getPosition().y;

        // update camera with correct coordinates after changes
        gameCam.update();

        // tells our renderer to draw only what our camera can see in the game world
        renderer.setView(gameCam);
    }


    public void handleInput(float dt) {

        // Keyboard controls
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        // if player is NOT moving, velocity is set to 0
        if (!(up | down | left | right)){
            player.b2body.setLinearVelocity(0, 0);
        }else {
            keyPressed = true;
        }
        if (up) {
//            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            player.b2body.setLinearVelocity(0, 100);
        }
        if (down) {
//            player.b2body.applyLinearImpulse(new Vector2(0, -4f), player.b2body.getWorldCenter(), true);
            player.b2body.setLinearVelocity(0, -100);
        }
        if (left) {
//            player.b2body.applyLinearImpulse(new Vector2(-4f, 0), player.b2body.getWorldCenter(), true);
            player.b2body.setLinearVelocity(-100, 0);
        }
        if (right) {
//            player.b2body.applyLinearImpulse(new Vector2(4f, 0), player.b2body.getWorldCenter(), true);
            player.b2body.setLinearVelocity(100, 0);
        }


        // Virtual gamepad
        boolean padUp = controller.isUpPressed();
        boolean padDown = controller.isDownPressed();
        boolean padLeft = controller.isLeftPressed();
        boolean padRight = controller.isRightPressed();

        if(padUp) {
//            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
            player.b2body.setLinearVelocity(0, 100);
        }
        if(padDown) {
//            player.b2body.applyLinearImpulse(new Vector2(0, -4f), player.b2body.getWorldCenter(), true);
            player.b2body.setLinearVelocity(0, -100);
        }
        if(padLeft) {
//            player.b2body.applyLinearImpulse(new Vector2(-4f, 0), player.b2body.getWorldCenter(), true);
            player.b2body.setLinearVelocity(-100, 0);
        }
        if(padRight) {
//            player.b2body.applyLinearImpulse(new Vector2(4f, 0), player.b2body.getWorldCenter(), true);
            player.b2body.setLinearVelocity(100, 0);
        }
        
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float dt) {
        if (multiplayer) updateServer(dt);
        update(dt);

        // clear game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1); // colour, alpha
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render game map
        renderer.render();

        // render our Box2DDebugLines
        b2dr.render(world, gameCam.combined);

        // render our controller
//        if (Gdx.app.getType() == Application.ApplicationType.Android)
        controller.draw();

        // tell our game batch to recognise where the gameCam is and render what the camera can see
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        hud.stage.draw();
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        controller.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }


    public void updateMyPosition(String idAction, float x, float y){
        Vector2 predictedPos = clientPrediction.get(idAction);
        if (predictedPos == null){
            player.b2body.setTransform(x, y, player.b2body.getAngle());
        }else if (floatEquals(predictedPos.x, x) && floatEquals(predictedPos.y, y)) {
            Gdx.app.log("Socket Prediction", "correct prediction");
            clientPrediction.remove(idAction);
        }
        else {
            player.b2body.setTransform(x, y, 0);
            clientPrediction.remove(idAction);
        }
    }

    // Update server when player moves
    public void updateServer(float dt){
        if (player != null && keyPressed){
            keyPressed = false;
			String actionID = ""+System.currentTimeMillis();
            Vector2 position = new Vector2(player.b2body.getPosition());
			clientPrediction.put(actionID, position);
			JSONObject object = new JSONObject();
			try {
                object.put("x", position.x);
                object.put("y", position.y);
				socket.emit("playerMoved", object);
			}catch (JSONException e){
				Gdx.app.log("SocketIO", "Error sending message");
			}
        }
    }

    // Try establishing the TCP connection between the player and the server.
    public void connectSocket(){
        try {
            socket = SocketClient.getInstance();
            socket.connect();
            configSocketEvents();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean floatEquals(float a, float b){
        return (Math.abs(a-b) < 0.001);
    }

    // Configure socket events after TCP connection has been established.
    public void configSocketEvents(){
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected");
            }
        });

        socket.on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    myID = data.getString("id");
                    Gdx.app.log("SocketIO", "My ID: " + myID);
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error getting id");
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    players.put(id, new Player(world));
                    Gdx.app.log("SocketIO", "New player has id: " + id);
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error getting id");
                }

            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    players.remove(id);
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error player disconnected");
                }

            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String id = data.getString("id");
                    Double x = data.getDouble("x");
                    Double y = data.getDouble("y");
                    if (players.get(id) != null){
                        players.get(id).b2body.setTransform(x.floatValue(), y.floatValue(),players.get(id).b2body.getAngle());
                    }
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error getting id");
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray onlinePlayers = (JSONArray) args[0];
                try {
                    for (int i = 0; i < onlinePlayers.length(); i++){
                        Player coopPlayer = new Player(world);
                        Vector2 position = new Vector2();
                        position.x = ((Double) onlinePlayers.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double) onlinePlayers.getJSONObject(i).getDouble("y")).floatValue();
                        coopPlayer.b2body.setTransform(position.x, position.y, coopPlayer.b2body.getAngle());
                        players.put(onlinePlayers.getJSONObject(i).getString("id"), coopPlayer);
                    }
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error getting id");
                }

            }
        });
    }
}
