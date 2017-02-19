package Homework_Qn_1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by kennethlimcp on 19/2/17.
 */

public class UserInterfaceTest {
    private UserInterface ui;

    @Before
    public void runBeforeEachTest() {
        ui = new UserInterface();
    }

    @After
    public void runAfterEachTest() {
        ui = null;
    }

    @Test
    public void setAndGetHealthTest() {
        ui.updateHealth(888);
        assertEquals(888, ui.getHealth());
    }

    @Test
    public void setAndGetScoreTest() {
        ui.updateScore(168);
        assertEquals(168, ui.getScore());
    }

    @Test
    public void setAndGetTimeTest() {
        ui.setTime(33);
        assertEquals(33, ui.getTime());
    }
}
