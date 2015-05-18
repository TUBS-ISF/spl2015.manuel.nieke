package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import transfer.RMIInputStream;
import transfer.RMIInputStreamImpl;
import transfer.RMIOutputStream;
import transfer.RMIOutputStreamImpl;

public class FileHosterServer extends UnicastRemoteObject {

	public FileHosterServer(int port) throws RemoteException {
		super(port);
	}

	private static int serverPort = 1099;
	private static int registryPort = 1099;

	enum saveOptionEnum {
		IN_MEMORY, HDD
	};

	enum identificationOptionEnum {
		ID, PATH, ID_PATH
	};

	private static saveOptionEnum saveOption = null;
	private static identificationOptionEnum identificationOption = null;

	Registry rmiRegistry;
	Map<String, File> fileMap = new HashMap<String, File>();

	public static void main(String[] args) {

		boolean sp, rp, so, io;
		sp = rp = so = io = false;

		// Read command line paramters
		for (String arg : args) {
			if (arg.equals("--help") || arg.equals("-h")) {
				printHelpText();
				return;
			}

			// Read selected parameters
			else if (arg.equals("-sp")) {
				if (serverPort == 1099) {
					sp = true;
					continue;
				} else {
					System.out.println("-sp selected multiple times!");
					return;
				}
			} else if (arg.equals("-rp")) {
				if (serverPort == 1099) {
					rp = true;
					continue;
				} else {
					System.out.println("-rp selected multiple times!");
					return;
				}
			} else if (arg.equals("-so")) {
				if (saveOption == null) {
					so = true;
					continue;
				} else {
					System.out.println("-so selected multiple times!");
					return;
				}
			} else if (arg.equals("-io")) {
				if (identificationOption == null) {
					io = true;
					continue;
				} else {
					System.out.println("-io selected multiple times!");
					return;
				}
			} else

			// Read parameters values
			if (sp) {
				try {
					serverPort = Integer.parseInt(arg);
				} catch (NumberFormatException e) {
					System.out
							.println("Invalid parameter for server port! Please specify a valid integer value.");
					return;
				}
			} else if(rp) {
				try {
					registryPort = Integer.parseInt(arg);
				} catch (NumberFormatException e) {
					System.out
							.println("Invalid parameter for registry port! Please specify a valid integer value.");
					return;
				}
			} else if(so) {
				if(arg.equals("memory")) {
					saveOption = saveOptionEnum.IN_MEMORY;
				} else if(arg.equals("hdd")) {
					saveOption = saveOptionEnum.HDD;
				} else {
					System.out.println("Invalid value for save option! Please select either memory or hdd.");
					return;
				}
			} else if(io) {
				if(arg.equals("id")) {
					identificationOption = identificationOptionEnum.ID;
				} else if(arg.equals("path")) {
					identificationOption = identificationOptionEnum.PATH;
				} else if(arg.equals("idpath")) {
					identificationOption = identificationOptionEnum.ID_PATH;
				} else {
					System.out.println("Invalid value for identification option! Please select either id, path or idpath.");
					return;
				}
			} else {
				System.out.println("Unkown parameter: " + arg);
				printHelpText();
				return;
			}
		}
		
		if(sp || rp || so || io || saveOption == null || identificationOption == null) {
			System.out.println("Invalid parameters!");
			printHelpText();
			return;
		}

		// Start server and return if unsuccessful
		if (!start()) {
			return;
		}

		try {
			System.out
					.println("File hoster started successfully!\nClients can now connect to "
							+ InetAddress.getLocalHost().getHostAddress()
							+ ":"
							+ registryPort + ".");
		} catch (UnknownHostException e) {
			System.out
					.println("Unknown error. Try restarting the application!");
		}

	}

	/**
	 * This method attempts to create a file hoster server and export it. If
	 * successful it then starts a registry and adds an entry for the server
	 * with the key "FileHosterServer".
	 * 
	 * @return whether the server and registry have been successfully started
	 */
	private static boolean start() {

		// Create server object and export it
		FileHosterServer server;
		try {
			server = new FileHosterServer(serverPort);
		} catch (RemoteException e) {
			System.out
					.println("File hoster could not be exported. Try specifying a different port with the -sp paramter.");
			return false;
		}

		// Create registry and add exported server object with key
		// "FileHosterServer"
		// TODO handle external registries
		try {
			server.rmiRegistry = LocateRegistry.createRegistry(registryPort);
			server.rmiRegistry.bind("FileHosterServer", server);
		} catch (RemoteException e) {
			System.out
					.println("Registry could not be created. Try specifying a different port with the -rp parameter.");
			return false;
		} catch (AlreadyBoundException e) {
			try {
				server.rmiRegistry.unbind("FileHosterServer");
				server.rmiRegistry.bind("Server", server);
			} catch (AccessException e1) {
				System.out
						.println("Unknown error. Try restarting the application!");
			} catch (RemoteException e1) {
				System.out
						.println("Unknown error. Try restarting the application!");
			} catch (NotBoundException e1) {
				System.out
						.println("Unknown error. Try restarting the application!");
			} catch (AlreadyBoundException e1) {
				System.out
						.println("Unknown error. Try restarting the application!");
			}
		}

		return true;
	}

	/**
	 * Write help text for command line parameters.
	 */
	private static void printHelpText() {
		System.out.println("Possible parameters are:");
		System.out
				.println("\t-sp <port>: The port the server application is listening on. (Default 1099)");
		System.out
				.println("\t-rp <port>: The port the registry is listening on. (Default 1099)");
		System.out
				.println("\t-so <save option>: \"memory\" to keep files in ram or \"hdd\" to write to disk (mandatory)");
		System.out
				.println("\t-io <identification option>: \"id\" to identify files by id, \"path\" to identify files by path and name \n\t\tor \"idpath\" to use a combination of both (mandatory)");
		System.out.println("\t--help -h This message");
	}

	private OutputStream getOutputStream(File f) throws IOException {
		return new RMIOutputStream(new RMIOutputStreamImpl(
				new FileOutputStream(f)));
	}

	private InputStream getInputStream(File f) throws IOException {
		return new RMIInputStream(
				new RMIInputStreamImpl(new FileInputStream(f)));
	}
}
