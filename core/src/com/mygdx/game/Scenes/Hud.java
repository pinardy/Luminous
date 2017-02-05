package com.mygdx.game.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MultiplayerGame;

/**
 * The Hud class is to display scores for players to see on their screen
 *
 * When the game camera moves, we want the Hud to stay the same.
 * Hence, we are using a separate Viewport for the Hud
 */

public class Hud {

    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    Label countDownLabel;
    static Label scoreValue;
    Label timeLabel;
    Label levelLabel;
    Label levelHeader;
    Label scoreLabel;

    public Hud(SpriteBatch sb){
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        viewport = new FitViewport(MultiplayerGame.V_WIDTH, MultiplayerGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top(); // put at top of stage
        table.setFillParent(true); // table is size of stage

        countDownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreValue = new Label(String.format("%06d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelHeader = new Label("LEVEL", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label("SCORE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // the entire top row will be marioLabel if it's the only label in the table
        table.add(scoreLabel).expandX().padTop(10);
        table.add(levelHeader).expandX().padTop(10); // at this pt, both labels share half of screen
        table.add(timeLabel).expandX().padTop(10);

        // creates a new row. everything below this will be in a new row
        table.row();
        table.add(scoreValue).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();

        // add table to stage
        stage.addActor(table);
    }

    public void update(float dt){
        timeCount += dt;
        if (timeCount > 1) { // 1 second
            worldTimer--; // our world timer is 1 second less
            countDownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public void addScore(int value){
        score += value;
        scoreValue.setText(String.format("%06d", score));
    }
}
