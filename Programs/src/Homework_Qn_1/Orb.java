package Homework_Qn_1;

public class Orb {
    /** An orb can be placed onto a pillar
     * Purpose: block the shadow from attacking the core
     * If the orb is on a pillar, the pillar is considered lit
     * Otherwise, the pillar is unlit
     */

    static boolean onPillar; // is the orb placed on a pillar?

    // pick orb from a pillar
    public void pickOrb(){
        onPillar = false;
    }

    // place orb on a pillar
    public void placeOrb(){
        onPillar = true;
    }

    // Get orb status (on or off pillar)
    public boolean getOrbStatus() { return onPillar;}
}
