/*
 * CRITTERS Main.java
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;

/*
 * Usage: java <pkg name>.Main <input file> test input file is
 * optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */

public class Main {

    /* Scanner connected to keyboard input, or input file */
    static Scanner kb;

    /* Input file, used instead of keyboard input if specified */
    private static String inputFile;

    /* If test specified, holds all console output */
    static ByteArrayOutputStream testOutputString;

    /* Use it or not, as you wish! */
    private static boolean DEBUG = false;

    /* if you want to restore output to console */
    static PrintStream old = System.out;

    /* Gets the package name.  The usage assumes that Critter and its
       subclasses are all in the same package. */
    private static String myPackage; // package of Critter file.

    /* Critter cannot be in default pkg. */
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /**
     * Main method.
     *
     * @param args args can be empty.  If not empty, provide two
     *             parameters -- the first is a file name, and the
     *             second is test (for test output, where all output
     *             to be directed to a String), or nothing.
     * @throws InvalidCritterException 
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            try {
                inputFile = args[0];
                kb = new Scanner(new File(inputFile));
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java <pkg name>.Main OR java <pkg name>.Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java <pkg name>.Main OR java <pkg name>.Main <input file> <test output>");
            }
            if (args.length >= 2) {
                /* If the word "test" is the second argument to java */
                if (args[1].equals("test")) {
                    /* Create a stream to hold the output */
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    /* Save the old System.out. */
                    old = System.out;
                    /* Tell Java to use the special stream; all
                     * console output will be redirected here from
                     * now */
                    System.setOut(ps);
                }
            }
        } else { // If no arguments to main
            kb = new Scanner(System.in); // Use keyboard and console
        }
        commandInterpreter(kb);
        System.out.flush();
    }

    /* Do not alter the code above for your submission. */

    /**
     * Command Interpreter method. This function reads in commands
     * one by one, and executes that command.
     *
     * @param kb   kb is the keyboard Scanner that is passed
     * 			   into the function. It should only be created
     * 			   once. 
     */
    private static void commandInterpreter(Scanner kb) {
    	Critter.clearWorld();
    	System.out.print("critters> ");
        String input = kb.nextLine();
        System.out.println();
        /* Split command into separate parts */
        String[] command = input.split(" ");
        /* Run commands until the 'quit' command is reached*/
        while(command[0] != "quit") {
        	switch(command[0]) {
        	case "quit":
				/* Report error for more than 1 token */
        		if(command.length > 1){
					System.out.println("error processing: " + input);
					break;
				}
        		return;
        	case "show":
				/* Report error for more than 1 token */
        		if(command.length > 1) {
        			System.out.println("error processing: " + input);
        			break;
        		}
        		Critter.displayWorld();
        		break;
			case "seed":
				/* Report error for more than 2 tokens */
				if(command.length > 2){
					System.out.println("error processing: " + input);
					break;
				}
				try {
					/* Set seed for debugging */
					Critter.setSeed(Integer.parseInt(command[1]));
				}
				catch(Exception e) {
					System.out.println("error processing: " + input);
				}
				break;
        	case "step":
				/* Report error for more than 2 tokens */
        		if(command.length > 2) {
        			System.out.println("error processing: " + input);
        			break;
        		}
        		/* Set default steps to 1 */
        		int num = 1;
        		try {
					/* Get step number parameter if there is one */
        			if(command.length > 1) num = Integer.parseInt(command[1]);
        			/* Invoke worldTimeStep() num times */
        			for(int i = 0; i < num; i++)
        				Critter.worldTimeStep();
        		}
        		catch(Exception e) {
        			System.out.println("error processing: " + input);
        		}
        		/*catch(IndexOutOfBoundsException e) {
        			Critter.worldTimeStep();
        		}
        		*/
        		break;
        	case "create":
				/* Report error for more than 3 tokens */
        		if(command.length > 3) {
        			System.out.println("error processing: " + input);
        			break;
        		}
        		String critterClass = null;
        		try {
        			/* Get critter class parameter */
            		critterClass = command[1];
            		num = 1;
					/* Get critter number parameter if there is one */
	        		if(command.length == 3) num = Integer.parseInt(command[2]);
	        		/* Invoke createCritter num times */
	        		for(int i = 0; i < num; i++) {
	        			Critter.createCritter(critterClass);
	        		}
        		}
        		catch (Exception e) {
        			System.out.println("error processing: " + input);
        		}
        		break;
        	case "stats":
				/* Report error for more than 2 tokens */
        		if(command.length > 2) {
        			System.out.println("error processing: " + input);
        			break;
        		}
        		/* Initialize List for Critter instances */
        		List<Critter> instances = null;
        		String className = null;
		        try {
					className = command[1];
					/* Get class object of the specified class from input */
					Class<?> critClass = Class.forName(myPackage + "." + className);
					/* Get runStats method of critClass class */
					Method statsMethod = critClass.getMethod("runStats", List.class);
					/* Get critter instances of specified Critter type */
					instances = Critter.getInstances(className);
					/* Invoke runStats for the critter instances if the class overrides Critter's runStats */
					statsMethod.invoke(critClass, instances);
				} catch (NoSuchMethodException e) {
		        	/* If the class doesn't override runStats, run Critter's runStats */
		        	Critter.runStats(instances);
		        } catch (Exception e) {
					System.out.println("error processing: " + input);
				}
        		break;
        	case "clear":
				/* Report error for more than 1 token */
        		if(command.length > 1) {
        			System.out.println("error processing: " + input);
        			break;
        		}
				Critter.clearWorld();
        		break;
        	default:
				/* Report and invalid command */
        		System.out.println("invalid command: " + input);
        		break;
        	}
        	/* Get next command */
        	System.out.print("critters> ");
        	input = kb.nextLine();
        	System.out.println();
        	/* Split command into separate tokens */
        	command = input.split(" ");
        }
    }
}
