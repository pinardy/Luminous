package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Shadow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
/** ShadowManagement is the handler of creating the Shadow object
 *  in the game world. (Shadows are the enemies of the game)
 */

public class ShadowManagement extends Thread {
    private Queue<Shadow> serverShadows;
    private boolean multiPlayer;

    private MultiplayerGame game = null;
    private CopyOnWriteArrayList<Shadow> shadows = new CopyOnWriteArrayList<Shadow>();
    public ArrayList<Rectangle> shadowStartPositions;
    private final Object shadowsLock = new Object();
    private float shadowX = 0f;
    private float shadowY = 0f;

    public ShadowManagement(MultiplayerGame game) {
        this.game = game;
        serverShadows = new LinkedList<Shadow>();
        calculateShadowStartPosition();
    }

    public ShadowManagement(MultiplayerGame game, boolean multiPlayer) {
        this(game);
        this.multiPlayer = multiPlayer;
    }

    @Override
    public void run() {
        Random rand = new Random();

        while (!Thread.interrupted()) {
            if (multiPlayer) {
                while (serverShadows.peek() != null && serverShadows.peek().getServerTime() <= Hud.timePassed) {
                    shadows.add(serverShadows.poll());
                    Gdx.app.log("Spawning new shadow", "sm thread");
                }
            }else {
                if (shadows.size() == 0) {
                    int randomShadow = rand.nextInt(shadowStartPositions.size());
                    Rectangle r = shadowStartPositions.get(randomShadow);
                    shadows.add(new Shadow((PlayScreen) game.getScreen(), r.getX(), r.getY()));
                    Gdx.app.log("Spawning new shadow", "sm thread");
                }
            }

            synchronized (shadowsLock) {
                for (Shadow s : shadows) {
                    if (!s.isAlive()) {
                        shadows.remove(s);
                    }
                }
            }
        }
        clearShadows();
    }

    public void calculateShadowStartPosition() {
        float coreX = game.corePosition.getX() + game.corePosition.getWidth()/2;
        float coreY = game.corePosition.getY() + game.corePosition.getHeight()/2;

        // create a shadow in our game world
        shadowStartPositions = new ArrayList<Rectangle>();
        for(Rectangle rect: game.getPillarPositions()) {
            Rectangle r = new Rectangle(rect);
            shadowStartPositions.add(r);
            float x = r.getX() + r.getWidth()/2;
            float y = r.getY() + r.getHeight()/2;

            float adjustedX = x + (x-coreX);
            float adjustedY = y + (y-coreY);

            r.setX(adjustedX);
            r.setY(adjustedY);
        }
    }

    public void update(float dt) {
        synchronized (shadowsLock) {
            for (Shadow s : shadows) {
                s.update(dt);
            }
        }
    }

    public Sprite getShadows() {
        for (Shadow x : shadows) {
            return x;
        }
        return null;
    }

    public ArrayList<Shadow> getMShadows(){
        ArrayList<Shadow> sprites = new ArrayList<Shadow>();
        for (Shadow x : shadows){
            sprites.add(x);
        }
        return sprites ;
    }

    public ArrayList<Rectangle> getShadowStartPositions() {
        return shadowStartPositions;
    }

    public void addServerShadows(Shadow shadow){
        serverShadows.offer(shadow);
    }

    public void clearShadows(){
        serverShadows.clear();
        shadows.clear();
    }
}
