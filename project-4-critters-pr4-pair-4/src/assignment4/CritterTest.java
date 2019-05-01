package assignment4;

/*
 * CRITTERS CritterTest.java
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

import static org.junit.Assert.*;

import org.junit.Test;

public class CritterTest {

  @Test
  public void testCreateCritter() {
    fail("Not yet implemented");
  }

  @Test
  public void testDisplayWorld() throws InvalidCritterException {
    Critter.createCritter("Clover");
    Critter.displayWorld();
  }
}
