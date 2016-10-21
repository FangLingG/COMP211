package emailclient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * ***********************************
 * Filename: SMTPConnect.java 
 * Names: Ling Fang 
 * Student-IDs: 201218935
 * Date:2016.10.15
 * ***********************************
 * Names: Hao Bai
 * Student-IDs: 201218765
 * Date:2016.10.15
 */

import java.net.*;
import java.io.*;
import static java.lang.Integer.parseInt;
import java.util.*;

/**
 * Open an SMTP connection to mailserver and send one mail.
 *
 */
public class SMTPConnect {

    /* The socket to the server */
    private Socket connection;


    /* Streams for reading from and writing to socket */
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    private static final int SMTP_PORT = 25;
    private static final String CRLF = "\r\n";

    /* Are we connected? Used in close() to determine what to do. */
    private boolean isConnected = false;

    /* Count the num of recipient address */
    private int numOfRcpt = 0;
    private int numOfcc = 0;
    private String checkRCPT[];
    private String checkCC[];


    /* Create an SMTPConnect object. Create the socket and the
    associated streams. Initialise SMTP connection. */
    public SMTPConnect(EmailMessage mailmessage) throws IOException {
        connection = new Socket(mailmessage.DestHost, SMTP_PORT);
        /* Fill in */;
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new DataOutputStream(connection.getOutputStream());
        //fromServer = new BufferedReader(new InputStreamReader(System.in));
        //toServer = System.out;
        /* Fill in */

 /* Read a line from server and check that the reply code is 220.
	If not, throw an IOException. */
        String response1 = fromServer.readLine();
        if (!response1.startsWith("220")) {
            throw new IOException("Unable to connect server.");
        }

        /* Fill in */

 /* SMTP handshake. We need the name of the local machine.
	Send the appropriate SMTP handshake command. */
        String localhost = InetAddress.getLocalHost().getHostName();
        sendCommand("HELO " + localhost, 250);
        isConnected = true;
    }

    /* Send the message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    public void send(EmailMessage mailmessage) throws IOException {
        // initialize the num of rcpt and cc address.
        checkRCPT = mailmessage.Recipient.split(",");
        checkCC = mailmessage.otherRecipient.split(",");
        numOfRcpt = checkRCPT.length;
        numOfcc = checkCC.length;
        /* Fill in */
 /* Send all the necessary commands to send a message. Call
	   sendCommand() to do the dirty work. Do _not_ catch the
	   exception thrown from sendCommand(). */
        sendCommand(("MAIL FROM: " + "<" + mailmessage.Sender + ">"), 250);

        //send email to all recipients
        if (numOfRcpt > 1) {
            for (int i = 0; i <= numOfRcpt; i++) {
                sendCommand("RCPT TO: " + "<" + checkRCPT[i] + ">", 250);
                numOfRcpt--;
            }
        } else {
            sendCommand("RCPT TO: " + "<" + mailmessage.Recipient + ">", 250);
        }

        if (numOfcc > 1) {
            for (int i = 0; i <= numOfRcpt; i++) {
                sendCommand("RCPT TO: " + "<" + checkCC[i] + ">", 250);
                numOfcc--;
            }
        } else {
            sendCommand("RCPT TO: " + "<" + mailmessage.otherRecipient + ">", 250);
        }

        sendCommand("DATA", 354);
        sendCommand(mailmessage.Headers + mailmessage.Body + CRLF + ".", 250);


        /* Fill in */
    }

    /* Close SMTP connection. First, terminate on SMTP level, then
    close the socket. */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT", 221);
            //clienSocket.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /* Send an SMTP command to the server. Check that the reply code is
    what is is supposed to be according to RFC 821. */
    private void sendCommand(String command, int rc) throws IOException {
        /* Fill in */
 /* Write command to server and read reply from server. */
        toServer.writeBytes(command + CRLF);
        /* Fill in */

 /* Fill in */
 /* Check that the server's reply code is the same as the parameter
	rc. If not, throw an IOException. */
        String response = fromServer.readLine().split(" ")[0];
        int replyCode = parseInt(response);
        System.out.println(response);
        //int intResponse = parseInt(responseFromServer);
        if (replyCode != rc) {
            throw new IOException("reply code is not matched.");
        }
        /* Fill in */
    }

    /* Destructor. Closes the connection if something bad happens. */
    protected void finalize() throws Throwable {
        if (isConnected) {
            close();
        }
        super.finalize();
    }
}
