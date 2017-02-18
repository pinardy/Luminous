package Homework_Qn_1;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.jmock.Mockery;
import org.jmock.Expectations;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


//@RunWith(Parameterized.class)

public class GameLogicTest {
    private GameLogic gameLogic;

    @Before
    public void runBeforeEachTest() {
        gameLogic = new GameLogic();
        Mockery context = new JUnit4Mockery();
    }

    @After
    public void runAfterEachTest() {
        gameLogic = null;
        context = null;
    }



    @Test
    public void shadowAttCoreIfTest() {
        final Pillar pillar = context.mock(pillar.class);

        context.checking(new Expectations() {{
            oneOf(pillar).lightStatus;
            will(returnValue(1));
        }});

        assertEquals(1, gameLogic.shadowAttCore());
    }

    @Test
    public void shadowAttCoreElseTest() {
        final Pillar pillar = context.mock(pillar.class);
        final Shadow shadow = context.mock(shadow.class);

        context.checking(new Expectations() {{
            oneOf(pillar).lightStatus;
            will(returnValue(0));
            oneOf(shadow).attackCore();
        }});

        assertEquals(0, gameLogic.shadowAttCore());
    }

    @Test
    public void checkGameStatusIfTest() {
        context.checking(new Expectations() {{
            oneOf(core).getHealth;
            will(returnValue(0));
        }});

        assertEquals(0, gameLogic.checkGameStatus());
    }

    @Test
    public void checkGameStatusElseTest() {
        final Core core = context.mock(core.class);

        context.checking(new Expectations() {{
            oneOf(core).getHealth;
            will(returnValue(10));
        }});

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
