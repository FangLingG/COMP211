/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailclient;

/**
 * ***********************************
* Filename: EmailMessage.java 
 * Names: Ling Fang
 * Student-IDs: 201218935
 * Date:2016.10.15
 * * Names: Hao Bai
 * Student-IDs: 201218765
 * Date:2016.10.15
 * ***********************************
 */
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Class for downloading one object from http server.
 *
 */
public class HttpInteract {

    private String host;
    private String path;
    private String requestMessage;

    private static final int HTTP_PORT = 80;
    private static final String CRLF = "\r\n";
    private static final int BUF_SIZE = 4096;
    private static final int MAX_OBJECT_SIZE = 102400;

    /* Create a HttpInteract object. */
    public HttpInteract(String url) {

        /* Split the "URL" into "host name" and "path name", and
		 * set host and path class variables.
		 * if URL is only a host name, use "/" as path
         */
        // String link = url.split("//")[1];
        String m[] = url.split("/", 2);
        host = m[0];
        if (m.length >= 2) {
            path = "/" + m[1];
        } else {
            path = "/";
        }


        /* Fill in */
 /* Construct requestMessage, add a header line so that
		 * server closes connection after one response. */
        requestMessage = "GET " + path + " HTTP/1.1" + CRLF + "Host: " + host + CRLF;
        /* Fill in */

        return;
    }

    /* Send Http request, parse response and return requested object
	 * as a String (if no errors),
	 * otherwise return meaningful error message.
	 * Don't catch Exceptions. EmailClient will handle them. */
    public String send() throws IOException {

        /* buffer to read object in 4kB chunks */
        char[] buf = new char[BUF_SIZE];

        /* Maximum size of object is 100kB, which should be enough for most objects.
		 * Change constant if you need more. */
        char[] body = new char[MAX_OBJECT_SIZE];

        String statusLine = "";	// status line
        int status;		// status code
        String headers = "";	// headers
        int bodyLength = -1;	// lenghth of body

        String[] tmp;

        /* The socket to the server */
        Socket connection;

        /* Streams for reading from and writing to socket */
        BufferedReader fromServer;
        DataOutputStream toServer;

        System.out.println("Connecting server: " + host + CRLF);

        /* Connect to http server on port 80.
		 * Assign input and output streams to connection. */
        connection = new Socket(host, HTTP_PORT);;
        fromServer = /* Fill in */ new BufferedReader(new InputStreamReader(connection.getInputStream()));;
        toServer = /* Fill in */ new DataOutputStream(connection.getOutputStream());;

        System.out.println("Send request:\n" + requestMessage);


        /* Send requestMessage to http server */
 /* Fill in */
        toServer.writeBytes(requestMessage + CRLF);
        /* Read the status line from response message */
        statusLine = fromServer.readLine();
        /* Fill in */;
        System.out.println("Status Line:\n" + statusLine + CRLF);

        /* Extract status code from status line. If status code is not 200,
		 * close connection and return an error message.
		 * Do NOT throw an exception */
        status = Integer.parseInt(statusLine.substring(9, 12));

        try {
            if (status != 200) {
                connection.close();
                System.out.println("status code is not 200");
            }
        } catch (UnknownHostException e) {
            throw e;
        }

        /* Fill in */
 /* Read header lines from response message, convert to a string,
 		 * and assign to "headers" variable.
		 * Recall that an empty line indicates end of headers.
		 * Extract length  from "Content-Length:" (or "Content-length:")
		 * header line, if present, and assign to "bodyLength" variable.
         */
        String line;

        while (!(line = fromServer.readLine()).isEmpty()) {
            headers += line + CRLF;
            if (line.startsWith("Content-Length:")
                    || line.startsWith("Content-length:")) {
                tmp = line.split(" ");
                int length = Integer.parseInt(tmp[1]);
                bodyLength = length;
            }
            //in case of 301, if response contains new location, return it to email client
            /*if (line.startsWith("Location:")) {
                tmp = line.split(" ");
                return tmp[1];
            }*/

        }
        /* Fill in */ 		// requires about 10 lines of code
        System.out.println(
                "Headers:\n" + headers + CRLF);


        /* If object is larger than MAX_OBJECT_SIZE, close the connection and
		 * return meaningful message. */
        if (bodyLength > MAX_OBJECT_SIZE) {
            /* Fill in */
            connection.close();
            return (/* Fill in */headers + bodyLength);
        }

        /* Read the body in chunks of BUF_SIZE using buf[] and copy the chunk
		 * into body[]. Stop when either we have
		 * read Content-Length bytes?? or when the connection is
		 * closed (when there is no Content-Length in the response).
		 * Use one of the read() methods of BufferedReader here, NOT readLine().
		 * Also make sure not to read more than MAX_OBJECT_SIZE characters.
         */
        
        int bytesRead = 0;
        while (bodyLength != -1 && bytesRead < bodyLength && bytesRead != -1) {
            bytesRead = fromServer.read(buf);                      
            for (int i = 0; i < bytesRead && (i + bytesRead) < MAX_OBJECT_SIZE; i++) {
                body[i + bytesRead] = buf[i];
            }
            bytesRead += bytesRead;           
        }


        /* Fill in */   // Requires 10-20 lines of code
        /* At this points body[] should hold to body of the downloaded object and
	* bytesRead should hold the number of bytes read from the BufferedReader
         */
        /* Close connection and return object as String. */
        System.out.println(
                "Done reading file. Closing connection.");
        connection.close();

        return (new String(body,
                0, bytesRead));
    }

}
