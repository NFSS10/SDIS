package protocols;

import java.io.IOException;
import java.net.DatagramPacket;

import Chunk.Chunk;
import Chunk.ChunkInfo;
import Server.Peer;
import Utils.Utils;

public class BackupSubProtocol implements Runnable
{

	private Chunk chunk;
	int waitingTime = 1000;
	public BackupSubProtocol(Chunk chunk)
	{
		this.chunk=chunk;
	}

	@Override
	public void run() 
	{
		
		boolean stop=false;
		int attempts=0;
		Peer.addStoredChunkId(chunk.getChunkID());
		while(!stop)
		{
			Peer.clearStoredConfirms(chunk.getChunkID());
			byte[] backupMessage = makeBackupMessage();
			
			DatagramPacket packet = new DatagramPacket(backupMessage,backupMessage.length,Peer.getMDBChannel().address,Peer.getMDBChannel().port);
			try 
			{
				Peer.getMDBChannel().socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			try 
			{
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
			int actualReplication = Peer.getStoredConfirms(chunk.getChunkID());
			
			if(actualReplication < chunk.getReplicationDegree())
			{
				if(attempts > 5)
				{
					System.out.println("Maximum attempts reached");
					stop = true;
				}
				else
				{
					waitingTime= waitingTime*2;
					attempts++;
				}
				
			}
			else if(actualReplication >= chunk.getReplicationDegree())
			{
				System.out.println("Next Chunk");
				
				int rpd = chunk.getReplicationDegree();
				ChunkInfo chunkInfo = new ChunkInfo(rpd);
				for(int i=0; i < actualReplication;i++)
				{
					chunkInfo.addPeerMirror(Peer.getStoredPeers(chunk.getChunkID()).get(i));
				}
				Peer.getInfo().saveChunk(chunk.getChunkID(),chunkInfo);
				
				stop = true;
			}
		}
		
		Peer.clearStored(chunk.getChunkID());
	}

	public byte[] makeBackupMessage()
	{
		String messageHeader ="PUTCHUNK"+" "+"1.0"+" "+Peer.getId()+" "+chunk.getChunkFileID()+" "+
						chunk.getChunkNum()+" "+chunk.getReplicationDegree()+" "+ "/r/n/r/n";
		
		byte[] messageBody = chunk.getData();
		byte[] message = Utils.juntarBytes(messageHeader.getBytes(),messageBody);
		
						
		return message;
		
	}

}
