package protocols;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;

import Server.Peer;
import Utils.Utils;

public class DeleteProtocol  implements Runnable {

	private File file;
	public DeleteProtocol(File file)
	{
		this.file = file;
	}
	
	@Override
	public void run()
	{
		try 
		{
			String fileId = Utils.makeFileId(file);
			if(Peer.getDatabase().isFileStored(fileId))
			{
				System.out.println("Is on database");
				File folder = new File("../Storage/Peer_"+Peer.getId()+"/Chunks/"+fileId);
				
				int size = (int)Utils.folderSize(folder);
				
				Utils.deleteFolder(folder);
				
				Peer.getDatabase().removeFileFromDatabase(fileId);
				Peer.getDisk().used = Peer.getDisk().used-size;
				Peer.saveDiskToFile();
			}
			if(Peer.getInfo().isFileStored(fileId))
			{
				Peer.getInfo().removeFileFromDatabase(fileId);
			}
			byte[] deleteMessage = makeDeleteMessage(fileId);
			DatagramPacket packet = new DatagramPacket(deleteMessage,deleteMessage.length,Peer.getMCChannel().address,Peer.getMCChannel().port);
			Peer.getMCChannel().socket.send(packet);
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] makeDeleteMessage(String fileId)
	{
		String messageHeader ="DELETE"+" "+"1.0"+" "+Peer.getId()+" "+fileId+" "+"/r/n/r/n";
		
		byte[] message= messageHeader.getBytes();
						
		return message;
		
	}
	
	
}
