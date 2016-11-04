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
 * Filename: Sender.java 
 * Names: LING FANG 
 * Student-IDs: 201218935
 * Date: 2016.11.4
 * ***********************************
 */
import java.util.LinkedList;
import java.util.Queue;
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
    // state information for the sender.
    private int seqNum = 0;
    private int ackNum;
    private int checkSum;
    private String payload;
    private Packet packet;
    // ======================================================
    // keep a copy of a packet for possible retransmission
    // ======================================================
    private Queue<Packet> copyOfPkt;
    // ==========================================================
    // state information added to control the state of the sender
    // ==========================================================

    private static boolean acked;

    // Also add any necessary methods (e.g. checkSum of a String)
    /**
     *
     * @param message
     * @return checkSum
     */
    public int checkSumming(String message) {
        byte[] bytes = message.getBytes();
        int res = 0;
        for (int i = 0; i < message.length(); i++) {
            res += bytes[i];
        }
        checkSum = res + seqNum + ackNum;
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
     * the constructor
     */
    public Sender(int entityName,
            EventList events,
            double pLoss,
            double pCorrupt,
            int trace,
            Random random) {
        super(entityName, events, pLoss, pCorrupt, trace, random);
    }

    /**
     *
     * @param message Message from application layer
     * This routine will be called whenever the application layer at the sender
     * has a message to send.
     */
    @Override
    protected void Output(Message message) {

        if (acked = true) {
            // ===============================================================
            // Let sequence number increase by one when new message Output().
            // ===============================================================

            seqNum += 1; //increase sequence number by one each time
            ackNum = seqNum + message.getData().length();
            payload = message.getData();
            checkSum = checkSumming(payload);
            packet = new Packet(seqNum, ackNum, checkSum, payload);
            copyOfPkt.offer(packet);
            udtSend(packet);
            startTimer(40);
            acked = false;
        }
    }

    /**
     *
     * @param packet "packet" is the (possibly corrupted) packet sent from the
     * receiver.
     * This routine will be called whenever a packet sent from the receiver
     * (i.e. as a result of a udtSend() being done by a receiver procedure)
     * arrives at the sender.
     */
    @Override
    protected void Input(Packet packet) {

        /* 
         * ==========================================================================
         * ignore payload of packet from receiver even if sometimes it is corrupted
         * if ACK were correct, set the state of Sender in order to send next message
         * if not, send packet again
         * ==========================================================================
         */
        if (packet.getSeqnum() == seqNum && packet.getAcknum() == ackNum && packet.getChecksum() == checkSum) {
            acked = true;

            /* 
             * ================================================
             * If Ack Info. correct, discard the copy of packet
             * ================================================
             */
            copyOfPkt.poll();
            stopTimer();
            if (!packet.getPayload().isEmpty()) {
                System.out.println("***************************************");
                System.out.println("poayload part from receiver being corrupted, but has no influence on data reliable, ignore it");
            }
            System.out.println("***************************************");
            System.out.println("packet " + seqNum + " send successfully");
            System.out.println("***************************************");
        } else {
            System.out.println("packet from receiver being corrupted, send packet again");
            stopTimer();
            udtSend(copyOfPkt.peek());
            startTimer(40);
        }

    }

    /* 
     * This routine will be called when the senders's timer expires (thus
     * generating a timer interrupt). If packet is unacked, send it again.
     */
    @Override
    protected void TimerInterrupt() {
        if (acked == false) {
            System.out.println("Timeout, send  packet " + seqNum + " again");
            udtSend(copyOfPkt.peek());
            startTimer(40);
        } else {
            System.out.println("last packet has already acked");
        }
    }

    /*
     * initialize member variables which were add to control the state of the
     * sender.
     */
    @Override
    protected void Init() {
        acked = true;
        copyOfPkt = new LinkedList();
    }
}
