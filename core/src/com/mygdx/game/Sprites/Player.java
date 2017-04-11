package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;

/** A Player is able to pick up an Orb and place it on a Pillar object
 */

public class Player extends Object {
    private boolean holdingOrb;
    public Orb mOrb;
    int startPosX;
    int startPosY;
    String id;

    public Player(World world) {
        this(world, (float)500, (float)500);
    }

    public Player(World world, float x, float y) {
        this(world, x, y, 500, 500);
    }

    public Player(World world, float x, float y, String id) {
        this(world, x, y, 500, 500);
        this.id = id;
    }

    public Player(World world, float x, float y, int startPosX, int startPosY) {
        super(world, x, y);
        this.startPosX = startPosX;
        this.startPosY = startPosY;
        this.holdingOrb = false;
        this.mOrb = null;
        this.id = "0";
        defineObject();
    }

    @Override
    public void defineObject() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startPosX, startPosY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        fdef.shape = shape;

        // creates the fixture for the body and sets the data to it
        fixture = b2body.createFixture(fdef);

        // The Player fixture is categorized as a Player using PLAYER_BIT
        setCategoryFilter(MultiplayerGame.PLAYER_BIT);

        // The player can collide with these
        setMaskFilter((short) ( MultiplayerGame.DEFAULT_BIT |
                                MultiplayerGame.PILLAR_BIT |
                                MultiplayerGame.LIGHTEDPILLAR_BIT |
                                MultiplayerGame.CORE_BIT |
                                MultiplayerGame.ORB_BIT |
                                MultiplayerGame.SHADOW_BIT));

        fixture.setUserData(this);
    }


    public void update(float dt) {
    }

    public void orbPick(int id) {
        setHoldingOrb(true);
        mOrb = PlayScreen.listOfOrbs.get(id);
    }

    public void orbPick(Orb orb) {
        setHoldingOrb(true);
        mOrb = orb;
    }

    public boolean isHoldingOrb() {
        return holdingOrb;
    }

    public Orb getHoldingOrb() {
        return this.mOrb;
    }

    private void setHoldingOrb(boolean state) {
        holdingOrb = state;
    }

    public Orb orbDrop() {
        setHoldingOrb(false);
//        mOrb.setPosition(b2body.getPosition().x+10, b2body.getPosition().y);
        mOrb.dropOrb();
        Orb toReturn = mOrb;
        mOrb = null;
        return toReturn;
    }
}
