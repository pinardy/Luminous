package com.mygdx.game.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.Screens.PlayScreen;

import junit.framework.TestCase;

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by kennethlimcp on 30/Mar/2017.
 */
public class ObjectTest extends TestCase {
    PlayScreen playScreen;
    float testX;
    float testY;
    ObjectMethod o;


    public void setUp() throws Exception {
        super.setUp();

        Vector2 vector = new Vector2(0.0f, 0.0f);
        World world = new World(vector, true);

        float testX = 1;
        float testY = 1;

        o = new ObjectMethod(world, testX, testY);
    }


    public void tearDown() throws Exception {
        PlayScreen playScreen = null;
        float testX = 0;
        float testY = 0;

        o = null;
    }

    @Test
    public void testAbstractDefineObject() throws Exception {

        assertEquals(o.getBooleanFlag(), false);
        o.defineObject();
        assertEquals(o.getBooleanFlag(), true);
    }

}

class ObjectMethod extends Object {
    private boolean defineObjectIsCalledInConstructor;

    public ObjectMethod(World world, float x, float y) {
        super(world, x, y);
        defineObjectIsCalledInConstructor = false;

    }

    @Override
    protected void defineObject() {
        defineObjectIsCalledInConstructor = true;
    }

    boolean getBooleanFlag() {
        return defineObjectIsCalledInConstructor;
    }
}