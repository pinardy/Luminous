package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;


public class Core extends InteractiveTileObject {
    public Core(PlayScreen screen, Rectangle bounds){
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(MultiplayerGame.CORE_BIT);
    }
}
