package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;
import com.sun.org.apache.xpath.internal.operations.Or;

/**
 * Created by Pin on 04-Feb-17.
 */

public class Player extends Sprite {
    public World world;
    public Body b2body;
    public boolean holdingOrb = false;
    public Orb mOrb = null;


    public Player(World world){
        this.world = world;
        definePlayer();
    }

    public void definePlayer() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(500, 500);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        fdef.shape = shape;

        fdef.filter.categoryBits = MultiplayerGame.PLAYER_BIT;

        // The player can collide with these
        fdef.filter.maskBits = MultiplayerGame.DEFAULT_BIT |
                MultiplayerGame.PILLAR_BIT |
                MultiplayerGame.LIGHTEDPILLAR_BIT |
                MultiplayerGame.CORE_BIT |
                MultiplayerGame.ORB_BIT |
                MultiplayerGame.SHADOW_BIT;

        fdef.friction = 10f;
        b2body.createFixture(fdef).setUserData(this);
    }


    public void update(float dt) {
        // player interaction
    }

    public void orbPick(int id) {
        holdingOrb = true;
        mOrb = PlayScreen.listOfOrbs.get(id);
    }

    public void orbPick(Orb orb) {
        holdingOrb = true;
        mOrb = orb;
    }

    public Orb orbDrop() {
        holdingOrb = false;
        mOrb.setPosition(b2body.getPosition().x+10, b2body.getPosition().y);
        mOrb.getDroped();
        Orb toReturn = mOrb;
        mOrb = null;
        return toReturn;
    }
}
