import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

	public static void main(String[] args) throws IOException {
        /*if (args.length != 4) {
            System.out.println("Usage: java Client <host_name> <port_number> <oper> <opnd>*"
            					+ "\nwhere"
            					+ "\n<host_name> is the name of the host running the server;"
            					+ "\n<port_number> is the server port;"
            					+ "\n<oper> is either ‘‘register’’or ‘‘lookup’’"
            					+ "\n<opnd>* is the list of arguments"
            					+ "\n  <plate number> <owner name>, for register;"
            					+ "\n  <plate number>, for lookup.\n");
            return;
       }*/
		System.out.println("Hax1337\n");
        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[256];
        String testStr = "coisoetal";
        buf = testStr.getBytes();
        
        
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
        		
        socket.send(packet);
    
        // get response
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);

	    // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("Quote of the Moment: " + received);
    
        socket.close();

	}

}
