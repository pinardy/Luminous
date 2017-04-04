package com.mygdx.game.Sprites;

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

    private float stateTime;
    private float coreX = MultiplayerGame.corePosition.getX() + MultiplayerGame.corePosition.getWidth()/2;
    private float coreY = MultiplayerGame.corePosition.getY() + MultiplayerGame.corePosition.getHeight()/2;
    private float speed = 10;
    private boolean hitPillar;
    private boolean alive;

    public Shadow(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        hitPillar = false;
        alive = true;
        setBounds(getX(), getY(), 16, 16);
    }

    public void update(float dt){
        stateTime += dt;

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

    public void collided() {
        hitPillar = true;
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    protected void defineObject() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        fdef.shape = shape;

        // creates the fixture for the body and sets the data to it
        fixture = b2body.createFixture(fdef);

        // the Shadow fixture is categorized as an Shadow using SHADOW_BIT
        setCategoryFilter(MultiplayerGame.SHADOW_BIT);

        // The shadow can collide with these
        setMaskFilter((short) (MultiplayerGame.CORE_BIT | MultiplayerGame.LIGHTEDPILLAR_BIT));

        fixture.setUserData(this);
    }

}
