package notification; 

import java.rmi.Remote; 
import java.rmi.RemoteException; 

public  interface  INotificationListener  extends Remote {
	
	public void notify(String fileName, String Message) throws RemoteException;


}
