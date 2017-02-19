package Homework_Qn_1;

public class Core {
    /** If the Core has zero or less health left, the game ends
     *  The core can be damaged by shadows
     */

    private int health;

    public Core() {
        this.health = 100;
    }

    public void decreaseHealth(int damage){
        if(this.health - damage >= 0) this.health -= damage;
    }
    public int getHealth() {
        return this.health;
    }
    public int setHealth(int hp){
        this.health = hp;
        return this.health;
    }

}
