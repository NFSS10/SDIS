
	
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
	
public class Server implements Hello {
	
	HashMap<String, String> hmap;
	
    public Server() {
    	hmap= new HashMap<String, String>();
    	hmap.put("12345","Celso");
    }

    public String procurarNome(String matricula) {
    	 System.err.println("A procurar matricula...");
    	 String nome = hmap.get(matricula);
    	 return nome;
    }
	
    public static void main(String args[]) {
	
	try {
	    Server obj = new Server();
	    Hello stub = (Hello) UnicastRemoteObject.exportObject(obj, 0);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry();
	    registry.bind("hello", stub);

	    System.err.println("Server ready");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}
    }
}