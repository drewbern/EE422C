/*
 * Do not change or submit this file.
 */

package assignment4;

import assignment4.Critter.TestCritter;

public class Clover extends TestCritter {

    public String toString() {
        return "@";
    }

    public boolean fight(String not_used) {
        return false;
    }

    public void doTimeStep() {
        setEnergy(getEnergy() + Params.PHOTOSYNTHESIS_ENERGY_AMOUNT);
    }
}
