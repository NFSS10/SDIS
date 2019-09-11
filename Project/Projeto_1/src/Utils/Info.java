package Utils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import Chunk.Chunk;
import Chunk.ChunkInfo;
import Server.Peer;

public class Info implements Serializable {
		
		//chunk id (hash+chunkNum) e a info do chunk correspondente (replication degree e onde esta guardado)
		HashMap<String, ChunkInfo> chunks_hashmap;
		//Ficheiros guardados
		HashMap<String, String> ficheiroGuardados;
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		
		public Info()
		{
			chunks_hashmap = new HashMap<String, ChunkInfo>();
			ficheiroGuardados = new HashMap<String, String>();
			
		}
		
		
		//-------------- Chunks -------------------------------
		public ChunkInfo getChunkInfo(String chunkID) 
		{
			return chunks_hashmap.get(chunkID);
		}

		public void saveChunk(String chunkID, ChunkInfo  chunkInfo)
		{
			chunks_hashmap.put(chunkID, chunkInfo);
			Peer.saveInfoToFile();
		}

		public boolean isChunkNaDatabase(String chunkID)
		{
			return chunks_hashmap.containsKey(chunkID);
		}

		public void removeFile(String chunkID)
		{
			if(chunks_hashmap.containsKey(chunkID))
			{
				chunks_hashmap.remove(chunkID);
			}
			
		}
		
		//-------------- Ficheiros guardados ------------------
		//Diz se o ficheiro esta guardado
		public boolean isFileStored(String fileID) 
		{
			return ficheiroGuardados.containsKey(fileID);
		}
		//Guarda o ficheiro no arraylist
		public void saveStoredFile(String fileID, String filePath) 
		{
			if(!ficheiroGuardados.containsKey(fileID))
			{
				ficheiroGuardados.put(fileID, filePath);
				Peer.saveInfoToFile();
			}
		}

		public void removeFileFromDatabase(String fileID)
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
			if(ficheiroGuardados.containsKey(fileID))
			{
				ficheiroGuardados.remove(fileID);
			}
			Peer.saveInfoToFile();
		}
		
		public HashMap<String, ChunkInfo> getChunks_hashmap()
		{
			return chunks_hashmap;
		}
			
		public HashMap<String,String> getFicheiroGuardados()
		{
			return ficheiroGuardados;
		}
		
		public ArrayList<String> getChunkIdsOfFile(String fileId)
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
			Peer.saveInfoToFile();
		}
	}

