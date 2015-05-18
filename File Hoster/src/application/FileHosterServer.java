package application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import transfer.RMIInputStream;
import transfer.RMIInputStreamImpl;
import transfer.RMIOutputStream;
import transfer.RMIOutputStreamImpl;

public class FileHosterServer extends UnicastRemoteObject implements
		IFileHosterServer {

	public FileHosterServer(int port) throws RemoteException {
		super(port);
	}

	final public static int BUF_SIZE = 1024 * 64;
	
	private static int serverPort = 1099;
	private static int registryPort = 1099;
	private int idCounter = 0;

	enum saveOptionEnum {
		IN_MEMORY, HDD
	};

	enum identificationOptionEnum {
		ID, PATH, ID_PATH
	};

	private static saveOptionEnum saveOption = null;
	private static identificationOptionEnum identificationOption = null;

	Registry rmiRegistry;

	// Maps to identify files by id or path for in memory saving
	Map<Integer, ByteArrayOutputStream> idFileMap;
	Map<String, ByteArrayOutputStream> pathFileMap;

	// Maps to identify the filepath for id or path for hdd saving
	Map<Integer, String> idPathMap;
	Set<String> filePathSet;

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
			} else if (rp) {
				try {
					registryPort = Integer.parseInt(arg);
				} catch (NumberFormatException e) {
					System.out
							.println("Invalid parameter for registry port! Please specify a valid integer value.");
					return;
				}
			} else if (so) {
				if (arg.equals("memory")) {
					saveOption = saveOptionEnum.IN_MEMORY;
				} else if (arg.equals("hdd")) {
					saveOption = saveOptionEnum.HDD;
				} else {
					System.out
							.println("Invalid value for save option! Please select either memory or hdd.");
					return;
				}
				so = false;
			} else if (io) {
				if (arg.equals("id")) {
					identificationOption = identificationOptionEnum.ID;
				} else if (arg.equals("path")) {
					identificationOption = identificationOptionEnum.PATH;
				} else if (arg.equals("idpath")) {
					identificationOption = identificationOptionEnum.ID_PATH;
				} else {
					System.out
							.println("Invalid value for identification option! Please select either id, path or idpath.");
					return;
				}
				io = false;
			} else {
				System.out.println("Unkown parameter: " + arg);
				printHelpText();
				return;
			}
		}

		if (sp || rp || so || io || saveOption == null
				|| identificationOption == null) {
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

	public synchronized ReturnContainer createNewFile(String name)
			throws IOException {
		ReturnContainer container = null;
		String path = null;
		switch (identificationOption) {
		case ID:
			registerFileID(name);
			container = new OutputIDContainer(idCounter++);
			break;
		case PATH:
			path = registerFilePath(name);
			container = new OutputPathContainer(path);
			break;
		case ID_PATH:
			path = registerFileIDPath(name);
			container = new OutputIDPathContainer(idCounter++, path);
			break;
		}

		return container;
	}

	public OutputStream getOutputStream(String name) throws IOException {
		
		OutputStream outputStream = null;
		
		if(saveOption == saveOptionEnum.HDD) {
			File file = new File(name);
			outputStream = new FileOutputStream(file);
		} else {
			outputStream = pathFileMap.get(name);
		}

		outputStream = new RMIOutputStream(
				new RMIOutputStreamImpl(outputStream));

		return outputStream;

	}
	
	public OutputStream getOutputStream(Integer ID) throws IOException {
		
		OutputStream outputStream = null;
		
		if(saveOption == saveOptionEnum.HDD) {
			File file = new File(idPathMap.get(ID));
			outputStream = new FileOutputStream(file);
		} else {
			outputStream = idFileMap.get(ID);
		}
		
		outputStream = new RMIOutputStream(
				new RMIOutputStreamImpl(outputStream));


		return outputStream;
	}

	public InputStream getInputStream(String path) throws IOException {
		InputStream inputStream = null;
		
		if(saveOption == saveOptionEnum.HDD) {
			File file = new File(path);
			inputStream = new FileInputStream(file);
		} else {
			ByteArrayOutputStream outputStream = pathFileMap.get(path);
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		}
		
		return new RMIInputStream(
				new RMIInputStreamImpl(inputStream));
	}
	
	public InputStream getInputStream(Integer id) throws IOException {
		 InputStream inputStream = null;
		
		if(saveOption == saveOptionEnum.HDD) {
			File file = new File(idPathMap.get(id));
			inputStream = new FileInputStream(file);
		} else {
			ByteArrayOutputStream outputStream = idFileMap.get(id);
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());
			
		}
		
		return new RMIInputStream(
				new RMIInputStreamImpl(inputStream));
	}

	private void registerFileID(String name) throws IOException {
		if (saveOption == saveOptionEnum.HDD) {
			File file = new File(name);
			if (idPathMap == null) {
				idPathMap = new HashMap<Integer, String>();
			}
			idPathMap.put(idCounter, file.getPath());
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} else {
			if (idFileMap == null) {
				idFileMap = new HashMap<Integer, ByteArrayOutputStream>();
			}
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			idFileMap.put(idCounter, outputStream);
		}
	}

	private String registerFilePath(String name) throws IOException {
		String path = null;
		if (saveOption == saveOptionEnum.HDD) {
			File file = new File(name);
			if (filePathSet == null) {
				filePathSet = new HashSet<String>();
			}
			filePathSet.add(file.getPath());
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			path = file.getPath();
		} else {
			if (pathFileMap == null) {
				pathFileMap = new HashMap<String, ByteArrayOutputStream>();
			}
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			pathFileMap.put(name, outputStream);
			path = name;
		}
		
		return path;
	}

	private String registerFileIDPath(String name) throws IOException {
		String path = null;
		if (saveOption == saveOptionEnum.HDD) {
			File file = new File(name);
			if (filePathSet == null) {
				filePathSet = new HashSet<String>();
			}
			if (idPathMap == null) {
				idPathMap = new HashMap<Integer, String>();
			}

			filePathSet.add(file.getPath());
			idPathMap.put(idCounter, file.getPath());

			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			path = file.getPath();
		} else {
			if (pathFileMap == null) {
				pathFileMap = new HashMap<String, ByteArrayOutputStream>();
			}
			if (idFileMap == null) {
				idFileMap = new HashMap<Integer, ByteArrayOutputStream>();
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			idFileMap.put(idCounter, outputStream);
			pathFileMap.put(name, outputStream);
			path = name;
		}
		return path;
	}
	
	public static void copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[BUF_SIZE];
		int len;
		while ((len = in.read(b)) >= 0) {
			out.write(b, 0, len);
		}
		in.close();
		out.close();
	}
}
