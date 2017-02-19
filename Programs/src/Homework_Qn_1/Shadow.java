package Homework_Qn_1;

public class Shadow {
    /**
     * The Shadow acts as the 'enemy' of the game
     * If the shadow comes in contact with the core, the core gets damaged
     */

    public static void attackCore(Core core, UserInterface ui){
        core.decreaseHealth(5);

        // updates health on user interface accordingly
        ui.updateHealth(core.getHealth());
    }


}
