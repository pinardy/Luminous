package Homework_Qn_1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by kennethlimcp on 19/2/17.
 */

public class PillarTest {
    Pillar pillar;

    @Before
    public void runBeforeEachTest() {
        pillar = new Pillar();
    }

    @After
    public void runAfterEachTest() {
        pillar = null;
    }

    @Test
    public void setLightTest() {
        pillar.setLight();
        assertEquals (1, pillar.getLightStatus());
    }

    @Test
    public void removeLightTest() {
        pillar.removeLight();
        assertEquals(0, pillar.getLightStatus());
    }

    @Test
    public void setAndClearTest() {
        pillar.setLight();
        assertEquals (1, pillar.getLightStatus());
        pillar.removeLight();
        assertEquals(0, pillar.getLightStatus());
    }
}
