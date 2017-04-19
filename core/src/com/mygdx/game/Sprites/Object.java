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

    private void initialize(World world, float x, float y) {
        fdef = new FixtureDef();
        this.filter = new Filter();

        this.world = world;
        setPosition(x, y);
        defineObject();
    }
    public Object(World world, float x, float y) {
        initialize(world, x, y);
    }

    public Object(World world, PlayScreen screen, float x, float y){
        super(screen.getAtlas().findRegion("shadowman"));
        initialize(world, x, y);
    }

    public Object(PlayScreen screen, float x, float y){
        super(screen.getAtlas().findRegion("shadowman"));
        initialize(screen.getWorld(), x, y);
    }

    public Object(PlayScreen screen, float x, float y, float posX, float posY){
        super(screen.getAtlas().findRegion("shadowman"));
        initialize(screen.getWorld(), posX, posY);
    }

    //Only used for testing to avoid invoking super()
    public Object(PlayScreen screen, float x, float y, boolean test){
        initialize(screen.getWorld(), x, y);
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
