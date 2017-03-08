package com.mygdx.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Sprites.Orb;

/**
 * Created by Pin on 21-Feb-17.
 */

public class WorldContactListener implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // We 'OR' the category bits of the fixtures when they collide together to get collision definition
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            //TODO: Fix crash for orb picking
            case MultiplayerGame.ORB_BIT | MultiplayerGame.PLAYER_BIT :
                if (fixA.getFilterData().categoryBits == MultiplayerGame.ORB_BIT){
                    boolean pickOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                    if (pickOrb){
//                        fixA.getUserData().orbPick();
                        ((Orb)fixA.getUserData()).getPicked();
                        Gdx.app.log("Picking orb","");
                    }
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.ORB_BIT){
                    boolean pickOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                    if (pickOrb){
//                        fixA.getUserData().orbPick();
                        ((Orb)fixB.getUserData()).getPicked();
                        Gdx.app.log("Picking orb","");
                    }
                }

        }


        Gdx.app.log("Begin contact","");
    }

    @Override
    public void endContact(Contact contact) {
        Gdx.app.log("End contact","");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
