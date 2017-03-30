package com.mygdx.game.Sprites;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;


/** Orb is the 'tool' of the game.
 * Players can pick up the Orb object
 * We do not want the Shadow to reach the Core
 * We prevent this by putting Orbs on the Pillars
 * This will make the Pillar object lit, thus causing the Shadow to disappear
 */

public class Orb extends Object{

    private float stateTime;
    private boolean setToPicked;
    private boolean picked;
    static float startPosX = 500;
    static float startPosY = 600;
    public int id = 0;

    public Orb(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        setBounds(getX(), getY(), 16, 16);
        setToPicked = false;
        picked = false;
        id = 0;
    }

    public Orb(PlayScreen screen, float x, float y, int id) {
        super(screen, x, y);
        stateTime = 0;
        setBounds(getX(), getY(), 16, 16);
        setToPicked = false;
        picked = false;
        id = 0;
    }

    public Orb(PlayScreen screen, float x, float y, float posX, float posY) {
        super(screen, x, y);
        startPosX = posX;
        startPosY = posY;
        stateTime = 0;
        setBounds(getX(), getY(), 16, 16);
        setToPicked = false;
        picked = false;
    }


    public void update(float dt){
        stateTime += dt;
        if (setToPicked && !picked){
            world.destroyBody(b2body);
            picked = true;
        }
        else if (!picked) {
            // move a bit over half the width of the sprite, and down half the height
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }

    }

    @Override
    protected void defineObject() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startPosX, startPosY);
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        // instantiate the fixture for the Orb object
        FixtureDef fdef = new FixtureDef();

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        fdef.shape = shape;

        // the Orb fixture is categorized as an Orb using ORB_BIT
        fdef.filter.categoryBits = MultiplayerGame.ORB_BIT;

        // an Orb object can collide with these
        fdef.filter.maskBits = MultiplayerGame.PILLAR_BIT
                | MultiplayerGame.PLAYER_BIT;

        // creates the fixture for the body and sets the data to it
        b2body.createFixture(fdef).setUserData(this);
    }

    public void getPicked() {
        setToPicked = true;
    }

    public void getDropped(){
        setToPicked = false;
        picked = false;
    }
}
