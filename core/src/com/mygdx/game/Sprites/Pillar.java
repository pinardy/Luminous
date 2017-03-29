package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;

/**
 * Created by Pin on 06-Feb-17.
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
