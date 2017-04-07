package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;


// Sprite is a library from LibGDX
public abstract class Object extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;

    public Object(PlayScreen screen, float x, float y){
        super(screen.getAtlas().findRegion("shadowman")); //temporary
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineObject();
    }

    // Every object is defined differently. Hence this method is abstract
    protected abstract void defineObject();
}
