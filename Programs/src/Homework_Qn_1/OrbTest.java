package Homework_Qn_1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by kennethlimcp on 19/2/17.
 */

public class OrbTest {
    private Orb orb;

    @Before
    public void runBeforeEachTest() {
        orb = new Orb();
    }

    @After
    public void runAfterEachTest() {
        orb = null;
    }

    @Test
    public void pickOrbTest() {
        orb.pickOrb();
        assertEquals(false, orb.getOrbStatus());
    }

    @Test
    public void placeOrbTest() {
        orb.placeOrb();
        assertEquals(true, orb.getOrbStatus());
    }
}
