package Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.PublicKey;

import Chunk.Chunk;
import Chunk.ChunkInfo;
import Server.Peer;

public class FileHandler
{
	public FileHandler()
	{
		
	}
	
	
	public void saveChunk(Chunk chunk, Header msgHeader)
	{
		createStorageDirectories(msgHeader);	
		
		writeFile("../Storage/Peer_"+Peer.getId()+"/Chunks/"+msgHeader.getFileId()+"/"+chunk.getChunkNum(),
					chunk.getData());
	}
	

	public void createPeerDiskAndDatabaseFolder(String peerID)
	{
		createSingleDirectory("../Database");
		createSingleDirectory("../Database/Peer_"+peerID);

		createSingleDirectory("../Disk");
		createSingleDirectory("../Disk/Peer_"+peerID);
	}
	

	public void createStorageDirectories(Header msgHeader)
	{
		createSingleDirectory("../Storage");
		createSingleDirectory("../Storage/Peer_"+Peer.getId());
		createSingleDirectory("../Storage/Peer_"+Peer.getId()+"/Chunks");
		createSingleDirectory("../Storage/Peer_"+Peer.getId()+"/Chunks/"+msgHeader.getFileId());
	}

	public void createSingleDirectory(String path)
	{
		File file = new File(path);
		if (!file.exists())
			file.mkdir();
	}
	
	
	private void writeFile(String path, byte[] data)
	{
		File file = new File(path);
		try 
		{
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(data);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] loadChunkData(Header header)
	{
		try 
		{
		System.out.println("peer: "+Peer.getId() +" fileid: "+header.getFileId() + " chunkNR:" + header.getChunkNum());
		File file = new File("../Storage/Peer_"+Peer.getId()+"/Chunks/"+header.getFileId()+"/"+header.getChunkNum());
		byte[] data = Files.readAllBytes(file.toPath());
		return data;
		
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

}
