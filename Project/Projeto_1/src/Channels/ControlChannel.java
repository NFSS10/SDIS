package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import Server.Peer;
import Service.MessageHandler;

public class ControlChannel extends SocketListener //chunk de um lado peer do outro
{
	
	public ControlChannel(InetAddress address,int port) 
	{
		super(address,port);
		
	}


	@Override
	protected void handler(DatagramPacket packet) throws IOException 
	{
		new Thread(new MessageHandler(packet)).start();	
	}
	
	

}

