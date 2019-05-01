package assignment4;

import java.util.Scanner;

import assignment4.Critter.TestCritter;

public class DummyMain {

	public static void main(String[] args) {
		// Clover and Critter1 fight
		Critter.TestCritter C1 = new MyCritter6();
		Critter.TestCritter C2 = new MyCritter6();
		Critter.TestCritter C3 = new MyCritter7();
		// Setting
		set(C1, Params.START_ENERGY, 1, 0);
		set(C2, Params.START_ENERGY, 1, 0);
		set(C3, Params.START_ENERGY, 2, 0);
		// Adding to world
		add(C1); add(C2); add(C3);
		
		
		Scanner kb = new Scanner(System.in); // Use keyboard and console
		Critter.displayWorld();
		while(useDebug()) {
			Critter.worldTimeStep();
			Critter.displayWorld();
		}
		
	}
	
	public static void set(Critter.TestCritter crit, int energy, int x_coord, int y_coord) {
		crit.setEnergy(energy);
		crit.setX_coord(x_coord);
		crit.setY_coord(y_coord);
	}
	
	public static void add(Critter.TestCritter crit) {
		TestCritter.getPopulation().add(crit);
	}
	
	public static boolean useKB(Scanner kb) {
		kb.nextLine();
		return true;
	}
	
	public static boolean useDebug() {
		return true;
	}

}
