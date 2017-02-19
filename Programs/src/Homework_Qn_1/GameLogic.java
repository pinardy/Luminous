package Homework_Qn_1;

public class GameLogic {
    UserInterface ui;
    Shadow shadow;
    Core core;
    Pillar pillar;
    Orb orb;

    public GameLogic() {
        // Instantiate our game world with the relevant objects
        ui = new UserInterface();
        shadow = new Shadow();
        core = new Core();
        pillar = new Pillar();
        orb = new Orb();
    }

    public int shadowAttCore(){
        if (pillar.getLightStatus() == 1){
            // shadow disappears, nothing happens
            return 0;
        } else{ // if pillar is unlit
            shadow.attackCore(core, ui);
            return 1;
        }
    }

    public int checkGameStatus(){
        if (core.getHealth() <= 0){
            System.out.println("Game is lost!");
            return 0;
        }
        else {
            System.out.println("Game is still in session");
            return 1;
        }
    }
    

}
