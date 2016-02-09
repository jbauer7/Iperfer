///////////////////////////////////////////////////////////////////////////////
// Title:            Iperfer
// Authors:          Joseph Bauer, Eric Johnson
///////////////////////////////////////////////////////////////////////////////
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.ServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Iperfer {

	// Constants	
	final static String CLIENT_MODE = "-c";
	final static String SERVER_MODE = "-s";
	final static String PORT = "-p";
	final static String HOST = "-h";
	final static String TIME = "-t";
	final static int SECONDS_2_NANO = 1000000000;
	final static int BIT_2_BYTE = 8;
	final static int Kb_2_Mb = 1000;
	final static int BYTE_2_KB=1000;
	final static int MSG_SIZE = 1000;
	final static int LOW_THRESHOLD_PORT = 1024;
	final static int HIGH_THRESHOLD_PORT = 65535;
	final static byte[] outBuffer = new byte[1000];
	private static boolean isClient=false;
	private static boolean isServer=false;


	/**
	 * Main method: Iperfer tool sends packets using TCP to measure network 
	 * bandwidth
	 */
	public static void main(String[] args) {
		
		//creating local variables
		double time = 0;
		int port=0;
		String host=null;
		
		//entry validation
		for(int i=0; i<args.length; i++){
			if(args[i].equals(CLIENT_MODE)){
				isClient=true;
				if(args.length!=7){
					System.out.println("Incorrect number of arguments needed to operate as client");
					System.exit(-1);
				}
			}
			else if(args[i].equals(SERVER_MODE)){
				isServer=true;
				if(args.length!=3){
					System.out.println("Incorrect number of arguments needed to operate as server");
					System.exit(-1);
				}
			}
			else if(args[i].equals(PORT)){
				try{
					port = Integer.parseInt(args[i+1]);
				}
				catch(NumberFormatException e){
					System.out.println("Invalid port " + e);
					System.exit(-1);
				}
				i++;
			}
			else if(args[i].equals(HOST)){
				host=args[i+1];
				i++;
			}
			else if(args[i].equals(TIME)){
				try{
					time=Double.parseDouble(args[i+1]);
					i++;
					if(time<0){
						throw new NumberFormatException("number is negative");
					}
				}
				catch(NumberFormatException e){
						System.out.println("Invalid time entry " + e);
						System.exit(-1);
				}
			}
			else{
				System.out.println("Invalid flag argument");
				System.exit(-1);
			}
		}
		
		//running server or client methods
		if(isClient){
			client(host,port,time);
		}
		if(isServer){
			server(port);
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

		long startTime=0;
		long endTime=0;
		int bytesSentCount=0;
		double KBsent=0;
		double timeinSecs=0;
		double rate;
		Socket mySocket;
		OutputStream out;

		try{
			mySocket = new Socket(hostname, serverPort);

			out = mySocket.getOutputStream();


			startTime = System.nanoTime();
			while((System.nanoTime()-startTime) < time*SECONDS_2_NANO){
				out.write(outBuffer, 0, MSG_SIZE);
				bytesSentCount+=MSG_SIZE;
			}  
			endTime = System.nanoTime();
			mySocket.close();
		}
		
		catch(IOException e){
			System.out.println("Error: Server IO exception");
			System.exit(-1);
		}
		KBsent = (double) bytesSentCount / (double) BYTE_2_KB;
		timeinSecs=((double) endTime - (double) startTime) /
				(double)SECONDS_2_NANO;
		
		//gets the amount of MegaBytes sent, multiplies by 8 to converts to 
		//Megabits, then divides by total time
		rate = ((KBsent / (double) Kb_2_Mb) * (double) BIT_2_BYTE) / timeinSecs;

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
		double KBreceived=0;
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
			System.exit(-1);
		}
		
		KBreceived = (double) byteReceivedCount/ (double) BYTE_2_KB;
		timeinSecs=((double) endTime - (double) startTime) /
				(double)SECONDS_2_NANO;
		
		
		//gets the amount of MegaBytes sent, multiplies by 8 to converts to 
		//Megabits, then divides by total time
		rate = ((KBreceived / (double) Kb_2_Mb) * (double) BIT_2_BYTE ) / 
				timeinSecs;

		//Server output bytes received and Rate
		System.out.println("received=" + KBreceived + " KB " +"rate=" + 
				rate + " Mbps");		
	}
}


