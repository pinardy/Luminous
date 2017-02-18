package Homework_Qn_1;

public class GameLogic {

    // Instantiate our game world with the relevant objects
    UserInterface ui = new UserInterface();
    Shadow shadow = new Shadow();
    Core core = new Core();
    Pillar pillar = new Pillar();
    Orb orb = new Orb();

    public void shadowAttCore(){
        if (pillar.lightStatus == 1){
            // shadow disappears, nothing happens
        } else{ // if pillar is unlit
            shadow.attackCore(core, ui);
        }
    }

    public void checkGameStatus(){
        if (core.getHealth() <= 0){
            System.out.println("Game is lost!");
        }
        else {
            System.out.println("Game is still in session");
        }
    }
    

}
