package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * Created by kennethlimcp on 04/Apr/2017.
 */
public class PillarTest extends TestCase {
    private PlayScreen playScreen;
    private Rectangle rectangle;
    private Pillar pillar;
    private int id, orbID;
    private Orb orb1, orb2;

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
        this.rectangle = Mockito.mock(Rectangle.class);
        id = 10;

        Vector2 vector = new Vector2(0.0f, 0.0f);
        World world = new World(vector, true);


        Mockito.when(playScreen.getWorld()).thenReturn(world);

        Mockito.when(rectangle.getWidth()).thenReturn((float) 1);
        Mockito.when(rectangle.getHeight()).thenReturn((float) 1);
        Mockito.when(rectangle.getX()).thenReturn((float) 1);
        Mockito.when(rectangle.getY()).thenReturn((float) 1);

        orbID = 88;
        orb1 = Mockito.mock(Orb.class);
        orb2 = Mockito.mock(Orb.class);

        pillar = new Pillar(playScreen, rectangle, id);
    }

    public void tearDown() throws Exception {
        pillar = null;
        playScreen = null;
        rectangle = null;
        id = 0;
        orbID = 0;
        orb1 = null;
        orb2 = null;
    }

    public void testSetmOrbByID() throws Exception {
        int orbID = 88;
        Orb orb1 = Mockito.mock(Orb.class);
        Orb orb2 = Mockito.mock(Orb.class);

        Mockito.when(playScreen.getOrbFromList(orbID)).thenReturn(orb1);

        pillar.setmOrb(88);
        assertEquals(orb1, pillar.getmOrb());
        assertNotSame(orb2, pillar.getmOrb());
    }

    public void testSetmOrbObject() throws Exception {
        pillar.setmOrb(orb1);
        assertEquals(orb1, pillar.getmOrb());
        pillar.setmOrb(orb2);
        assertEquals(orb2, pillar.getmOrb());
    }

    public void testReleaseOrbWithoutSetting() throws Exception {
        pillar.releaseOrb();
        assertEquals(null, pillar.getmOrb());
    }

    public void testReleaseOrbAfterSetting() throws Exception {
        pillar.setmOrb(orb1);
        Orb returnOrb = pillar.releaseOrb();
        assertEquals(orb1, returnOrb);
    }
}