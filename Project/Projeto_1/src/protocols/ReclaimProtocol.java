package protocols;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Random;

import Server.Peer;
import Utils.Header;

public class ReclaimProtocol implements Runnable{

	private int pretendedSpace;
	
	public ReclaimProtocol(int pretendedSpace)
	{
		this.pretendedSpace=pretendedSpace*1024; //KBs para bytes
	}
	
	@Override
	public void run() 
	{
		if(pretendedSpace > Peer.getDisk().capacity)
		{
			System.out.println("The disk does not have that much space!");
		}
		else
		{	
			int freedSpace=0;
			if(Peer.getDatabase().getFicheiroGuardados().size()>0)
			{
				System.out.println("Reclaming, please wait...");
				while(freedSpace<pretendedSpace)
				{
					Random random = new Random();
					ArrayList<String> keys  = new ArrayList<String>(Peer.getDatabase().getChunks_hashmap().keySet());
					String randomKey = keys.get( random.nextInt(keys.size()));
					String fileId= randomKey.split("-")[0];
					String chunkNr= randomKey.split("-")[1];
					File fileToDelete = new File("../Storage/Peer_"+Peer.getId()+"/Chunks/"+fileId+"/"+chunkNr);
					if(justOnes(keys))
					{
						System.out.println("BREAK!");
						break;
					}
					if(fileToDelete.exists())
					{
						System.out.println("EXISTE!");
						if(Peer.getDatabase().getChunkInfo(randomKey).getPeerMirrors().size()==1)
						{
							System.out.println("Só existe uma copia deste ficheiro");
						}
						else
						{
							freedSpace+=fileToDelete.length();
							fileToDelete.delete();
							Peer.getDatabase().removeChunkFromDB(randomKey);
							Peer.getDisk().used = (int) (Peer.getDisk().used - fileToDelete.length());
							Peer.saveDiskToFile();
							sendRemovedMessage(fileId,chunkNr);
						}
					}
					
				}
				System.out.println("Reclaming,completed...");
			}
		}
		
	}
	
	public boolean justOnes(ArrayList<String> keys)
	{
		for(int i=0; i<Peer.getDatabase().getChunks_hashmap().size();i++)
		{
			if(Peer.getDatabase().getChunkInfo(keys.get(i)).getPeerMirrors().size()>1)
			{
				return false;
			}
		}
		return true;
	}
	
	private void sendRemovedMessage(String fileId, String chunkNr) 
	{
	
		String messageString ="REMOVED"+" "+"1.0"+" "+Peer.getId()+" "+fileId+" "+
				chunkNr+" "+"/r/n/r/n";
		byte[] message = messageString.getBytes();
		DatagramPacket packet = new DatagramPacket(message,message.length,Peer.getMCChannel().address,Peer.getMCChannel().port);
		
		try 
		{
			Peer.getMCChannel().socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
