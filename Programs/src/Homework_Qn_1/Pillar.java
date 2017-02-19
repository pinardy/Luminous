package Homework_Qn_1;


public class Pillar {
    /** The pillar can have a light orb placed on it.
     *  If there is an orb on the pillar, it is considered lit (lightStatus = 1)
     *  If there is no orb on the pillar, it is considered unlit (lightStatus = 0)
     */

    // default status is unlit
    private int lightStatus = 0; // 0 is unlit, 1 is lit

    public void setLight(){
        lightStatus = 1;
    }
    public void removeLight(){
        lightStatus = 0;
    }
    public int getLightStatus() { return lightStatus; }


}
