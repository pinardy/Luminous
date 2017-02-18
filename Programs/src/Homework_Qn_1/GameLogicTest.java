package Homework_Qn_1;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)

public class GameLogicTest {
    private GameLogic gameLogic;

    // TODO: update this
    public GameLogicTest(){

    }

    @Before
    public void runBeforeEachTest() {
        gameLogic = new GameLogic();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() {
        return Arrays.asList (new Object [][] {
                //TODO: 100% branch coverage



        });
    }

    @Test
    public void test() {
        //TODO: input appropriate assertion test
    }

}
