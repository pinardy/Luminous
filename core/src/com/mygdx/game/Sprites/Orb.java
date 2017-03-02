package com.mygdx.game.Sprites;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;

/**
 * Created by Pin on 06-Feb-17.
 */

public class Orb extends Object{

    private float stateTime;

    public Orb(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        setBounds(getX(), getY(), 16, 16);
    }

    public void update(float dt){
        stateTime += dt;
        // move a bit over half the width of the sprite, and down half the height
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

    }

    @Override
    protected void defineObject() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(12, 50);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        fdef.shape = shape;

        fdef.filter.categoryBits = MultiplayerGame.ORB_BIT;

        // The orb can collide with these
        fdef.filter.maskBits = MultiplayerGame.PILLAR_BIT
                | MultiplayerGame.PLAYER_BIT;

        b2body.createFixture(fdef);
    }
}
