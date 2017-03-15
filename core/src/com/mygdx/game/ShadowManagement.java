package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Shadow;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by kennethlimcp on 08/Mar/2017.
 */

public class ShadowManagement extends Thread {
    private MultiplayerGame game = null;
    private CopyOnWriteArrayList<Shadow> shadows = new CopyOnWriteArrayList<Shadow>();
    private final Object shadowsLock = new Object();

    public ShadowManagement(MultiplayerGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        calculateShadowStartPosition();
        Random rand = new Random();

        while (true) {
            if (shadows.size() == 0) {
                int randomShadow = rand.ints(1, 0, game.getPillarPositions().size()).findFirst().getAsInt();

                Rectangle r = game.getPillarPositions().get(randomShadow);
                shadows.add(new Shadow((PlayScreen) game.getScreen(), r.getX(), r.getY()));
                System.out.println("Spawning new shadow");
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
}
