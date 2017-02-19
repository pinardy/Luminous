package Homework_Qn_1;

public class Core {
    /** If the Core has zero or less health left, the game ends */
    static int health;

    public static void decreaseHealth(int damage){
        health -= damage;
    }
    public static int getHealth() {
        return health;
    }

}
