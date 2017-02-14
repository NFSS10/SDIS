import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server {

	public static void main(String[] args) throws IOException {
		DatagramSocket serverSocket = new DatagramSocket(4445);
        byte[] receiveData = new byte[1024];
        byte[] sendData = new byte[1024];
        System.out.println("running");
        while(true)
           {
              DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
              serverSocket.receive(receivePacket);
              
              String sentence = new String( receivePacket.getData());
              System.out.println("RECEIVED: " + sentence);
              
              InetAddress IPAddress = receivePacket.getAddress();
              int port = receivePacket.getPort();
              
              String capitalizedSentence = sentence.toUpperCase();
              sendData = capitalizedSentence.getBytes();
              DatagramPacket sendPacket =
              new DatagramPacket(sendData, sendData.length, IPAddress, port);
              serverSocket.send(sendPacket);
           }
  
	}
}
