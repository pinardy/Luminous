package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Tools.B2WorldCreator;
import com.mygdx.game.Tools.WorldContactListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

        initializeGraphics(true);
    }

    public Orb(PlayScreen screen, float x, float y, boolean graphics) {
        super(screen, x, y, graphics);
        stateTime = 0;
        setBounds(getX(), getY(), 16, 16);
        ToPick = false;
        picked = false;
        this.id = 0;

        initializeGraphics(graphics);
    }

    public Orb(PlayScreen screen, float x, float y, int id, boolean graphics) {
        this(screen, x, y, graphics);
        this.id = id;
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

    private void initializeGraphics(boolean graphics) {
        //graphics
        if(graphics) {
            orbGraphics = new TextureRegion(getTexture(),1, 199, 400, 300);
            setBounds(getX(), getY(), 16,16);
            setRegion(orbGraphics);
        }
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

    public static boolean onFloor(Orb orb){
        if (!WorldContactListener.multiplayer) { //single player
            for (Pillar p : B2WorldCreator.listOfPillars){
                if (p.hasOrb()){
                    return false;
                }
            }
            if (PlayScreen.player.isHoldingOrb()){
                return false;
            }else{
                return true;
            }
        }else{//multiplayer
            for (Pillar p : B2WorldCreator.listOfPillars){
                if (p.hasOrb() && p.getmOrb().getID()==orb.getID()){ //if orb is on pillar
                    return false;
                }
            }

            for (Map.Entry<String, Player> player : PlayScreen.getPlayers().entrySet()) {
                if (player.getValue().isHoldingOrb() && player.getValue().getHoldingOrb().getID() == orb.getID()){
                    return false;
                }
            }
            return true;
        }
    }
}
