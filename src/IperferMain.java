///////////////////////////////////////////////////////////////////////////////
// Title:            Iperfer
// Authors:          Joseph Bauer, Eric Johnson
///////////////////////////////////////////////////////////////////////////////
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.ServerSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	final static char[] outBuffer = new char[1000];


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
		if(serverPort<LOW_THRESHOLD_PORT || serverPort>HIGH_THRESHOLD_PORT){
			System.out.println("Error: port number must be in the range 1024 "
					+ "to 65535");
			System.exit(-1);
		}

		long startTime;

		try{
			Socket mySocket = new Socket(hostname, serverPort);
			PrintWriter out = new PrintWriter(mySocket.getOutputStream(), true);

			startTime=System.nanoTime();
			while((System.nanoTime()-startTime) < time*SECONDS_2_NANO){
				out.write(outBuffer);
			}  

			mySocket.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}


		System.out.println("sent=6543 KB rate=5.234 Mbps");
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
		long startTime=0;
		long endTime =0;
		double rate;

		try{
			//Create Server socket listen for client
			ServerSocket serverSocket = new ServerSocket(listenPort);
			Socket clientSocket = serverSocket.accept();
			
			//Start time used to calculate rate of transmission
			startTime= System.nanoTime();
			
			//Open reader on input stream
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()), MSG_SIZE);

			//count bytes transmitted
			while( -1 != (byteCount=in.read(outBuffer, 0, MSG_SIZE-1))){
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

		//Calculate rate
		rate = ((double) byteReceivedCount/(((double) endTime - 
				(double) startTime) / (double) SECONDS_2_NANO)) / 
				(double) Kb_2_Mb;

		//Server output bytes received and Rate
		System.out.println("sent=" + byteReceivedCount + " KB" + "rate=" + 
				rate + " Mbps");		
	}
}


