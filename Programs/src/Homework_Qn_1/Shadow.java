package Homework_Qn_1;

public class Shadow {

    public static void attackCore(Core core, UserInterface ui){
        core.decreaseHealth(5);

        // updates health on user interface accordingly
        ui.updatehealth(Core.getHealth());
    }


}
