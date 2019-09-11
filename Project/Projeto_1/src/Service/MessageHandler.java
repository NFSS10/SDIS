package Service;

import java.awt.TrayIcon.MessageType;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

import Chunk.Chunk;
import Chunk.ChunkInfo;
import Server.Peer;
import Utils.FileHandler;
import Utils.Header;
import Utils.Utils;


public class MessageHandler implements Runnable 
{
	private DatagramPacket packet;
	private String message;
	private Header header;
	private byte[] body;

	public MessageHandler(DatagramPacket packet) 
	{
		this.packet = packet;
		this.message = null;
		this.header= null;
		this.body = null;
	}

	public void run() 
	{
		//Ve qual o tipo de mensagem
		message = new String(packet.getData(), 0, packet.getLength());
		
		Header header = new Header(message);
		if(header.getMessageType().equals("PUTCHUNK")||header.getMessageType().equals("CHUNK"))
		extractBody(message);
		
		System.out.println("message: "+ header.getMessageType());
		switch (header.getMessageType())
		{
		case "PUTCHUNK":
			{
				handlePUTCHUNK(header);
			}
			break;
		case "STORED":
			{
				handleSTORED(header);
			}
			break;
		case "GETCHUNK":
			{
				handleGETCHUNK(header);
			}
			break;
		case "CHUNK":
			{
				handleCHUNK(header);
			}
			break;
		case "DELETE":
			{
				handleDELETE(header);
			}
			break;
		case "REMOVED":
			{
				handleREMOVED(header);
			}
			break;
		default:
			break;
		}
	}

	
	private void handlePUTCHUNK(Header header)
	{
		FileHandler fileHandler = new FileHandler();
		Chunk chunk = new Chunk(header.getFileId(),header.getChunkNum(),header.getReplication_degree(),body);
		if(body.length<Peer.getDisk().getFreeSpace())
		{
			boolean check = new File("../Storage/Peer_"+Peer.getId()+"/Chunks/"+chunk.getChunkFileID()+"/"+chunk.getChunkNum()).exists();
			if (check)
			{
					System.out.println("ja existe");
					String chunkID = header.getFileId()+"-"+header.getChunkNum();
					ChunkInfo chunkInfo = new ChunkInfo(header.getReplication_degree());
					chunkInfo.addPeerMirror(Peer.getId());
					Peer.getDatabase().saveChunk(chunkID, chunkInfo);
					Peer.getDatabase().saveStoredFile(header.getFileId());
					
					try
					{
						Random r = new Random();
						int Low = 0;
						int High = 400;
						
						int sleepTime = r.nextInt(High-Low) + Low;
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					sendStoredMessage(header);
			}
			else
			{
				
					Peer.addStoredChunkId(chunk.getChunkID());
					fileHandler.saveChunk(chunk,header);
					
					Peer.getDisk().used= Peer.getDisk().used+ chunk.getData().length;
					Peer.saveDiskToFile();
					String chunkID = header.getFileId()+"-"+header.getChunkNum();
					ChunkInfo chunkInfo = new ChunkInfo(header.getReplication_degree());
					chunkInfo.addPeerMirror(Peer.getId());
					
					Peer.getDatabase().saveChunk(chunkID, chunkInfo);
					Peer.getDatabase().saveStoredFile(header.getFileId());
					
					try
					{
						Random r = new Random();
						int Low = 0;
						int High = 400;
						
						int sleepTime = r.nextInt(High-Low) + Low;
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					sendStoredMessage(header);
			}
		}
		else
		{
			System.out.println("Not enough space");
		}
		
	}
	
	private void handleSTORED(Header header) 
	{
		String chunkID = header.getFileId()+"-"+header.getChunkNum();
		String senderID = header.getServerId();
		
		if(Peer.getDatabase().isChunkNaDatabase(chunkID))
		{
			ChunkInfo chunkInfo = Peer.getDatabase().getChunkInfo(chunkID);
			chunkInfo.addPeerMirror(header.getServerId());
			Peer.getDatabase().saveChunk(chunkID, chunkInfo);
		}
			
		Peer.addStoredConfirm(chunkID, senderID);
	}
	
	
	
	private void handleGETCHUNK(Header header)
	{
		String chunkID = header.getFileId()+"-"+header.getChunkNum();

		if (Peer.getDatabase().isChunkNaDatabase(chunkID)) 
		{ 
			try 
			{
				Random r = new Random();
				int Low = 0;
				int High = 400;
				int sleepTime = r.nextInt(High-Low) + Low;
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}

		
			byte[] data = FileHandler.loadChunkData(header); //ir buscar a data ao ficheiro

			Chunk chunk = new Chunk(header.getFileId(),header.getChunkNum(), -1, data);

			sendChunkMessage(chunk); //mandar mensagem "CHUNK"
			}
	}
	 
	
	private void handleCHUNK(Header header) 
	{
		Chunk chunk = new Chunk(header.getFileId(),header.getChunkNum(),-1,body);
		if(Peer.getMDRChannel().getChunk()== null)
		{
			Peer.getMDRChannel().setChunk(chunk);
		}
		
	}

	
	private void handleDELETE(Header header)
	{
		System.out.println("DeleteHandled");
			
		if(Peer.getDatabase().isFileStored(header.getFileId()))
		{
			System.out.println("Is on database");
			File folder = new File("../Storage/Peer_"+Peer.getId()+"/Chunks/"+header.getFileId());
			
			int size = (int)Utils.folderSize(folder);
			
			Utils.deleteFolder(folder);
			
			Peer.getDatabase().removeFileFromDatabase(header.getFileId());
			Peer.getDisk().used = Peer.getDisk().used-size;
			Peer.saveDiskToFile();
		}
		if(Peer.getInfo().isFileStored(header.getFileId()))
		{
			Peer.getInfo().removeFileFromDatabase(header.getFileId());
		}
		
	}
	
	private void handleREMOVED(Header header)
	{
		String chunkId = header.getFileId()+"-"+header.getChunkNum();
		if(Peer.getInfo().isChunkNaDatabase(chunkId))
		{
			ChunkInfo chunkInfo = Peer.getInfo().getChunkInfo(chunkId);
			chunkInfo.removeMirror(header.getServerId());
			Peer.getInfo().saveChunk(chunkId, chunkInfo);
		}
		if(Peer.getDatabase().isChunkNaDatabase(chunkId))
		{
			ChunkInfo chunkInfo = Peer.getDatabase().getChunkInfo(chunkId);
			chunkInfo.removeMirror(header.getServerId());
			Peer.getDatabase().saveChunk(chunkId, chunkInfo);
			
			if(chunkInfo.getPeerMirrors().size()<chunkInfo.getReplicationDegree())
			{
				
				try 
				{
					File file = new File("../Storage/Peer_"+Peer.getId()+"/Chunks/"+header.getFileId()+"/"+header.getChunkNum());
					byte[] data;
					data = Files.readAllBytes(file.toPath());
					Chunk chunk = new Chunk(header.getFileId(),header.getChunkNum(),chunkInfo.getReplicationDegree(),data);
					Random r = new Random();
					int Low = 0;
					int High = 400;
					Peer.addStoredChunkId(chunk.getChunkID());
					int sleepTime = r.nextInt(High-Low) + Low;
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sendPutchunkMessage(chunk);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
		
	}
	

	private void extractBody(String message) 
	{
		body = Arrays.copyOfRange(packet.getData(), message.lastIndexOf("/r/n/r/n") + 8, packet.getLength());	
	}
	
	private void sendStoredMessage(Header header) 
	{
		String message ="STORED"+" "+"1.0"+" "+Peer.getId()+" "+header.getFileId()+" "+
				header.getChunkNum()+" "+"/r/n/r/n";
		byte[] storedMessage = message.getBytes();
		DatagramPacket packet = new DatagramPacket(storedMessage,storedMessage.length,Peer.getMCChannel().address,Peer.getMCChannel().port);
		
		try
		{
			Peer.getMCChannel().socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendChunkMessage(Chunk chunk) 
	{
		String messageHeader ="CHUNK"+" "+"1.0"+" "+Peer.getId()+" "+chunk.getChunkFileID()+" "+
								chunk.getChunkNum()+" "+"/r/n/r/n";
		
		byte[] messageBody = chunk.getData();
		byte[] message = Utils.juntarBytes(messageHeader.getBytes(),messageBody);
		DatagramPacket packet = new DatagramPacket(message,message.length,Peer.getMDRChannel().address,Peer.getMDRChannel().port);
		
		try
		{
			Peer.getMDRChannel().socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendPutchunkMessage(Chunk chunk) 
	{
		
		String messageHeader ="PUTCHUNK"+" "+"1.0"+" "+Peer.getId()+" "+chunk.getChunkFileID()+" "+
				chunk.getChunkNum()+" "+chunk.getReplicationDegree()+" "+ "/r/n/r/n";

		byte[] messageBody = chunk.getData();
		byte[] message = Utils.juntarBytes(messageHeader.getBytes(),messageBody);
		
		DatagramPacket packet = new DatagramPacket(message,message.length,Peer.getMDBChannel().address,Peer.getMDBChannel().port);
		
		try 
		{
			Peer.getMDBChannel().socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	
}