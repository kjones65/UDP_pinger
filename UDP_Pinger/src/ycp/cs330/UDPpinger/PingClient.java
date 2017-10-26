package ycp.cs330.UDPpinger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Date;

public class PingClient {
	
	public static void main(String[] args) throws Exception{
		
		// check if arguments are valid
		if (args.length != 2) {
	         System.out.println("Invalid arguments, must be server name and port");
	         return;
	      }
		
		// get server to ping and port number, and initialize sequence number
		int port = Integer.parseInt(args[1]);
		System.out.println(port);
		InetAddress server = InetAddress.getByName(args[0]);
		int seq = 0;
		
		// create datagram socket to send/receive UDP packets
		// and set timeout to 1 second
		DatagramSocket socket = new DatagramSocket();
		
		
		while(seq < 15) {
			// get time to put in to udp packet
			Date date = new Date();
			long sentTime = date.getTime();
			
			// create contents of packet, and put into a byte array
			String contents = "PING " + seq + " " + sentTime + " \n";
			byte[] buffer = new byte[1024];
			buffer = contents.getBytes();
			
			// create packet to send 
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server, port);
			// send the packet to the server
			socket.send(packet);
			
			
			// attempt to receive packet from server
			try {
				
				socket.setSoTimeout(1000);
				// create packet to receive message from server
				DatagramPacket serverPacket = new DatagramPacket(new byte[1024], 1024);
				//attempt to receive message from server
				socket.receive(serverPacket);
				// get time of packet arrival
				date = new Date();
				long receiveTime = date.getTime();
				
				// print the data
				printData(serverPacket, sentTime, receiveTime);
				
			} catch (SocketTimeoutException e) {
				// Packet timed out, print out error message
				System.out.println("Packet " + seq + " timed out.");
			}
			// get sequence number of next packet
			seq++;
			// wait 2 seconds before sending another message
			Thread.sleep(2000);	
		}
	}
	
	// Print data, most of this method comes from the PingServer class
	private static void printData(DatagramPacket serverPacket, long sentTime, long receiveTime) throws Exception {
		// Obtain references to the packet's array of bytes.
	      byte[] buf = serverPacket.getData();

	      // Wrap the bytes in a byte array input stream,
	      // so that you can read the data as a stream of bytes.
	      ByteArrayInputStream bais = new ByteArrayInputStream(buf);

	      // Wrap the byte array output stream in an input stream reader,
	      // so you can read the data as a stream of characters.
	      InputStreamReader isr = new InputStreamReader(bais);

	      // Wrap the input stream reader in a buffered reader,
	      // so you can read the character data a line at a time.
	      // (A line is a sequence of chars terminated by any combination of \r and \n.) 
	      BufferedReader br = new BufferedReader(isr);

	      // The message data is contained in a single line, so read this line.
	      String line = br.readLine();

	      // Print host address and data received from it.
	      System.out.println(
	         "Received from " + 
	         serverPacket.getAddress().getHostAddress() + 
	         ": " +
	         new String(line) + " Delay - " + (receiveTime-sentTime));
	}
}
