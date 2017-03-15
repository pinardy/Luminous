package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Shadow;

import java.util.ArrayList;

/**
 * Created by kennethlimcp on 08/Mar/2017.
 */

public class ShadowManagement extends Thread {
    private MultiplayerGame game = null;
    private ArrayList<Shadow> shadows = new ArrayList<Shadow>();

    public ShadowManagement(MultiplayerGame game) {
        this.game = game;
    }

    @Override
    public void run() {
        calculateShadowStartPosition();
        //this will draw all the shawdow for testing as we prefer to create them randomly
        long oldTime = System.currentTimeMillis();

        for(Rectangle r: game.getPillarPositions()) {
            while(System.currentTimeMillis() - oldTime < 10000) {};

            shadows.add(new Shadow((PlayScreen) game.getScreen(), r.getX(), r.getY()));
            oldTime = System.currentTimeMillis();
            System.out.println("Shadow created!");
        }

        while(true) {

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
        for(Shadow s: shadows) {
            s.update(dt);
        }
    }
}
