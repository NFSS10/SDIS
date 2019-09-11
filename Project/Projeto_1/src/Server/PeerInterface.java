package Server;


import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterface extends Remote{

	void backup(File file, int replicationDegree) throws RemoteException;

	void restore(File file) throws RemoteException;

	void delete(File file) throws RemoteException;

	void reclaim(int amount) throws RemoteException;
	
	void state() throws RemoteException;

}
