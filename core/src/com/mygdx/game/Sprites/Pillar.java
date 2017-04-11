package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;

/** A lit Pillar is used to block shadows from reaching the Core
 *  Shadows will pass through unlit Pillar objects
 *  A Pillar can be lit or unlit depending on whether it is carrying an Orb
 */

public class Pillar extends InteractiveTileObject {
    public int id;
    private Orb mOrb;
    private PlayScreen screen;
    private boolean hasOrb;

    public Pillar(PlayScreen screen, Rectangle bounds, int id) {
        super(screen, bounds);
        this.id = id;
        this.screen = screen;
        fixture.setUserData(this);
        setCategoryFilter(MultiplayerGame.PILLAR_BIT);
    }

    public Orb getmOrb() {
        return this.mOrb;
    }

    public float positionX() {
        float positionX = this.fixture.getBody().getPosition().x;
        return positionX;
    }

    public float positionY(){
        float positionY =  this.fixture.getBody().getPosition().y;
        return positionY;
    }

    public void setmOrb(int id) {
        this.mOrb = screen.getOrbFromList(id);
        hasOrb = true;
    }

    public void setmOrb(Orb mOrb) {
        this.mOrb = mOrb;
        hasOrb = true;
    }

    public Orb releaseOrb() {
        Orb toReturn = mOrb;
        mOrb = null;
        hasOrb = false;
        return toReturn;
    }
    public boolean hasOrb(){
        return hasOrb;
    }
}
