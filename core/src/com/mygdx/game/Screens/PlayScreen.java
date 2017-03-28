package com.mygdx.game.Screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.ShadowManagement;
import com.mygdx.game.Sprites.Orb;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.Tools.B2WorldCreator;
import com.mygdx.game.Tools.Controller;
import com.mygdx.game.Tools.WorldContactListener;

import java.util.ArrayList;

/**
 * Created by Pin on 04-Feb-17.
 */

public class PlayScreen implements Screen {

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

    }

    public void update(float dt) {
        // handle input first
        handleInput(dt);
        world.step(1 / 60f, 6, 2);

        player.update(dt);
        orb.update(dt);
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

        // Keyboard controls
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        // if player is NOT moving, velocity is set to 0
        if (!(up | down | left | right)){
            player.b2body.setLinearVelocity(0, 0);
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
        update(dt);

        // clear game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1); // colour, alpha
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //shader to hide visibility
        renderer.setView(gameCam);
//        ShaderProgram shader = new ShaderProgram(Gdx.files.internal("Shaders/BasicLightingVertex.txt"),
//                Gdx.files.internal("Shaders/BasicLightingFragment.txt"));

        ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shaders/BasicLightingVertex.glsl"),
                Gdx.files.internal("shaders/BasicLightingFragment.glsl"));

        shader.pedantic = false;
        if (!shader.isCompiled())
            throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());

        shader.begin();
        shader.setUniformMatrix("u_worldView", gameCam.combined);
        //light's origin point
        shader.setUniformf("u_lightPos", new Vector2(gameCam.position.x,gameCam.position.y));
        renderer.getBatch().setShader(shader);
        // render game map
        renderer.render();
        renderer.getBatch().setShader(null); //un-set the shader
        shader.end();

        // render our Box2DDebugLines
//        b2dr.render(world, gameCam.combined);

        // render our controller
//        if (Gdx.app.getType() == Application.ApplicationType.Android)
        controller.draw();

        // tell our game batch to recognise where the gameCam is and render what the camera can see
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);

        hud.stage.draw();

        //to reveal full visibility
        if (WorldContactListener.fullVisibility==1){
            // render game map
            renderer.render();
            // render our Box2DDebugLines
            b2dr.render(world, gameCam.combined);
            controller.draw();
        }

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
}
