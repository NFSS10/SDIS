package Chunk;


/**
 * Tem a informação do chunk
 */
public class Chunk
{
	private static final int TAM_MAX = 64000; //64 KBytes
	
	private String chunk_FileID;
	private int chunkNum;
	private int replicationDegree;
	private byte[] data;
	
	
	public Chunk(String fileID, int chunkNum, int replicationDeg, byte[] data)
	{
		this.chunk_FileID = fileID;
		this.chunkNum = chunkNum;
		this.replicationDegree=replicationDeg;
		this.data = data;
		
	}
	
	public String getChunkID()
	{
		return chunk_FileID + "-"+ chunkNum;
	}
	
	public String getChunkFileID()
	{
		return chunk_FileID;
	}
	public int getChunkNum()
	{
		return chunkNum;
	}	
	public int getReplicationDegree()
	{
		return replicationDegree;
	}
	public byte[] getData()
	{
		return data;
	}
	
	
}
