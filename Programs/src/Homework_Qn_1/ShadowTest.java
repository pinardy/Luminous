package Homework_Qn_1;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by polarvenezia on 19/2/17.
 */
public class ShadowTest {
    
    Core core;
    UserInterface ui;
    
    @Before
    public void runBeforeTest(){
        core = new Core();
        ui = new UserInterface();
    }

    @Test
    public void testWithCore(){
        int currentHealth = core.getHealth();
        Shadow.attackCore(core, ui);
        assertEquals(currentHealth-5, core.getHealth());
    }

    @Test
    public void testWithUI(){
        int currentHealth = core.getHealth();
        Shadow.attackCore(core, ui);
        assertEquals(currentHealth-5, ui.getHealth());
    }


    @After
    public void runAfterTest(){
        core = null;
        ui = null;
    }
}
