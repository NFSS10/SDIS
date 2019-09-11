package Channels;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

import Chunk.Chunk;
import Service.MessageHandler;

public class RestoreChannel  extends SocketListener 
{

	private Chunk chunk;
	public RestoreChannel(InetAddress address,int port)
	{
		super(address,port);
		this.chunk = null;
	}

	@Override
	protected void handler(DatagramPacket packet) throws IOException 
	{
		new Thread(new MessageHandler(packet)).start();	
	}
	
	public Chunk getChunk()
	{
		return chunk;
	}
	
	public void setChunk(Chunk chunk)
	{
		this.chunk = chunk;
	}
	
	
}
