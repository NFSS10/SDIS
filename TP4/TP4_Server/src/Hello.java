import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public interface Hello extends Remote { 
	
    String procurarNome(String matricula) throws RemoteException;
}