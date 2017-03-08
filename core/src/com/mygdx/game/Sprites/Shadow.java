package com.mygdx.game.Sprites;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;

/**
 * Created by Pin on 06-Feb-17.
 */

public class Shadow extends Object{

    private float stateTime;
    private float coreX = MultiplayerGame.corePosition.getX() + MultiplayerGame.corePosition.getWidth()/2;
    private float coreY = MultiplayerGame.corePosition.getY() + MultiplayerGame.corePosition.getHeight()/2;

    public Shadow(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        setBounds(getX(), getY(), 16, 16);
    }

    public void update(float dt){
        stateTime += dt;

        float speedX = -(getX() - coreX)/10;
        float speedY = -(getY() - coreY)/10;


        b2body.setLinearVelocity(speedX, speedY);
        // move a bit over half the width of the sprite, and down half the height
//        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

    }

    @Override
    protected void defineObject() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);

        fdef.filter.categoryBits = MultiplayerGame.SHADOW_BIT;

        // The shadow can collide with these
        fdef.filter.maskBits = MultiplayerGame.PILLAR_BIT|
                            MultiplayerGame.CORE_BIT;


        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
    }
}
