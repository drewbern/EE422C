package assignment5;

/*
 * CRITTERS GUI Critter2.java
 * EE422C Project 5 submission by
 * Replace <...> with your actual data.
 * Fawadul Haq
 * fh5277
 * 16225
 * Drew Bernard
 * dhb653
 * 16225
 * Slip days used: 0
 * Spring 2019
 */

import javafx.scene.paint.Color;

/**
 * Fights if it has to, otherwise runs.
 *
 */

public class Critter2 extends Critter{

	@Override
	public CritterShape viewShape() {
		return CritterShape.STAR;
	}

	@Override
	public javafx.scene.paint.Color viewColor() {
		return Color.YELLOW;
	}

	@Override
	public void doTimeStep() {
		int i = getRandomInt(8);
		if(getRandomInt(2) == 1)
			if(super.look(i, false) != null)
				walk(i);
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
