package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by kennethlimcp on 04/Apr/2017.
 */
public class PlayerTest extends TestCase {
    private Player player;

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


        Vector2 vector = new Vector2(0.0f, 0.0f);
        World world = new World(vector, true);

        player = new Player(world);

    }

    public void tearDown() throws Exception {
        player = null;
    }

    //TODO - need to figure out how to override the call to PlayScreen
    //Check that getHoldingOrb() returns the orb that was picked with orbPick()
    //@pre-condition:  orbPick() is passed a mocked Orb object
    //@post-condition: getHoldingOrb() returns mocked Orb object
    @Test
    public void testGetHoldingOrb() throws Exception {
        Orb orb1 = Mockito.mock(Orb.class);

        PlayScreen.listOfOrbs.add(orb1);
        player.orbPick(orb1);

        assertEquals(player.getHoldingOrb(), orb1);

    }

    //Check that orbPick() updates the status to true
    //@pre-condition:  isHoldingOrb() is false
    //@post-condition: isHoldingOrb() is true
    @Test
    public void testOrbPickUsingObject() throws Exception {
        Orb orb1 = Mockito.mock(Orb.class);

        assertEquals(false, player.isHoldingOrb());
        player.orbPick(orb1);
        assertEquals(true, player.isHoldingOrb());
    }

    //Check that isHoldingOrb() returns the correct status
    //@pre-condition:  orbPick() is called
    //@post-condition: isHoldingOrb() is true
    @Test
    public void testIsHoldingOrb() throws Exception {
        Orb orb1 = Mockito.mock(Orb.class);

        assertFalse(player.isHoldingOrb());
        player.orbPick(orb1);
        assertTrue(player.isHoldingOrb());

    }

    //Check that Orbdrop() updates the status variable correctly
    //@pre-condition:  isHoldingOrb() is true
    //@post-condition: isHoldingOrb() is false
    @Test
    public void testOrbDrop() throws Exception {
        Orb orb1 = Mockito.mock(Orb.class);

        player.orbPick(orb1);
        assertTrue(player.isHoldingOrb());
        assertEquals(orb1, player.orbDrop());
        assertFalse(player.isHoldingOrb());


    }

    //Check that Players's category bit is get to 2
    //@pre-condition:  instantiate a player
    //@post-condition: category bit is 2
    @Test
    public void testCategoryBits() throws Exception {
        assertEquals(player.getFilterData().categoryBits, PLAYER_BIT);
    }


    //Check that Player's mask bits are set to match:
    // DEFAULT_BIT | PILLAR_BIT | LIGHTEDPILLAR_BIT | CORE_BIT | ORB_BIT | SHADOW_BIT
    //@pre-condition:  instantiate a player
    //@post-condition: mask bits matches (DEFAULT_BIT | PILLAR_BIT | LIGHTEDPILLAR_BIT | CORE_BIT | ORB_BIT | SHADOW_BIT)
    @Test
    public void testMaskBits() throws Exception {
        assertEquals(player.getFilterData().maskBits, DEFAULT_BIT | PILLAR_BIT | LIGHTEDPILLAR_BIT | CORE_BIT | ORB_BIT | SHADOW_BIT);
    }

}