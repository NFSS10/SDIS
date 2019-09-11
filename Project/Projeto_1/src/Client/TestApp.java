package Client;

import Server.*;

import java.io.File;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestApp {

	private static String peer_ap = null;
	private static String operation = null;
	private static String filePath = null;
	private static int replication_degree = -1;
	private static PeerInterface stub;
	
	
	public static void main(String[] args) throws AccessException, RemoteException, NotBoundException 
	{
		TestApp app = new TestApp(args);
		app.start();
		if(operation.equals("BACKUP"))
		{
			backupOperation();
		}
		if(operation.equals("RESTORE"))
		{
			restoreOperation();
		}
		if(operation.equals("DELETE"))
		{
			deleteOperation();
		}
		if(operation.equals("RECLAIM"))
		{
			reclaimOperation();
		}
		if(operation.equals("STATE"))
		{
			stateOperation();
		}
	}
	
	
	public void start() throws AccessException, RemoteException, NotBoundException
	{
		Registry registry = LocateRegistry.getRegistry("localhost");
	    stub = (PeerInterface) registry.lookup(peer_ap);
	}
	
	private TestApp(String[] args) 
	{
		 //VERIFICAR OS ARGUMENTOS;
		if(args.length < 2 && args.length > 4)
		{
			System.out.println("Usage: TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
			System.out.println("Usage: TestApp <peer_ap> <sub_protocol> <opnd_1>");
			System.out.println("Usage: TestApp <peer_ap> <sub_protocol>");
			System.exit(0);
		}
		else 
		{
			if (args.length==4)
			{
				if(!verifyFile(args[2]))
				{
					System.out.println("Not a valid file path");
					System.exit(0);
				}
				setPeer_ap(args[0]);
				setOperation(args[1]);
				setFilePath(args[2]);
				setReplication_degree(Integer.parseInt(args[3]));
				
			}
			else if(args.length==3)
			{
				if(!verifyFile(args[2]) && (args[1].equals("BACKUP") ||args[1].equals("RESTORE")))
				{
					System.out.println("Not a valid file path");
					System.exit(0);
				}
				setPeer_ap(args[0]);
				setOperation(args[1]);
				setFilePath(args[2]);
			}
			else if(args.length==2)
			{
				setPeer_ap(args[0]);
				setOperation(args[1]);
			}
		}
	}

	public static void backupOperation() throws RemoteException
	{
		System.out.println("Backup: "+filePath+"\nReplication degree: "+replication_degree+"\n");
		File file = new File(filePath);
		stub.backup(file, replication_degree);
	}
	
	public static void restoreOperation() throws RemoteException
	{
		System.out.println("Restore: "+filePath+"\n");
		File file = new File(filePath);
		stub.restore(file);
	}
	
	public static void deleteOperation() throws RemoteException
	{
		System.out.println("Delete: "+filePath+"\n");
		File file = new File(filePath);
		stub.delete(file);
	}
	
	public static void reclaimOperation() throws RemoteException
	{
		System.out.println("Reclaim: "+filePath+" KBs\n");
		stub.reclaim(Integer.parseInt(filePath));
	}
	
	public static void stateOperation() throws RemoteException
	{
		System.out.println("State of the peer: "+ peer_ap);
		stub.state();
	}
	
	public static boolean verifyFile(String path) 
	{
		File file = new File(path);
		if (!file.exists() || !file.isFile()) {
			System.out.println("This is not a valid file path");
			return false;
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//===========####=======Getters and Setters=======####==============
	public static int getReplication_degree() {
		return replication_degree;
	}

	public static void setReplication_degree(int replication_degree) {
		TestApp.replication_degree = replication_degree;
	}

	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		TestApp.filePath = filePath;
	}

	public static String getOperation() {
		return operation;
	}

	public static void setOperation(String operation) {
		TestApp.operation = operation;
	}

	public static String getPeer_ap() {
		return peer_ap;
	}

	public static void setPeer_ap(String peer_ap) {
		TestApp.peer_ap = peer_ap;
	}


}
