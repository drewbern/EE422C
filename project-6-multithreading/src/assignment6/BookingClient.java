package assignment6;
/* MULTITHREADING <MyClass.java>
 * EE422C Project 6 submission by
 * Drew Bernard
 * dhb653
 * 16225
 * Slip days used: 2
 * Spring 2019
 */

import java.util.*;
import java.lang.Thread;

import assignment6.Theater.BoxOffice;

public class BookingClient {

    public Map<String, Integer> office;
    public Theater theater;

    /**
     * @param office  maps box office id to number of customers in line
     * @param theater the theater where the show is playing
     */
    public BookingClient(Map<String, Integer> office, Theater theater) {
        this.office = office;
        this.theater = theater;
    }

    /**
     * Starts the box office simulation by creating (and starting) threads
     * for each box office to sell tickets for the given theater
     *
     * @return list of threads used in the simulation,
     * should have as many threads as there are box offices
     */
    public List<Thread> simulate() {
        List<Thread> threads = new ArrayList<>();

        // Iterate through Box offices and create threads for each
        Iterator it = office.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            BoxOffice bO = theater.new BoxOffice((String) pair.getKey(), (Integer) pair.getValue());
            Thread bOThread = new Thread(bO);
            bOThread.start();

            threads.add(bOThread);
        }

        return threads;

    }

    public static void main(String[] args) {

        Map<String, Integer> test_office = new HashMap<String, Integer>();


        test_office.put("BX1", 3);
        test_office.put("BX3", 3);
        test_office.put("BX2", 4);
        test_office.put("BX5", 3);
        test_office.put("BX4", 3);

        Theater test_theater = new Theater(3, 5, "Ouija");
        BookingClient bc = new BookingClient(test_office, test_theater);


        // Create box offices
        List<Thread> BoxOffices = bc.simulate();


        // Join them
        for(Thread t : BoxOffices) {
            try {
                t.join();
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }

    }
}
