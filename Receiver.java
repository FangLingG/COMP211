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
 * Filename: Receiver.java Names: Student-IDs: Date:
 * ***********************************
 */
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Ling
 */
public class Receiver extends NetworkHost {

    /*
     * Predefined Constants (static member variables):
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
     *  Message: Used to encapsulate a message coming from application layer
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
    // state information for the receiver.
    private int checksum;
    private int seqnum;
    private int acknum;
    private boolean acked;

    // Also add any necessary methods (e.g. checksum of a String)
    /**
     *
     * @param packet
     * @return checksum
     */
    public int checkSum(Packet packet) {

        byte[] bytes = packet.getPayload().getBytes();
        int res = 0;
        for (int i = 0; i < packet.getPayload().length(); i++) {
            res += bytes[i];
        }
        checksum = res + packet.getSeqnum() + packet.getAcknum();
        return checksum;
    }

    // This is the constructor.  Don't touch!
    /**
     *
     * @param entityName
     * @param events
     * @param pLoss
     * @param pCorrupt
     * @param trace
     * @param random
     */
    public Receiver(int entityName,
            EventList events,
            double pLoss,
            double pCorrupt,
            int trace,
            Random random) {
        super(entityName, events, pLoss, pCorrupt, trace, random);
    }

    // This routine will be called whenever a packet sent from the sender
    // (i.e. as a result of a udtSend() being done by a Sender procedure)
    // arrives at the receiver. Argument "packet" is the (possibly corrupted)
    // packet sent from the sender.
    /**
     *
     * @param packet
     */
    protected void Input(Packet packet) {
        seqnum = packet.getSeqnum();
        acknum = packet.getAcknum();
        checksum = checkSum(packet);
        System.out.println(seqnum + " " + acknum + " " + checksum);
        if (checksum == packet.getChecksum()) {
            deliverData(packet.getPayload());
            packet = new Packet(seqnum, acknum, checksum);
            udtSend(packet);
            acked = true;
        } else {
            packet.setChecksum(checksum);
            packet.setPayload("");
            udtSend(packet);           
        }

    }

    // This routine will be called once, before any of your other receiver-side
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of the receiver).
    /**
     *
     */
    protected void Init() {
        acked = false;
    }
}
