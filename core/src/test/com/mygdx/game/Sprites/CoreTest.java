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
public class CoreTest extends TestCase {
    private PlayScreen playScreen;
    private Rectangle rectangle;
    final short CORE_BIT = 8;

    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }

    //Check that the core's category bit is correctly set
    //@pre-condition:  new core instance is created
    //@post-condition: bit information should be set to 8
    @Test
    public void testCategoryBit() throws Exception {
        this.playScreen = Mockito.mock(PlayScreen.class);
        this.rectangle = Mockito.mock(Rectangle.class);

        Vector2 vector = new Vector2(0.0f, 0.0f);
        World world = new World(vector, true);

        Mockito.when(playScreen.getWorld()).thenReturn(world);
        Mockito.when(rectangle.getWidth()).thenReturn((float) 1);
        Mockito.when(rectangle.getHeight()).thenReturn((float) 1);
        Mockito.when(rectangle.getX()).thenReturn((float) 1);
        Mockito.when(rectangle.getY()).thenReturn((float) 1);

        Core core = new Core(playScreen, rectangle);

        Filter f = core.getFilterData();
        assertEquals(f.categoryBits, CORE_BIT);
    }
}