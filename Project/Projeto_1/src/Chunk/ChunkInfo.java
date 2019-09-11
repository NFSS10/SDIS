package Chunk;


import java.io.Serializable;
import java.util.ArrayList;
/*Contem informacao do chunk
 * Replication degree do chunk e os peers na qual o chunk esta guardado
 * */
public class ChunkInfo implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int replicationDeg;
	private String filePath;
	private ArrayList<String> peers; //peers onde o chunk esta guardado
	
	
	public  ChunkInfo(int replicationDeg)
	{
		this.replicationDeg = replicationDeg;
		this.peers = new ArrayList<String>();
		this.filePath = null;
	}
	
	
	public synchronized void setFilePath(String fileP)
	{
		filePath = fileP;
	}
	
	public synchronized String getFilePath()
	{
		return filePath;
	}

	public synchronized void addPeerMirror(String peerID)
	{
		if(!peers.contains(peerID))
		peers.add(peerID);
	}
	
	public synchronized void removeMirror(String peerID) 
	{
		peers.remove(peerID);
	}
	public synchronized ArrayList<String> getPeerMirrors() 
	{
		return peers;
	}
	

	
	
	public int getReplicationDegree() 
	{
		return replicationDeg;
	}
	public void setReplicationDegree(int repDeg)
	{
		replicationDeg = repDeg;
	}

	
	


}
