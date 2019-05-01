package assignment5;

/*
 * CRITTERS GUI Critter1.java
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
 * Walks and reproduces 1 out of 8 times. Fights Critter 2 only. 
 * 
 * @author Fawadul Haq
 */

public class Critter1 extends Critter{

	@Override
	public CritterShape viewShape() {
		return CritterShape.ELLIPSE;
	}

	@Override
	public javafx.scene.paint.Color viewColor() {
		return Color.BLUE;
	}

	@Override
	public void doTimeStep() {
		walk(getRandomInt(8));
		if(getRandomInt(8) == 0) {
			super.look(getRandomInt(8), false);
			reproduce(new Critter1(), getRandomInt(8));
		}
		
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
