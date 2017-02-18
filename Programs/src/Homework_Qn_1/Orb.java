package Homework_Qn_1;

public class Orb {
    static boolean onPillar; // is the orb placed on a pillar?

    // pick orb from a pillar
    public void pickOrb(){
        onPillar = false;
    }

    // place orb on a pillar
    public void placeOrb(){
        onPillar = true;
    }
}
