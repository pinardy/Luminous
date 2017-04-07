package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;

/** Shadow is the 'enemy' of the game.
 * We do not want the Shadow to reach the Core
 * We prevent this by putting Orbs on the Pillars
 * This will make the Pillar object lit, thus causing the Shadow to disappear
 */


public class Shadow extends Object{
    private int serverTime;

    private float stateTime;
    private float coreX = MultiplayerGame.corePosition.getX() + MultiplayerGame.corePosition.getWidth()/2;
    private float coreY = MultiplayerGame.corePosition.getY() + MultiplayerGame.corePosition.getHeight()/2;
    private float speed = 10;
    private boolean hitPillar;
    private boolean alive;
    private TextureRegion shadowMan;

    public Shadow(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        hitPillar = false;
        alive = true;

        shadowMan = new TextureRegion(getTexture(), 0,0,16,16);
//        setBounds(getX(), getY(), 16, 16);
        setBounds(0,0,16,16);
        setRegion(shadowMan);
    }

    // construct from server
    public Shadow(PlayScreen screen, float x, float y, int time) {
        super(screen, x, y);
        serverTime = time;
        stateTime = 0;
        hitPillar = false;
        alive = true;
        setBounds(getX(), getY(), 16, 16);
    }

    public void update(float dt){
        stateTime += dt;
        setPosition(b2body.getPosition().x - getWidth()/2,
                b2body.getPosition().y - getHeight()/2);

        // if the Shadow object exists
        if(b2body != null) {
            float speedX = -(getX() - coreX)/speed;
            float speedY = -(getY() - coreY)/speed;
            b2body.setLinearVelocity(speedX, speedY);
        }

        // if the Shadow hits a lit pillar and is alive, we destroy it
        if(hitPillar && alive) {
            world.destroyBody(b2body);
            alive = false;
        }
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

        // instantiate the fixture for the Shadow object
        FixtureDef fdef = new FixtureDef();

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        fdef.shape = shape;

        // the Shadow fixture is categorized as an Shadow using SHADOW_BIT
        fdef.filter.categoryBits = MultiplayerGame.SHADOW_BIT;

        // The shadow can collide with these
        fdef.filter.maskBits = MultiplayerGame.CORE_BIT | MultiplayerGame.LIGHTEDPILLAR_BIT;

        // creates the fixture for the body and sets the data to it
        b2body.createFixture(fdef).setUserData(this);
    }

    public void collided() {
        hitPillar = true;
    }

    public boolean isAlive() {
        return alive;
    }
}
