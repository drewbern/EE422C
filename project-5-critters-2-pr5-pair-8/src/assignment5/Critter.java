/*
 * CRITTERS GUI Critter.java
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

package assignment5;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;
import java.lang.Math;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.util.Duration;



/*
 * See the PDF for descriptions of the methods and fields in this
 * class.
 * You may add fields, methods or inner classes to Critter ONLY
 * if you make your additions private; no new public, protected or
 * default-package code or data can be added to Critter.
 */

public abstract class Critter {

    /* START --- NEW FOR PROJECT 5 */
    public enum CritterShape {
        CIRCLE,
        SQUARE,
        TRIANGLE,
        DIAMOND,
        STAR,
        HEXAGON,
        ELLIPSE,
    }

    /* the default color is white, which I hope makes critters invisible by default
     * If you change the background color of your View component, then update the default
     * color to be the same as you background
     *
     * critters must override at least one of the following three methods, it is not
     * proper for critters to remain invisible in the view
     *
     * If a critter only overrides the outline color, then it will look like a non-filled
     * shape, at least, that's the intent. You can edit these default methods however you
     * need to, but please preserve that intent as you implement them.
     */
    public javafx.scene.paint.Color viewColor() {
        return javafx.scene.paint.Color.WHITE;
    }

    public javafx.scene.paint.Color viewOutlineColor() {
        return viewColor();
    }

    public javafx.scene.paint.Color viewFillColor() {
        return viewColor();
    }

    public abstract CritterShape viewShape();

    /**
     * Determines whether there exists a critter in a location based on the given parameters.
     *
     * @param direction the direction in which to look
     * @param steps 	boolean on whether to look one step or two steps ahead
     * @return null 	if there is no critter there; the located Critter's toString name
     */
    protected final String look(int direction, boolean steps) {
    	energy -= Params.LOOK_ENERGY_COST;
    	int[] coord = findCoord(direction, steps); // coordinate to be checked

    	// if during doTimeStep
    	if(!fightPhase) {
    		Critter observed = popGrid[coord[1]][coord[0]]; // Use the grid, because it is a snapshot
    														// of the field at the beginning of the timestep
    		if(observed == null)
    			return null;
    		else return observed.toString();
    	}
    	// during doEncounter
    	else {
    		Critter observed = existCrit(coord); // uses population to find coord, because
    											 // it has up-to-date locations of critters
    		if(observed == null)
    			return null;
    		else return observed.toString();
    	}
    }

    /**
     * Prints out simply the number of critters of the given type <Critter>
     * and its .toString.
     *
     * @param critters A list of all the Critter instances of a specific critter on
     * 		  grid at the current moment.
     * @return A string of the stats.
     */
    public static String runStats(List<Critter> critters) {

        String stats = new String(critters.size() + " critters as follows -- "
        		+ critters.get(0).toString() + ":" + critters.size());

        return stats;
    }


    /**
     * Displays the world on the given grid pane. The world consists of rectangles for
     * the grid and Shapes for the critters that exist in it.
     *
     * @param pane the grid to which we post the world
     * @param size the length of the side of one cell in the grid
     */
    public static void displayWorld(GridPane pane, double size) {
        // Repaint the background
    	Painter.paint(pane, size);
    	rtArray.clear();

    	// Populate new grid with critters
        for(int i = 0; i < Params.WORLD_HEIGHT; i++) {
        	for(int j = 0; j < Params.WORLD_WIDTH; j++) {
        		Critter crit = popGrid[i][j];
        		if(crit != null) {
        		    // Get critter shape
        		    Shape shape = getShape(crit,size);
        		    // Create rotate transition object
                    RotateTransition rt = new RotateTransition(Duration.millis(2000),shape);
                    // Randomly rotate shapes left or right
                    if(Math.random()<0.5) {
                        rt.setByAngle(360);
                    } else {
                        rt.setByAngle(-360);
                    }
                    // Rotate indefinitely
                    rt.setCycleCount(Animation.INDEFINITE);
                    rt.setInterpolator(Interpolator.LINEAR);
                    rt.play();
                    // Add RotateTransition object to ArrayList
                    rtArray.add(rt);
        			pane.add(shape, crit.x_coord, crit.y_coord);
        		}
        	}
        }
    }


    /**
     * Returns a Polygon shape designated by the given critter and defined
     * to fit in a square of side length = size.
     *
     * @param crit critter for whom we are obtaining a shape
     * @param size length of one side of a cell in the grid
     * @return the Shape requested by the critter's viewShape()
     */
    private static Shape getShape(Critter crit, double size) {
		Shape currShape;

		CritterShape shapeName = crit.viewShape();
		switch(shapeName) {
		case SQUARE:
			currShape = CritterPolygon.getRectangle(crit.viewFillColor(),
					crit.viewOutlineColor(), size, size);
			break;
		case CIRCLE:
			currShape = CritterPolygon.getCircle(crit.viewFillColor(),
					crit.viewOutlineColor(), size, size);
			break;
		case TRIANGLE:
			currShape = CritterPolygon.getTriangle(crit.viewFillColor(),
					crit.viewOutlineColor(), size, size);
			break;
		case DIAMOND:
			currShape = CritterPolygon.getDiamond(crit.viewFillColor(),
					crit.viewOutlineColor(), size, size);
			break;
		case STAR:
			currShape = CritterPolygon.getStar(crit.viewFillColor(),
					crit.viewOutlineColor(), size, size);
			break;
		case HEXAGON:
			currShape = CritterPolygon.getHexagon(crit.viewFillColor(), 
					crit.viewOutlineColor(), size, size);
			break;
		case ELLIPSE:
			currShape = CritterPolygon.getEllipse(crit.viewFillColor(), 
					crit.viewOutlineColor(), size, size); 
			break;
		default:
			currShape = CritterPolygon.getCircle(crit.viewFillColor(),
					crit.viewOutlineColor(), size, size);
			break;
		}

		return currShape;
    }

	/* END --- NEW FOR PROJECT 5
			rest is unchanged from Project 4 */

    private int energy = 0;

    private int x_coord;
    private int y_coord;
    private static boolean fightPhase = false;	// whether or not the current phase is doEncounters()
    private static int timestep = 0;
    private boolean tryMoved = false;	// whether the critter has moved yet this timestep


    private static List<Critter> population = new ArrayList<Critter>();
    private static ArrayList<RotateTransition> rtArray = new ArrayList<>();
    private static Critter[][] popGrid =
    		new Critter[Params.WORLD_HEIGHT][Params.WORLD_WIDTH]; // Grid of critters in population
    															  // Is updated at the end of every worldtimestep();
    private static List<Critter> babies = new ArrayList<Critter>();

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
            /* Update Grid with new critter*/

            popGrid[((Critter) crit).y_coord][((Critter) crit).x_coord] = (Critter) crit;
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            throw new InvalidCritterException(critter_class_name);
        }
        catch (Exception e) {}
    }

    /**
     * Gets a list of critters of a specific type.
     *
     * @param critter_class_name What kind of Critter is to be listed.
     *                           Unqualified class name.
     * @return List of Critters.
     * @throws InvalidCritterException
     */
    public static List<Critter> getInstances(String critter_class_name)
            throws InvalidCritterException {
        List<Critter> critList = new ArrayList<>();
        Object foo = null;
        try {
            // Instantiate unspecified class
            Class<?> critClass = null;
            // Define class using reflection
            critClass = Class.forName(myPackage + "." + critter_class_name);
            // Instantiate class
            foo = critClass.getConstructor().newInstance();
        }
        // Catch instantiation errors
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new InvalidCritterException(critter_class_name);
        }
        // Collect instances of that critter class into Critter ArrayList for return
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
        for(int i = 0; i < popGrid.length; i++) {
            for(int j = 0; j < popGrid[0].length; j++) {
                popGrid[i][j] = null;
            }
        }

    }

    public static void worldTimeStep() {
        timestep++;
        // Individual timesteps
        for(Critter crit : population) {
            crit.tryMoved = false;
            crit.doTimeStep();
        }

        // Remove critters dead from doTimeStep();
        cullDead();

        doEncounters();

        // Subtract rest energy
        for(Critter crit : population) {
            crit.energy -= Params.REST_ENERGY_COST;
        }

        try {
            genClover();
        } catch (InvalidCritterException e) {}

        // Removing critters dead from resting
        cullDead();

        // Add babies
        population.addAll(babies);
        babies.clear();

        // Update Grid
        popGrid = new Critter[Params.WORLD_HEIGHT][Params.WORLD_WIDTH]; // refresh Grid
    	for(Critter crit : population) {
    		popGrid[crit.y_coord][crit.x_coord] = crit;
    	}
    }

    /**
     * Removes dead critters from the population
     */
    private static void cullDead() {
    	Iterator<Critter> iter = population.iterator();
        while(iter.hasNext()) {
            Critter crit = iter.next();
            if(crit.energy <= 0)
                iter.remove();
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

        // Remove critters that are dead or have moved away from encounters
        // Remove dead critters from population as well
        Iterator<Critter> iter = encounters.iterator();
        for(int i = 0; i < 2; i++){ // Only check the first two (A and B)
            Critter X = iter.next();
            if(X.getEnergy() <= 0) {
            	population.remove(X);
                iter.remove();
            }
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
                population.remove(B);
            }else { // Critter B wins
                B.energy += A.energy/2;
                A.energy = 0;
                encounters.remove(A);
                population.remove(A);
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

    protected final void walk(int direction) {
        if(!tryMoved) {
            int[] coord = findCoord(direction, false);	// find the coordinate
            if(!(fightPhase && existCrit(coord) != null)) { // It can't be both the fightPhase and a critter exists at coord
                // Move the critter
                x_coord = coord[0]; y_coord = coord[1];
            }
        }
        tryMoved = true; // if walk() is invoked this timestep, it can move no any longer this timestep
        energy -= Params.WALK_ENERGY_COST;
    }

    protected final void run(int direction) {
        if(!tryMoved) {
            int[] coord = findCoord(direction, true);	// find the coordinate
            if(!(fightPhase && existCrit(coord) != null)) { // It can't be both the fightPhase and a critter exists at coord
                // Move the critter
                x_coord = coord[0]; y_coord = coord[1];
            }
        }
        tryMoved = true; // if run() is invoked this timestep, it can move no longer this timestep
        energy -= Params.RUN_ENERGY_COST;
    }

    protected final void reproduce(Critter offspring, int direction) {
        if(this.energy < Params.MIN_REPRODUCE_ENERGY)
            return;
        // Assigning energy
        offspring.energy = this.energy/2;
        this.energy = (int) Math.ceil(this.energy/2.0);
        // Assigning location
        int[] coordArr = findCoord(direction, false);
        offspring.x_coord = coordArr[0];
        offspring.y_coord = coordArr[1];

        babies.add(offspring);
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
     * @return Critter at that coordinate. If none exists, then null.
     */
    private Critter existCrit(int[] coord) {
        for(Critter crit : population) {	// find a live critter that matches the coordinate
            if(crit.x_coord == coord[0] && crit.y_coord == coord[1] && crit.energy > 0)
                return crit;
        }
        return null;
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
    }

    /*
     * General class with static methods that return the enumerated Shapes all critters can display
     */
    private static class CritterPolygon{
    	/**
    	 * Returns a diamond-shaped Polygon based on parameters passed in. It is designed to fill the pane
    	 * it will be added to. Diamond is best shown when pane is square or a tall rectangle.
    	 *
    	 * @param fillColor		color to fill inside shape
    	 * @param outlineColor	color to stroke outline shape
    	 * @param paneWidth		width in pixels of pane to which this shape will be added.
    	 * @param paneHeight	height in pixels of pane to which this shape will be added.
    	 * @return diamond		Shape (Polygon) in a diamond shape.
    	 */
    	static Polygon getDiamond (Color fillColor, Color outlineColor, double paneWidth, double paneHeight) {
    		Polygon diamond = new Polygon();
    		paneWidth -= 2;
    		paneHeight -= 2;

    		// Calculating the points of the diamond
    		ObservableList<Double> list = diamond.getPoints();

    		list.add(paneWidth/2.0); list.add(0.0);            // top corner
    		list.add(paneWidth);	 list.add(paneHeight/2.0); // right corner
    		list.add(paneWidth/2.0); list.add(paneHeight);   // bottom corner
    		list.add(0.0);         	 list.add(paneHeight/2.0); // left corner

    		// Paint
    		diamond.setFill(fillColor);
    		diamond.setStroke(outlineColor);

    		return diamond;
    	}

    	/**
    	 * Returns a Circle based on parameters passed in. It is designed to fill the pane
    	 * it will be added to. Circle is best shown when pane is square.
    	 *
    	 * @param fillColor		color to fill inside shape
    	 * @param outlineColor	color to stroke outline shape
    	 * @param paneWidth		width in pixels of pane to which this shape will be added.
    	 * @param paneHeight	height in pixels of pane to which this shape will be added.
    	 * @return circle		Shape (Circle) to be added to a pane
    	 */
    	static Circle getCircle (Color fillColor, Color outlineColor, double paneWidth, double paneHeight) {
    		Circle circle = new Circle();

    		// Drawing circle
    		circle.setCenterX(paneWidth/2.0);
    		circle.setCenterY(paneHeight/2.0);
    		circle.setRadius(--paneWidth/2.0);
    		circle.setFill(fillColor);
    		circle.setStroke(outlineColor);

    		return circle;
    	}

    	/**
    	 * Returns a Rectangle based on parameters passed in. It is designed to fill the pane
    	 * it will be added to. Rectangle is best shown when pane is a quadrilateral.
    	 *
    	 * @param fillColor		color to fill inside shape
    	 * @param outlineColor	color to stroke outline shape
    	 * @param paneWidth		width in pixels of pane to which this shape will be added.
    	 * @param paneHeight	height in pixels of pane to which this shape will be added.
    	 * @return rect 		Shape (Rectangle) to be added to a pane
    	 */
    	static Rectangle getRectangle (Color fillColor, Color outlineColor, double paneWidth, double paneHeight) {
    		Rectangle rect = new Rectangle();

    		// Drawing rectangle
    		rect.setWidth(paneWidth);
    		rect.setHeight(paneHeight);
    		rect.setFill(fillColor);
    		rect.setStroke(outlineColor);

    		return rect;
    	}

    	/**
    	 * Returns a triangle polygon based on parameters passed in. It is designed to fill the pane
    	 * it will be added to. triangle is equilateral when pane is a square.
    	 *
    	 * @param fillColor		color to fill inside shape
    	 * @param outlineColor	color to stroke outline shape
    	 * @param paneWidth		width in pixels of pane to which this shape will be added.
    	 * @param paneHeight	height in pixels of pane to which this shape will be added.
    	 * @return triangle 	Shape (Triangle) to be added to a pane
    	 */
    	static Polygon getTriangle (Color fillColor, Color outlineColor, double paneWidth, double paneHeight) {
    		Polygon triangle = new Polygon();
    		paneWidth -= 1;
    		paneHeight -= 1;

    		// Calculating the points of the triangle
    		ObservableList<Double> list = triangle.getPoints();

    		list.add((paneWidth+1)/2.0); list.add(1.0);			// top corner
    		list.add(1.0); 		 	 list.add(paneHeight);	// left corner
    		list.add(paneWidth); 	 list.add(paneHeight);	// right corner

    		// Paint
    		triangle.setFill(fillColor);
    		triangle.setStroke(outlineColor);

    		return triangle;
    	}

    	/**
    	 * Returns a star polygon based on parameters passed in. It is designed to fill the pane
    	 * it will be added to. star is even when pane is a square.
    	 *
    	 * @param fillColor		color to fill inside shape
    	 * @param outlineColor	color to stroke outline shape
    	 * @param paneWidth		width in pixels of pane to which this shape will be added.
    	 * @param paneHeight	height in pixels of pane to which this shape will be added.
    	 * @return star		 	Shape (Star) to be added to a pane
    	*/
    	static Polygon getStar (Color fillColor, Color outlineColor, double paneWidth, double paneHeight) {
    		Polygon star = new Polygon();
    		paneWidth -= 2;
    		paneHeight -= 2;

    		// Calculating the points of a star
    		ObservableList<Double> list = star.getPoints();

    		// We start at the top point of the star and continue clockwise
    		list.add(paneWidth/2.0);     list.add(2.0);
    		list.add(4*paneWidth/6.0);   list.add(paneHeight/3.0);
    		list.add(paneWidth-1);       list.add(paneHeight/3.0);
    		list.add(9*paneWidth/12.0);  list.add(2*paneHeight/3.0);
    		list.add(10*paneWidth/12.0); list.add(paneHeight);
    		list.add(paneWidth/2.0);     list.add(5*paneHeight/6.0);
    		list.add(2*paneWidth/12.0);  list.add(paneHeight);
    		list.add(3*paneWidth/12.0);  list.add(2*paneWidth/3.0);
    		list.add(0.0);               list.add(paneHeight/3.0);
    		list.add(4*paneWidth/12.0);  list.add(paneHeight/3.0);

    		// Paint
    		star.setFill(fillColor);
    		star.setStroke(outlineColor);

    		return star;
    	}

    	/**
    	 * Returns a Hexagon polygon based on parameters passed in. It is designed to fill the pane
    	 * it will be added to. 
    	 *
    	 * @param fillColor		color to fill inside shape
    	 * @param outlineColor	color to stroke outline shape
    	 * @param paneWidth		width in pixels of pane to which this shape will be added.
    	 * @param paneHeight	height in pixels of pane to which this shape will be added.
    	 * @return hex		 	Shape (Hexagon) to be added to a pane
    	*/
    	static Polygon getHexagon (Color fillColor, Color outlineColor, double paneWidth, double paneHeight) {
    		Polygon hex = new Polygon();
    		
    		// Calculating points
    		ObservableList<Double> list = hex.getPoints();
    		
    		list.add((paneWidth+2)/3);	list.add(2.0);
    		list.add(2*(paneWidth+2)/3);list.add(2.0);
    		list.add(paneWidth);		list.add((paneHeight+2)/3);
    		list.add(paneWidth);		list.add(2*(paneHeight+2)/3);
    		list.add(2*(paneWidth+2)/3);list.add(paneHeight-1);
    		list.add((paneWidth+2)/3);	list.add(paneHeight-1);
    		list.add(2.0);				list.add(2*(paneHeight+2)/3);
    		list.add(2.0);				list.add((paneHeight+2)/3);
    		
    		// Paint
    		hex.setFill(fillColor);
    		hex.setStroke(outlineColor);
    		
    		return hex;
    	}
    	
    	/**
    	 * Returns a Ellipse based on parameters passed in. It is designed to fill the pane
    	 * it will be added to. 
    	 *
    	 * @param fillColor		color to fill inside shape
    	 * @param outlineColor	color to stroke outline shape
    	 * @param paneWidth		width in pixels of pane to which this shape will be added.
    	 * @param paneHeight	height in pixels of pane to which this shape will be added.
    	 * @return hex		 	Shape (Ellipse) to be added to a pane
    	*/
    	static Ellipse getEllipse (Color fillColor, Color outlineColor, double paneWidth, double paneHeight) {
    		Ellipse  ellipse = new Ellipse();
    		
    		// Drawing ellipse
    		ellipse.setCenterX(paneWidth/2.0);
    		ellipse.setCenterY(paneWidth/2.0);
    		ellipse.setRadiusX(--paneWidth/2.0);
    		ellipse.setRadiusY(--paneWidth/4.0);
    		ellipse.setFill(fillColor);
    		ellipse.setStroke(outlineColor);

    		return ellipse;
    	}
    }


    private static class Painter{
    	/*
    	 * Paint the grid lines in black.  The purpose is two-fold -- to indicate boundaries of
    	 * icons, and as place-holders for empty cells.  Without placeholders, grid may not display properly.
    	 */
    	private static void paintGridLines(GridPane grid, double size) {
    		ColumnConstraints cc;
    		RowConstraints rc;
    		for (int i = 0; i < Params.WORLD_WIDTH; i++) {
    			for (int j = 0; j < Params.WORLD_HEIGHT; j++) {
    				Shape s = new Rectangle(size, size);
    				s.setFill(null);
    				s.setStroke(Color.BLACK);
    				grid.add(s, i, j);
    			}
    		}
    	}


    	/**
    	 * Paints the icon shapes on a grid.
    	 */
    	public static void paint(GridPane grid, double size) {
    		grid.getChildren().clear(); // clear the grid
    		paintGridLines(grid, size);		// paint the borders
    	}
    }


}
