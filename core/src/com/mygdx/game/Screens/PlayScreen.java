package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Scenes.SpriteSheet;
import com.mygdx.game.ShadowManagement;
import com.mygdx.game.SocketClient;
import com.mygdx.game.Sprites.Orb;
import com.mygdx.game.Sprites.Pillar;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.Sprites.Shadow;
import com.mygdx.game.Tools.B2WorldCreator;
import com.mygdx.game.Tools.Controller;
import com.mygdx.game.Tools.WorldContactListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/** PlayScreen is the container for most of the game.
 * Graphics are rendered and displayed on the PlayScreen which the user sees
 */

public class PlayScreen implements Screen {

    // multi-player
    public static final String ID_PLAYER = "id";
    public static final String ID_ORB = "orbID";
    public static final String ID_PILLAR = "pillarID";
    private Socket socket;
    private String myID;
    HashMap<String, Vector2> clientPrediction;
    private HashMap<String, Player> players;
    private HashMap<String, LinkedList<Vector2>> playerActions;
    private boolean keyPressed;
    private boolean multiplayer;

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
    private TextureAtlas atlas;

    // List of Orbs
    public static ArrayList<Orb> listOfOrbs = new ArrayList<Orb>();

    private ArrayList<Orb> getListOfOrbs() {
        return listOfOrbs;
    }

    public Orb getOrbFromList(int id) {
        return listOfOrbs.get(id);
    }

    private void addToListOfOrbs(Orb o) {
        listOfOrbs.add(o);
    }

    // Music
    private Music music;

    // Controller
    public static Controller controller;

    // distance between shadow and player
    private float xDistance;
    private float yDistance;

    private ShadowManagement sm = null;

    public PlayScreen(MultiplayerGame game, boolean multiplayer) {
        players = new HashMap<String, Player>();
        playerActions = new HashMap<String, LinkedList<Vector2>>();
        clientPrediction = new HashMap<String, Vector2>();
        keyPressed = false;
        this.multiplayer = multiplayer;
        WorldContactListener.multiplayer = multiplayer;
        atlas = new TextureAtlas("shadowman.atlas");

        this.game = game;

        // create cam to follow player throughout world
        gameCam = new OrthographicCamera();

        // create a FitViewPort to maintain virtual aspect ratio
        gamePort = new FitViewport(MultiplayerGame.V_WIDTH, MultiplayerGame.V_HEIGHT, gameCam);

        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map_easy_edited.tmx"); // game world is created in a tmx file
        renderer = new OrthogonalTiledMapRenderer(map);

        // initially set our gamcam to be centered correctly at the start of of map
        gameCam.position.set((gamePort.getWorldWidth() / 2), (gamePort.getWorldHeight() / 2), 0);

        // World(Vector2 gravity, boolean doSleep)
        world = new World(new Vector2(0, 0), true);

        // allows for debug lines of our box2d world
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);

        if (!multiplayer) {
            // create a player in our game world
            player = new Player(world);

            // create an orb in our game world
            orb = new Orb(this, .32f, .32f);
            listOfOrbs.add(orb);
        }

        // controller
        controller = new Controller();

        world.setContactListener(new WorldContactListener());

        sm = new ShadowManagement(game, multiplayer);
        sm.calculateShadowStartPosition();
        sm.start();

        // multi-player initialization
        if (multiplayer) connectSocket();
    }

    public void update(float dt) {
        // handle input first
        handleInput(dt);
        world.step(1 / 60f, 6, 2);

        player.update(dt);
//        orb.update(dt);
        hud.update(dt);

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
        keyboardMovement();
        androidMovement();
    }

    private void androidMovement() {
        // Virtual gamepad
        boolean padUp = controller.isUpPressed();
        boolean padDown = controller.isDownPressed();
        boolean padLeft = controller.isLeftPressed();
        boolean padRight = controller.isRightPressed();

        // if a directional button is pressed, keyPressed is set to true
        if (padUp | padDown | padLeft | padRight){
            keyPressed = true;
        }
        // directional movements
        if(padUp) {
            player.b2body.setLinearVelocity(0, 100);
        }
        if(padDown) {
            player.b2body.setLinearVelocity(0, -100);
        }
        if(padLeft) {
            player.b2body.setLinearVelocity(-100, 0);
        }
        if(padRight) {
            player.b2body.setLinearVelocity(100, 0);
        }
    }

    private void keyboardMovement(){
        // Desktop keyboard controls
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

        // directional movements
        if(up) {
            player.b2body.setLinearVelocity(0, 100);
        }
        if(down) {
            player.b2body.setLinearVelocity(0, -100);
        }
        if(left) {
            player.b2body.setLinearVelocity(-100, 0);
        }
        if(right) {
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
        Gdx.gl.glClearColor(0, 0, 0, 0); // colour, alpha
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setView(gameCam);

        //Full visibility
        if (WorldContactListener.fullVisibility==1){
            renderer.render();
//            b2dr.render(world, gameCam.combined); //render fixture outlines

            // tell our game batch to recognise where the gameCam is and render what the camera can see
            //render shadows
            game.batch.setProjectionMatrix(gameCam.combined);
            game.batch.begin();
            if(sm.getShadows()!=null){
                sm.getShadows().setSize(30,40);
                sm.getShadows().draw(game.batch);
                if (!multiplayer){
                    if (orb.onFloor()){
                        orb.setSize(20,20);
                        orb.draw(game.batch);
                    }
                }
            }
            game.batch.end();
            game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

            //render glow on pillar when map is lit
            if (WorldContactListener.indicateOrbOnPillar){
                ShaderProgram pillarGlow = new ShaderProgram(Gdx.files.internal("shaders/pillarLightingVertex.glsl"),
                        Gdx.files.internal("shaders/pillarLightingFragment.glsl"));
                pillarGlow.pedantic = false;
                if (!pillarGlow.isCompiled())
                    throw new GdxRuntimeException("Couldn't compile shader: " + pillarGlow.getLog());
                pillarGlow.begin();
                pillarGlow.setUniformMatrix("u_worldView", gameCam.combined);
                pillarGlow.setUniformf("u_worldColor", Color.GOLD);
                pillarGlow.setUniformf("u_lightPos", new Vector2(WorldContactListener.lightedPillarX, WorldContactListener.lightedPillarY));
                renderer.getBatch().setShader(pillarGlow);
                renderer.render();
                renderer.getBatch().setShader(null); //un-set the shader
                pillarGlow.end();
            }
            controller.draw();
        } else {
            //in the dark - game starting state

            // tell our game batch to recognise where the gameCam is and render what the camera can see
            game.batch.setProjectionMatrix(gameCam.combined);

            //render glow on pillar when map is not fully visible
            if (WorldContactListener.indicateOrbOnPillar) {

                ShaderProgram pillarGlow = new ShaderProgram(Gdx.files.internal("shaders/pillarLightingVertex.glsl"),
                        Gdx.files.internal("shaders/pillarLightingFragment.glsl"));
                pillarGlow.pedantic = false;
                if (!pillarGlow.isCompiled())
                    throw new GdxRuntimeException("Couldn't compile shader: " + pillarGlow.getLog());
                pillarGlow.begin();
                pillarGlow.setUniformMatrix("u_worldView", gameCam.combined);
                pillarGlow.setUniformf("u_worldColor", Color.GOLD);
                pillarGlow.setUniformf("u_lightPos", new Vector2(WorldContactListener.lightedPillarX,
                        WorldContactListener.lightedPillarY)); //light's origin position
                renderer.getBatch().setShader(pillarGlow);
                renderer.render();
                renderer.getBatch().setShader(null); //un-set the shader
                pillarGlow.end();
            }

            //black shader to hide visibility - glow on player
            ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/BasicLightingVertex.glsl"),
                    Gdx.files.internal("shaders/BasicLightingFragment.glsl"));
            shader.pedantic = false;
            if (!shader.isCompiled())
                throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
            shader.begin();
            shader.setUniformMatrix("u_worldView", gameCam.combined);
            shader.setUniformf("u_worldColor", Color.WHITE);
            if (WorldContactListener.indicateOrb) {
                shader.setUniformf("u_worldColor", Color.GOLD);
            }
            shader.setUniformf("u_lightPos", new Vector2(gameCam.position.x, gameCam.position.y));
            renderer.getBatch().setShader(shader);
            renderer.render();
            renderer.getBatch().setShader(null); //un-set the shader
            shader.end();

            //render shadow if player is certain distance from shadow
            if (sm.getShadows() != null) {
                xDistance = Math.abs(gameCam.position.x - sm.getShadows().getX());
                yDistance = Math.abs(gameCam.position.y - sm.getShadows().getY());
                if (xDistance <= 60.0f && yDistance <= 60.0f) {
                    game.batch.begin();
                    sm.getShadows().setSize(30, 40);
                    sm.getShadows().draw(game.batch);
                    game.batch.end();
                }
            }
            xDistance = Math.abs(gameCam.position.x - orb.getX());
            yDistance = Math.abs(gameCam.position.y - orb.getY());
            if (xDistance <= 60.0f && yDistance <= 60.0f) {
                if (orb.onFloor()) {
                    game.batch.begin();
                    orb.setSize(20, 20);
                    orb.draw(game.batch);
                    game.batch.end();
                }
            }

            controller.draw();
        }

        // Game Over
        if (gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

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

    // for checking if game is over
    public boolean gameOver(){
        if(Hud.timeIsUp | Hud.coreIsDead){
            return true;
        }
        return false;
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
        for (String id:playerActions.keySet()){
            if (!playerActions.get(id).isEmpty()){
                Vector2 newPos = playerActions.get(id).poll();
                players.get(id).b2body.setTransform(newPos.x, newPos.y,players.get(id).b2body.getAngle());
            }
        }
        if (player != null && keyPressed){
            keyPressed = false;
            // client prediction;
//			String actionID = ""+System.currentTimeMillis();
            Vector2 position = new Vector2(player.b2body.getPosition());
//			clientPrediction.put(actionID, position);
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
            if (!socket.connected()) socket.connect();
            configSocketEvents();
            configSocketOrb();
            initializeFromServer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean floatEquals(float a, float b){
        return (Math.abs(a-b) < 0.001);
    }

    private void initializeShadows(JSONArray shadows){
        try {
            for (int i = 0; i < shadows.length(); i++) {
                JSONObject shadow = shadows.getJSONObject(i);
                Rectangle r = game.getPillarPositions().get(shadow.getInt("direction"));
                sm.addServerShadows(new Shadow(this, r.getX(), r.getY(), shadow.getInt("time")));
            }
        }catch (JSONException e){
            Gdx.app.log("SocketIO", "Error parsing shadow json");
        }
    }

    private void initializeOrbs(JSONArray orbs){
        try {
            for (int i = 0; i < orbs.length(); i++) {
                JSONObject orb = orbs.getJSONObject(i);
                Double x = orb.getDouble("x");
                Double y = orb.getDouble("y");
                listOfOrbs.add(new Orb(this, .32f, .32f, x.floatValue(), y.floatValue(), orb.getInt("id")));
            }
        }catch (JSONException e){
            Gdx.app.log("SocketIO", "Error parsing orb json");
        }
    }

    private void initializePlayers(JSONArray onlineplayers){
        try {
            for (int i = 0; i < onlineplayers.length(); i++) {
                JSONObject onlinePlayer = onlineplayers.getJSONObject(i);
                String id = onlinePlayer.getString("id");
                Double x = onlinePlayer.getDouble("x");
                Double y = onlinePlayer.getDouble("y");
                if (id.equals(SocketClient.myID)) {
                    player = new Player(world, x.floatValue(), y.floatValue(), id);
                    players.put(id, player);
                }
                else {
                    players.put(id, new Player(world, x.floatValue(), y.floatValue(), id));
                    playerActions.put(id, new LinkedList<Vector2>());
                }
            }
        }catch (JSONException e){
            Gdx.app.log("SocketIO", "Error parsing orb json");
        }
    }

    private void initializeGameStatus(JSONObject gameStatus){
        try {
            int duration = gameStatus.getInt("time");
            int health = gameStatus.getInt("health");
            int level = gameStatus.getInt("level");
            hud.initializeStatus(duration, health, level);
        }catch (JSONException e){
            Gdx.app.log("SocketIO", "Error parsing game status");
            e.printStackTrace();
        }
    }

    private void initializeFromServer(){
        initializePlayers(SocketClient.players);
        initializeOrbs(SocketClient.orbs);
        initializeShadows(SocketClient.shadows);
        initializeGameStatus(SocketClient.status);
        socket.emit("finished");
    }

    // Configure socket events after TCP connection has been established.
    public void configSocketEvents(){
//        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                Gdx.app.log("SocketIO", "Connected");
//            }
//        });
//
//        socket.on("socketID", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                JSONObject data = (JSONObject) args[0];
//                try {
//                    myID = data.getString("id");
//                    Gdx.app.log("SocketIO", "My ID: " + myID);
//                    players.put(myID, player);
//                }catch (Exception e){
//                    Gdx.app.log("SocketIO", "error getting id");
//                }
//            }
//        }).on("newPlayer", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                JSONObject data = (JSONObject) args[0];
//                try {
//                    String id = data.getString("id");
//                    players.put(id, new Player(world));
//                    playerActions.put(id, new LinkedList<Vector2>());
//                    Gdx.app.log("SocketIO", "New player has id: " + id);
//                }catch (Exception e){
//                    Gdx.app.log("SocketIO", "error getting new player");
//                }
//
//            }
//        });
        socket.on("playerDisconnected", new Emitter.Listener() {
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
                    if (players.get(id) != null && !id.equals(myID)){
                        playerActions.get(id).offer(new Vector2(x.floatValue(), y.floatValue()));
                    }
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error getting id");
                    e.printStackTrace();
                }
            }
        });
//        socket.on("getPlayers", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                JSONArray onlinePlayers = (JSONArray) args[0];
//                try {
//                    for (int i = 0; i < onlinePlayers.length(); i++){
//                        Player coopPlayer = new Player(world);
//                        Vector2 position = new Vector2();
//                        position.x = ((Double) onlinePlayers.getJSONObject(i).getDouble("x")).floatValue();
//                        position.y = ((Double) onlinePlayers.getJSONObject(i).getDouble("y")).floatValue();
//                        coopPlayer.b2body.setTransform(position.x, position.y, coopPlayer.b2body.getAngle());
//                        players.put(onlinePlayers.getJSONObject(i).getString("id"), coopPlayer);
//                        playerActions.put(onlinePlayers.getJSONObject(i).getString("id"), new LinkedList<Vector2>());
//                    }
//                }catch (Exception e){
//                    Gdx.app.log("SocketIO", "error getting id");
//                }
//
//            }
//        });
    }

    private void configSocketOrb(){
        socket.on("pickUpOrb", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String orbOwnerID = data.getString(ID_PLAYER);
                    int orbID = data.getInt(ID_ORB);
                    Gdx.app.log("SocketIO", "picking orb "+orbID);
                    players.get(orbOwnerID).orbPick(orbID);
                    getListOfOrbs().get(orbID).setToPick();
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error picking up orb");
                    e.printStackTrace();
                }
            }
//        }).on("dropOrb", new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                // requires to know the player ID
//                JSONObject data = (JSONObject) args[0];
//                try {
//                    String orbOwnerID = data.getString(ID_PLAYER);
//                    players.get(orbOwnerID).orbDrop();
//                    Gdx.app.log("SocketIO", "dropping orb");
//                }catch (Exception e){
//                    Gdx.app.log("SocketIO", "error dropping orb");
//                    e.printStackTrace();
//                }
//            }
        }).on("placeOrbOnPillar", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String orbOwnerID = data.getString(ID_PLAYER);
                    int pillarID = data.getInt(ID_PILLAR);
                    Orb orb = players.get(orbOwnerID).orbDrop();
                    Pillar pillar = B2WorldCreator.listOfPillars.get(pillarID);
                    pillar.setmOrb(orb);
                    pillar.setCategoryFilter(MultiplayerGame.LIGHTEDPILLAR_BIT);
                    MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();
                    Gdx.app.log("SocketIO", "placing orb");
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error placing orb");
                    e.printStackTrace();
                }
            }
        }).on("pickOrbFromPillar", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String orbOwnerID = data.getString(ID_PLAYER);
                    int pillarID = data.getInt(ID_PILLAR);
                    Pillar pillar = B2WorldCreator.listOfPillars.get(pillarID);
                    players.get(orbOwnerID).orbPick(pillar.releaseOrb());
                    pillar.setCategoryFilter(MultiplayerGame.PILLAR_BIT);
                    MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();
                    Gdx.app.log("SocketIO", "picking up orb from pillar");
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error placing orb");
                    e.printStackTrace();
                }
            }
        });
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }
}
