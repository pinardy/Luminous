package com.mygdx.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.SocketClient;
import com.mygdx.game.Tools.WorldContactListener;

import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/** GameEndScreen is the screen users will see when either of the two conditions are met:
 * 1) Time is up (Victory)
 * 2) Core health is 0 (Game over)
 */

public class GameEndScreen implements Screen {
    private Game game;
    private Viewport viewport;
    private Stage stage;
    private boolean ready;

    public GameEndScreen(Game game){
        this.game = game;
        viewport = new FitViewport(MultiplayerGame.V_WIDTH, MultiplayerGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MultiplayerGame) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        // centering of the container for our items to display
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        // labels to be displayed on screen
        Label scoreLabel = new Label("Score: ", font);
        Label tapLabel = new Label("Tap anywhere to go back to the main menu", font);
        Label scoreValue = new Label(String.valueOf(Hud.score), font);

        // image for "Game Over"
        Image gameOverImg = new Image(new Texture("gameover.png"));
        gameOverImg.setSize(250, 48);

        // image for "Victory"
        Image victoryImg = new Image(new Texture("win.png"));
        victoryImg.setSize(400, 60);

        // Arrangement of the labels using a table
        // lose
        if (Hud.coreDead()) {
            table.add(gameOverImg).size(gameOverImg.getWidth(), gameOverImg.getHeight());
        }
        // win
        if (Hud.timesUp()) {
            table.add(victoryImg).size(victoryImg.getWidth(), victoryImg.getHeight());
            Socket socket = SocketClient.getInstance();
            socket.on("start", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        SocketClient.shadows = data.getJSONArray("shadows");
                        SocketClient.orbs = data.getJSONArray("orbs");
                        SocketClient.players = data.getJSONArray("players");
                        SocketClient.status = data.getJSONObject("gameStatus");
                        SocketClient.hostID = data.getString("host");
                        if (SocketClient.hostID.equals(SocketClient.myID))
                            SocketClient.isHost = true;
                        Gdx.app.log("SocketIO", SocketClient.shadows.toString());
                        Gdx.app.log("SocketIO", SocketClient.orbs.toString());
                        Gdx.app.log("SocketIO", SocketClient.players.toString());
                        Gdx.app.log("SocketIO", SocketClient.status.toString());
                        Gdx.app.log("SocketIO", SocketClient.hostID);
                        Gdx.app.log("SocketIO", "Game starts");
                        ready = true;
                    }catch (Exception e){
                        Gdx.app.log("SocketIO", "error starting game");
                    }
                }
            });
            socket.emit("ready");
        }
        table.row();
        table.add(scoreLabel);
        table.row();
        table.add(scoreValue).pad(5);
        table.row();
        table.add(tapLabel);

        // add the table to the stage
        stage.addActor(table);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // go back to StartScsreen

        if (Hud.coreDead()) {
            if (Gdx.input.justTouched()) {
                game.setScreen(new StartScreen(game));
                // resets the game variables
                resetGameStatus();
                // if multiplayer mode, player leaves after leaving the GameEndScreen
                if (StartScreen.hasJoin) {
                    StartScreen.hasJoin = false;
                    StartScreen.ready = false;
                    SocketClient.getInstance().emit("leave");
                }
                dispose();
            }
        } else {
            //TODO: next level

            //TODO: emit
            if (!PlayScreen.multiplayer){
                resetGameStatus();
                game.setScreen(new PlayScreen((MultiplayerGame) game, false));
                dispose();
            } else {
                if (ready) {
                    resetGameStatus();
                    game.setScreen(new PlayScreen((MultiplayerGame) game, true));
                    Hud.difficulty += 1;
                    dispose();
                }
            }
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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

    public void resetGameStatus(){
        // resets the game variables
        Hud.health = 5;
        Hud.worldTimer = 300;
        Hud.timeIsUp = false;
        Hud.coreIsDead = false;
        WorldContactListener.fullVisibility = 0;
    }
}
