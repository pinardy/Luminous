package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Screens.PlayScreen;

import java.util.ArrayList;

public class MultiplayerGame extends Game {

    /**
     * SpriteBatch is a container for our images, textures, etc.
     * SpriteBatch is memory intensive, hence we make it public
     * so other screens will access this one SpriteBatch
     */

    // title for our game
    public static final String TITLE = "LUMINOUS";

    // virtual width and height for game
    public static final int V_WIDTH = 850;
    public static final int V_HEIGHT = 500;

    // powers of 2 so its easier to OR bits tgt
    public static final short DEFAULT_BIT = 1;
    public static final short PLAYER_BIT = 2;
    public static final short PILLAR_BIT = 4;
    public static final short CORE_BIT = 8;
    public static final short ORB_BIT = 16;
    public static final short SHADOW_BIT = 32;
    public static final short LIGHTEDPILLAR_BIT = 64;

    public static SpriteBatch batch;
    public static ArrayList<Rectangle> pillarPositions = new ArrayList<Rectangle>();
    public static Rectangle corePosition = new Rectangle();

    public static AssetManager manager;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // handling music
        manager = new AssetManager();
        manager.load("audio/music/dungeon_peace.mp3", Music.class);
        manager.load("audio/sounds/woosh.mp3", Sound.class);
        manager.load("audio/sounds/pickOrb.mp3", Sound.class);
        manager.load("audio/sounds/evilCrack.mp3", Sound.class);
        manager.load("audio/sounds/shadowVanish.mp3", Sound.class);
        manager.finishLoading();

        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render(); // delegate render method to playscreen
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public ArrayList<Rectangle> getPillarPositions() {
        return pillarPositions;
    }
}
