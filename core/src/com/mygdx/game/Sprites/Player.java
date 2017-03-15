package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MultiplayerGame;

/**
 * Created by Pin on 04-Feb-17.
 */

public class Player extends Sprite {
    public World world;
    public Body b2body;
    private boolean pickingOrb = false;


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
                MultiplayerGame.CORE_BIT |
                MultiplayerGame.ORB_BIT |
                MultiplayerGame.SHADOW_BIT;


        b2body.createFixture(fdef).setUserData(this);
    }


    public void update(float dt) {
        // player interaction
    }

    public void orbPick() {
        pickingOrb = true;
    }

    public void orbDrop() {
        pickingOrb = false;
    }
}
