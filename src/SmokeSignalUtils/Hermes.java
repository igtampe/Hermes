package SmokeSignalUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Hermes is a tiny class that can be used to communicate with SmokeSignal servers from a Java Application
 * If you plan on using this, consider not accessing it directly, instead constructing helper classes
 * that can call it instead!
 * @author igtampe
 */
public class Hermes {

	final static String DefaultIP="localhost";
	final static int DefaultPort=797;

	/**
	 * Sends a command to a SmokeSignal server
	 * @param ClientMSG Message to send to the server
	 * @param DebugMode DebugMode, that will spit out the progress of the BinaryReader while it reads
	 * @param ServerIP IP of the server you want to connect to
	 * @param Port Port of the server you want to connect to
	 * @return A return message from the server
	 */
	public static String ServerCommand(String ClientMSG, boolean DebugMode, String ServerIP,int Port) {
		//This is all encapsulated in a try, just in case.
		try {
			//This sets up a socket, and connects to the server.
			Socket theSocket=new Socket(ServerIP, Port);

			//The Readers and Writers
			DataOutputStream MercuryOne = new DataOutputStream(theSocket.getOutputStream());
			DataInputStream MercuryTwo = new DataInputStream(theSocket.getInputStream());

			//Formatting for the SentMSG
			String SentMSG;
			
			//For some reason, the Java ByteWriter doesn't send the first character.
			SentMSG=" " + ClientMSG;
			
			//Just to make sure we capture SentMSG's original length
			int S=SentMSG.length();
			
			//For some reason, the Java ByteWriter also only sends if there's 72 characters, so this takes care of that.
			for (int i = S; i < 73; i++) {SentMSG= SentMSG + " ";}

			//We send it
			MercuryOne.writeBytes(SentMSG);
			
			//Initialize some variables
			byte ServerMSG = 0;
			String ReturnMSG="";
			
			//This takes care of the first junk byte
			MercuryTwo.readByte();
			
			//This waits to make sure we have received algo.
			while ((MercuryTwo.available()==0)) {}
			
			//This reads until there's nothing to read. The ByteReader reads one byte at a time, and it's translated on the fly.
			while (!(MercuryTwo.available()==0)) {
				ServerMSG=MercuryTwo.readByte();	
				ReturnMSG=ReturnMSG + new String(new byte[] {ServerMSG});
				if (DebugMode) {System.out.println(MercuryTwo.available() + " " + ReturnMSG);}
			}
			
			//Close and return
			theSocket.close();
			return ReturnMSG;
		}
		catch (UnknownHostException e) {System.out.println("haha silly I couldn't find that host");}
		catch (IOException e) {e.printStackTrace();}
		//If anything happens for whatever reason, return E
		return "E";}

	/**
	 * Sends a command to a SmokeSignal server. Will send to the default IP and Port.
	 * @param ClientMSG Message to send to the server
	 * @return A return message from the server
	 */
	public static String ServerCommand(String ClientMSG) {return ServerCommand(ClientMSG,false,DefaultIP,DefaultPort);}
	public static String ServerCommand(String ClientMSG,boolean DebugMode) {return ServerCommand(ClientMSG,DebugMode,DefaultIP,DefaultPort);}
	
	/**
	 * Used to send commands to a SmokeSignal V7 Server that supports Authentication.
	 * We send it by default as plaintext, but your SmokeSignal Application can use any encryption you want to send it, and decode it on the other end.
	 * 
	 * @param Username Username of the client
	 * @param Password Password of the client.
	 * @param ClientMSG Message you want to send to the server
	 * @return A return from the server.
	 */
	public static String AuthenticatedServerCommand(String Username, String Password, String ClientMSG) {return ServerCommand(Username+"|"+Password+"|"+ClientMSG,false,DefaultIP,DefaultPort);}
	
}
