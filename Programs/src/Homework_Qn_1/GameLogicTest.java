package Homework_Qn_1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class GameLogicTest {
    private GameLogic gameLogic;

    @Before
    public void runBeforeEachTest() {
        gameLogic = new GameLogic();
    }

    @After
    public void runAfterEachTest() {
        gameLogic = null;
    }

    @Test
    public void shadowAttCoreIfTest() {
        gameLogic.pillar.setLight();
        int result = gameLogic.shadowAttCore();
        // expect a 0 since there is light in the way
        assertEquals(0, result);
    }

    @Test
    public void shadowAttCoreElseTest() {
        gameLogic.pillar.removeLight();
        int result = gameLogic.shadowAttCore();
        // expect a 1 since there is pillar is unlit. Shadow can pass through
        assertEquals(1, result);
    }

    @Test
    public void checkGameStatusIfTest() {
        gameLogic.core.setHealth(0);
        int result = gameLogic.checkGameStatus();
        // expect a 0 since core has no health left
        assertEquals(0, result);
    }

    @Test
    public void checkGameStatusElseTest() {
        gameLogic.core.setHealth(5);
        int result = gameLogic.checkGameStatus();
        // expect a 1 since core still has health left (i.e. health > 0)
        assertEquals(1, result);
    }
}
