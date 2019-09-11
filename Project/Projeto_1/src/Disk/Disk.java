package Disk;
import java.io.Serializable;

import Server.Peer;

/**
 * Tem informação sobre o espaço do peer
 */
public class Disk implements Serializable
{

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_CAPACITY = 2048000; //bytes  32 Chunks no total
	public int capacity;
	public int used;
	
	public Disk()
	{
		capacity = DEFAULT_CAPACITY;
		used=0;
	}
	
	public synchronized int getFreeSpace()
	{
		return capacity-used;
		
	}
	//depois deste metodo escrever para o ficheiro
	public synchronized void addCapacity(int bytes)
	{
		capacity += bytes;
		Peer.saveDiskToFile();
	}
	
	public synchronized void setCapacity(int bytes) 
	{
		capacity = bytes;
		Peer.saveDiskToFile();
	}
	
	
}
