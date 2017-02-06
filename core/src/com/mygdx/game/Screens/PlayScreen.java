package com.mygdx.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.Tools.B2WorldCreator;

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

    // Sprites
    private Player player;


    public PlayScreen (MultiplayerGame game){
        this.game = game;


        // create cam to follow player throughout world
        gameCam = new OrthographicCamera();

        // create a FitViewPort to maintain virtual aspect ratio
        gamePort = new FitViewport(MultiplayerGame.V_WIDTH, MultiplayerGame.V_HEIGHT, gameCam);

        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map_easy.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        gameCam.position.set((gamePort.getWorldWidth() / 2) , (gamePort.getWorldHeight() / 2) , 0) ;

        // World(Vector2 gravity, boolean doSleep)
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();

        // create a player in our game world
        player = new Player(world);

        new B2WorldCreator(world, map);

    }

    public void update(float dt){
        // handle input first
        handleInput(dt);
        world.step(1/60f, 6, 2);

        // track movement of player
        gameCam.position.x = player.b2body.getPosition().x;
        gameCam.position.y = player.b2body.getPosition().y;

        // update camera with correct coordinates after changes
        gameCam.update();

        // tells our renderer to draw only what our camera can see in the game world
        renderer.setView(gameCam);
    }


    public void handleInput(float dt) {

        //TODO: Player keeps moving in a certain direction (doesn't slow down)
        // Up-Down-Left-Right movement
        if(Gdx.input.isKeyPressed(Input.Keys.UP)){
            player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            player.b2body.applyLinearImpulse(new Vector2(0, -4f), player.b2body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            player.b2body.applyLinearImpulse(new Vector2(-4f, 0), player.b2body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            player.b2body.applyLinearImpulse(new Vector2(4f, 0), player.b2body.getWorldCenter(), true);
        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float dt) {
        update(dt);

        // clear game screen with black
        Gdx.gl.glClearColor(0,0,0,1); // colour, alpha
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render game map
        renderer.render();

        // render our Box2DDebugLines
        b2dr.render(world, gameCam.combined);

        // tell our game batch to recognise where the gameCam is and render what the camera can see
        game.batch.setProjectionMatrix(gameCam.combined);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
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
