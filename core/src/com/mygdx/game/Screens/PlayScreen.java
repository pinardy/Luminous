package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.badlogic.gdx.utils.Array;
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
import java.util.List;
import java.util.Map;

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
    //synchronize with worldcontact
//    public static String currentUser;
    private Socket socket;
    HashMap<String, Vector2> clientPrediction;
    private static HashMap<String, Player> players;
    public static HashMap<String, LinkedList<Vector2>> playerActions;
    private boolean keyPressed;
    public static boolean multiplayer;

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
    public static Player.State returnPlayerEPos;

    // List of Orbs
    public static ArrayList<Orb> listOfOrbs = new ArrayList<Orb>();

    public ArrayList<Orb> getListOfOrbs() {
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
        if (players != null) players.clear();
        if (playerActions != null) playerActions.clear();
        players = new HashMap<String, Player>();
        playerActions = new HashMap<String, LinkedList<Vector2>>();
        clientPrediction = new HashMap<String, Vector2>();
        listOfOrbs = new ArrayList<Orb>();
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
        if (multiplayer) map = mapLoader.load("map_hard.tmx");
        else map = mapLoader.load("map_easy_edited.tmx"); // game world is created in a tmx file
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
            player = new Player(world, this);

            // create an orb in our game world
            orb = new Orb(this, .32f, .32f);
            listOfOrbs.add(orb);
        }

        // controller
        controller = new Controller();

        world.setContactListener(new WorldContactListener());

        sm = new ShadowManagement(game, multiplayer);
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

        ArrayList<Vector2> litPillars = new ArrayList<Vector2>();
        ArrayList<Color> litPillarColors = new ArrayList<Color>();

        int pillarCount = 0;
        for (Pillar p : B2WorldCreator.listOfPillars) {
            litPillars.add(null);
            litPillarColors.add(null);
            if (p.hasOrb()) {
                litPillars.set(pillarCount, new Vector2(p.positionX(),p.positionY()));
                litPillarColors.set(pillarCount, Color.GOLD);
                pillarCount++;
            }
        }
        while(pillarCount<4){
            litPillars.set(pillarCount,new Vector2(0,0));
            litPillarColors.set(pillarCount,Color.CLEAR);
            pillarCount++;
        }


        //whole map is lit when player touches pillar
        if (WorldContactListener.fullVisibility == 1) {
            renderer.render();
            b2dr.render(world, gameCam.combined); //render fixture outlines

            // tell our game batch to recognise where the gameCam is and render what the camera can see
            //render shadows
            game.batch.setProjectionMatrix(gameCam.combined);
            if (multiplayer){
                game.batch.begin();
                for (Shadow serverShadow : sm.getMShadows()) {
                    serverShadow.setSize(30, 40);
                    serverShadow.draw(game.batch);
                }
                game.batch.end();
                game.batch.begin();
                for (int x = 0; x < listOfOrbs.size(); x++) {
                    if (Orb.onFloor(listOfOrbs.get(x))) {
                        listOfOrbs.get(x).setSize(20, 20);
                        listOfOrbs.get(x).draw(game.batch);
                    }
                }
                game.batch.end();
                game.batch.begin();
                for (Map.Entry<String,Player>player : PlayScreen.players.entrySet()){
                    player.getValue().setSize(40,40);
                    player.getValue().draw(game.batch);
                }
                game.batch.end();
            }else{
                game.batch.begin();
                if (sm.getShadows() != null) {
                    sm.getShadows().setSize(30, 40);
                    sm.getShadows().draw(game.batch);
                }
                game.batch.end();
                game.batch.begin();
                if (Orb.onFloor(orb)) {
                    orb.setSize(20, 20);
                    orb.draw(game.batch);
                }
                game.batch.end();
                game.batch.begin();
                PlayScreen.player.setSize(40,40);
                PlayScreen.player.draw(game.batch);
                game.batch.end();
            }
            game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

            //render glow on pillar when map is lit
            ShaderProgram pillarGlow = new ShaderProgram(Gdx.files.internal("shaders/pillarLightingVertex.glsl"),
                    Gdx.files.internal("shaders/pillarLightingFragment.glsl"));
            pillarGlow.pedantic = false;
            if (!pillarGlow.isCompiled())
                throw new GdxRuntimeException("Couldn't compile shader: " + pillarGlow.getLog());

            //for 4 orbs
            pillarGlow.begin();
            pillarGlow.setUniformMatrix("u_worldView", gameCam.combined);

            pillarGlow.setUniformf("u_lightPosPillarA", litPillars.get(0));
            pillarGlow.setUniformf("u_lightPosPillarB", litPillars.get(1));
            pillarGlow.setUniformf("u_lightPosPillarC", litPillars.get(2));
            pillarGlow.setUniformf("u_lightPosPillarD", litPillars.get(3));

            pillarGlow.setUniformf("u_worldColorPillarA", litPillarColors.get(0));
            pillarGlow.setUniformf("u_worldColorPillarB", litPillarColors.get(1));
            pillarGlow.setUniformf("u_worldColorPillarC", litPillarColors.get(2));
            pillarGlow.setUniformf("u_worldColorPillarD", litPillarColors.get(3));

            renderer.getBatch().setShader(pillarGlow);
            renderer.render();
            renderer.getBatch().setShader(null); //un-set the shader
            pillarGlow.end();
        } else {
            //in the dark - game starting state
            //render glow on pillar when map is not fully visible
            //render glow for player moving around (gold when holding orb)

            // tell our game batch to recognise where the gameCam is and render what the camera can see
            game.batch.setProjectionMatrix(gameCam.combined);
            ShaderProgram pillarGlow = new ShaderProgram(Gdx.files.internal("shaders/BasicLightingVertex.glsl"),
                    Gdx.files.internal("shaders/BasicLightingFragment.glsl"));
            pillarGlow.pedantic = false;
            if (!pillarGlow.isCompiled())
                throw new GdxRuntimeException("Couldn't compile shader: " + pillarGlow.getLog());

            //coded for 4 orbs
            pillarGlow.begin();
            pillarGlow.setUniformMatrix("u_worldView", gameCam.combined);

            pillarGlow.setUniformf("u_lightPosPillarA", litPillars.get(0));
            pillarGlow.setUniformf("u_lightPosPillarB", litPillars.get(1));
            pillarGlow.setUniformf("u_lightPosPillarC", litPillars.get(2));
            pillarGlow.setUniformf("u_lightPosPillarD", litPillars.get(3));

            pillarGlow.setUniformf("u_worldColorPillarA", litPillarColors.get(0));
            pillarGlow.setUniformf("u_worldColorPillarB", litPillarColors.get(1));
            pillarGlow.setUniformf("u_worldColorPillarC", litPillarColors.get(2));
            pillarGlow.setUniformf("u_worldColorPillarD", litPillarColors.get(3));

            String s = "";
            if (PlayScreen.player.isHoldingOrb())
                pillarGlow.setUniformf("u_worldColorPlayer", Color.GOLD); //indicate glow on player
            else
                pillarGlow.setUniformf("u_worldColorPlayer", Color.WHITE);//player as per normal

            pillarGlow.setUniformf("u_lightPosPlayer", new Vector2(gameCam.position.x, gameCam.position.y));
            renderer.getBatch().setShader(pillarGlow);
            renderer.render();
            renderer.getBatch().setShader(null); //un-set the shader
            pillarGlow.end();

            //render shadow if player is certain distance from shadow
            if (sm.getShadows() != null) {
                xDistance = Math.abs(gameCam.position.x - sm.getShadows().getX());
                yDistance = Math.abs(gameCam.position.y - sm.getShadows().getY());
                if (xDistance <= 60.0f && yDistance <= 60.0f) {
                    if (multiplayer) {
                        game.batch.begin();
                        for (Sprite serverShadow : sm.getMShadows()) {
                            serverShadow.setSize(30, 40);
                            serverShadow.draw(game.batch);
                        }
                        game.batch.end();
                    } else{
                        game.batch.begin();
                        sm.getShadows().setSize(30, 40);
                        sm.getShadows().draw(game.batch);
                        game.batch.end();
                    }
                }
            }
            if (!multiplayer) { //in singleplayer mode
                game.batch.begin();
                xDistance = Math.abs(gameCam.position.x - orb.getX());
                yDistance = Math.abs(gameCam.position.y - orb.getY());
                if (xDistance <= 60.0f && yDistance <= 60.0f) {
                    if (Orb.onFloor(orb)) {
                        orb.setSize(20, 20);
                        orb.draw(game.batch);
                    }
                }
                PlayScreen.player.setSize(40,40);
                PlayScreen.player.draw(game.batch);
                game.batch.end();
            } else { //in multiplayer mode
                game.batch.begin();
                for (int x = 0; x < listOfOrbs.size(); x++) {
                    xDistance = Math.abs(gameCam.position.x - listOfOrbs.get(x).getX());
                    yDistance = Math.abs(gameCam.position.y - listOfOrbs.get(x).getY());
                    if (xDistance <= 60.0f && yDistance <= 60.0f) {
                        if (Orb.onFloor(listOfOrbs.get(x))) {
                            listOfOrbs.get(x).setSize(20, 20);
                            listOfOrbs.get(x).draw(game.batch);
                        }
                    }
                }
                game.batch.end();

                for (Map.Entry<String, Player> player : players.entrySet()) {
                    //always render current player
                    if (player.getKey().equals(PlayScreen.player.getID())){
                        game.batch.begin();
                        player.getValue().setSize(40, 40);
                        player.getValue().draw(game.batch);
                        game.batch.end();
                    }else{
                        game.batch.begin();
                        xDistance = Math.abs(gameCam.position.x - player.getValue().getX());
                        yDistance = Math.abs(gameCam.position.y - player.getValue().getY());
                        if (xDistance <= 60.0f && yDistance <= 60.0f) {
                            player.getValue().setSize(40, 40);
                            player.getValue().draw(game.batch);
                        }
                        game.batch.end();
                    }
                }
            }
        }

        controller.draw();
        // Game Over
        if (gameOver()){
            sm.interrupt();
            socket.off();
            game.setScreen(new GameEndScreen(game));
            dispose();
        }

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    // for checking if game is over
    public boolean gameOver(){
        if(Hud.timesUp() | Hud.coreDead()){
            return true;
        }
        return false;
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

    public static HashMap<String, Player> getPlayers(){
        HashMap<String, Player> playerCopy = new  HashMap<String, Player>();
        for(Map.Entry<String, Player> player : players.entrySet()){
            playerCopy.put(player.getKey(), player.getValue());
        }
        return playerCopy;
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

                if (Player.playerE.getID().equals(id)) {

                    float xDistance = newPos.x - Player.playerE.getX();
                    float yDistance = newPos.y - Player.playerE.getY();
                    float zero = 20.00000f;

                    if (yDistance > zero ) {
                        returnPlayerEPos = Player.State.UP;
                    } else if (yDistance < zero) {
                        returnPlayerEPos = Player.State.DOWN;
                    } else if (xDistance < zero) {
                        returnPlayerEPos = Player.State.LEFT;
                    } else if (xDistance > zero) {
                        returnPlayerEPos = Player.State.RIGHT;
                    } else {
                        returnPlayerEPos = Player.State.STAND;
                    }
                }
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
                Rectangle r = sm.getShadowStartPositions().get(shadow.getInt("direction"));
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
                listOfOrbs.add(new Orb(PlayScreen.this, .32f, .32f, x.floatValue(), y.floatValue(), orb.getInt("id")));
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
                    player = new Player(world, this, x.floatValue(), y.floatValue(), id);
                    players.put(id, player);
                }
                else {
                    players.put(id, new Player(world, this, x.floatValue(), y.floatValue(), id));
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
            hud.initializeStatus(300, health, level);
        }catch (JSONException e){
            Gdx.app.log("SocketIO", "Error parsing game status");
            e.printStackTrace();
        }
    }

    private void updateGameStatus(JSONObject gameStatus){
        try {
            int duration = gameStatus.getInt("time");
            int health = gameStatus.getInt("health");
            int level = gameStatus.getInt("level");
            hud.updateStatus(duration, health, level);
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
                    if (players.get(id) != null && !id.equals(SocketClient.myID)){
                        playerActions.get(id).offer(new Vector2(x.floatValue(), y.floatValue()));
                    }
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error getting id");
                    e.printStackTrace();
                }
            }
        }).on("gameStatus", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                updateGameStatus(data);
            }
        }).on("end", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String condition = data.getString("condition");
                    if (condition.equals("time")){
                        Hud.timeIsUp = true;
                    }else if (condition.equals("health")){
                        Hud.health = 0;
                    }
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error ending game");
                }
            }
        });
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
                    int orbID = data.getInt(ID_ORB);
                    pillar.setCategoryFilter(MultiplayerGame.PILLAR_BIT);
                    MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();
                    Gdx.app.log("SocketIO", "picking up orb from pillar");
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error picking orb");
                    e.printStackTrace();
                }
            }
        });
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }
}
