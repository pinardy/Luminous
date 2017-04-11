package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Tools.WorldContactListener;


/** Orb is the 'tool' of the game.
 * Players can pick up the Orb object
 * We do not want the Shadow to reach the Core
 * We prevent this by putting Orbs on the Pillars
 * This will make the Pillar object lit, thus causing the Shadow to disappear
 */

public class Orb extends Object{

    private float stateTime;
    private boolean ToPick;
    private boolean picked;
    static float startPosX = 500;
    static float startPosY = 600;
    private int id;
    private TextureRegion orbGraphics;

    public Orb(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        setBounds(getX(), getY(), 16, 16);
        ToPick = false;
        picked = false;
        this.id = 0;

        //graphics
        orbGraphics = new TextureRegion(getTexture(), 55, 67, 350, 367);
        setBounds(getX(), getY(), 16,16);
        setRegion(orbGraphics);
    }

    public Orb(PlayScreen screen, float x, float y, int id) {
        this(screen, x, y);
        this.id = 0;

    }


    public Orb(PlayScreen screen, float x, float y, float posX, float posY, int id) {
        this(screen, x, y, posX, posY);

        this.id = id;
    }

    public Orb(PlayScreen screen, float x, float y, float posX, float posY) {
        this(screen, x, y);
        startPosX = posX;
        startPosY = posY;
    }

    public void update(float dt){
        stateTime += dt;
        if (getToPick() && !getPicked()){
            world.destroyBody(b2body);
            setPicked();
        }
        else if (!getPicked()) {
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

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        fdef.shape = shape;

        // creates the fixture for the body and sets the data to it
        fixture = b2body.createFixture(fdef);

        // the Orb fixture is categorized as an Orb using ORB_BIT
        setCategoryFilter(MultiplayerGame.ORB_BIT);

        // an Orb object can collide with these
        setMaskFilter((short) (MultiplayerGame.PILLAR_BIT | MultiplayerGame.PLAYER_BIT));

        fixture.setUserData(this);
    }

    public boolean getToPick() {
        return ToPick;
    }

    public void setToPick() {
        ToPick = true;
    }

    public boolean getPicked() {
        return picked;
    }

    private void setPicked() {
        picked = true;
    }

    public void dropOrb(){
        ToPick = false;
        picked = false;
    }

    public int getID() {
        return this.id;
    }

//    public void setHangingOnPillar(int x){
//        if (x==0){
//            hangingOnPillar = false;
//        }else if(x==1){
//            hangingOnPillar = true;
//        }
//    }


    public boolean onFloor(){
        if (WorldContactListener.indicateOrb|| WorldContactListener.indicateOrbOnPillar) //whenever orb is held on player or hanging on pillar
            return false;
        return true;
    }

}
