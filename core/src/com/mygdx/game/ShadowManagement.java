package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Shadow;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by kennethlimcp on 08/Mar/2017.
 */

public class ShadowManagement extends Thread {
    private ArrayList<Shadow> serverShadows = new ArrayList<Shadow>();

    private MultiplayerGame game = null;
    private CopyOnWriteArrayList<Shadow> shadows = new CopyOnWriteArrayList<Shadow>();
    private final Object shadowsLock = new Object();
    private boolean multiPlayer;

    public ShadowManagement(MultiplayerGame game) {
        this.game = game;
    }

    public ShadowManagement(MultiplayerGame game, boolean multiPlayer) {
        this.game = game;
        this.multiPlayer = multiPlayer;
    }

    @Override
    public void run() {
        calculateShadowStartPosition();
        Random rand = new Random();

        while (true) {
            if (shadows.size() == 0) {
                int randomShadow = rand.nextInt(game.getPillarPositions().size());
                Rectangle r = game.getPillarPositions().get(randomShadow);
                shadows.add(new Shadow((PlayScreen) game.getScreen(), r.getX(), r.getY()));
                Gdx.app.log("Spawning new shadow","sm thread");
            }

            synchronized (shadowsLock) {
                for (Shadow s : shadows) {
                    if (!s.isAlive()) {
                        shadows.remove(s);
                    }
                }
            }
        }
    }

    private void calculateShadowStartPosition() {
        float coreX = game.corePosition.getX() + game.corePosition.getWidth()/2;
        float coreY = game.corePosition.getY() + game.corePosition.getHeight()/2;

        // create a shadow in our game world
        for(Rectangle r: game.getPillarPositions()) {
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

    public void addServerShadows(Shadow shadow){
        serverShadows.add(shadow);
    }
}
