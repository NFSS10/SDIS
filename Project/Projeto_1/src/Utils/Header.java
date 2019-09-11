package Utils;

public class Header 
{
	private String messageType;
	private String version;
	private String serverId;
	private String fileId;
	private int chunkNum;
	private int replication_degree;
	
	public Header(String message)
	{
		String header = message.substring(0,message.indexOf("/r"));
		String[]splited = header.split(" ");
		messageType = splited[0];
		version = splited[1];
		serverId = splited[2];
		fileId = splited[3];
		if(!messageType.equals("DELETE"))
		{
		chunkNum = Integer.parseInt(splited[4]);
		}
		if(messageType.equals("PUTCHUNK"))
		{
			replication_degree = Integer.parseInt(splited[5]);
		}
	}

	public int getReplication_degree() 
	{
		return replication_degree;
	}

	public void setReplication_degree(int replication_degree)
	{
		this.replication_degree = replication_degree;
	}

	public int getChunkNum() 
	{
		return chunkNum;
	}

	public void setChunkNum(int chunkNum)
	{
		this.chunkNum = chunkNum;
	}

	public String getFileId() 
	{
		return fileId;
	}

	public void setFileId(String fileId)
	{
		this.fileId = fileId;
	}

	public String getServerId()
	{
		return serverId;
	}

	public void setServerId(String serverId)
	{
		this.serverId = serverId;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getMessageType() 
	{
		return messageType;
	}

	public void setMessageType(String messageType)
	{
		this.messageType = messageType;
	}
	
	
	
}
