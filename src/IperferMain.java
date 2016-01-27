///////////////////////////////////////////////////////////////////////////////
// Title:            Iperfer
// Authors:          Joseph Bauer, Eric Johnson
///////////////////////////////////////////////////////////////////////////////

public class IperferMain {
	
// Constants	
final static String CLIENT_MODE = "-c";
final static String SERVER_MODE = "-s";
final static int SECONDS_2_NANO = 1000000000;
final static int LOW_THRESHOLD_PORT = 1024;
final static int HIGH_THRESHOLD_PORT = 65535;


    /**
 	 * Main method: Iperfer tool sends packets using TCP to measure network 
 	 * bandwidth
 	 */
	public static void main(String[] args) {
		if(args.length>0 && args[0].equals(CLIENT_MODE) && args.length==7){
			client(args[2],Integer.valueOf(args[4]), Double.valueOf(args[6]));
		}
		else if(args.length>0 && args[0].equals(SERVER_MODE) && args.length == 4){
			server(Integer.valueOf(args[2]));
		}
		else{
			System.out.println("Error: missing or additional arguments");
			System.exit(-1);
		}
	}
	
	
	/**
     * Send data as quickly as possible to the server keeping track of packets sent
     *
     * @param  String hostname
     * @param  int serverPort
     * @param  double time (in seconds)
     * @return void
     */
	private static void client(String hostname, int serverPort, double time){
		long startTime;
		
		if(serverPort<LOW_THRESHOLD_PORT || serverPort>HIGH_THRESHOLD_PORT){
			System.out.println("Error: port number must be in the range 1024 to 65535");
			System.exit(-1);
		}
		
		startTime=System.nanoTime();
		
		while((System.nanoTime()-startTime) < time*SECONDS_2_NANO){
			//send packets
		}
		
		//close connection
		
		System.out.println("sent=6543 KB rate=5.234 Mbps");
		
	}
	
	
	/**
     * Receive data from client as quickly as possible keeping track of packets received
     *
     * @param  int listenPort
     * @return void
     */
	private static void server(int listenPort){
		
		if(listenPort<LOW_THRESHOLD_PORT || listenPort>HIGH_THRESHOLD_PORT){
			System.out.println("Error: port number must be in the range 1024 to 65535");
			System.exit(-1);
		}
	}
}


