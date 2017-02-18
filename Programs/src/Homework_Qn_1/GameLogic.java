package Homework_Qn_1;

public class GameLogic {

    // Instantiate our game world with the relevant objects
    UserInterface ui = new UserInterface();
    Shadow shadow = new Shadow();
    Core core = new Core();
    Pillar pillar = new Pillar();
    Orb orb = new Orb();

    public int shadowAttCore(){
        if (pillar.lightStatus == 1){
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
