package Homework_Qn_1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by kennethlimcp on 19/2/17.
 */

public class CoreTest {
    private Core core = new Core();

    @Before
    public void runBeforeEachTest() {
        core = new Core();
    }

    @After
    public void runAfterEachTest() {
        core = null;
    }

    @Test
    public void checkHealthAtStartTest() {
        assertEquals(100, core.getHealth());
    }

    @Test
    public void checkDecreaseHealthTest() {
        core.decreaseHealth(55);
        assertEquals(45, core.getHealth());
    }

    @Test
    public void decreaseHealthMoreThan100Test() {
        core.decreaseHealth(100);
        core.decreaseHealth(100);
        assertEquals(0, core.getHealth());
    }
}
