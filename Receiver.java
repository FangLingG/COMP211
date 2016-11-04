/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ling
 */
/**
 * ***********************************
 * Filename: Receiver.java 
 * Names: LING FANG 
 * Student-IDs: 201218935
 * Date: 2016.11.4
 * ***********************************
 */
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

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
     *          ack field of "ack", a checkSum field of "check", and a
     *          payload of "newPayload"
     *      Packet (int seq, int ack, int check)
     *          chreate a new Packet with a sequence field of "seq", an
     *          ack field of "ack", a checkSum field of "check", and
     *          an empty payload
     *    Methods:
     *      boolean setSeqnum(int n)
     *          sets the Packet's sequence field to "n"
     *          returns true on success, false otherwise
     *      boolean setAcknum(int n)
     *          sets the Packet's ack field to "n"
     *          returns true on success, false otherwise
     *      boolean setChecksum(int n)
     *          sets the Packet's checkSum to "n"
     *          returns true on success, false otherwise
     *      boolean setPayload(String newPayload)
     *          sets the Packet's payload to "newPayload"
     *          returns true on success, false otherwise
     *      int getSeqnum()
     *          returns the contents of the Packet's sequence field
     *      int getAcknum()
     *          returns the contents of the Packet's ack field
     *      int getChecksum()
     *          returns the checkSum of the Packet
     *      String getPayload()
     *          returns the Packet's payload
     *
     */
    // Add any necessary class variables here. They can hold
    // state information for the receiver.
    private int checkSum;
    private int seqNum;
    private int ackNum;
    //==========================
    // acked: state information
    //========================== 
    private static boolean acked;
    //==================================================
    //should not be static, just keep a copy of a packet
    //==================================================

    private static Queue<Packet> copyOfPkt;
    private static Stack<Integer> checkDuplicate = new Stack();

    /**
     *
     * @param packet
     * @return checkSum checksum of a Packet. 
     * Same approach with Sender
     */
    public int checkSumming(Packet packet) {
        byte[] bytes = packet.getPayload().getBytes();
        int res = 0;
        for (int i = 0; i < packet.getPayload().length(); i++) {
            res += bytes[i];
        }
        checkSum = res + packet.getSeqnum() + packet.getAcknum();
        return checkSum;
    }

    /**
     *
     * @param entityName
     * @param events
     * @param pLoss
     * @param pCorrupt
     * @param trace
     * @param random
     * This is the constructor
     */
    public Receiver(int entityName,
            EventList events,
            double pLoss,
            double pCorrupt,
            int trace,
            Random random) {
        super(entityName, events, pLoss, pCorrupt, trace, random);
    }

    /**
     *
     * @param packet packet sent from the sender
     */
    @Override
    protected void Input(Packet packet) {
        copyOfPkt.offer(packet);
        seqNum = packet.getSeqnum();
        ackNum = packet.getAcknum();
        checkSum = checkSumming(packet);

        /*
         * ====================================================================
         * handle the premature timeout or delayed ACK if a sequence number has
         * been acked but not last sequence number received it is a duplicate
         * ====================================================================
         */
        if (!(checkDuplicate.peek() != seqNum && checkDuplicate.contains(seqNum))) {
            if (checkSum == packet.getChecksum()) {

                /*
                 * ===================================================================
                 * Detect and discard duplicate data, appliacation will not receive it
                 * ===================================================================
                 */
                if (!getReceivedData().contains(packet.getPayload())) {
                    deliverData(packet.getPayload());
                    copyOfPkt.poll();
                }
                packet = new Packet(seqNum, ackNum, checkSum);
                udtSend(packet);
                acked = true;
                checkDuplicate.push(seqNum);

            } else {
                // ======================================================
                // send packet back to Sender, to require the same packet
                // ======================================================
                udtSend(copyOfPkt.peek());
            }
        } else {
            System.out.println("***************************************");
            System.out.println("Duplicate packet, discard it");
            System.out.println("***************************************");
        }

    }

    /* 
     * initialization (member variables added to control the state
     * of the receiver).
     */
    @Override
    protected void Init() {
        acked = false;
        copyOfPkt = new LinkedList();
        //prevent complier from throwing expection 
        checkDuplicate.push(0);
    }
}
