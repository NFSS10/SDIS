package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import Channels.BackupChannel;
import Channels.ControlChannel;
import Channels.RestoreChannel;
import Disk.*;
import Utils.Database;
import Utils.FileHandler;
import Utils.Info;
import protocols.BackupProtocol;
import protocols.DeleteProtocol;
import protocols.ReclaimProtocol;
import protocols.RestoreProtocol;


public class Peer implements PeerInterface
{

	private static final String DISK_NAME = "disco.data";
	private static final String DATABASE_NAME = "database.data";
	private static final String INFO_NAME = "info.data";
	private static ControlChannel MCChannel;
	private static BackupChannel MDBChannel;
	private static RestoreChannel MDRChannel;
	private static Disk disk;
	private static Info info;
	private static Database database;
	private static String id;
	private static int port;
	private static InetAddress ip;
	private static HashMap<String, ArrayList<String>> storedMap;
	
	public static void main(String[] args)  throws IOException, ClassNotFoundException 
	{
		if(!validArguments(args))
		{	
			System.out.println("USAGE: Peer <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAdress> <mdrPort> <RemoteObjName>");
			return;
		}
		else
		{
			InetAddress mcAddress = null, mdbAddress = null , mdrAddress = null;
			int mcPort = 0,mdbPort=0,mdrPort=0;
			

			id=args[6];
				
			mcAddress = InetAddress.getByName(args[0]);
			mcPort = Integer.parseInt(args[1]);

			mdbAddress = InetAddress.getByName(args[2]);
			mdbPort = Integer.parseInt(args[3]);

			mdrAddress = InetAddress.getByName(args[4]);
			mdrPort = Integer.parseInt(args[5]);

			storedMap = new HashMap<String, ArrayList<String>>();
			MCChannel= new ControlChannel(mcAddress,mcPort);
			MDBChannel= new BackupChannel(mdbAddress,mdbPort);
			MDRChannel= new RestoreChannel(mdrAddress,mdrPort);
			
			  
	        new Thread(MCChannel).start();
	        new Thread(MDBChannel).start();
	        new Thread(MDRChannel).start();
			
			//criar rmi
			Peer server = new Peer();
			PeerInterface remoteObj = (PeerInterface) UnicastRemoteObject.exportObject(server,0);
	        Registry registry= LocateRegistry.getRegistry();
	        registry.rebind(id, remoteObj);	
	        
	        (new FileHandler()).createPeerDiskAndDatabaseFolder(id);
	        loadDiskFromFile();
	        loadDatabaseFromFile();
	        loadInfoFromFile();
	      
		}
	
	}
	
	
	
	
	public Peer() throws RemoteException
	{
		
	}
	

	public static boolean validArguments(String[] args)
	{
		
		if(args.length !=7)
		{
			if(args.length >7)
				System.out.println("Too much arguments");
			else
				System.out.println("Not enough arguments");
			return false;
		}
		if(!isNumeric(args[1]) || !isNumeric(args[3]) || !isNumeric(args[5]))
		{
			System.out.println("Ports not valid");
			return false;
		}
		
		
		return true;
	}
	public static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}

	
	
	
	
	
//=======================================================================
	
	private static void createDatabase() 
	{
		database = new Database();
		
		saveDatabaseToFile();
	}	
	private static void createDisk() 
	{
		disk = new Disk();
		
		saveDiskToFile();
	}

	private static void createInfo() 
	{
		info = new Info();
		
		saveInfoToFile();
	}
	private static void loadDiskFromFile() throws ClassNotFoundException, IOException 
	{
		try 
		{
			FileInputStream diskFile = new FileInputStream("../Disk/Peer_"+id+"/"+DISK_NAME);

			ObjectInputStream diskObj = new ObjectInputStream(
					diskFile);

			disk = (Disk) diskObj.readObject();

			diskObj.close();
			
		} catch (FileNotFoundException e) {
			createDisk();
		}
		System.out.println("Disk Loaded");
	}
	private static void loadInfoFromFile() throws ClassNotFoundException, IOException 
	{
		try 
		{
			FileInputStream infoFile = new FileInputStream("../Disk/Peer_"+id+"/"+INFO_NAME);

			ObjectInputStream infoObj = new ObjectInputStream(
					infoFile);

			info = (Info) infoObj.readObject();

			infoObj.close();
			
		} catch (FileNotFoundException e) {
			createInfo();
		}
		System.out.println("Info Loaded");
	}
	public static void loadDatabaseFromFile() throws ClassNotFoundException, IOException 
	{
		try {
			FileInputStream databaseFile = new FileInputStream("../Database/Peer_"+id+"/"+DATABASE_NAME);

			ObjectInputStream databaseObj = new ObjectInputStream(
					databaseFile);

			database = (Database) databaseObj.readObject();

			databaseObj.close();
			
		} catch (FileNotFoundException e) {
			createDatabase();
		}
		System.out.println("Database Loaded");
	}
	
	public  static void saveDiskToFile() 
	{
		try 
		{
			FileOutputStream diskFile = new FileOutputStream("../Disk/Peer_"+id+"/"+DISK_NAME);
			ObjectOutputStream diskObj = new ObjectOutputStream(diskFile);
			diskObj.writeObject(disk);
			diskObj.close();
		} catch (FileNotFoundException e) 
		{
			//se nao encontrar disco criado
			System.out.println("disk: file not found");
			createDisk();
		} catch (IOException e) 
		{

		}
		
	}
	public  static void saveDatabaseToFile() 
	{
		try {
			FileOutputStream databaseFile = new FileOutputStream("../Database/Peer_"+id+"/"+DATABASE_NAME);
			ObjectOutputStream databaseObj = new ObjectOutputStream(databaseFile);
			databaseObj.writeObject(database);
			databaseObj.close();
		} catch (FileNotFoundException e) 
		{
			//se nao encontrar database criado
			System.out.println("database: file not found");
			createDatabase();
		} catch (IOException e) 
		{

		}
		
	}
	
	public  static void saveInfoToFile() 
	{
		try 
		{
			FileOutputStream infoFile = new FileOutputStream("../Disk/Peer_"+id+"/"+INFO_NAME);
			ObjectOutputStream infoObj = new ObjectOutputStream(infoFile);
			infoObj.writeObject(info);
			infoObj.close();
		} catch (FileNotFoundException e) 
		{
			//se nao encontrar disco criado
			System.out.println("Info: file not found");
			createInfo();
		} catch (IOException e) 
		{

		}
		
	}
	
	public static InetAddress getIp()
	{
		return ip;
	}
	
	public static int getPort()
	{
		return port;
	}
	
	public static String getId()
	{
		return id;
	}
	
	
	public static Disk getDisk()
	{
		return disk;
	}
	
	public static Database getDatabase()
	{
		return database;
	}
	
	public static Info getInfo()
	{
		return info;
	}
	
	public static ControlChannel getMCChannel()
	{
		return MCChannel;
	}

	public static BackupChannel getMDBChannel()
	{
		return MDBChannel;
	}

	public static RestoreChannel getMDRChannel()
	{
		return MDRChannel;
	}



	@Override
	public void backup(File file, int replicationDegree) throws RemoteException 
	{
		new Thread(new BackupProtocol(file, replicationDegree)).start();
	}




	@Override
	public void restore(File file) throws RemoteException 
	{
		new Thread(new RestoreProtocol(file)).start();
		
	}




	@Override
	public void delete(File file) throws RemoteException 
	{
		new Thread(new DeleteProtocol(file)).start();
		
	}




	@Override
	public void reclaim(int amount) throws RemoteException 
	{
		new Thread(new ReclaimProtocol(amount)).start();
		
	}
	
	
	public  static void addStoredChunkId(String chunkID)
	{
		if (!storedMap.containsKey(chunkID))
			storedMap.put(chunkID, new ArrayList<String>());
	}

	public  static void clearStoredConfirms(String chunkID)
	{
		storedMap.get(chunkID).clear();
	}

	public static int getStoredConfirms(String chunkID)
	{
		return storedMap.get(chunkID).size();
	}
	
	public static  ArrayList<String> getStoredPeers(String chunkID)
	{
		return storedMap.get(chunkID);
	}
	
	public  static void clearStored(String chunkID)
	{
		storedMap.remove(chunkID);
	}

	public static void addStoredConfirm(String chunkID, String senderID) 
	{
		if (storedMap.containsKey(chunkID))
			if (!storedMap.get(chunkID).contains(senderID))
				storedMap.get(chunkID).add(senderID);
	}
	
	public static HashMap<String, ArrayList<String>> getStoredMap()
	{
		return storedMap;
	}




	@Override
	public void state() throws RemoteException {
		System.out.println("Peer "+id+" :");
		System.out.println("==================INITIATED===================");
		for ( String key :info.getFicheiroGuardados().keySet())
		{
			ArrayList<String> chunkIds = info.getChunkIdsOfFile(key);
			System.out.println("File Path: "+info.getFicheiroGuardados().get(key));
			System.out.println("File Id: "+key);
			System.out.println("File Replication Degree: "+info.getChunkInfo(chunkIds.get(0)).getReplicationDegree());
			for (int i=0; i< chunkIds.size();i++)
			{
				System.out.println("       ->ChunkId: "+ chunkIds.get(i));
				System.out.println("       Perceived replication degree: "+ info.getChunkInfo( chunkIds.get(i)).getPeerMirrors().size());
			}
			
		}
		System.out.println("====================SAVED=====================");
		for ( String key :database.getChunks_hashmap().keySet() ) 
		{
			File file = new File("../Storage/Peer_"+id+"/Chunks/"+key.split("-")[0]+"/"+key.split("-")[1]);
			System.out.println("       ->ChunkId: "+ key);
			System.out.println("         Size: "+ file.length());
			System.out.println("         Perceived replication degree: "+ database.getChunkInfo(key).getPeerMirrors().size());
			System.out.println("------------------------------------------------");
		}
		System.out.println("=====================DISK=====================");
		System.out.println("Used: "+disk.used);
		System.out.println("Free space: "+disk.getFreeSpace());
	}


}

