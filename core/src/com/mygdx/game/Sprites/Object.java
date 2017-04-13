package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;


// Sprite is a library from LibGDX
public abstract class Object extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    protected FixtureDef fdef;
    protected Fixture fixture;
    protected Filter filter;

    //TODO: Figure how to overload the constructors
    public Object(World world, float x, float y){
        fdef = new FixtureDef();
        this.filter = new Filter();

        this.world = world;
        setPosition(x, y);
    }

    public Object(World world, PlayScreen screen, float x, float y){
        super(screen.getAtlas().findRegion("shadowman"));

        fdef = new FixtureDef();
        this.filter = new Filter();

        this.world = world;
        setPosition(x, y);
        defineObject();
    }

    public Object(PlayScreen screen, float x, float y){
        super(screen.getAtlas().findRegion("shadowman"));

        fdef = new FixtureDef();
        this.filter = new Filter();

        this.world = screen.getWorld();
        setPosition(x, y);
        defineObject();
    }


    // Every object is defined differently. Hence this method is abstract
    protected abstract void defineObject();

    public void setCategoryFilter(short filterBit){
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public void setMaskFilter(short filterBit){
        filter.maskBits = filterBit;
        fixture.setFilterData(filter);
    }

    public Filter getFilterData() {
        return fixture.getFilterData();
    }
}
