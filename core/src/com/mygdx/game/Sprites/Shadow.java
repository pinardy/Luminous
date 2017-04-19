package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.utils.Array;
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
    private boolean graphics = true;

    //graphics
    private TextureRegion shadowMan;
    private float creationX;
    private float creationY;
    private Animation<TextureRegion> shadowRun;

    public Shadow(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTime = 0;
        hitPillar = false;
        alive = true;

        initializeGraphics(true);
    }

    public Shadow(PlayScreen screen, float x, float y, boolean graphics) {
        super(screen, x, y, graphics);
        stateTime = 0;
        hitPillar = false;
        alive = true;
        this.graphics = graphics;

        initializeGraphics(graphics);
    }

    // construct from server
    public Shadow(PlayScreen screen, float x, float y, int time) {
        this(screen, x, y);
        serverTime = time;

        initializeGraphics(true);
    }

    private void initializeGraphics(boolean graphics) {
        if(graphics) {
            shadowMan = new TextureRegion(getTexture(), 0, 0, 16, 16);
            setBounds(0, 0, 16, 16);
            setRegion(shadowMan);

            Array<TextureRegion> frames = new Array<TextureRegion>();
            for (int i = 0; i < 12; i++) {
                frames.add(new TextureRegion(getTexture(), i * 32, 0, 32, 57));
            }
            shadowRun = new Animation(0.1f, frames);
        }
    }

    public void update(float dt){
        stateTime += dt;

        //to render graphics on fixture
        setPosition(b2body.getPosition().x - getWidth()/2,
                b2body.getPosition().y - getHeight()/2);

        //for animation
        if(graphics&&shadowRun!=null) setRegion(shadowRun.getKeyFrame(stateTime, true)); //boolean for looping

        // if the Shadow object exists
        if(b2body != null) {
            float speedX = -(creationX - coreX)/(speed);
            float speedY = -(creationY - coreY)/(speed);
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
        creationX = getX();
        creationY = getY();
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

    public int getServerTime() {
        return serverTime;
    }
}
