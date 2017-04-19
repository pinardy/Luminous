package com.mygdx.game.Tools;

import com.badlogic.gdx.*;

import com.mygdx.game.MultiplayerGame;
import com.mygdx.game.Screens.PlayScreen;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kennethlimcp on 18/Apr/2017.
 */



public class B2WorldCreatorTest {
    @Test
    public void test() {


        MultiplayerGame game = new MultiplayerGame();
        game.create();

        PlayScreen screen = new PlayScreen(game, true);



        B2WorldCreator creator = new B2WorldCreator(screen);
    }

}