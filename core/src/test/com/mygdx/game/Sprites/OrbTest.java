package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.*;
import static org.mockito.Mockito.mock;

/* Created by kennethlimcp on 02/Apr/2017.
 */

public class OrbTest extends TestCase {
    private PlayScreen playScreen;
    private Orb orb;
    private float x;
    private float y;

    // For identifying the type of object
    // powers of 2 so its easier to OR bits tgt
    final short DEFAULT_BIT = 1;
    final short PLAYER_BIT = 2;
    final short PILLAR_BIT = 4;
    final short CORE_BIT = 8;
    final short ORB_BIT = 16;
    final short SHADOW_BIT = 32;
    final short LIGHTEDPILLAR_BIT = 64;


    public void setUp() throws Exception {
        super.setUp();

        this.playScreen = mock(PlayScreen.class);

        PlayScreen spyScreen = Mockito.spy(playScreen);

        Vector2 vector = new Vector2(0.0f, 0.0f);
        World world = new World(vector, true);

        Mockito.when(playScreen.getWorld()).thenReturn(world);
    }


    public void tearDown() throws Exception {
        orb = null;
        playScreen = null;
    }


    //Test that default constructor set ID to 0
    //@pre-condition:  none
    //@post-condition: ID is set to 0
    @Test
    public void testDefaultConstructor() {
        Orb orb = new Orb(playScreen, x, y, false);
        assertEquals(0, orb.getID());
    }

    //Test that default overloaded constructor can set the ID
    //@pre-condition:  none
    //@post-condition: ID is set to 888
    @Test
    public void testSetIDConstructor() {
        int id = 888;

        Orb orb = new Orb(playScreen, x, y, id, false);
        assertEquals(id, orb.getID());
    }

    //Check that dropOrb() updates the status variables correctly
    //@pre-condition:  getToPick() and getPicked() are true
    //@post-condition: getToPick() and getPicked() are false
    @Test
    public void testDropOrb() throws Exception {
        Orb orb = new Orb(playScreen, x, y, false);

        orb.setToPick();
        orb.update((float)0.0);

        assertTrue(orb.getToPick());
        assertTrue(orb.getPicked());
        orb.dropOrb();
        assertFalse(orb.getToPick());
        assertFalse(orb.getPicked());

    }

    //Check that getID() returns the correct ID set
    //@pre-condition:  set ID to 1234
    //@post-condition: ID is 1234
    @Test
    public void testGetID() throws Exception {
        int id = 1234;

        Orb orb = new Orb(playScreen, x, y, id, false);
        assertEquals(id, orb.getID());
    }

    //Check that setToPick() updates status variable to true
    //@pre-condition:  getToPick() is false
    //@post-condition: getToPick() is true
    @Test
    public void testSetToPick() throws Exception {
        Orb orb = new Orb(playScreen, x, y, false);

        assertFalse(orb.getToPick());
        orb.setToPick();
        assertTrue(orb.getToPick());
    }

    //Check that getPicked() updates status variable to true
    //@pre-condition:  getPicked() is false
    //@post-condition: getPicked() is true
    @Test
    public void testSetPicked() throws Exception {
        Orb orb = new Orb(playScreen, x, y, false);

        assertFalse(orb.getPicked());
        orb.setToPick();
        orb.update((float)0.0);
        assertTrue(orb.getPicked());
    }

    //Check that Orb's category bit is get to 16
    //@pre-condition:  instantiate an orb
    //@post-condition: category bit is 16
    @Test
    public void testCategoryBits() throws Exception {
        Orb orb = new Orb(playScreen, x, y, false);
        assertEquals(orb.getFilterData().categoryBits, ORB_BIT);
    }

    //Check that Orb's mask bits are set to match pillar and player
    //@pre-condition:  instantiate an orb
    //@post-condition: mask bits matches (PILLAR_BIT | PLAYER_BIT)
    @Test
    public void testMaskBits() throws Exception {
        Orb orb = new Orb(playScreen, x, y, false);
        assertEquals(orb.getFilterData().maskBits, PILLAR_BIT | PLAYER_BIT);
    }
}