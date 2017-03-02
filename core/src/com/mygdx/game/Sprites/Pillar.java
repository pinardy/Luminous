package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;

/**
 * Created by Pin on 06-Feb-17.
 */

public class Pillar extends InteractiveTileObject {
    public Pillar(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
//        fixture.setUserData(this);
        setCategoryFilter(MultiplayerGame.PILLAR_BIT);

    }
}
