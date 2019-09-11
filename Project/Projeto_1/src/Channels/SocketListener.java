package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Server.Peer;
import Utils.Header;

public abstract class SocketListener implements Runnable 
{

	public static final int PACKET_TAM_MAX = 65000; //64 KB do body + 1 KB para o header

	public MulticastSocket socket;
	public InetAddress address;
	public int port;


	public SocketListener(InetAddress address, int port) 
	{
		this.address = address;
		this.port = port;
	}

	public void run() 
	{
		openSocket();

		byte[] buffer = new byte[PACKET_TAM_MAX];
		boolean terminado = false;
		
		while (!terminado) 
		{
			try {
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				
				String message = new String(packet.getData(), 0, packet.getLength());
				Header header = new Header(message);
				
				
				if (!header.getServerId().equals(Peer.getId())) 
					handler(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		closeSocket();
	}

	private void openSocket() 
	{
		try 
		{
			socket = new MulticastSocket(port);
			socket.setTimeToLive(1);
			socket.joinGroup(address);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeSocket() 
	{
		if (socket != null)
			socket.close();
	}
	
	protected abstract void handler(DatagramPacket packet) throws IOException;
	

}