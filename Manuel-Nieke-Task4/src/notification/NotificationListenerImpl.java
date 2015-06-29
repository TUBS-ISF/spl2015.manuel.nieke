package notification;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NotificationListenerImpl extends UnicastRemoteObject implements INotificationListener {
	
	public NotificationListenerImpl() throws RemoteException{
		super();
	}
	
	public NotificationListenerImpl(int port) throws RemoteException{
		super(port);
	}

	public void notify(String fileName, String message) throws RemoteException {
		System.out.println("File "+fileName + " changed: " + message);

	}

}
