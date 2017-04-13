package com.mygdx.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.SocketClient;
import com.mygdx.game.Tools.Controller;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/** StartScreen is the screen users will see when the game app is started
 * Users can choose to :
 * 1) play single-player
 * 2) play multi-player
 * 3) read the help section
 */

public class StartScreen implements Screen {
    private Game game;
    private Viewport viewport;
    private Stage stage;
    private Socket socket;
    private static Table table;
    public static boolean hasJoin;

    //TODO: Change these variables to read from server
    private int capacity = 2;
    private static int numOfPlayers = 0;
    public static boolean ready;

    // Music
    private Music music;

    // Labels for number of players and connection status
    static Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
    static Label numOfPlayersLabel = new Label("", font);
    static Label connectedLabel = new Label("", font);

    public StartScreen(final Game game){
        this.game = game;
        viewport = new FitViewport(MultiplayerGame.V_WIDTH, MultiplayerGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MultiplayerGame) game).batch);

        // enable the listener for buttons
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.bottom();
        table.setFillParent(true);

        createContent(table);
        ready = false;

        stage.addActor(table);

        // play music
        music = MultiplayerGame.manager.get("audio/music/dungeon_peace.mp3", Music.class);
        music.setLooping(true);
        music.play();
    }

    private void createContent(Table table) {

        // images of buttons
        Image logoImg = new Image(new Texture("luminousicon.png"));
        logoImg.setSize(243, 240);

        final Image joinImg = new Image(new Texture("joinGame.png"));
        joinImg.setSize(108, 48);


        // Only for debugging in single player mode
        Image singleImg = new Image(new Texture("singleMode.png"));
        singleImg.setSize(108, 48);

        final Image helpImg = new Image(new Texture("help.png"));
        helpImg.setSize(108, 48);

        // Arrangement of the labels using a table
        table.row().pad(5, 5, 5, 5);
        table.add();
        table.add(logoImg);
        table.row().pad(5, 5, 50, 5);
        table.add(joinImg).size(joinImg.getWidth(), joinImg.getHeight());
        table.getCells();
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

        // Logic for connecting to server
        joinImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!hasJoin) {
                    hasJoin = true;
                    if (!SocketClient.isConnected()) {
                        // Join the server
                        joinImg.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("leaveGame.png"))));
                        connectSocket();
                        socket.emit("room", capacity);

                    } else {
                        SocketClient.getInstance().emit("room", capacity);
                    }

                    // Set the labels to show connected
                    numOfPlayersLabel.setText("Waiting for " + playersLeft() + " more players");
                    connectedLabel.setText("Connected to server!");
                } else {
                    joinImg.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture("joinGame.png"))));

                    SocketClient.getInstance().emit("leave", 0); // leave room
                    hasJoin = false; // player is no longer in room

                    // Set labels to show leaving room
                    numOfPlayersLabel.setText("");
                    connectedLabel.setText("Disconnected from server!");
                }

            }
        });

        helpImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HelpScreen(game));
                dispose();
            }
        });
    }

    public Table getTable(){
        return table;
    }

    public int playersLeft(){
        return capacity - numOfPlayers;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
        if (ready){
            game.setScreen(new PlayScreen((MultiplayerGame) game, true));
            dispose();
        }
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


    private void configSocketEvents(){
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
                    SocketClient.myID = data.getString("id");
                    numOfPlayers = data.getInt("numOfPlayers");
                    numOfPlayersLabel.setText("Waiting for " + playersLeft() + " more players");
                    if (numOfPlayers == capacity) numOfPlayersLabel.setText("Loading...");
                    Gdx.app.log("SocketIO", "My ID: " + SocketClient.myID);
                } catch (Exception e) {
                    Gdx.app.log("SocketIO", "error getting id");
                    e.printStackTrace();
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    numOfPlayers++;
                    if (numOfPlayers == capacity) numOfPlayersLabel.setText("Loading...");
                    else numOfPlayersLabel.setText("Waiting for " + playersLeft() + " more players");
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error getting new player");
                }
            }
        }).on("start", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                hasJoin = true;
                numOfPlayersLabel.setText("");
                connectedLabel.setText("");
                JSONObject data = (JSONObject) args[0];
                try {
                    SocketClient.shadows = data.getJSONArray("shadows");
                    SocketClient.orbs = data.getJSONArray("orbs");
                    SocketClient.players = data.getJSONArray("players");
                    SocketClient.status = data.getJSONObject("gameStatus");
                    Gdx.app.log("SocketIO", SocketClient.shadows.toString());
                    Gdx.app.log("SocketIO", SocketClient.orbs.toString());
                    Gdx.app.log("SocketIO", SocketClient.players.toString());
                    Gdx.app.log("SocketIO", SocketClient.status.toString());
                    Gdx.app.log("SocketIO", "Game starts");
                    ready = true;
                }catch (Exception e){
                    Gdx.app.log("SocketIO", "error starting game");
                    e.printStackTrace();
                }
            }
        });
    }
}