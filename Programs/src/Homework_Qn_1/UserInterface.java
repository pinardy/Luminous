package Homework_Qn_1;

public class UserInterface {
    /** The User Interface keeps track of the following:
     *  1) Score
     *  2) Health of core
     *  3) Time
     */


    int score;
    int health = 100;
    int timer;

    public void updateScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void updateHealth(int hp) {
        health = hp;
    }

    public int getHealth() {
        return health;
    }

    public void setTime(int time) {
        timer = time;
    }

    public int getTime() { return timer; }

}
