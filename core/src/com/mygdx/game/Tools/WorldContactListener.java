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

import java.util.Map;

import io.socket.client.Socket;


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
    public static String selfPlayer;
    public static boolean multiplayer;

    //for shaders
    public static int fullVisibility = 0;
    public boolean playerPillar = false;
    public boolean sameState = false;
    public static boolean indicateOrb = false;
    public static boolean indicateOrbOnPillar = false;
    public static float lightedPillarX;
    public static float lightedPillarY;
    public boolean playerOrbPillar = false;

    @Override
    public void beginContact(Contact contact) {
        if (multiplayer){
            socket = SocketClient.getInstance();
        }

        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // We 'OR' the category bits of the fixtures when they collide together to get collision definition
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){
            // =-=-= PLAYER collides with ORB =-=-=  // done multiplayer
            case MultiplayerGame.ORB_BIT | MultiplayerGame.PLAYER_BIT:
                if (fixA.getFilterData().categoryBits == MultiplayerGame.ORB_BIT){
                    if (((Player) fixB.getUserData()).isHoldingOrb() == false) {
                        boolean pickOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                        boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                        if (pickOrb | pickOrbAndroid) {
                            // Updates Orb's status to setToPick
                            Orb toBePicked = (Orb) fixA.getUserData();
//                            indicateOrb = true;

                            // Updates Player's status to pickingOrb
                            if (multiplayer) updateServerOrb(PICK_UP_ORB, toBePicked.getID());
                            else {
                                ((Player) fixB.getUserData()).orbPick(toBePicked.getID());
                                toBePicked.setToPick();
                            }
                            Gdx.app.log("Picking orb", "");
                        }
                    }
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.ORB_BIT) {
                    if (((Player) fixA.getUserData()).isHoldingOrb() == false) {
                        boolean pickOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                        boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                        if (pickOrb | pickOrbAndroid) {
                            // Updates Orb's status to getPicked
                            Orb toBePicked = (Orb) fixB.getUserData();
//                            indicateOrb = true;

                            // Updates Player's status to pickingOrb
                            if (multiplayer) updateServerOrb(PICK_UP_ORB, toBePicked.getID());
                            else {
                                ((Player) fixA.getUserData()).orbPick(toBePicked.getID());
                                toBePicked.setToPick();
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

                if (fixA.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean placeOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                    //fixA is player in this case:
                    String id = ((Player) fixA.getUserData()).getID();
                    if (id.equals(PlayScreen.player.getID())){
                        selfPlayer = id;
                        fullVisibility = 1;
                        playerPillar = true;
                    }

                    if (placeOrb | pickOrbAndroid){
                        if (((Player) fixA.getUserData()).isHoldingOrb() == true) {
                            // Updates Pillar's status to lighted
                            Pillar pillar = ((Pillar) fixB.getUserData());
                            pillar.setCategoryFilter(MultiplayerGame.LIGHTEDPILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();
//                            indicateOrbOnPillar = true;
                            lightedPillarX = pillar.positionX();
                            lightedPillarY = pillar.positionY();

                            // Updates Player's status to not carrying orb
                            if (multiplayer) updateServerOrb(PLACE_ORB, pillar.id);
                            else {
                                Orb orb = ((Player) fixA.getUserData()).orbDrop();
                                pillar.setmOrb(orb);
                            }
                            Gdx.app.log("Pillar is LIT"+" with orb ", "");
//                            indicateOrb = false;
                        }
                    }
                }
                else if (fixB.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean placeOrb = Gdx.input.isKeyPressed(Input.Keys.A);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                    //fixB is player in this case:
                    String id = ((Player) fixB.getUserData()).getID();
                    if (id.equals(PlayScreen.player.getID())){
                        selfPlayer = id;
                        fullVisibility = 1;
                        playerPillar = true;
                    }

                    if (placeOrb | pickOrbAndroid){
                        if (((Player) fixB.getUserData()).isHoldingOrb() == true) {
                            // Updates Pillar's status to lighted
                            Pillar pillar = ((Pillar) fixA.getUserData());
                            pillar.setCategoryFilter(MultiplayerGame.LIGHTEDPILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();
//                            indicateOrbOnPillar = true;
                            lightedPillarX = pillar.positionX();
                            lightedPillarY = pillar.positionY();

                            // Updates Player's status to not carrying orb
                            if (multiplayer) updateServerOrb(PLACE_ORB, pillar.id);
                            else {
                                Orb orb = ((Player) fixB.getUserData()).orbDrop();
                                pillar.setmOrb(orb);
                            }
                            Gdx.app.log("Pillar is LIT" + " with orb ", "");
//                            indicateOrb = false;
                        }
                    }
                }
                break;

            // =-=-= PLAYER collides with LIGHTED PILLAR =-=-=
            case MultiplayerGame.PLAYER_BIT | MultiplayerGame.LIGHTEDPILLAR_BIT:

                if (fixA.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT){
                    boolean grabOrb = Gdx.input.isKeyPressed(Input.Keys.S);
                    boolean pickOrbAndroid = PlayScreen.controller.isOrbPressed();

                    //fixA is player in this case:
                    String id = ((Player) fixA.getUserData()).getID();
                    if (id.equals(PlayScreen.player.getID())){
                        selfPlayer = id;
                        fullVisibility = 1;
                        playerPillar = true;
                    }

                    if (grabOrb | pickOrbAndroid){
                        if (((Player) fixA.getUserData()).isHoldingOrb() == false) {
                            // Updates Pillar's status to lighted
                            Pillar pillar = ((Pillar) fixB.getUserData());
                            pillar.setCategoryFilter(MultiplayerGame.PILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();
//                            indicateOrb = true;
                            playerOrbPillar = true;
//                            indicateOrbOnPillar = false;

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

                    //fixB is player in this case:
                    String id = ((Player) fixB.getUserData()).getID();
                    if (id.equals(PlayScreen.player.getID())){
                        selfPlayer = id;
                        fullVisibility = 1;
                        playerPillar = true;
                    }

                    if (grabOrb | pickOrbAndroid){
                        if (((Player) fixB.getUserData()).isHoldingOrb() == false) {
                            // Updates Pillar's status to lighted
                            Pillar pillar = ((Pillar) fixA.getUserData());
                            pillar.setCategoryFilter(MultiplayerGame.PILLAR_BIT);
                            MultiplayerGame.manager.get("audio/sounds/woosh.mp3", Sound.class).play();
//                            indicateOrb = true;

                            //check if it is that player
                            if (id.equals(PlayScreen.player.getID())){
                                playerOrbPillar = true;
                            }
//                            indicateOrbOnPillar = false;

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
        boolean endContact = true;
        if(selfPlayer!=null) {
            endContact = (!multiplayer) ||
                    (selfPlayer.equals(PlayScreen.player.getID()));

            Fixture fixA = contact.getFixtureA();
            Fixture fixB = contact.getFixtureB();

            /* Check if the contact was between player and pillar:
            if true and if the player Id is the host player id, then disable full visibility
             */
            int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
            if ((cDef == (MultiplayerGame.PLAYER_BIT | MultiplayerGame.PILLAR_BIT))
                    || (cDef == (MultiplayerGame.PLAYER_BIT | MultiplayerGame.LIGHTEDPILLAR_BIT))){
                if (fixA.getFilterData().categoryBits == MultiplayerGame.PLAYER_BIT) {
                    if (((Player) fixA.getUserData()).getID().equals(selfPlayer)) {
                        selfPlayer = null;
                        fullVisibility = 0;
                    }
                }else {
                    if (((Player) fixB.getUserData()).getID().equals(selfPlayer)) {
                        selfPlayer = null;
                        fullVisibility = 0;
                    }
                }
            }
        }

        if (endContact) {
            if (playerPillar && sameState) {
                fullVisibility = 1;
                playerPillar = false;
                sameState = false;
            } else if (sameState) {
                //pass
                sameState = false;
            } else {
//                fullVisibility = 0;
                playerPillar = false;
            }

            if (playerOrbPillar) {
                playerOrbPillar = false;
            }
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
