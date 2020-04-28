package main;
import SmokeSignalUtils.Hermes;
public class TestMessage {
	
	//This command should trigger the Dummy Extension to return "Congrats! You've connected to the server!"
	public static void main(String[] args) {System.out.println(Hermes.ServerCommand("CONNECTED"));}
}
