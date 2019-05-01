package assignment4;

/*
 * CRITTERS Critter4.java
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

/* Bottom Feeder */
public class Critter4 extends Critter {
	public int lastDirection = 0;
	public int twice = 0;
	public boolean lastFought = false;

	/* Critter runs in a square */
    @Override
    public void doTimeStep() {
        if(lastDirection == 8)
        	lastDirection = 0;
        run(lastDirection);
        twice++;
        if(twice == 2) {
        	twice = 0;
        }
    }

    /* Always fights(consumes) clovers, fights if it is between  */
  	@Override
  	public boolean fight(String opponent) {
  		if(opponent == "assignment4.Clover") {
  			return true;
  		}
  		if(getEnergy() <= 40) {
  			run(getRandomInt(8));
  			return false;
  		}
  		if(getEnergy() > 40 && getEnergy() < 80) {
  			walk(getRandomInt(8));
  			return false;
  		}
  		return true;
  	}
  
  	@Override
  	public String toString() {
  		return "4";
  	}
}
