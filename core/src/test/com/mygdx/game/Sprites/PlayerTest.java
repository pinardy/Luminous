package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;

import junit.framework.TestCase;

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

    //TODO
    public void testDefineObject() throws Exception {

    }

    //TODO
    public void testUpdate() throws Exception {

    }


    //TODO - need to figure out how to override the call to PlayScreen
    public void testOrbPickUsingID() throws Exception {

    }

    public void testOrbPickUsingObject() throws Exception {
        Orb orb1 = Mockito.mock(Orb.class);

        player.orbPick(orb1);
        assertEquals(true, player.isHoldingOrb());
    }

    public void testIsHoldingOrb() throws Exception {
        Orb orb1 = Mockito.mock(Orb.class);

        assertFalse(player.isHoldingOrb());
        player.orbPick(orb1);
        assertTrue(player.isHoldingOrb());

    }

    public void testOrbDrop() throws Exception {
        Orb orb1 = Mockito.mock(Orb.class);

        player.orbPick(orb1);
        assertTrue(player.isHoldingOrb());
        assertEquals(orb1, player.orbDrop());
        assertFalse(player.isHoldingOrb());


    }

    public void testCategoryBits() throws Exception {
        assertEquals(player.getFilterData().categoryBits, PLAYER_BIT);
    }

    public void testMaskBits() throws Exception {
        assertEquals(player.getFilterData().maskBits, DEFAULT_BIT | PILLAR_BIT | LIGHTEDPILLAR_BIT | CORE_BIT | ORB_BIT | SHADOW_BIT);
    }

}