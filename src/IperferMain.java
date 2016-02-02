///////////////////////////////////////////////////////////////////////////////
// Title:            Iperfer
// Authors:          Joseph Bauer, Eric Johnson
///////////////////////////////////////////////////////////////////////////////
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class IperferMain {

	// Constants	
	final static String CLIENT_MODE = "-c";
	final static String SERVER_MODE = "-s";
	final static int SECONDS_2_NANO = 1000000000;
	final static int Kb_2_Mb = 1000;
	final static int MSG_SIZE = 1000;
	final static int LOW_THRESHOLD_PORT = 1024;
	final static int HIGH_THRESHOLD_PORT = 65535;
	final static byte[] outBuffer = new byte[1000];


	/**
	 * Main method: Iperfer tool sends packets using TCP to measure network 
	 * bandwidth
	 */
	public static void main(String[] args) {
		if(args.length>0 && args[0].equals(CLIENT_MODE) && args.length==7){
			client(args[2],Integer.parseInt(args[4]), 
					Double.parseDouble(args[6]));
		}
		else if(args.length>0 && args[0].equals(SERVER_MODE) && 
				args.length == 3){
			server(Integer.valueOf(args[2]));
		}
		else{
			System.out.println("Error: missing or additional arguments");
			System.exit(-1);
		}
	}


	/**
	 * Send data as quickly as possible to the server keeping track of packets
	 * sent
	 *
	 * @param  String hostname
	 * @param  int serverPort
	 * @param  double time (in seconds)
	 * @return void
	 */
	private static void client(String hostname, int serverPort, double time){
		//Check if port is within acceptable range
		if(serverPort<LOW_THRESHOLD_PORT || serverPort>HIGH_THRESHOLD_PORT){
			System.out.println("Error: port number must be in the range 1024 "
					+ "to 65535");
			System.exit(-1);
		}
		//Local Variables
		long startTime = 0;
		long endTime = 0;
		int bytesSentCount=0;
		int KBsent=0;
		double timeinSecs=0;
		double rate;
		Socket mySocket;
		OutputStream out;

		try{
			mySocket = new Socket(hostname, serverPort);
			out = mySocket.getOutputStream();	
			startTime=System.nanoTime();
			while((System.nanoTime()-startTime) < time*SECONDS_2_NANO){
				out.write(outBuffer, 0, MSG_SIZE);
				bytesSentCount+=MSG_SIZE;
			}
			out.flush();
			endTime = System.nanoTime();
			mySocket.close();
		}
		
		catch(IOException e){
			e.printStackTrace();
		}
		KBsent = bytesSentCount/1000;
		timeinSecs=((double) endTime - (double) startTime)/SECONDS_2_NANO;
		
		//rate = ((double) bytesSentCount/(((double) endTime - 
		//		(double) startTime) / (double) SECONDS_2_NANO)) / 
		//		(double) Kb_2_Mb;
		
		//gets the amount of MegaBytes sent, multiplies by 8 to converts to Megabits, then divides by total time
		rate = ((KBsent/Kb_2_Mb)*8)/timeinSecs;
		
		//Calculate rate
		//rate = ((double) bytesSentCount / time) / (double) Kb_2_Mb;
		//Client side output
		System.out.println("sent=" +KBsent+ " KB " + "rate="+rate+" Mbps");
	}
	/**
	 * Receive data from client as quickly as possible keeping track of packets 
	 * received
	 *
	 * @param  int listenPort
	 * @return void
	 */
	private static void server(int listenPort){
		//Check if port is within acceptable range
		if(listenPort<LOW_THRESHOLD_PORT || listenPort>HIGH_THRESHOLD_PORT){
			System.out.println("Error: port number must be in the range 1024 to"
					+ " 65535");
			System.exit(-1);
		}
		
		// Local Variables 
		int byteReceivedCount=0;
		int byteCount=0;
		int KBreceived=0;
		double timeinSecs=0;
		long startTime=0;
		long endTime =0;
		double rate;
		ServerSocket serverSocket;
		Socket clientSocket;
		InputStream in;

		try{
			//Create Server socket listen for client
			serverSocket = new ServerSocket(listenPort);
			clientSocket = serverSocket.accept();
			in = clientSocket.getInputStream();
			//Start time used to calculate rate of transmission
			startTime= System.nanoTime();
			

			//count bytes transmitted
			while( -1 != (byteCount=in.read(outBuffer, 0, MSG_SIZE))){
				byteReceivedCount+=byteCount;
			}
			
			//Record end time and close socket
			endTime=System.nanoTime();
			serverSocket.close();
		}
		catch(SocketTimeoutException s){
			System.out.println("Error: Socket timed out");
			System.exit(-1);
		}
		catch(IOException e){
			System.out.println("Error: Server IO exception");
			e.printStackTrace();
		}
		KBreceived=byteReceivedCount/1000;
		timeinSecs=((double) endTime - (double) startTime)/SECONDS_2_NANO;
		
		//Calculate rate
		//rate = ((double) byteReceivedCount/(((double) endTime - 
		//		(double) startTime) / (double) SECONDS_2_NANO)) / 
		//		(double) Kb_2_Mb;
		
		//gets the amount of MegaBytes sent, multiplies by 8 to converts to Megabits, then divides by total time
		rate = ((KBreceived/Kb_2_Mb)*8)/timeinSecs;

		//Server output bytes received and Rate
		System.out.println("sent=" + byteReceivedCount + " KB " +"rate=" + 
				rate + " Mbps");		
	}
}


