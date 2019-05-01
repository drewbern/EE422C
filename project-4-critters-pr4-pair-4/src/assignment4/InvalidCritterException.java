package assignment4;

/*
 * CRITTERS InvalidCritterException.java
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

public class InvalidCritterException extends Exception {

    String offending_class;

    public InvalidCritterException(String critter_class_name) {
        offending_class = critter_class_name;
    }

    public String toString() {
        return "Invalid Critter Class: " + offending_class;
    }
}
