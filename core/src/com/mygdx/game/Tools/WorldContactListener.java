package com.mygdx.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * Created by Pin on 21-Feb-17.
 */

public class WorldContactListener implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
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
