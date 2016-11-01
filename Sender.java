/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2;

/**
 *
 * @author Ling
 */
/**
 * ***********************************
 * Filename: Sender.java Names: Student-IDs: Date:
 * ***********************************
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.util.Random;

/**
 * STOP AND WAIT PROTOCOL
 *
 * @author Ling
 */
public class Sender extends NetworkHost {

    /*
     * Predefined Constant (static member variables):
     *
     *   int MAXDATASIZE : the maximum size of the Message data and
     *                     Packet payload
     *
     *
     * Predefined Member Methods:
     *
     *  void startTimer(double increment):
     *       Starts a timer, which will expire in
     *       "increment" time units, causing the interrupt handler to be
     *       called.  You should only call this in the Sender class.
     *  void stopTimer():
     *       Stops the timer. You should only call this in the Sender class.
     *  void udtSend(Packet p)
     *       Sends the packet "p" into the network to arrive at other host
     *  void deliverData(String dataSent)
     *       Passes "dataSent" up to application layer. You should only call this in the
     *       Receiver class.
     *  double getTime()
     *       Returns the current time in the simulator.  Might be useful for
     *       debugging.
     *  String getReceivedData()
     *       Returns a String with all data delivered to receiving process.
     *       Might be useful for debugging. You should only call this in the
     *       Sender class.
     *  void printEventList()
     *       Prints the current event list to stdout.  Might be useful for
     *       debugging, but probably not.
     *
     *
     *  Predefined Classes:
     *
     *  Message: Used to encapsulate the message coming from application layer
     *    Constructor:
     *      Message(String inputData):
     *          creates a new Message containing "inputData"
     *    Methods:
     *      boolean setData(String inputData):
     *          sets an existing Message's data to "inputData"
     *          returns true on success, false otherwise
     *      String getData():
     *          returns the data contained in the message
     *  Packet: Used to encapsulate a packet
     *    Constructors:
     *      Packet (Packet p):
     *          creates a new Packet, which is a copy of "p"
     *      Packet (int seq, int ack, int check, String newPayload)
     *          creates a new Packet with a sequence field of "seq", an
     *          ack field of "ack", a checksum field of "check", and a
     *          payload of "newPayload"
     *      Packet (int seq, int ack, int check)
     *          chreate a new Packet with a sequence field of "seq", an
     *          ack field of "ack", a checksum field of "check", and
     *          an empty payload
     *    Methods:
     *      boolean setSeqnum(int n)
     *          sets the Packet's sequence field to "n"
     *          returns true on success, false otherwise
     *      boolean setAcknum(int n)
     *          sets the Packet's ack field to "n"
     *          returns true on success, false otherwise
     *      boolean setChecksum(int n)
     *          sets the Packet's checksum to "n"
     *          returns true on success, false otherwise
     *      boolean setPayload(String newPayload)
     *          sets the Packet's payload to "newPayload"
     *          returns true on success, false otherwise
     *      int getSeqnum()
     *          returns the contents of the Packet's sequence field
     *      int getAcknum()
     *          returns the contents of the Packet's ack field
     *      int getChecksum()
     *          returns the checksum of the Packet
     *      String getPayload()
     *          returns the Packet's payload
     *
     */
    // Add any necessary class variables here. They can hold
    // state information for the sender.
    private int seqnum = 0;
    private int acknum;
    private int checksum;
    private String payload;
    Packet packet;

    //state information
    private boolean acked ;  
    
    // Also add any necessary methods (e.g. checksum of a String)
    /**
     *
     * @param message
     * @return checksum
     */
    public int checkSum(String message) {
        byte[] bytes = message.getBytes();
        int res = 0;
        for (int i = 0; i < message.length(); i++) {
            res += bytes[i];
        }
        checksum = res + seqnum + acknum;
        return checksum;
    }

    // This is the constructor.  Don't touch!
    public Sender(int entityName,
            EventList events,
            double pLoss,
            double pCorrupt,
            int trace,
            Random random) {
        super(entityName, events, pLoss, pCorrupt, trace, random);
    }

    // This routine will be called whenever the application layer at the sender
    // has a message to send.  The job of your protocol is to insure that
    // the data in such a message is delivered in-order, and correctly, to
    // the receiving application layer.
    /**
     *
     * @param message
     */
    protected void Output(Message message) {
        if (acked = true) {
            seqnum += message.getData().length();
            acknum = seqnum + message.getData().length();
            payload = message.getData();
            checksum = checkSum(payload);
            packet = new Packet(seqnum, acknum, checksum, payload);
            udtSend(packet);
            startTimer(15);           
            acked = false;
           // System.out.println(acked);
        }
    }

    // This routine will be called whenever a packet sent from the receiver
    // (i.e. as a result of a udtSend() being done by a receiver procedure)
    // arrives at the sender.  "packet" is the (possibly corrupted) packet
    // sent from the receiver.
    /**
     *
     * @param packet
     */
    protected void Input(Packet packet) {
        if (packet.getSeqnum() == seqnum && packet.getAcknum() == acknum && packet.getChecksum() == checksum) {
            acked = true;
            stopTimer();
            System.out.println("Received seqnum: " + packet.getSeqnum() + " acknum: " + packet.getAcknum() + " checkcum: " + packet.getChecksum() + " payload:" + packet.getPayload());
            System.out.println("packet " + (seqnum) + " send successfully");
        }  else {
         // if (packet.getSeqnum() != seqnum || packet.getAcknum() != acknum || packet.getChecksum() != checksum) {
            System.out.println("Received seqnum: " + packet.getSeqnum() + " acknum: " + packet.getAcknum());
            /**
             * packet.setPayload(payload); packet.setSeqnum(seqnum);
             * packet.setAcknum(acknum); packet.setChecksum(checksum);
            *
             */
           // System.out.println(acked);
            packet = new Packet(seqnum, acknum, checksum, payload);
            stopTimer();
            udtSend(packet);
            startTimer(35);

        }
        /**
         * else { acked = true; stopTimer(); System.out.println("Received
         * seqnum: " + packet.getSeqnum() + " acknum: " + packet.getAcknum() + "
         * checkcum: " + packet.getChecksum() + " payload:" +
         * packet.getPayload()); System.out.println("packet " + (seqnum) + "
         * send successfully");
         *
         * }
         *
         */
        

    }

    // This routine will be called when the senders's timer expires (thus
    // generating a timer interrupt). You'll probably want to use this routine
    // to control the retransmission of packets. See startTimer() and
    // stopTimer(), above, for how the timer is started and stopped.
    /**
     * Timeout control
     */
    protected void TimerInterrupt() {
        if (acked = false) {
            udtSend(packet);
            startTimer(35);
        } else {
            System.out.println("last packet has already acked");
        }
    }

    // This routine will be called once, before any of your other sender-side
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of the sender).
    /**
     *
     */
    protected void Init() {
        this.acked = true;
        //System.out.println(acked);
    }
}
