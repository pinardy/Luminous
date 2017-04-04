package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;

import junit.framework.TestCase;
import org.mockito.Mockito;
import static org.mockito.BDDMockito.given;

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

        this.playScreen = Mockito.mock(PlayScreen.class);

        Vector2 vector = new Vector2(0.0f, 0.0f);
        World world = new World(vector, true);

        Mockito.when(playScreen.getWorld()).thenReturn(world);
    }

    public void tearDown() throws Exception {
        orb = null;
        playScreen = null;
    }

    public void testDefaultConstructor() {
        Orb orb = new Orb(playScreen, x, y);
        assertEquals(orb.getID(), 0);
    }

    public void testSetIDConstructor() {
        int id = 888;

        Orb orb = new Orb(playScreen, x, y, id);
        assertEquals(orb.getID(), id);
    }


    public void testUpdate() throws Exception {

    }

    public void testDefineObject() throws Exception {

    }


    public void testDropOrb() throws Exception {
        Orb orb = new Orb(playScreen, x, y);

        orb.setToPick();
        orb.update((float)0.0);

        assertTrue(orb.getToPick());
        assertTrue(orb.getToPick());
        orb.dropOrb();
        assertFalse(orb.getToPick());
        assertFalse(orb.getPicked());

    }

    public void testGetID() throws Exception {
        int id = 1234;

        Orb orb = new Orb(playScreen, x, y, id);
        assertEquals(orb.getID(), id);
    }

    public void testSetToPick() throws Exception {
        Orb orb = new Orb(playScreen, x, y);

        assertFalse(orb.getToPick());
        orb.setToPick();
        assertTrue(orb.getToPick());
    }


    public void testSetPicked() throws Exception {
        Orb orb = new Orb(playScreen, x, y);

        assertFalse(orb.getPicked());
        orb.setToPick();
        orb.update((float)0.0);
        assertTrue(orb.getPicked());
    }

    public void testCategoryBits() throws Exception {
        Orb orb = new Orb(playScreen, x, y);
        assertEquals(orb.getFilterData().categoryBits, ORB_BIT);
    }

    public void testMaskBits() throws Exception {
        Orb orb = new Orb(playScreen, x, y);
        assertEquals(orb.getFilterData().maskBits, PILLAR_BIT | PLAYER_BIT);
    }
}