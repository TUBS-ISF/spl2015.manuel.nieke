package application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class FileHosterServer extends UnicastRemoteObject {
	
	public FileHosterServer(int port) throws RemoteException {
		super(port);
	}
	
	private static int serverPort = 1099;
	private static int registryPort = 1099;
	
	Registry rmiRegistry;

	public static void main(String[] args) {
		
		// Start server and return if unsuccessful
		if(!start()) {
			return;
		}

		try {
			System.out.println("File hoster started successfully!\nClients can now connect to " + InetAddress.getLocalHost().getHostAddress() + ":" + registryPort + ".");
		} catch (UnknownHostException e) {
			System.out.println("Unknown error. Try restarting the application!");
		}
		
		
	}
	
	/**
	 * This method attempts to create a file hoster server and export it.
	 * If successful it then starts a registry and adds an entry for the server with the key "FileHosterServer".
	 * 
	 * @return whether the server and registry have been successfully started 
	 */
	private static boolean start() {
		
		// Create server object and export it
		FileHosterServer server;
		try {
			server = new FileHosterServer(serverPort);
		} catch (RemoteException e) {
			System.out.println("File hoster could not be exported. Try specifying a different port with the -sp paramter.");
			return false;
		}
		
		// Create registry and add exported server object with key "FileHosterServer"
		// TODO handle external registries
		try {
			server.rmiRegistry = LocateRegistry.createRegistry(registryPort);
			server.rmiRegistry.bind("FileHosterServer", server);
		} catch (RemoteException e) {
			System.out.println("Registry could not be created. Try specifying a different port with the -rp parameter.");
			return false;
		} catch (AlreadyBoundException e) {
			try {
				server.rmiRegistry.unbind("FileHosterServer");
				server.rmiRegistry.bind("Server", server);
			} catch (AccessException e1) {
				System.out.println("Unknown error. Try restarting the application!");
			} catch (RemoteException e1) {
				System.out.println("Unknown error. Try restarting the application!");
			} catch (NotBoundException e1) {
				System.out.println("Unknown error. Try restarting the application!");
			} catch (AlreadyBoundException e1) {
				System.out.println("Unknown error. Try restarting the application!");
			}
		}
		
		return true;
	}

}
