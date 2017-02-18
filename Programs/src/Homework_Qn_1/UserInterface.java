package Homework_Qn_1;

public class UserInterface {
    static int score;
    static int health;
    static int timer;

    public void updateScore(int score) {
        this.score = score;
    }

    public int getScore(int score) {
        return score;
    }

    public static void updatehealth(int hp) {
        health = hp;
    }

    public static int getHealth(int health) {
        return health;
    }

    public static void setTime(int time) {
        timer = time;
    }

    public static int getTime(int time) {
        return time;
    }

}
