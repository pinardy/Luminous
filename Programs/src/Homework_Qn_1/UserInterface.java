package Homework_Qn_1;

public class UserInterface {
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
