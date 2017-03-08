package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Shadow;

import java.util.ArrayList;
<<<<<<< HEAD
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
=======
>>>>>>> c70d6ed... add: new shadow every 10 seconds

/**
 * Created by kennethlimcp on 08/Mar/2017.
 */

public class ShadowManagement extends Thread {
    private MultiplayerGame game = null;
<<<<<<< HEAD
    private CopyOnWriteArrayList<Shadow> shadows = new CopyOnWriteArrayList<Shadow>();
    private final Object shadowsLock = new Object();
=======
    private ArrayList<Shadow> shadows = new ArrayList<Shadow>();
>>>>>>> c70d6ed... add: new shadow every 10 seconds

    public ShadowManagement(MultiplayerGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        calculateShadowStartPosition();
<<<<<<< HEAD

        Random rand = new Random();
        long oldTime = System.currentTimeMillis();

        while(true) {
            if(shadows.size() == 0) {
                int randomShadow = rand.ints(1, 0, game.getPillarPositions().size()).findFirst().getAsInt();

                Rectangle r = game.getPillarPositions().get(randomShadow);
                shadows.add(new Shadow((PlayScreen) game.getScreen(), r.getX(), r.getY()));
                System.out.println("Spawning new shadow");
            }

            synchronized (shadowsLock) {
                for(Shadow s: shadows) {
                    if (!s.isAlive()) {
                        shadows.remove(s);
                    }
                }
            }
=======
        //this will draw all the shawdow for testing as we prefer to create them randomly
        long oldTime = System.currentTimeMillis();

        for(Rectangle r: game.getPillarPositions()) {
            while(System.currentTimeMillis() - oldTime < 10000) {};
            shadows.add(new Shadow((PlayScreen) game.getScreen(), r.getX(), r.getY()));
            shadows.get(shadows.size()-1).update(1);
            oldTime = System.currentTimeMillis();
            System.out.println("Shadow created!");
        }

        while(true) {
>>>>>>> c70d6ed... add: new shadow every 10 seconds

        }
    }

<<<<<<< HEAD


=======
>>>>>>> c70d6ed... add: new shadow every 10 seconds
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
<<<<<<< HEAD
        synchronized (shadowsLock) {
            for(Shadow s: shadows) {
                s.update(dt);
            }
=======
        for(Shadow s: shadows) {
            s.update(dt);
>>>>>>> c70d6ed... add: new shadow every 10 seconds
        }
    }
}
