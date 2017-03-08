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
    private float speed = 10;

    public Shadow(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        setBounds(getX(), getY(), 16, 16);
    }

    public void update(float dt){
        stateTime += dt;

        float speedX = -(getX() - coreX)/speed;
        float speedY = -(getY() - coreY)/speed;

        b2body.setLinearVelocity(speedX, speedY);
        b2body.setActive(false);
    }

    public void setActive(boolean active) {
        b2body.setActive(active);
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
