package Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import Chunk.Chunk;
import Chunk.ChunkInfo;
import Server.Peer;

public class Database implements Serializable
{
	
	//chunk id (hash+chunkNum) e a info do chunk correspondente (replication degree e onde esta guardado)
	HashMap<String, ChunkInfo> chunks_hashmap;
	//Ficheiros guardados
	ArrayList<String> ficheiroGuardados;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public Database()
	{
		chunks_hashmap = new HashMap<String, ChunkInfo>();
		ficheiroGuardados = new ArrayList<String>();
		
	}
	
	
	//-------------- Chunks -------------------------------
	public  ChunkInfo getChunkInfo(String chunkID) 
	{
		return chunks_hashmap.get(chunkID);
	}

	public  void saveChunk(String chunkID, ChunkInfo  chunkInfo)
	{
		chunks_hashmap.put(chunkID, chunkInfo);
		Peer.saveDatabaseToFile();
	}

	public  boolean isChunkNaDatabase(String chunkID)
	{
		return chunks_hashmap.containsKey(chunkID);
	}

	public  void removeFile(String chunkID)
	{
		if(chunks_hashmap.containsKey(chunkID))
		{
			chunks_hashmap.remove(chunkID);
		}
		
	}
	
	//-------------- Ficheiros guardados ------------------
	//Diz se o ficheiro esta guardado
	public  boolean isFileStored(String fileID) 
	{
		return ficheiroGuardados.contains(fileID);
	}
	//Guarda o ficheiro no arraylist
	public  void saveStoredFile(String fileID) 
	{
		if(!ficheiroGuardados.contains(fileID))
		{
			ficheiroGuardados.add(fileID);
			Peer.saveDatabaseToFile();
		}
	}

	public  void removeFileFromDatabase(String fileID)
	{
		int number =0;
		for(int i=0; i<chunks_hashmap.size();i++ )
		{
			String chunkId = fileID+"-"+number;
			if(chunks_hashmap.containsKey(chunkId))
			{
				chunks_hashmap.remove(chunkId);
				i--;
			}
			number++;
		}
		if(ficheiroGuardados.contains(fileID))
		{
			ficheiroGuardados.remove(fileID);
		}
		Peer.saveDatabaseToFile();
	}
	
	public  HashMap<String, ChunkInfo> getChunks_hashmap()
	{
		return chunks_hashmap;
	}
		
	public  ArrayList<String> getFicheiroGuardados()
	{
		return ficheiroGuardados;
	}
	
	public  ArrayList<String> getChunkIdsOfFile(String fileId)
	{
		ArrayList<String>chunkIds = new ArrayList<String>();
		for(int i=0; i<chunks_hashmap.size();i++)
		{
			String chunkId = fileId+"-"+i;
			if(chunks_hashmap.containsKey(chunkId))
			{
				chunkIds.add(chunkId);
			}
		}
		return chunkIds;
	}
	
	public void removeChunkFromDB(String chunkId)
	{
		ArrayList<String>chunkIds = new ArrayList<String>();
		String[] splits = chunkId.split("-");
		if(chunks_hashmap.containsKey(chunkId))
		{
			chunks_hashmap.remove(chunkId);
		}
		chunkIds = getChunkIdsOfFile(splits[0]);
		if(chunkIds.size()==0)
		{
			ficheiroGuardados.remove(splits[0]);
		}
		Peer.saveDatabaseToFile();
	}
}
