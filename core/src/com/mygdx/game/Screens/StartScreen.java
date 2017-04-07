package com.mygdx.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Tools.Controller;

/** StartScreen is the screen users will see when the game app is started
 * Users can choose to play single-player, multi-player, or read the help section
 */

public class StartScreen implements Screen {
    private Game game;
    private Viewport viewport;
    private Stage stage;
    public boolean hasJoined = false;

    //TODO: Change these variables to read from server
    static int capacity = 2;
    static int numOfPlayers = 0;
    static int playersLeftToJoin = capacity - numOfPlayers;

    static Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
    static Label numOfPlayersLabel = new Label("", font);
    static Label connectedLabel = new Label("", font);

    public StartScreen(final Game game){
        this.game = game;
        viewport = new FitViewport(MultiplayerGame.V_WIDTH, MultiplayerGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MultiplayerGame) game).batch);

        //enable the listener for buttons
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.bottom();
        table.setFillParent(true);

        createContent(table);

        stage.addActor(table);
    }

    private void createContent(Table table) {

        Image logoImg = new Image(new Texture("luminousicon.png"));
        logoImg.setSize(243, 240);

        Image joinImg = new Image(new Texture("joinGame.png"));
        joinImg.setSize(108, 48);

        // Only for debugging in single player mode
        Image singleImg = new Image(new Texture("singleMode.png"));
        singleImg.setSize(108, 48);

        Image helpImg = new Image(new Texture("help.png"));
        helpImg.setSize(108, 48);

        table.row().pad(5, 5, 5, 5);
        table.add();
        table.add(logoImg);
        table.row().pad(5, 5, 50, 5);
        table.add(joinImg).size(joinImg.getWidth(), joinImg.getHeight());
        table.add(singleImg).size(singleImg.getWidth(), singleImg.getHeight());
        table.add(helpImg).size(helpImg.getWidth(), helpImg.getHeight());

        table.row().pad(5, 5, 5, 5);
        table.add();
        table.add(connectedLabel);
        table.row().pad(5, 5, 5, 5);
        table.add();
        table.add(numOfPlayersLabel);

        //Only for debugging in single player mode
        singleImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen((MultiplayerGame) game, false));
                dispose();
            };
        });

        //Add logic for connecting to server here
        joinImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (playersLeft() == 0){
                    game.setScreen(new PlayScreen((MultiplayerGame) game, true));
                    dispose();
                }
                if (!hasJoined) {
                    //TODO: Join the server

                    hasJoined = true;
                    //TODO: Update player count (capacity, numOfPlayers)


                    //TODO: Set the labels accordingly
                    numOfPlayersLabel.setText("Waiting for " + playersLeftToJoin + " more players");
                    connectedLabel.setText("Connected to server!");
                }
            }
        });

    }

    public int playersLeft(){
        return playersLeftToJoin;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
//        if(Gdx.input.justTouched()) {
//            game.setScreen(new PlayScreen((MultiplayerGame) game));
//            dispose();
//        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        Gdx.app.log("Start Screen", String.valueOf(joinButPressed));

        stage.draw();
    }


    @Override
    public void resize(int width, int height) {

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
        stage.dispose();
    }
}