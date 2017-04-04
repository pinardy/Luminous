package com.mygdx.game.Sprites;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by kennethlimcp on 04/Apr/2017.
 */
public class InteractiveTileObjectTest extends TestCase {
    private InteractiveTileObject ito;
    private PlayScreen playScreen;
    private Rectangle rectangle;

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

        Vector2 vector = new Vector2(0.0f, 0.0f);
        World world = new World(vector, true);

        Mockito.when(playScreen.getWorld()).thenReturn(world);
        Mockito.when(rectangle.getWidth()).thenReturn((float) 1);
        Mockito.when(rectangle.getHeight()).thenReturn((float) 1);
        Mockito.when(rectangle.getX()).thenReturn((float) 1);
        Mockito.when(rectangle.getY()).thenReturn((float) 1);


        ito = new InteractiveTileObject(playScreen, rectangle){
            @Override
            public void setCategoryFilter(short filterBit) {
                super.setCategoryFilter(filterBit);
            }

            @Override
            public Filter getCategoryFilter() {
                return super.getCategoryFilter();
            }
        };

    }

    public void tearDown() throws Exception {
        ito = null;
    }

    //This will indirect test get method as well
    @Test
    public void testDefaultCategoryFilter() throws Exception {

        Filter f = ito.getCategoryFilter();

        assertEquals(f.categoryBits, DEFAULT_BIT);

        ito.setCategoryFilter(PILLAR_BIT);
        f = ito.getCategoryFilter();
        assertEquals(f.categoryBits, PILLAR_BIT);

    }

    @Test
    public void testSetCategoryFilter() throws Exception {

        ito.setCategoryFilter(PLAYER_BIT);
        Filter f = ito.getCategoryFilter();
        assertEquals(f.categoryBits, PLAYER_BIT);

        f = null;
        ito.setCategoryFilter(CORE_BIT);
        f = ito.getCategoryFilter();
        assertEquals(f.categoryBits, CORE_BIT);

        f = null;
        ito.setCategoryFilter(ORB_BIT);
        f = ito.getCategoryFilter();
        assertEquals(f.categoryBits, ORB_BIT);

        f = null;
        ito.setCategoryFilter(SHADOW_BIT);
        f = ito.getCategoryFilter();
        assertEquals(f.categoryBits, SHADOW_BIT);

        f = null;
        ito.setCategoryFilter(LIGHTEDPILLAR_BIT);
        f = ito.getCategoryFilter();
        assertEquals(f.categoryBits, LIGHTEDPILLAR_BIT);
    }
}