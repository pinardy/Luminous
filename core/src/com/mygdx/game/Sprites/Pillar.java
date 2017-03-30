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
    public Pillar(PlayScreen screen, Rectangle bounds, int id) {
        super(screen, bounds);
        this.id = id;
        fixture.setUserData(this);
        setCategoryFilter(MultiplayerGame.PILLAR_BIT);
    }

    public void setmOrb(int id) {
        this.mOrb = PlayScreen.listOfOrbs.get(id);
    }

    public void setmOrb(Orb mOrb) {
        this.mOrb = mOrb;
    }

    public Orb releaseOrb() {
        Orb toReturn = mOrb;
        mOrb = null;
        return toReturn;
    }
}
