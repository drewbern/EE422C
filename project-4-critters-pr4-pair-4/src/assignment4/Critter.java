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

package assignment4;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;
import java.lang.Math;

/* 
 * See the PDF for descriptions of the methods and fields in this
 * class. 
 * You may add fields, methods or inner classes to Critter ONLY
 * if you make your additions private; no new public, protected or
 * default-package code or data can be added to Critter.
 */

public abstract class Critter {
////////////// Fields ///////////////
    private int energy = 0;

    private int x_coord;
    private int y_coord;

    private static List<Critter> population = new ArrayList<Critter>();
    private static List<Critter> babies = new ArrayList<Critter>();
    private static boolean fightPhase = false;	// whether or not the current phase is doEncounters() 
    
    private static int timestep = 0;
    private boolean tryMoved = false;	// whether the critter has moved yet this timestep

/////////////////////////////////////
    
    /* Gets the package name.  This assumes that Critter and its
     * subclasses are all in the same package. */
    private static String myPackage;

    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    private static Random rand = new Random();

    public static int getRandomInt(int max) {
        return rand.nextInt(max);
    }

    public static void setSeed(long new_seed) {
        rand = new Random(new_seed);
    }

    /**
     * create and initialize a Critter subclass.
     * critter_class_name must be the qualified name of a concrete
     * subclass of Critter, if not, an InvalidCritterException must be
     * thrown.
     *
     * @param critter_class_name
     * @throws InvalidCritterException
     */
    public static void createCritter(String critter_class_name)
            throws InvalidCritterException {
    	try {
    		/* Create critter */
    		Class<?> critClass = null;
    		critClass = Class.forName(myPackage + "." + critter_class_name);
    		Object crit = critClass.getConstructor().newInstance();
    		population.add((Critter) crit);
    		/* Initialize critter instance variables*/
    		((Critter) crit).energy = Params.START_ENERGY;
    		((Critter) crit).x_coord = getRandomInt(Params.WORLD_WIDTH);
    		((Critter) crit).y_coord = getRandomInt(Params.WORLD_HEIGHT);
    		((Critter) crit).tryMoved = false;
    		Critter.fightPhase = false;
    	}
    	catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
    		throw new InvalidCritterException(critter_class_name);
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Gets a list of critters of a specific type.
     *
     * @param critter_class_name What kind of Critter is to be listed.
     *        Unqualified class name.
     * @return List of Critters.
     * @throws InvalidCritterException
     * @throws ClassNotFoundException 
     */
    public static List<Critter> getInstances(String critter_class_name)
            throws InvalidCritterException {
    	List<Critter> critList = new ArrayList<>();
    	Object foo = null;
    	try {
			Class<?> critClass = null;
			critClass = Class.forName(myPackage + "." + critter_class_name);
			foo = critClass.getConstructor().newInstance();
    	}
    	catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
    		throw new InvalidCritterException(critter_class_name);
    	}
    	for(Critter crit : population) {
    		if(((Critter) foo).getClass().isInstance(crit)){
    			critList.add(crit);
    		}
    	}
    	return critList;
    }

    /**
     * Clear the world of all critters, dead and alive
     */
    public static void clearWorld() {
        population.clear();
    }

    /**
     * Performs the doTimeSteps of all critters. Resolves encounters. Subtracts rest energy.
     * Generates new clover. Removes dead critters. And adds offspring into the field.
     */
    public static void worldTimeStep() {
        timestep++;
        
        // Individual timesteps
        for(Critter crit : population) {
        	crit.tryMoved = false;
        	crit.doTimeStep();
        }
        
        doEncounters();
        
        // Subtract rest energy
        for(Critter crit : population) { 
        	crit.energy -= Params.REST_ENERGY_COST;
        }
        
	    try {
	    	genClover();
	    } catch (InvalidCritterException e) {
	    	e.printStackTrace();
	    }
        
        // Removing dead
        Iterator<Critter> iter = population.iterator();
        while(iter.hasNext()) {
        	Critter crit = iter.next();
        	if(crit.energy <= 0)
        		iter.remove();
        }
        
        // Add babies
        population.addAll(babies); 
        babies.clear();
        	
    }

    /**
     * Displays the world of critters in a grid-like field.
     *  All critters are displayed by their .toString() methods.
     */
    public static void displayWorld() {
    	HashMap<String, Critter> locationMap = new HashMap<String, Critter>();
    	/* Create location HashMap that has the location as the key, and the Critter as the value */
        for(Critter crit : population) { 
        	locationMap.put("" + crit.x_coord + crit.y_coord, crit);
        }
    	
        /* Traverse though the rows of the world */
        for(int i = 0; i < Params.WORLD_HEIGHT + 2; i++) {
        	/* Traverse through the columns of the world */
        	for(int j = 0; j < Params.WORLD_WIDTH + 2; j++) {
        		/* Print the top and the bottom boundaries */
        		if((i == 0 && j == 0) || (j == 0 && i == (Params.WORLD_HEIGHT + 1))) {
        			System.out.print("+");
        			for(int k = 0; k < Params.WORLD_WIDTH; k++)
        				System.out.print("-");
        			System.out.println("+");
        			break;
        		/* Print the side boundaries */
        		} else if(j == 0 || j == Params.WORLD_WIDTH + 1) {
        			System.out.print("|");
        			if(j == Params.WORLD_WIDTH + 1) System.out.println("");
        		/* Print either a space, or a critter if the critter exists at the location */
        		} else {
        			String location = ""+(j-1)+(i-1);
        			if(locationMap.containsKey(location)) {
        				System.out.print(locationMap.get(""+(j-1)+(i-1)).toString());
        			} else {
        				System.out.print(" ");
        			}
        		}
        	}
        }
    }

    /**
     * Resolves the encounters of every critter that shares a location with another critter. 
     * No critter should share the same coordinate after this method.
     */
    private static void doEncounters() {
    	fightPhase = true;
    	
    	HashMap<String, ArrayList<Critter>> critMap = makeCritMap();
    	
    	// Iterate through all the coordinates and their critters
    	for(String coordString : critMap.keySet()) {
    		ArrayList<Critter> encounters = critMap.get(coordString);
    		
    		while(encounters.size() > 1) {	// Only if more than 1 critter share the same coord
	    			doFight(encounters);
    		}
    	}
    	
    	fightPhase = false;
    }
    
    /**
     * Makes a Map of coordinates inhabiting critters and all the alive critters that share that coordinate.
     * 
     * @return the map containing coordinates and critters
     */
    private static HashMap<String, ArrayList<Critter>> makeCritMap(){
    	HashMap<String, ArrayList<Critter>> critMap = new HashMap<String, ArrayList<Critter>>();
    	
    	for(Critter crit : population) {
    		String coordString = new String("" + crit.x_coord + crit.y_coord);
    		
    		if(crit.energy > 0) { // Only include ALIVE critters in map
	    		// If this is the first critter with this coord, initialize a new ArrayList
	    		if(!(critMap.containsKey(coordString))) { 
	    			critMap.put(coordString, new ArrayList<Critter>());
	    			critMap.get(coordString).add(crit);
	    		}
	    		// Else add to the list of critters that share this coord
	    		else
	    			critMap.get(coordString).add(crit);
	    		}
    	}
    	
    	return critMap;
    }
    
    /**
     * Resolves a single fight between the first two critters in an array list of critters
     * Removes a critter from the list if it dies or runs away
     * 
     * @param encounters ArrayList<Critter> whose fights need to be resolved
     */
    private static void doFight(ArrayList<Critter> encounters) {
    	Critter A = encounters.get(0);
    	Critter B = encounters.get(1);
    	int[] coordArr = new int[]{A.x_coord, A.y_coord}; // original, shared coordinate
    	
    	boolean fightA = A.fight(B.toString());
    	boolean fightB = B.fight(A.toString());
    	
    	// Removing critters that are dead or have moved away
    	Iterator<Critter> iter = encounters.iterator();
        for(int i = 0; i < 2; i++){ // Only check the first two (A and B)
        	Critter X = iter.next();
        	if(X.getEnergy() <= 0)
        		iter.remove();
        	else if(!(X.x_coord == coordArr[0] && X.y_coord == coordArr[1]))
        		iter.remove();
        }
        
        // If they both remain, initiate fight
        if(encounters.contains(A) && encounters.contains(B)) {
        	int powerA = 0, powerB = 0;
        	if(fightA) {
        		powerA = getRandomInt(A.energy);
        	}
        	if(fightB) {
        		powerB = getRandomInt(B.energy);
        	}
        	if(powerA >= powerB) { // Critter A wins
        		A.energy += B.energy/2;
        		B.energy = 0;
        		encounters.remove(B);
        	}else { // Critter B wins
        		B.energy += A.energy/2;
        		A.energy = 0;
        		encounters.remove(A);
        	}
        }
    	
    }
    
    /**
     * Generates new clover on the field, number depending on REFRESH_CLOVER_COUNT
     * Throws InvalidCritterException iff there is no Clover class in sight.
     */
    private static void genClover() throws InvalidCritterException {
    	for(int i = 0; i < Params.REFRESH_CLOVER_COUNT; i++)
    		createCritter("Clover");
    }
    
    /**
     * Prints out how many Critters of each type there are on the
     * board.
     *
     * @param critters List of Critters.
     */
    public static void runStats(List<Critter> critters) {
        System.out.print("" + critters.size() + " critters as follows -- ");
        Map<String, Integer> critter_count = new HashMap<String, Integer>();
        for (Critter crit : critters) {
            String crit_string = crit.toString();
            critter_count.put(crit_string, critter_count.getOrDefault(crit_string, 0) + 1);
        }
        String prefix = "";
        for (String s : critter_count.keySet()) {
            System.out.print(prefix + s + ":" + critter_count.get(s));
            prefix = ", ";
        }
        System.out.println();
    }

    public abstract void doTimeStep();

    public abstract boolean fight(String oponent);

    /* a one-character long string that visually depicts your critter
     * in the ASCII interface */
    public String toString() {
        return "";
    }

    protected int getEnergy() {
        return energy;
    }

    /**
     * Moves critter one location based on the direction. Does not move critter if critter
     * has already moved this timestep or if during the fight phase there is another
     * critter in that position.
     * 
     * @param direction The radial direction in which to walk.
     */
    protected final void walk(int direction) {

    	if(!tryMoved) {
    		int[] coord = findCoord(direction, false);	// find the coordinate
        	if(!(fightPhase && existCrit(coord))) { // It can't be both the fightPhase and a critter exists at coord
        		// Move the critter
            	x_coord = coord[0]; y_coord = coord[1];
        	}
        }
    	tryMoved = true; // if walk() is invoked this timestep, it can move no any longer this timestep
        energy -= Params.WALK_ENERGY_COST;
    }

    /**
     * Moves critter two locations based on the direction. Does not move critter if critter
     * has already moved this timestep or if during the fight phase there is another
     * critter in that position.
     * 
     * @param direction The radial direction in which to run.
     */
    protected final void run(int direction) {
    	
    	if(!tryMoved) {
    		int[] coord = findCoord(direction, true);	// find the coordinate
    		if(!(fightPhase && existCrit(coord))) { // It can't be both the fightPhase and a critter exists at coord
    			// Move the critter
	        	x_coord = coord[0]; y_coord = coord[1];
    		} 
    	}
		tryMoved = true; // if run() is invoked this timestep, it can move no longer this timestep
    	energy -= Params.RUN_ENERGY_COST;
    }
    
    /**
     * Creates an int array with the new location of a critter, given the direction. 
     * Moves one or two spaces, depending on isRun.
     * 
     * @param dir direction in which to move.
     * @param isRun whether the movement is a run or walk.
     * @return coord int array where item 0 is the new x_coord and item 1 is the new y_coord
     */
    private int[] findCoord(int dir, boolean isRun) {
    	int[] coord = new int[2];	// holds new coordinate values
    	coord[0] = x_coord;
    	coord[1] = y_coord;
    	
    	// Change coordinates
    	moveCoord(coord, dir);
    	if(isRun) moveCoord(coord,dir); // Moves twice for run
    	
    	return coord;
    }
    
    /**
     * Modifies the param coord according to the direction given.
     * Applies torus property.
     * 
     * @param coord an int array to be modified holding the x and y coordinates
     * @param dir the direction to move in
     */
    private void moveCoord(int[] coord, int dir) {
    	// Moves coordinate to appropriate position
    	switch(dir) {
    	case 0:
    		coord[0] += 1;
    		break;
    	case 1:
    		coord[0] += 1;
    		coord[1] -= 1;
    		break;
    	case 2:
    		coord[1] -= 1;
    		break;
    	case 3:
    		coord[0] -= 1;
    		coord[1] -= 1;
    		break;
    	case 4:
    		coord[0] -= 1;
    		break;
    	case 5:
    		coord[0] -= 1;
    		coord[1] += 1;
    		break;
    	case 6:
    		coord[1] += 1;
    		break;
    	case 7:
    		coord[0] += 1;
    		coord[1] += 1;
    		break;
    	default:
    		break;
    	}
    	
    	// Apply torus property
    	if(coord[1] < 0)
    		coord[1] += Params.WORLD_HEIGHT; 
    	else if(coord[1] > Params.WORLD_HEIGHT-1)
    		coord[1] -= Params.WORLD_HEIGHT;
    		
    	if(coord[0] < 0)
    		coord[0] += Params.WORLD_WIDTH;
    	else if(coord[0] > Params.WORLD_WIDTH-1)
    		coord[0] -= Params.WORLD_WIDTH;
    }

    /**
     * Method that returns whether or not a critter exists in the coordinate given.
     * 
     * @param coord coordinate array to check whether a critter exists in or not
     * @return boolean answering question
     */
    private boolean existCrit(int[] coord) {
    	for(Critter crit : population) {	// find a live critter that matches the coordinate
    		if(crit.x_coord == coord[0] && crit.y_coord == coord[1] && crit.energy > 0)
    			return true;
    	}
    	return false;
    }
    
    /**
     * Places offspring in babies list in the given direction from parent. 
     * Reassigns energies of baby and parent.
     * 
     * @param offspring to be placed in the given direction.
     * @param direction direction around parent to place offspring.
     */
    protected final void reproduce(Critter offspring, int direction) {
        if(this.energy < Params.MIN_REPRODUCE_ENERGY)
        	return;
        // Assigning energy
        offspring.energy = this.energy/2;
        this.energy = (int) Math.ceil(this.energy/2);
        // Assigning location
        int[] coordArr = findCoord(direction, false);
        offspring.x_coord = coordArr[0];
        offspring.y_coord = coordArr[1];
        
        babies.add(offspring);
    }

    /**
     * The TestCritter class allows some critters to "cheat". If you
     * want to create tests of your Critter model, you can create
     * subclasses of this class and then use the setter functions
     * contained here.
     * <p>
     * NOTE: you must make sure that the setter functions work with
     * your implementation of Critter. That means, if you're recording
     * the positions of your critters using some sort of external grid
     * or some other data structure in addition to the x_coord and
     * y_coord functions, then you MUST update these setter functions
     * so that they correctly update your grid/data structure.
     */
    static abstract class TestCritter extends Critter {

        protected void setEnergy(int new_energy_value) {
            super.energy = new_energy_value;
        }

        protected void setX_coord(int new_x_coord) {
            super.x_coord = new_x_coord;
        }

        protected void setY_coord(int new_y_coord) {
            super.y_coord = new_y_coord;
        }

        protected int getX_coord() {
            return super.x_coord;
        }

        protected int getY_coord() {
            return super.y_coord;
        }

        /**
         * This method getPopulation has to be modified by you if you
         * are not using the population ArrayList that has been
         * provided in the starter code.  In any case, it has to be
         * implemented for grading tests to work.
         */
        protected static List<Critter> getPopulation() {
            return population;
        }

        /**
         * This method getBabies has to be modified by you if you are
         * not using the babies ArrayList that has been provided in
         * the starter code.  In any case, it has to be implemented
         * for grading tests to work.  Babies should be added to the
         * general population at either the beginning OR the end of
         * every timestep.
         */
        protected static List<Critter> getBabies() {
            return babies;
        }
    } // End of TestCritter
}
