package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;

import junit.framework.TestCase;

import org.mockito.Mockito;

/**
 * Created by kennethlimcp on 04/Apr/2017.
 */
public class ShadowTest extends TestCase {
    private Shadow shadow;
    private float x, y;

    public void setUp() throws Exception {
        super.setUp();

        PlayScreen playScreen = Mockito.mock(PlayScreen.class);

        Vector2 vector = new Vector2(0.0f, 0.0f);
        World world = new World(vector, true);

        Mockito.when(playScreen.getWorld()).thenReturn(world);

        x = (float) 1.0;
        y = (float) 1.0;
        shadow = new Shadow(playScreen, x, y, false);

    }

    public void tearDown() throws Exception {
        shadow = null;
        x = 0;
        y = 0;
    }

    //TODO
    public void testUpdate() throws Exception {

    }


    public void testSetActive() throws Exception {

        shadow.setActive(true);
        assertTrue(shadow.b2body.isActive());

        shadow.setActive(false);
        assertFalse(shadow.b2body.isActive());
    }

    //Tested in testIsAlive()
    public void testCollided() throws Exception {

    }

    public void testIsAlive() throws Exception {
        //Since the game is not running, shadow should be alive on initialization
        assertTrue(shadow.isAlive());
        shadow.collided();
        shadow.update(0);
        assertFalse(shadow.isAlive());
    }

    //TODO
    public void testDefineObject() throws Exception {

    }

}