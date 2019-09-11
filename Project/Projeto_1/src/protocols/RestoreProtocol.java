package protocols;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

import Chunk.Chunk;
import Server.Peer;
import Utils.FileHandler;
import Utils.Utils;

public class RestoreProtocol implements Runnable {

	private File file;
	private int MAX_SIZE_CHUNK=64000;
	private int WAITING_TIME = 500;
	private int counts = 0;
	public RestoreProtocol(File file)
	{
		this.file= file;
		
	}
	
	@Override
	public void run() 
	{
		try 
		{
			
			String fileID = Utils.makeFileId(file);
			byte[] data;
			data = Files.readAllBytes(file.toPath());
			int numChunks = data.length/MAX_SIZE_CHUNK+1;
			for (int i=0; i<numChunks; i++)
			{
				boolean stop = false;
				byte[] restoreMessage = makeRestoreMessage(i,fileID);
				DatagramPacket packet = new DatagramPacket(restoreMessage,restoreMessage.length,Peer.getMCChannel().address,Peer.getMCChannel().port);
				Peer.getMCChannel().socket.send(packet);
				
				while(!stop)
				{
					Thread.sleep(500); //esperar que respondam todos os peers
					if(Peer.getMDRChannel().getChunk()!=null && Peer.getMDRChannel().getChunk().getChunkNum()==i)
					{
						counts=0;
						stop=true;
						System.out.println("writing chunk nr: " + Peer.getMDRChannel().getChunk().getChunkNum() );
						writeToFile(Peer.getMDRChannel().getChunk());
						Peer.getMDRChannel().setChunk(null);
					}
					else 
					{
						if(counts>=5)
						{
							stop=true;
							System.out.println("timeout");
						}
						else
						{
							Thread.sleep(WAITING_TIME);
							WAITING_TIME = WAITING_TIME*2;
							counts++;
						}
					}
				}
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	public byte[] makeRestoreMessage(int chunkNr, String fileId)
	{
		String messageHeader ="GETCHUNK"+" "+"1.0"+" "+Peer.getId()+" "+fileId+" "+
						chunkNr+" "+"/r/n/r/n";
		
		byte[] message= messageHeader.getBytes();
						
		return message;
		
	}
	
	public void writeToFile(Chunk chunk)
	{
		File newFile = createFile();
		
		Path path = Paths.get(newFile.getPath());
		
		try 
		{
			Files.write(path, chunk.getData(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public File createFile()
	{
		FileHandler filehandler = new FileHandler();
		filehandler.createSingleDirectory("../restore");
		File newfile = new File("../restore/"+file.getName());
		try {
			newfile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newfile;
	}
	
}
