package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import Server.Peer;
import Service.MessageHandler;

public class BackupChannel extends SocketListener
{
	public BackupChannel(InetAddress address, int port)
	{
		super(address, port);
	}

	@Override
	public void handler(DatagramPacket packet) 
	{
		new Thread(new MessageHandler(packet)).start();
	}
	
}