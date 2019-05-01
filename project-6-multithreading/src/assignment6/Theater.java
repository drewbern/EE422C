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

public class Theater {

    public int numRows;
    public int seatsPerRow;
    public String show;
    public Seat bestAvailableSeat;
    public Integer accessingClient = 0;
    public List<Ticket> ticketLog = new ArrayList<>();

    /**
     * the delay time you will use when print tickets
     */
    private int printDelay = 50; // 50 ms.  Use it in your Thread.sleep()

    public void setPrintDelay(int printDelay) {
        this.printDelay = printDelay;
    }

    public int getPrintDelay() {
        return printDelay;
    }

    /**
     * Represents a seat in the theater
     * A1, A2, A3, ... B1, B2, B3 ...
     */
    static class Seat {
        private int rowNum;
        private int seatNum;

        public Seat(int rowNum, int seatNum) {
            this.rowNum = rowNum;
            this.seatNum = seatNum;
        }

        public int getSeatNum() {
            return seatNum;
        }

        public int getRowNum() {
            return rowNum;
        }

        @Override
        public String toString() {
            String result = "";
            int tempRowNumber = rowNum + 1;
            do {
                tempRowNumber--;
                result = ((char) ('A' + tempRowNumber % 26)) + result;
                tempRowNumber = tempRowNumber / 26;
            } while (tempRowNumber > 0);
            result += seatNum;
            return result;
        }
    }

    /**
     * Represents a ticket purchased by a client
     */
    static class Ticket {
        private String show;
        private String boxOfficeId;
        private Seat seat;
        private int client;
        public static final int ticketStringRowLength = 31;


        public Ticket(String show, String boxOfficeId, Seat seat, int client) {
            this.show = show;
            this.boxOfficeId = boxOfficeId;
            this.seat = seat;
            this.client = client;
        }

        public Seat getSeat() {
            return seat;
        }

        public String getShow() {
            return show;
        }

        public String getBoxOfficeId() {
            return boxOfficeId;
        }

        public int getClient() {
            return client;
        }

        @Override
        public String toString() {
            String result, dashLine, showLine, boxLine, seatLine, clientLine, eol;

            eol = System.getProperty("line.separator");

            dashLine = new String(new char[ticketStringRowLength]).replace('\0', '-');

            showLine = "| Show: " + show;
            for (int i = showLine.length(); i < ticketStringRowLength - 1; ++i) {
                showLine += " ";
            }
            showLine += "|";

            boxLine = "| Box Office ID: " + boxOfficeId;
            for (int i = boxLine.length(); i < ticketStringRowLength - 1; ++i) {
                boxLine += " ";
            }
            boxLine += "|";

            seatLine = "| Seat: " + seat.toString();
            for (int i = seatLine.length(); i < ticketStringRowLength - 1; ++i) {
                seatLine += " ";
            }
            seatLine += "|";

            clientLine = "| Client: " + client;
            for (int i = clientLine.length(); i < ticketStringRowLength - 1; ++i) {
                clientLine += " ";
            }
            clientLine += "|";

            result = dashLine + eol +
                    showLine + eol +
                    boxLine + eol +
                    seatLine + eol +
                    clientLine + eol +
                    dashLine;

            return result;
        }
    }

    /**
     * Represents a box office that interacts with customers purchasing tickets
     */
    public class BoxOffice implements Runnable {
        private String Id;
        private Integer clientsWaiting;

        public BoxOffice(String Id, int clientsWaiting) {
            this.Id = Id;
            this.clientsWaiting = clientsWaiting;
        }

        // Runnable to iterate through
        @Override
        public void run(){
            for(int i = clientsWaiting; i > 0; i--) {
                Seat seat = bestAvailableSeat();

                synchronized (accessingClient) {

                    if (printTicket(Id, seat, accessingClient) == null) return;

                    accessingClient++;

                }
            }
        }
    }

    public Theater(int numRows, int seatsPerRow, String show) {
        this.numRows = numRows;
        this.seatsPerRow = seatsPerRow;
        this.show = show;
        this.bestAvailableSeat = new Seat(0,1);
    }

    /**
     * Calculates the best seat not yet reserved
     *
     * @return the best seat or null if theater is full
     */
    public synchronized Seat bestAvailableSeat() {

        // If there are no more available seats, return null
        if(bestAvailableSeat == null) {
            return null;
        }

        int row = bestAvailableSeat.getRowNum();
        int seatNum = bestAvailableSeat.getSeatNum() + 1;

        // Set next best seat
        if(bestAvailableSeat.getSeatNum() == seatsPerRow){
            row = bestAvailableSeat.getRowNum() + 1;
            seatNum = 1;
        }

        Seat lastBestSeat = bestAvailableSeat;
        bestAvailableSeat = new Seat(row, seatNum);

        return lastBestSeat;
    }

    /**
     * Prints a ticket for the client after they reserve a seat
     * Also prints the ticket to the console
     *
     * @param seat a particular seat in the theater
     * @return a ticket or null if a box office failed to reserve the seat
     */
    public synchronized Ticket printTicket(String boxOfficeId, Seat seat, int client) {

        // Return null if sold out
        if (seat == null) return null;
        if (ticketLog.size() == numRows*seatsPerRow) {
            System.out.println("Sorry, we are sold out!");
            return null;
        }

        //Create new ticket
        Ticket issuedTicket = new Ticket(show, boxOfficeId, seat, client);
        System.out.println(issuedTicket.toString());
        ticketLog.add(issuedTicket);


        // Delay for print delay
        try {
            Thread.sleep(getPrintDelay());
        } catch (InterruptedException e) {
            return issuedTicket;
        }

        return issuedTicket;

    }

    /**
     * Compares two tickets' seat numbers based on seat preference
     */
    public Comparator<Ticket> compareTicket = (T1, T2) -> {
        int retval = T1.getSeat().toString().compareTo(T2.getSeat().toString());
        return retval;
    };

    /**
     * Lists all tickets sold for this theater in order of purchase
     *
     * @return list of tickets sold
     */
    public List<Ticket> getTransactionLog() {
        // Sort the log based on the ticket seat
        Collections.sort(ticketLog, compareTicket);
        return ticketLog;
    }
}
