package assignment4;

/*
 * CRITTERS Critter3.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * Fawadul Haq
 * fh5277
 * 16225
 * Drew Bernard
 * dhb653
 * 16225
 * Slip days used: 1
 * Spring 2019
 */

public class Critter3 extends Critter{
	public int lastDirection = 0;
	public boolean lastFought = false;

	/* Rotate direction by on each walk */
    @Override
    public void doTimeStep() {
        if(lastDirection == 8)
        	lastDirection = 0;
        walk(lastDirection);
        lastDirection++;
    }

    /* Fight if didn't fight in last encounter, don't fight if did fight in last encounter */
    @Override
    public boolean fight(String opponent) {
        if(lastFought) {
        	lastFought = false;
            return false;
        }
        lastFought = true;
        return true;
    }

    public String toString() {
        return "3";
    }
}
