package com.mygdx.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MultiplayerGame;

/**
 * The Hud class is to display game information on the screen
 * Game information includes score, time left, health
 */

public class Hud implements Disposable{

    public Stage stage;
    /* When the game camera moves, we want the Hud to stay the same.
       Hence, we are using a separate Viewport for the Hud */
    private Viewport viewport;

    // score/time tracking variables
    public static int worldTimer;
    public static float timeCount;
    public static int score;
    public static int health;
    public static int level;
    public static int timePassed;

    // for checking if game is over
    public static boolean timeIsUp;
    public static boolean coreIsDead;

    // Scene2D widgets
    private static Label scoreLabel;
    private static Label scoreValue;
    private Label levelHeader;
    private Label levelLabel;
    private Label timeLabel;
    private Label countDownLabel;
    private Label healthLabel;
    private static Label healthValue;

    public Hud(SpriteBatch sb){
        // define our tracking variables
        initializeStatus();

        // setup the HUD viewport using a new camera separate from our gamecam
        // define our stage using that viewport and our game's SpriteBatch
        viewport = new FitViewport(MultiplayerGame.V_WIDTH, MultiplayerGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top(); // put at top of stage
        table.setFillParent(true); // table is size of stage

        // score
        scoreLabel = new Label("SCORE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreValue = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // level
        levelHeader = new Label("LEVEL", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // time
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        countDownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // core's health
        healthLabel = new Label("HEALTH", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        healthValue = new Label(String.format("%06d", health), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(scoreLabel).expandX().padTop(10);
        table.add(levelHeader).expandX().padTop(10); // at this pt, both labels share half of screen
        table.add(timeLabel).expandX().padTop(10);
        table.add(healthLabel).expandX().padTop(10);

        // creates a new row. everything below this will be in a new row
        table.row();
        table.add(scoreValue).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();
        table.add(healthValue).expandX();

        // add table to stage
        stage.addActor(table);
    }

    public void update(float dt){
        timeCount += dt;
        if (timeCount >= 1) { // 1 second
            worldTimer--; // our world timer is 1 second less
            timePassed++;
            countDownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value){
        score += value;
        scoreValue.setText(String.format("%06d", score));
    }

    public static void reduceHealth(){
        health --;
        healthValue.setText(String.format("%06d", health));
    }

    // for checking if the game time is up
    public static boolean timesUp(){
        return timeIsUp;
    }

    // for checking if the Core's health is 0
    public static boolean coreDead(){
        return coreIsDead;
    }



    @Override
    public void dispose() {
        stage.dispose();
    }

    // Initialize the game status in default single player mode
    public void initializeStatus(){
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        health = 5;
        timeIsUp = false;
        coreIsDead = false;
        timePassed = 0;
    }

    // Initialize the game status with specified settings (mostly used from server.
    public void initializeStatus(int duration, int newHealth, int newLevel){
        worldTimer = duration;
        level = newLevel;
        health = newHealth;
        timeCount = 0;
        score = 0;
        timeIsUp = false;
        coreIsDead = false;
        timePassed = 0;
    }
}
