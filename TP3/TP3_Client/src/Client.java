import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

		public static void main(String[] args) throws IOException {
	        
	        if (args.length != 4) {
	            System.err.println(
	            		"Usage: java Client <host_name> <port_number> <oper> <opnd>*"
            					+ "\nwhere"
            					+ "\n<host_name> is the name of the host running the server;"
            					+ "\n<port_number> is the server port;"
            					+ "\n<oper> is either ‘‘register’’or ‘‘lookup’’"
            					+ "\n<opnd>* is the list of arguments"
            					+ "\n  <plate number> <owner name>, for register;"
            					+ "\n  <plate number>, for lookup.\n");
	            System.exit(1);
	        }

	        String hostName = args[0];
	        int portNumber = Integer.parseInt(args[1]);

	        try (
	            Socket echoSocket = new Socket(hostName, portNumber);
	            PrintWriter out =
	                new PrintWriter(echoSocket.getOutputStream(), true);
	            BufferedReader in =
	                new BufferedReader(
	                    new InputStreamReader(echoSocket.getInputStream()));
	            BufferedReader stdIn =
	                new BufferedReader(
	                    new InputStreamReader(System.in))
	        ) {
	            String userInput;
	            while ((userInput = stdIn.readLine()) != null) {
	                out.println(userInput);
	                System.out.println("echo: " + in.readLine());
	            }
	        } catch (UnknownHostException e) {
	            System.err.println("Don't know about host " + hostName);
	            System.exit(1);
	        } catch (IOException e) {
	            System.err.println("Couldn't get I/O for the connection to " +
	                hostName);
	            System.exit(1);
	        } 
	    }


}


