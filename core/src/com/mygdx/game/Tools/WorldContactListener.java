package com.mygdx.game.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Sprites.Orb;
import com.mygdx.game.Sprites.Pillar;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.Sprites.Shadow;

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
            // =-=-= PLAYER collides with ORB =-=-=
            case MultiplayerGame.ORB_BIT | MultiplayerGame.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == MultiplayerGame.ORB_BIT){
                    if (((Player)fixA.getUserData()).holdingOrb == false) {
                        boolean pickOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                        boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                        if (pickOrb | pickOrbAndroid) {
                            // Updates Player's status to pickingOrb
                            ((Player) fixA.getUserData()).orbPick();

                            // Updates Orb's status to getPicked
                            ((Orb) fixA.getUserData()).getPicked();

                            Gdx.app.log("Picking orb", "");
                        }
                    }
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.ORB_BIT) {
                    if (((Player) fixA.getUserData()).holdingOrb == false) {
                        boolean pickOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                        boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                        if (pickOrb | pickOrbAndroid) {
                            // Updates Player's status to pickingOrb
                            ((Player) fixA.getUserData()).orbPick();

                            // Updates Orb's status to getPicked
                            ((Orb) fixB.getUserData()).getPicked();

                            MultiplayerGame.manager.get("audio/sounds/pickOrb.mp3", Sound.class).play();

                            Gdx.app.log("Picking orb", "");
                        }
                    }
                }
                break;

            // =-=-= SHADOW collides with CORE =-=-=
            case MultiplayerGame.SHADOW_BIT | MultiplayerGame.CORE_BIT:
                if (fixA.getFilterData().categoryBits == MultiplayerGame.SHADOW_BIT){
                    ((Shadow) fixA.getUserData()).collided();
                    Hud.reduceHealth();
                    MultiplayerGame.manager.get("audio/sounds/evilCrack.mp3", Sound.class).play();
                    Gdx.app.log("Shadow hits core","fixA");
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.SHADOW_BIT){
                    ((Shadow) fixB.getUserData()).collided();
                    Hud.reduceHealth();
                    MultiplayerGame.manager.get("audio/sounds/evilCrack.mp3", Sound.class).play();
                    Gdx.app.log("Shadow hits core","fixB");
                }
                break;

            // =-=-= SHADOW collides with LIGHTED PILLAR =-=-=
            case MultiplayerGame.SHADOW_BIT | MultiplayerGame.LIGHTEDPILLAR_BIT :
                if (fixA.getFilterData().categoryBits == MultiplayerGame.SHADOW_BIT){
                    ((Shadow) fixA.getUserData()).collided();
                    Hud.addScore(10);
                    Gdx.app.log("Shadow hits pillar","fixA");
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.SHADOW_BIT){
                    ((Shadow) fixB.getUserData()).collided();
                    Hud.addScore(10);
                    Gdx.app.log("Shadow hits pillar","fixB");
                }
                break;

            // =-=-= ORB collides with PILLAR =-=-=
            case MultiplayerGame.ORB_BIT | MultiplayerGame.PILLAR_BIT:
                // do nothing
                break;

            // =-=-= PLAYER collides with PILLAR =-=-=
            case MultiplayerGame.PLAYER_BIT | MultiplayerGame.PILLAR_BIT:
                if (fixA.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean placeOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                    if (placeOrb | pickOrbAndroid){
                        if (((Player)fixA.getUserData()).holdingOrb == true) {
                            // Updates Pillar's status to lighted
                            ((Pillar) fixB.getUserData()).setCategoryFilter(MultiplayerGame.LIGHTEDPILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();

                            // Updates Player's status to not carrying orb
                            ((Player) fixA.getUserData()).orbDrop();

                            Gdx.app.log("Pillar is LIT", "");
                        }
                    }
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean placeOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                    if (placeOrb | pickOrbAndroid){
                        if (((Player)fixB.getUserData()).holdingOrb == true) {
                            // Updates Pillar's status to lighted
                            ((Pillar) fixA.getUserData()).setCategoryFilter(MultiplayerGame.LIGHTEDPILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();

                            // Updates Player's status to not carrying orb
                            ((Player) fixB.getUserData()).orbDrop();

                            Gdx.app.log("Pillar is LIT", "");
                        }
                    }
                }
                break;

            // =-=-= PLAYER collides with LIGHTED PILLAR =-=-=
            case MultiplayerGame.PLAYER_BIT | MultiplayerGame.LIGHTEDPILLAR_BIT:
                if (fixA.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean grabOrb = Gdx.input.isKeyPressed(Input.Keys.S);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                    if (grabOrb | pickOrbAndroid){
                        if (((Player)fixA.getUserData()).holdingOrb == false) {
                            // Updates Pillar's status to lighted
                            ((Pillar) fixB.getUserData()).setCategoryFilter(MultiplayerGame.PILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();

                            // Updates Player's status to not carrying orb
                            ((Player) fixA.getUserData()).orbPick();

                            Gdx.app.log("Picked orb from pillar", "");
                        }
                    }
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean grabOrb = Gdx.input.isKeyPressed(Input.Keys.S);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();


                    if (grabOrb | pickOrbAndroid){
                        if (((Player)fixB.getUserData()).holdingOrb == false) {
                            // Updates Pillar's status to lighted
                            ((Pillar) fixA.getUserData()).setCategoryFilter(MultiplayerGame.PILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();

                            // Updates Player's status to not carrying orb
                            ((Player) fixB.getUserData()).orbPick();

                            Gdx.app.log("Picked orb from pillar", "");
                        }
                    }
                }
                break;
        }
//        Gdx.app.log("Begin contact","");
    }

    @Override
    public void endContact(Contact contact) {
//        Gdx.app.log("End contact","");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
