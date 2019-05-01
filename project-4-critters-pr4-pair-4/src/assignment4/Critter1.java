package assignment4;

/*
 * CRITTERS Critter.java
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

/**
 * Walks and reproduces 1 out of 8 times. Fights Critter 2 only. 
 * 
 * @author Fawadul Haq
 */

public class Critter1 extends Critter{
	
	@Override
	public void doTimeStep() {
		walk(getRandomInt(8));
		if(getRandomInt(8) == 0)
			reproduce(new Critter1(), getRandomInt(8));
	}
	
	@Override
	public boolean fight(String opponent) {
		if(opponent.equals("2"))
			if(getEnergy() > 20)
				return true;
		return false;
	}
	
	public String toString() {
		return "1";
	}
}
