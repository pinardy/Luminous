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
import com.mygdx.game.SocketClient;
import com.mygdx.game.Sprites.Orb;
import com.mygdx.game.Sprites.Pillar;
import com.mygdx.game.Sprites.Player;
import com.mygdx.game.Sprites.Shadow;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/** WorldContactListener handles the logic for the numerous combinations
 * of collisions between different objects
 *
 */

public class WorldContactListener implements ContactListener{
    public final int PICK_UP_ORB = 1001;
    public final int DROP_ORB = 1002;
    public final int PLACE_ORB = 1003;
    public final int PICK_PILLAR_ORB = 1004;
    private Socket socket;
    public static boolean multiplayer;
    public static int fullVisibility = 0;
    private boolean playerPillar = false;
    private boolean sameState = false;

    @Override
    public void beginContact(Contact contact) {
        if (multiplayer){
            socket = SocketClient.getInstance();
//            configureSocketOrb();
        }

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // We 'OR' the category bits of the fixtures when they collide together to get collision definition
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            // =-=-= PLAYER collides with ORB =-=-=  // done multiplayer
            case MultiplayerGame.ORB_BIT | MultiplayerGame.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == MultiplayerGame.ORB_BIT){
                    if (((Player)fixB.getUserData()).holdingOrb == false) {
                        boolean pickOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                        boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                        if (pickOrb | pickOrbAndroid) {
                            // Updates Orb's status to getPicked
                            Orb toBePicked = (Orb) fixA.getUserData();

                            // Updates Player's status to pickingOrb
                            if (multiplayer) updateServerOrb(PICK_UP_ORB, toBePicked.id);
                            else {
                                ((Player) fixB.getUserData()).orbPick(toBePicked.id);
                                toBePicked.getPicked();
                            }
                            Gdx.app.log("Picking orb", "");
                        }
                    }
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.ORB_BIT) {
                    if (((Player) fixA.getUserData()).holdingOrb == false) {
                        boolean pickOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                        boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                        if (pickOrb | pickOrbAndroid) {

                            // Updates Orb's status to getPicked
                            Orb toBePicked = (Orb) fixB.getUserData();

                            // Updates Player's status to pickingOrb
                            if (multiplayer) updateServerOrb(PICK_UP_ORB, toBePicked.id);
                            else {
                                ((Player) fixA.getUserData()).orbPick(toBePicked.id);
                                toBePicked.getPicked();
                            }

                            MultiplayerGame.manager.get("audio/sounds/pickOrb.mp3", Sound.class).play();

                            Gdx.app.log("Picking orb", "");
                        }
                    }
                }
                break;

            // =-=-= SHADOW collides with CORE =-=-=
            case MultiplayerGame.SHADOW_BIT | MultiplayerGame.CORE_BIT:
                sameState = true;

                if (fixA.getFilterData().categoryBits == MultiplayerGame.SHADOW_BIT){
                    ((Shadow) fixA.getUserData()).collided();
                    Hud.reduceHealth();
                    MultiplayerGame.manager.get("audio/sounds/evilCrack.mp3", Sound.class).play();
                    if (Hud.health == 0){
                        Hud.coreIsDead = true;
                    }
                    Gdx.app.log("Shadow hits core","fixA");
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.SHADOW_BIT){
                    ((Shadow) fixB.getUserData()).collided();
                    Hud.reduceHealth();
                    MultiplayerGame.manager.get("audio/sounds/evilCrack.mp3", Sound.class).play();

                    if (Hud.health == 0){
                        Hud.coreIsDead = true;
                    }
                    Gdx.app.log("Shadow hits core","fixB");
                }
                break;

            // =-=-= SHADOW collides with LIGHTED PILLAR =-=-=
            case MultiplayerGame.SHADOW_BIT | MultiplayerGame.LIGHTEDPILLAR_BIT :
                sameState = true;
                if (fixA.getFilterData().categoryBits == MultiplayerGame.SHADOW_BIT){
                    ((Shadow) fixA.getUserData()).collided();
                    Hud.addScore(10);
                    MultiplayerGame.manager.get("audio/sounds/shadowVanish.mp3", Sound.class).play();
                    Gdx.app.log("Shadow hits pillar","fixA");
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.SHADOW_BIT){
                    ((Shadow) fixB.getUserData()).collided();
                    Hud.addScore(10);
                    MultiplayerGame.manager.get("audio/sounds/shadowVanish.mp3", Sound.class).play();
                    Gdx.app.log("Shadow hits pillar","fixB");
                }
                break;

            // =-=-= ORB collides with PILLAR =-=-=
            case MultiplayerGame.ORB_BIT | MultiplayerGame.PILLAR_BIT:
                // do nothing
                break;

            // =-=-= PLAYER collides with PILLAR =-=-=  //
            case MultiplayerGame.PLAYER_BIT | MultiplayerGame.PILLAR_BIT:
                fullVisibility = 1;
                playerPillar = true;

                if (fixA.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean placeOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                    if (placeOrb | pickOrbAndroid){
                        if (((Player)fixA.getUserData()).holdingOrb == true) {
                            // Updates Pillar's status to lighted
                            Pillar pillar = ((Pillar) fixB.getUserData());
                            pillar.setCategoryFilter(MultiplayerGame.LIGHTEDPILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();

                            // Updates Player's status to not carrying orb
                            if (multiplayer) updateServerOrb(PLACE_ORB, pillar.id);
                            else {
                                Orb orb = ((Player) fixA.getUserData()).orbDrop();
                                pillar.setmOrb(orb);
                            }
                            Gdx.app.log("Pillar is LIT"+" with orb ", "");
                        }
                    }
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean placeOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                    if (placeOrb | pickOrbAndroid){
                        if (((Player)fixB.getUserData()).holdingOrb == true) {
                            // Updates Pillar's status to lighted
                            Pillar pillar = ((Pillar) fixA.getUserData());
                            pillar.setCategoryFilter(MultiplayerGame.LIGHTEDPILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();

                            // Updates Player's status to not carrying orb
                            if (multiplayer) updateServerOrb(PLACE_ORB, pillar.id);
                            else {
                                Orb orb = ((Player) fixB.getUserData()).orbDrop();
                                pillar.setmOrb(orb);
                            }
                            Gdx.app.log("Pillar is LIT" + " with orb ", "");
                        }
                    }
                }
                break;

            // =-=-= PLAYER collides with LIGHTED PILLAR =-=-=
            case MultiplayerGame.PLAYER_BIT | MultiplayerGame.LIGHTEDPILLAR_BIT:
                fullVisibility = 1;
                playerPillar = true;

                if (fixA.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean grabOrb = Gdx.input.isKeyPressed(Input.Keys.S);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                    if (grabOrb | pickOrbAndroid){
                        if (((Player)fixA.getUserData()).holdingOrb == false) {
                            // Updates Pillar's status to lighted
                            Pillar pillar = ((Pillar) fixB.getUserData());
                            pillar.setCategoryFilter(MultiplayerGame.PILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();

                            // Updates Player's status to not carrying orb
                            if (multiplayer) updateServerOrb(PICK_PILLAR_ORB, pillar.id);
                            else ((Player) fixA.getUserData()).orbPick(pillar.releaseOrb());

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
                            Pillar pillar = ((Pillar) fixA.getUserData());
                            pillar.setCategoryFilter(MultiplayerGame.PILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();

                            // Updates Player's status to not carrying orb
                            if (multiplayer) updateServerOrb(PICK_PILLAR_ORB, pillar.id);
                            else ((Player) fixB.getUserData()).orbPick(pillar.releaseOrb());

                            Gdx.app.log("Picked orb from pillar", "");
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (playerPillar && sameState) {
            fullVisibility = 1;
            playerPillar = false;
            sameState = false;
        }
        else if (sameState){
            //pass
            sameState = false;
        }
        else{
            fullVisibility = 0;
            playerPillar = false;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }


    private void updateServerOrb(int action, int id){

        JSONObject object = new JSONObject();

        try {
            switch (action) {
                case PICK_UP_ORB:
                    object.put("orbID", id);
                    socket.emit("pickUpOrb", object);
                    break;
                case DROP_ORB:
                    object.put("orbID", id);
                    socket.emit("dropOrb", object);
                    break;
                case PLACE_ORB:
                    object.put("pillarID", id);
                    socket.emit("placeOrbOnPillar", object);
                    break;
                case PICK_PILLAR_ORB:
                    object.put("pillarID", id);
                    socket.emit("pickOrbFromPillar", object);
                    break;
                default:
                    break;
            }
        }catch (JSONException e){
            Gdx.app.log("SocketIO", "Error sending message");
        }
    }
}
