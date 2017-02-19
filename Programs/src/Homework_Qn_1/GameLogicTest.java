package Homework_Qn_1;

import org.jmock.Expectations;
import org.jmock.Expectations.*;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.jmock.Mockery;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


public class GameLogicTest {
    private GameLogic gameLogic;
    private Mockery context;

    @Mock private Pillar pillar;
    private Shadow shadow;
    private Core core;

    @Before
    public void runBeforeEachTest() {

        gameLogic = new GameLogic();

        final Mockery context = new JUnit4Mockery();
        pillar = context.mock(Pillar.class);
        shadow = context.mock(Shadow.class);
        core = context.mock(Core.class);
    }

    @After
    public void runAfterEachTest() {
        gameLogic = null;
        context = null;
    }

    @Test
    public void shadowAttCoreIfTest() {
        context.checking(new Expectations() {{
            oneOf(pillar).getLightStatus();
            will(returnValue(1));
        }});

        assertEquals(1, gameLogic.shadowAttCore());
    }

    @Test
    public void shadowAttCoreElseTest() {
//        context.checking(new Expectations() {{}
//            oneOf(pillar).;
//            will(returnValue(0));
//            oneOf(shadow).attackCore();
//        });
//
//        assertEquals(0, gameLogic.shadowAttCore());
    }

    @Test
    public void checkGameStatusIfTest() {
        context.checking(new Expectations() {
//            oneOf(core).getHealth;
//            will(returnValue(0));
        });

        assertEquals(0, gameLogic.checkGameStatus());
    }

    @Test
    public void checkGameStatusElseTest() {
        context.checking(new Expectations() {
//            oneOf(core).getHealth;
//            will(returnValue(10));
        });

        assertEquals(1, gameLogic.checkGameStatus());
    }

    // TODO: update this
//    public GameLogicTest(){
//
//    }
//
//    @Before
//    public void runBeforeEachTest() {
//        gameLogic = new GameLogic();
//    }
//
//    @Parameterized.Parameters
//    public static Collection<Object[]> parameters() {
//        return Arrays.asList (new Object [][] {
//                //TODO: 100% branch coverage
//        });
//    }
//
//    @Test
//    public void test() {
//        //TODO: input appropriate assertion test
//    }

}
