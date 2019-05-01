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
 * Fights if it has to, otherwise runs.
 *
 */

public class Critter2 extends Critter{
	
	@Override
	public void doTimeStep() {
		if(getRandomInt(2) == 1)
			walk(getRandomInt(8));
	}
	
	@Override
	public boolean fight(String opponent) {
		if(getEnergy() > 4*Params.RUN_ENERGY_COST) {
			run(getRandomInt(8));
			return false;
		}
		return true;
	}
	
	public String toString() {
		return "2";
	}
}
