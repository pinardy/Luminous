package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Tools.WorldContactListener;

import java.util.Arrays;
import java.util.Map;

/** A Player is able to pick up an Orb and place it on a Pillar object
 */

public class Player extends Object {
    private boolean holdingOrb;
    public Orb mOrb;
    int startPosX;
    int startPosY;
    String id;

    //Animation and graphics
    private TextureRegion playerStand;
    public enum State{UP, DOWN, LEFT, RIGHT, STAND}
    public State currentState;
    public State prevState;
    public static Player playerE;
    private Animation<TextureRegion> playerUp, playerDown, playerLeft, playerRight;
    private Animation<TextureRegion> playerUpB, playerDownB, playerLeftB, playerRightB;

    private float stateTimer;

    public Player(World world){
        this(world, (float)500, (float)500);
    }
    public Player(World world, float x, float y){
        super(world, x, y);
    }
    public Player(World world, PlayScreen screen) {
        this(world, screen, (float)500, (float)500);
    }
    public Player(World world, PlayScreen screen, float x, float y) {
        this(world, screen, x, y, 500, 500);
    }
    public Player(World world, PlayScreen screen, float x, float y, String id) {
        this(world, screen, x, y, 500, 500);
        this.id = id;
    }

    public Player(World world, PlayScreen screen, float x, float y, int startPosX, int startPosY) {
        super(world, screen, startPosX, startPosY);
        this.world = world;
        this.startPosX = startPosX;
        this.startPosY = startPosY;
        this.holdingOrb = false;
        this.id = "0";

        //graphics
        playerStand = new TextureRegion(getTexture(), 400,237,32,32);
        setBounds(getX(), getY(), 16, 16);
        setRegion(playerStand);

        //Animation
        currentState = State.STAND;
        prevState = State.STAND;
        stateTimer = 0;

        //fixture
        defineObject();

        Array<TextureRegion> frames = new Array<TextureRegion>(12);
        for (int row = 0; row < 4; row++){
            for (int col = 0; col < 3; col++) {
                frames.add(new TextureRegion(getTexture(), 400+col*32, 237+row*32, 32, 32));
            }
        }

        playerDown = new Animation(0.1f, frames.get(0), frames.get(1), frames.get(2));
        playerLeft = new Animation(0.1f, frames.get(3), frames.get(4), frames.get(5));
        playerRight = new Animation(0.1f, frames.get(6), frames.get(7), frames.get(8));
        playerUp = new Animation(0.1f, frames.get(9), frames.get(10), frames.get(11));

        playerDownB = new Animation(0.1f, frames.get(0), frames.get(1), frames.get(2));
        playerLeftB = new Animation(0.1f, frames.get(3), frames.get(4), frames.get(5));
        playerRightB = new Animation(0.1f, frames.get(6), frames.get(7), frames.get(8));
        playerUpB = new Animation(0.1f, frames.get(9), frames.get(10), frames.get(11));

    }


    @Override
    public void defineObject() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(startPosX, startPosY);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // shape
        CircleShape shape = new CircleShape();
        shape.setRadius(10);
        fdef.shape = shape;

        // creates the fixture for the body and sets the data to it
        fixture = b2body.createFixture(fdef);

        // The Player fixture is categorized as a Player using PLAYER_BIT
        setCategoryFilter(MultiplayerGame.PLAYER_BIT);

        // The player can collide with these
        setMaskFilter((short) ( MultiplayerGame.DEFAULT_BIT |
                MultiplayerGame.PILLAR_BIT |
                MultiplayerGame.LIGHTEDPILLAR_BIT |
                MultiplayerGame.CORE_BIT |
                MultiplayerGame.ORB_BIT |
                MultiplayerGame.SHADOW_BIT));

        fixture.setUserData(this);
    }


    public void update(float dt) {
        //to render graphics on fixture
        if (WorldContactListener.multiplayer){
            for (Map.Entry<String, Player> player : PlayScreen.getPlayers().entrySet()) {
                if (!player.getKey().equals(PlayScreen.player.getID())){
                    //render for other players
                    playerE = player.getValue(); //set value for playscreen
                    player.getValue().setPosition(player.getValue().b2body.getPosition().x - getWidth() / 2,
                        player.getValue().b2body.getPosition().y - getHeight() / 2);
                    player.getValue().setRegion(getOtherFrame(dt,player.getValue()));
                    PlayScreen.returnPlayerEPos = null;
                }
                else{
                    player.getValue().setPosition(b2body.getPosition().x - getWidth()/2,
                            b2body.getPosition().y - getHeight()/2);

                    player.getValue().setRegion(getFrame(dt));//for animation
                }
            }
        }else{
            //single player
            setPosition(b2body.getPosition().x - getWidth()/2,
                    b2body.getPosition().y - getHeight()/2);

            setRegion(getFrame(dt));//for animation
        }
    }

    //for singleplayer
    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region = playerStand;
        switch(currentState){
            case UP:
                region = playerUp.getKeyFrame(stateTimer, true);
                break;
            case DOWN:
                region = playerDown.getKeyFrame(stateTimer, true);
                break;
            case LEFT:
                region = playerLeft.getKeyFrame(stateTimer, true);
                break;
            case RIGHT:
                region = playerRight.getKeyFrame(stateTimer, true);
                break;
            case STAND:
                //static
                region = playerStand;
                break;
        }
        stateTimer = stateTimer + dt;
        return region;
    }

    //rendering other player for multiplayer
    public TextureRegion getOtherFrame(float dt, Player player){

        State currentStateB = PlayScreen.returnPlayerEPos;
        TextureRegion region = playerStand;

        if (currentStateB==null){
            return playerStand;
        }

        switch(currentStateB){
            case UP:
                region = playerUpB.getKeyFrame(stateTimer, true);
                break;
            case DOWN:
                region = playerDownB.getKeyFrame(stateTimer, true);
                break;
            case LEFT:
                region = playerLeftB.getKeyFrame(stateTimer, true);
                break;
            case RIGHT:
                region = playerRightB.getKeyFrame(stateTimer, true);
                break;
            case STAND:
                region = playerStand;
                break;
        }
        return region;
    }

    //single player
    public State getState(){
        if(b2body.getLinearVelocity().y > 99){
            //up
            return State.UP;
        } else if (b2body.getLinearVelocity().y < -99){
            //down
            return State.DOWN;
        } else if (b2body.getLinearVelocity().x < -99){
            //left
            return State.LEFT;
        } else if (b2body.getLinearVelocity().x > 99){
            //right
            return State.RIGHT;
        }else{
            //not moving
            return State.STAND;
        }
    }

    //mult-player
    public State getMState(Player player) {
        State ret = State.STAND;
        for (String id : PlayScreen.playerActions.keySet()) {
            if (player.getID().equals(id)&& !PlayScreen.playerActions.get(id).isEmpty()) {

                Vector2 otherPlayerVec = PlayScreen.playerActions.get(id).poll();
                float xDistance = otherPlayerVec.x - player.getX();
                float yDistance = otherPlayerVec.y - player.getY();
                float zero = 20.000f;

                if (yDistance > zero) {
                    //up
                    ret = State.UP;
                } else if (yDistance < zero) {
                    //down
                    ret = State.DOWN;
                } else if (xDistance < zero) {
                    //left
                    ret = State.LEFT;
                } else if (xDistance > zero) {
                    //right
                    ret = State.RIGHT;
                } else {
                    //not moving
                    ret = State.STAND;
                }
            }
        }
        return ret;
    }


    public void orbPick(int id) {
        setHoldingOrb(true);
        mOrb = PlayScreen.listOfOrbs.get(id);
    }

    public void orbPick(Orb orb) {
        setHoldingOrb(true);
        mOrb = orb;
    }

    public boolean isHoldingOrb() {
        return holdingOrb;
    }

    public Orb getHoldingOrb() {
        return this.mOrb;
    }

    private void setHoldingOrb(boolean state) {
        holdingOrb = state;
    }

    public Orb orbDrop() {
        setHoldingOrb(false);
        mOrb.dropOrb();
        Orb toReturn = mOrb;
        mOrb = null;
        return toReturn;
    }

    public String getID(){
        return this.id;
    }
}
