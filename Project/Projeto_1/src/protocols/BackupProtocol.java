package protocols;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import Chunk.Chunk;
import Server.Peer;
import Utils.Utils;

public class BackupProtocol implements Runnable
{
	private File file;
	private int replication_degree;
	private int MAX_SIZE_CHUNK = 64000;
	
	public BackupProtocol (File file,int ReplicationDegree)
	{
		this.file=file;
		replication_degree= ReplicationDegree;
	}

	@Override
	public void run() 
	{
		try 
		{
			String fileId = Utils.makeFileId(file);
			byte[] data = Files.readAllBytes(file.toPath());
			int numChunks = data.length/MAX_SIZE_CHUNK+1; // arredondar sempre para cima
			System.out.println("numChunks: " + numChunks);
			ByteArrayInputStream stream = new ByteArrayInputStream(data);
			for (int i=0; i<numChunks; i++)
			{
				byte[] chunkData;
				
				byte[] stream64 = new byte[MAX_SIZE_CHUNK];
				//se for multiplo e o ultimo chunk
				if(numChunks==i+1 && data.length%MAX_SIZE_CHUNK==0)
				{
					chunkData = new byte[0];
				}
				else //se for outro qualquer
				{
					int read = stream.read(stream64, 0, stream64.length);
					chunkData = Arrays.copyOfRange(stream64, 0, read);
				}
				Chunk fileChunk = new Chunk(fileId,i,replication_degree,chunkData);
				Thread chunkThread = new Thread(new BackupSubProtocol(fileChunk));
				chunkThread.start();
				
				try 
				{
					chunkThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
			}
		
			Peer.getInfo().saveStoredFile(fileId, file.getAbsolutePath());
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		
	}
	
	
	
}
