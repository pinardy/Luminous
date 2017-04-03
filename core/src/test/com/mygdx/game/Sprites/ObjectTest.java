package com.mygdx.game.Sprites;

import com.mygdx.game.Screens.PlayScreen;

import junit.framework.TestCase;

import org.junit.Before;
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

        PlayScreen playScreen = Mockito.mock(PlayScreen.class);
        float testX = 1;
        float testY = 1;

        o = new ObjectMethod(playScreen, testX, testY);
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

    public ObjectMethod(PlayScreen screen, float x, float y) {
        super(screen, x, y);
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