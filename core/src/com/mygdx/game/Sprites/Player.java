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

public class Player extends Sprite {
    public World world;
    public Body b2body;
    public boolean holdingOrb = false;
    public Orb mOrb = null;
    public String id;


    public Player(World world){
        this.world = world;
        definePlayer(500, 500);
    }

    public Player(World world, float x, float y, String id){
        this.world = world;
        this.id = id;
        definePlayer(x, y);
    }

    // initialize the player at position (x, y) in the map
    public void definePlayer(float x, float y) {
        BodyDef bdef = new BodyDef();
        bdef.position.set(x, y);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // instantiate the fixture for the Player object
        FixtureDef fdef = new FixtureDef();

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        fdef.shape = shape;

        // The Player fixture is categorized as a Player using PLAYER_BIT
        fdef.filter.categoryBits = MultiplayerGame.PLAYER_BIT;

        // The player can collide with these
        fdef.filter.maskBits = MultiplayerGame.DEFAULT_BIT |
                MultiplayerGame.PILLAR_BIT |
                MultiplayerGame.LIGHTEDPILLAR_BIT |
                MultiplayerGame.CORE_BIT |
                MultiplayerGame.ORB_BIT |
                MultiplayerGame.SHADOW_BIT;

        // creates the fixture for the body and sets the data to it
        b2body.createFixture(fdef).setUserData(this);
    }


    public void update(float dt) {
    }

    public void orbPick(int id) {
        holdingOrb = true;
        mOrb = PlayScreen.listOfOrbs.get(id);
    }

    public void orbPick(Orb orb) {
        holdingOrb = true;
        mOrb = orb;
    }

    // Drop the orb and return it
    public Orb orbDrop() {
        holdingOrb = false;
//        mOrb.setPosition(b2body.getPosition().x+10, b2body.getPosition().y);
        mOrb.getDropped();
        Orb toReturn = mOrb;
        mOrb = null;
        return toReturn;
    }
}
