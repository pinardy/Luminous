package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;

/**
 * Created by Pin on 06-Feb-17.
 */

// Sprite is a library from LibGDX
public abstract class Object extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;

    public Object(PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineObject();
    }

    protected abstract void defineObject();
}
