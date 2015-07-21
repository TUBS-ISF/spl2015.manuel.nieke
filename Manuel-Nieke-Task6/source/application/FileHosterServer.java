package application; 

import java.io.BufferedWriter; 
import java.io.ByteArrayInputStream; 
import java.io.ByteArrayOutputStream; 

import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.PrintWriter; 
import java.net.InetAddress; 
import java.net.UnknownHostException; 
import java.rmi.AccessException; 
import java.rmi.AlreadyBoundException; 
import java.rmi.NotBoundException; 
import java.rmi.RemoteException; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry; 
import java.rmi.server.UnicastRemoteObject; 
import java.util.ArrayList; 
import java.util.Date; 
import java.util.HashMap; 
import java.util.HashSet; 
import java.util.List; 
import java.util.Map; 
import java.util.Map.Entry; 
import java.util.Set; 
import java.util.zip.ZipEntry; 
import java.util.zip.ZipInputStream; 
import java.util.zip.ZipOutputStream; 

import transfer.RMIInputStream; 
import transfer.RMIInputStreamImpl; 
import transfer.RMIOutputStream; 
import transfer.RMIOutputStreamImpl; 
import notification.*; 
import faulttolerance.*; 
import statistics.*; 

import java.io.BufferedReader; 
import java.io.FileReader; 
import java.security.NoSuchAlgorithmException; 
import java.util.Random; 

import javax.naming.AuthenticationException; 

import application.OutputIDContainer; 
import application.OutputIDPathContainer; 
import application.OutputPathContainer; 

import statistics.MemorySizePlugin; 
import statistics.StatisticsPlugin; 
import java.nio.file.Files; 
import java.nio.file.Paths; 

import static java.nio.file.StandardCopyOption.*; 

public   class  FileHosterServer  implements IFileHosterServer {
	

	final public static int BUF_SIZE = 1024 * 64;

	

	private static int serverPort = 5001;

	
	private static int registryPort = 5000;

	
	private int idCounter = 0;

	

	 enum  saveOptionEnum {
		IN_MEMORY ,  HDD}

	;

	

	 enum  identificationOptionEnum {
		ID ,  PATH ,  ID_PATH}

	;

	

	private ChecksumPlugin checksumPlugin;

	
	private List<StatisticsPlugin> statisticPlugins;

	

	private static saveOptionEnum saveOption = null;

	
	private static identificationOptionEnum identificationOption = null;

	

	Registry rmiRegistry;

	

	// Maps to identify files by id or path for in memory saving
	Map<Integer, ByteArrayOutputStream> idFileMap;

	
	Map<String, ByteArrayOutputStream> pathFileMap;

	

	// Maps to identify the filepath for id or path for hdd saving
	Map<Integer, String> idPathMap;

	
	Set<String> filePathSet;

	

	Map<Integer, String> IDToPathMapping;

	

	// #ifdef Benachrichtigung
	// @ Map<INotificationListener, List<String>> watchedFiles = new
//@	// HashMap<INotificationListener, List<String>>();
	// #endif

	 private static void  main__wrappee__FileHoster  (String[] args) {

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
				if (serverPort == 5001) {
					sp = true;
					continue;
				} else {
					System.out.println("-sp selected multiple times!");
					return;
				}
			} else if (arg.equals("-rp")) {
				if (registryPort == 5000) {
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

	

	public static void main(String[] args) {

		List<String> newArgs = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			if (setUser) {
				userPath = args[i];
				setUser = false;
				break;
			} else {
				if (args[i].equals("-u")) {
					setUser = true;
				} else {
					newArgs.add(args[i]);
				}
			}
		}

		if (setUser) {
			System.out.println("No path to user file specified!");
			printHelpText();
		}
		String[] argsArray = new String[newArgs.size()];
		newArgs.toArray(argsArray);
		main__wrappee__FileHoster(argsArray);
	}

	

	/**
	 * This method attempts to create a file hoster server and export it. If
	 * successful it then starts a registry and adds an entry for the server
	 * with the key "FileHosterServer".
	 * 
	 * @return whether the server and registry have been successfully started
	 */
	 private static boolean  start__wrappee__FileHoster() {

		// Create server object and export it
		FileHosterServer server;
		IFileHosterServer stub;
		try {
			server = new FileHosterServer();
			// #ifdef VerschluesselteUebertragung
			// @ System.setProperty("javax.net.ssl.keyStore", "keystore");
			// @ System.setProperty("javax.net.ssl.keyStorePassword", "123456");
			// @ System.setProperty("javax.net.ssl.trustStore", "truststore");
			// @ System.setProperty("javax.net.ssl.trustStorePassword",
//@			// "123456");
			// @
			// @ SslRMIServerSocketFactory sslServerSocketFactory = new
//@			// SslRMIServerSocketFactory();
			// @ SslRMIClientSocketFactory sslClientSocketFactory = new
//@			// SslRMIClientSocketFactory();
			// @
			// @ stub = (IFileHosterServer)
//@			// UnicastRemoteObject.exportObject(server,
			// @ serverPort, sslClientSocketFactory, sslServerSocketFactory);
			// #else
			stub = (IFileHosterServer) UnicastRemoteObject.exportObject(server,
					serverPort);
			// #endif
		} catch (RemoteException e) {
			System.out
					.println("File hoster could not be exported. Try specifying a different port with the -sp paramter.");
			return false;
		}
		// #ifdef VerschluesselteUebertragung
		// @ catch (IOException e) {
		// @ System.out
		// @
//@		// .println("File hoster could not be exported. Try specifying a different port with the -sp paramter.");
		// @ return false;
		// @ }
		// #endif

		// Create registry and add exported server object with key
		// "FileHosterServer"
		// TODO handle external registries
		try {
			server.rmiRegistry = LocateRegistry.createRegistry(registryPort);
			server.rmiRegistry.bind("FileHosterServer", stub);
		} catch (RemoteException e) {
			System.out
					.println("Registry could not be created. Try specifying a different port with the -rp parameter.");
			System.out.println(e.getMessage());
			e.printStackTrace();
			return false;
		} catch (AlreadyBoundException e) {
			try {
				server.rmiRegistry.unbind("FileHosterServer");
				server.rmiRegistry.bind("FileHosterServer", stub);
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

	

	 private static boolean  start__wrappee__Benutzerkonten  () {
		if (!start__wrappee__FileHoster())
			return false;
		else {
			try {
				File file = new File(userPath);
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split("\t");
					String user = tokens[0];
					String password = tokens[1];
					userPasswordMap.put(user, password);
				}

				return true;
			} catch (IOException e) {
				System.out
						.println("Users file could not be read! Check if path has been correctly set.");
				return false;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out
						.println("Error while reading user file. Make sure make sure its formatting is correct (user \t password)");
				return false;
			}
		}
	}

	

	private static boolean start() {
		if (!start__wrappee__Benutzerkonten())
			return false;
		else {
			try {
				// Read user groups from file
				File file = new File(groupFile);
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] users = line.split("\t");
					String group = users[0];
					Set<String> userGroup = new HashSet<String>();
					for (int i = 1; i < users.length; i++) {
						String user = users[i];
						userGroup.add(user);
					}
					
					if(usersForGroup.containsKey(group)) {
						Set<String> userSet = usersForGroup.get(group);
						userSet.addAll(userGroup);
					} else {
						Set<String> userSet = new HashSet<String>();
						userSet.addAll(userGroup);
						usersForGroup.put(group, userSet);
					}

					for (String user : users) {
						
						if (groupsForUser.containsKey(user)) {
							Set<String> groups = groupsForUser.get(user);
							groups.add(group);
						} else {
							Set<String> groups = new HashSet<String>();
							groups.add(group);
							groupsForUser.put(user, groups);
						}
						
					}
				}

				return true;
			} catch (IOException e) {
				System.out
						.println("Group file could not be read! Check if path has been correctly set.");
				return false;
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
				System.out
						.println("Error while reading group file. Make sure make sure its formatting is correct (group \t user \t user \t ...)");
				return false;
			}
		}
	}

	

	/**
	 * Write help text for command line parameters.
	 */
	 private static void  printHelpText__wrappee__FileHoster  () {
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

	

	private static void printHelpText() {
		printHelpText__wrappee__FileHoster();
		System.out
				.println("\t-u <file>: The file from which users and passwords should be read (default: users.txt)");
	}

	

	// Old methods without authentication now throw exception
	public synchronized ReturnContainer createNewFile  (String name) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	

	 private synchronized ReturnContainer  createNewFileImpl__wrappee__FileHoster  (String name)
			throws IOException {
		
		String[] pathTokens = name.split("/");
		String currentPath = "";
		for(int i=0; i < pathTokens.length - 1; i++) {
			currentPath += pathTokens[i];
			File file = new File(currentPath);
			file.mkdir();
		}

		if (checksumPlugin == null) {
			// Comment lines to de-/activate checksum plugins (at most one)
			// checksumPlugin = CRC32Plugin.getInstance();
			checksumPlugin = Adler32Plugin.getInstance();
		}
		if (statisticPlugins == null) {
			statisticPlugins = new ArrayList<StatisticsPlugin>();
			// Comment lines to de-/activate statistics plugins
			statisticPlugins.add(AccessCountPlugin.getInstance());
			statisticPlugins.add(FileCountPlugin.getInstance());
			statisticPlugins.add(FileSizePlugin.getInstance());
			statisticPlugins.add(MemorySizePlugin.getInstance());
		}

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

		// #ifdef Logging
		logFileCreated(name);
		// #endif

		for (StatisticsPlugin plugin : statisticPlugins) {
			plugin.fileAdded(name);
		}

		return container;
	}

	

	 private synchronized ReturnContainer  createNewFileImpl__wrappee__Fehlerauftritte  (String name)
			throws IOException {
		ReturnContainer container = createNewFileImpl__wrappee__FileHoster(name);

		if (!pluginAdded) {
			statisticPlugins.add(new DefectCountPlugin());
			pluginAdded = true;
		}

		return container;
	}

	
	private synchronized ReturnContainer createNewFileImpl(String name)
			throws IOException {
		ReturnContainer container = createNewFileImpl__wrappee__Fehlerauftritte(name);

		File file = new File("redundancy/" + name);
		file.mkdirs();

		Files.copy(Paths.get(name), Paths.get("redundancy/" + name),
				REPLACE_EXISTING);

		return container;
	}

	

	public OutputStream getOutputStream  (String name) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	
	
	private OutputStream getOutputStreamImpl(String name) throws IOException {

		OutputStream outputStream = null;

		if (saveOption == saveOptionEnum.HDD) {
			File file = null;
			// #ifdef Komprimierung
			// @ file = new File(name);
			// @ outputStream = new FileOutputStream(file);
			// @ ZipOutputStream zipOutputStream = new
//@			// ZipOutputStream(outputStream);
			// @ String fileName = name.substring(0, name.length() - 4);
			// @ zipOutputStream.putNextEntry(new ZipEntry(fileName));
			// @ outputStream = zipOutputStream;
			// #else
			file = new File(name);
			outputStream = new FileOutputStream(file);
			// #endif
		} else {
			outputStream = pathFileMap.get(name);
		}

		if (checksumPlugin != null)
			outputStream = checksumPlugin.getOutputStream(name, outputStream);

		outputStream = new RMIOutputStream(
				new RMIOutputStreamImpl(outputStream));

		// #ifdef Logging
		logOutputStream(name);
		// #endif

		// #ifdef Benachrichtigung
		// @ for(INotificationListener listener: watchedFiles.keySet()) {
		// @ List<String> files = watchedFiles.get(listener);
		// @ if(files.contains(name)) {
		// @ listener.notify(name, "Output stream retrieved");
		// @ }
		// @ }
		// #endif

		return outputStream;

	}

	

	public OutputStream getOutputStream  (Integer ID) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	
	
	private OutputStream getOutputStreamImpl(Integer ID) throws IOException {
		OutputStream outputStream = null;

		// Open output stream if file exists on hdd or use saved stream
		if (saveOption == saveOptionEnum.HDD) {
			File file = new File(idPathMap.get(ID));
			outputStream = new FileOutputStream(file);
			// #ifdef Komprimierung
			// @ ZipOutputStream zipOutputStream = new
//@			// ZipOutputStream(outputStream);
			// @ String zipName = idPathMap.get(ID);
			// @ String fileName = zipName.substring(0, zipName.length() - 4);
			// @ zipOutputStream.putNextEntry(new ZipEntry(fileName));
			// @ outputStream = zipOutputStream;
			// #endif
		} else {
			outputStream = idFileMap.get(ID);
		}

		// #ifdef Logging
		logOutputStream(ID.toString());
		// #endif

		// #ifdef Benachrichtigung
		// @ for(INotificationListener listener: watchedFiles.keySet()) {
		// @ List<String> files = watchedFiles.get(listener);
		// @ if(files.contains(ID.toString())) {
		// @ listener.notify(ID.toString(), "Output stream retrieved");
		// @ }
		// @ }
		// #endif

		if (checksumPlugin != null)
			outputStream = checksumPlugin.getOutputStream(ID, outputStream);

		// Create ouput stream for remote usage
		outputStream = new RMIOutputStream(
				new RMIOutputStreamImpl(outputStream));

		return outputStream;
	}

	

	public InputStream getInputStream  (String path) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	
	
	private InputStream getInputStreamImpl(String path) throws IOException {
		InputStream inputStream = null;

		// Open output stream if file exists on hdd or use saved stream
		if (saveOption == saveOptionEnum.HDD) {
			File file = new File(path);
			inputStream = new FileInputStream(file);
			// #ifdef Komprimierung
			// @ ZipInputStream zipInputStream = new
//@			// ZipInputStream(inputStream);
			// @
			// @ // Position inputstream on next (only) entry
			// @ zipInputStream.getNextEntry();
			// @ inputStream = zipInputStream;
			// #endif
		} else {
			ByteArrayOutputStream outputStream = pathFileMap.get(path);
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		}

		if (checksumPlugin != null)
			inputStream = checksumPlugin.getInputStream(path, inputStream);

		// #ifdef Logging
		logInputStream(path);
		// #endif

		// #ifdef Benachrichtigung
		// @ for(INotificationListener listener: watchedFiles.keySet()) {
		// @ List<String> files = watchedFiles.get(listener);
		// @ if(files.contains(path)) {
		// @ listener.notify(path, "Input stream retrieved");
		// @ }
		// @ }
		// #endif

		// Create ouput stream for remote usage
		return new RMIInputStream(new RMIInputStreamImpl(inputStream));
	}

	

	public InputStream getInputStream  (Integer ID) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	

	private InputStream getInputStreamImpl(Integer id) throws IOException {
		InputStream inputStream = null;

		// Open input stream if file exists on hdd or use saved stream
		if (saveOption == saveOptionEnum.HDD) {
			File file = new File(idPathMap.get(id));
			inputStream = new FileInputStream(file);
			// #ifdef Komprimierung
			// @ ZipInputStream zipInputStream = new
//@			// ZipInputStream(inputStream);
			// @
			// @ // Position inputstream on next (only) entry
			// @ zipInputStream.getNextEntry();
			// @ inputStream = zipInputStream;
			// #endif
		} else {
			ByteArrayOutputStream outputStream = idFileMap.get(id);
			inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		}

		if (checksumPlugin != null)
			inputStream = checksumPlugin.getInputStream(id, inputStream);

		// #ifdef Logging
		logInputStream(id.toString());
		// #endif

		// #ifdef Benachrichtigung
		// @ for(INotificationListener listener: watchedFiles.keySet()) {
		// @ List<String> files = watchedFiles.get(listener);
		// @ if(files.contains(id.toString())) {
		// @ listener.notify(id.toString(), "Input stream retrieved");
		// @ }
		// @ }
		// #endif

		// Create input stream for remote usage
		return new RMIInputStream(new RMIInputStreamImpl(inputStream));
	}

	

	public void deleteFile  (Integer id) throws IOException, RemoteException,
			AuthenticationException {
		throw new AuthenticationException();
	}

	

	private void deleteFileImpl(Integer id) throws IOException {
		if (saveOption == saveOptionEnum.HDD) {
			String path = idPathMap.get(id);
			File file = new File(path);
			file.delete();
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.fileDeleted(path);
			}
		} else {
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.fileChanged(null);
			}
			idFileMap.remove(id);
		}

		if (identificationOption == identificationOptionEnum.ID_PATH) {
			String path = IDToPathMapping.get(id);
			IDToPathMapping.remove(id);

			if (saveOption == saveOptionEnum.HDD) {
				filePathSet.remove(path);
			} else {
				pathFileMap.remove(path);
			}
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.fileChanged(path);
			}
		}

		if (checksumPlugin != null)
			checksumPlugin.delete(id);

		// #ifdef Logging
		logFileDeleted(id.toString());
		// #endif

		// #ifdef Benachrichtigung
		// @ for(INotificationListener listener: watchedFiles.keySet()) {
		// @ List<String> files = watchedFiles.get(listener);
		// @ if(files.contains(id.toString())) {
		// @ listener.notify(id.toString(), "File deleted");
		// @ }
		// @ }
		// #endif
	}

	

	public void deleteFile  (String path) throws IOException, RemoteException,
			AuthenticationException {
		throw new AuthenticationException();
	}

	
	
	private void deleteFileImpl(String path) throws IOException {
		if (saveOption == saveOptionEnum.HDD) {
			File file = new File(path);
			file.delete();
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.fileChanged(path);
			}
		} else {
			pathFileMap.remove(path);
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.fileChanged(null);
			}
		}

		if (identificationOption == identificationOptionEnum.ID_PATH) {
			Integer key = null;
			Set<Entry<Integer, String>> entries = IDToPathMapping.entrySet();
			for (Entry<Integer, String> entry : entries) {
				if (entry.getValue().equals(path)) {
					key = (Integer) entry.getKey();
					IDToPathMapping.remove(key);
					break;
				}
			}

			if (saveOption == saveOptionEnum.HDD) {
				idPathMap.remove(key);
				for(StatisticsPlugin plugin : statisticPlugins) {
					plugin.fileChanged(path);
				}
			} else {
				idFileMap.remove(key);
				for(StatisticsPlugin plugin : statisticPlugins) {
					plugin.fileChanged(null);
				}
			}
		}

		if (checksumPlugin != null)
			checksumPlugin.delete(path);

		// #ifdef Logging
		logFileDeleted(path);
		// #endif

		// #ifdef Benachrichtigung
		// @ for(INotificationListener listener: watchedFiles.keySet()) {
		// @ List<String> files = watchedFiles.get(listener);
		// @ if(files.contains(path)) {
		// @ listener.notify(path, "File deleted");
		// @ }
		// @ }
		// #endif
	}

	

	public String[] listFiles  () throws RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	

	private String[] listFilesImpl() {
		String[] array = null;
		List<String> list = new ArrayList<String>();
		switch (identificationOption) {
		case ID:
			if (saveOption == saveOptionEnum.HDD) {
				for (Integer id : idPathMap.keySet()) {
					list.add(id.toString());
				}
			} else {
				for (Integer id : idFileMap.keySet()) {
					list.add(id.toString());
				}
			}
			break;

		case PATH:
			if (saveOption == saveOptionEnum.HDD) {
				for (String path : filePathSet) {
					list.add(path);
				}
			} else {
				for (String path : pathFileMap.keySet()) {
					list.add(path);
				}
			}
			break;

		case ID_PATH:
			for (Integer key : IDToPathMapping.keySet()) {
				list.add(key.toString() + " : " + IDToPathMapping.get(key));
			}
			break;
		}

		array = (String[]) list.toArray();

		return array;
	}

	

	/**
	 * A helper method for creating a new file and linking that file to an ID.
	 * 
	 * @param name
	 *            the name of the new file. If that file already exists it is
	 *            overwritten
	 * @throws IOException
	 *             if something went wrong during file creation
	 */
	private void registerFileID(String name) throws IOException {
		// If file is written on HDD only the path is remembered
		if (saveOption == saveOptionEnum.HDD) {
			File file = null;
			// #ifdef Komprimierung
			// @ file = new File(name + ".zip");
			// #else
			file = new File(name);
			// #endif
			if (idPathMap == null) {
				idPathMap = new HashMap<Integer, String>();
			}
			idPathMap.put(idCounter, file.getPath());
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
		} else {
			// For in memory saving a ByteArrayOutputStream is used instead of
			// file
			if (idFileMap == null) {
				idFileMap = new HashMap<Integer, ByteArrayOutputStream>();
			}
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			idFileMap.put(idCounter, outputStream);
		}
	}

	

	/**
	 * A helper method for creating a new file and linking that file to its
	 * path.
	 * 
	 * @param name
	 *            the name of the new file. If that file already exists it is
	 *            overwritten
	 * @return the "internal" path of the file which is used for retrieval
	 * @throws IOException
	 *             if something went wrong during file creation
	 */
	private String registerFilePath(String name) throws IOException {
		String path = null;
		// If file is written on HDD only the path is remembered
		if (saveOption == saveOptionEnum.HDD) {
			File file = null;
			// #ifdef Komprimierung
			// @ file = new File(name + ".zip");
			// #else
			file = new File(name);
			// #endif
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
			// For in memory saving a ByteArrayOutputStream is used instead of
			// file
			if (pathFileMap == null) {
				pathFileMap = new HashMap<String, ByteArrayOutputStream>();
			}
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			pathFileMap.put(name, outputStream);
			path = name;
		}

		return path;
	}

	

	/**
	 * A helper method for creating a new file and linking that file to its path
	 * and id.
	 * 
	 * @param name
	 *            the name of the new file. If that file already exists it is
	 *            overwritten
	 * @return the "internal" path of the file which is used for retrieval
	 * @throws IOException
	 *             if something went wrong during file creation
	 */
	private String registerFileIDPath(String name) throws IOException {
		String path = null;
		if (IDToPathMapping == null) {
			IDToPathMapping = new HashMap<Integer, String>();
		}
		// If file is written on HDD only the path is remembered
		if (saveOption == saveOptionEnum.HDD) {
			File file = null;
			// #ifdef Komprimierung
			// @ file = new File(name + ".zip");
			// #else
			file = new File(name);
			// #endif
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
			// If file is written on HDD only the path is remembered
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

		IDToPathMapping.put(idCounter, path);
		return path;
	}

	

	/**
	 * A helper method to copy contents of an input stream to an output stream.
	 * 
	 * @param in
	 *            the source input stream
	 * @param out
	 *            the destination output stream
	 * @throws IOException
	 *             if something went wrong during copying
	 */
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

	

	// #ifdef Logging
	private void logInputStream(String identifier) {
		appendLog("Input stream retrieved for: " + identifier);
	}

	

	private void logOutputStream(String identifier) {
		appendLog("Output stream retrieved for: " + identifier);
	}

	

	private void logFileCreated(String name) {
		appendLog("File created: " + name);
	}

	

	private void logFileDeleted(String name) {
		appendLog("File deleted: " + name);
	}

	

	private void appendLog(String message) {
		new File("log").mkdir();
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new FileWriter("log/log.txt", true)));
			Date date = new Date();
			String timestamp = "[" + date.toString() + "] ";
			out.println(timestamp + message);
			out.close();
		} catch (IOException e) {

		}
	}

	

	public boolean commit  (Integer id) throws RemoteException,
			AuthenticationException {
		throw new AuthenticationException();
	}

	
	
	  private boolean  commitImpl__wrappee__FileHoster  (Integer id) {
		
		if(saveOption == saveOptionEnum.HDD) {
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.fileChanged(idPathMap.get(id));
			}
		} else {
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.fileChanged(null);
			}
		}
		
		for(StatisticsPlugin plugin : statisticPlugins) {
			plugin.printCurrentStats("statistics.txt");
		}
		
		if (checksumPlugin != null)
			return checksumPlugin.check(id);
		else
			return true;
	}

	

	 private boolean  commitImpl__wrappee__FehlerLogging  (Integer id) {
		boolean result = commitImpl__wrappee__FileHoster(id);

		if (!result) {
			appendLog("Defect discovered while reading file " + id + ".");
		}

		return result;
	}

	
	
	 private boolean  commitImpl__wrappee__Fehlerauftritte  (Integer id) {
		boolean successful = commitImpl__wrappee__FehlerLogging(id);
		
		if(!successful) {
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.defectFound();
			}

		}
		
		return successful;
	}

	

	private boolean commitImpl(Integer id) {
		String path = idPathMap.get(id);
		if (!commitImpl__wrappee__Fehlerauftritte(id)) {
			// Restore file to last checkpoint
			try {
				Files.copy(Paths.get("redundancy/" + path), Paths.get(path),
						REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Error while writing to redundant file!");
			}
			return false;
		} else {
			// Update checkpoint
			try {
				Files.copy(Paths.get(path), Paths.get("redundancy/" + path),
						REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Error while writing to redundant file!");
			}
			return true;
		}
	}

	

	public boolean commit  (String path) throws RemoteException,
			AuthenticationException {
		throw new AuthenticationException();
	}

	

	  private boolean  commitImpl__wrappee__FileHoster  (String path) {
		
		if(saveOption == saveOptionEnum.HDD) {
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.fileChanged(path);
			}
		} else {
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.fileChanged(null);
			}
		}
		
		if (checksumPlugin != null)
			return checksumPlugin.check(path);
		else
			return true;
	}

	

	 private boolean  commitImpl__wrappee__FehlerLogging  (String path) {
		boolean result = commitImpl__wrappee__FileHoster(path);

		if (!result) {
			appendLog("Defect discovered while reading file " + path + ".");
		}

		return result;
	}

	

	 private boolean  commitImpl__wrappee__Fehlerauftritte  (String path) {
		boolean successful = commitImpl__wrappee__FehlerLogging(path);
		
		if(!successful) {
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.defectFound();
			}

		}
		
		return successful;
	}

	

	private boolean commitImpl(String path) {
		if (!commitImpl__wrappee__Fehlerauftritte(path)) {
			// Restore file to last checkpoint
			try {
				Files.copy(Paths.get("redundancy/" + path), Paths.get(path),
						REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Error while writing to redundant file!");
			}
			return false;
		} else {
			// Update checkpoint
			try {
				Files.copy(Paths.get(path), Paths.get("redundancy/" + path),
						REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Error while writing to redundant file!");
			}

			return true;
		}
	}

	
	private static Map<String, String> userPasswordMap = new HashMap<String, String>();

	
	private static Map<String, String> userTokenMap = new HashMap<String, String>();

	
	private static Map<String, String> tokenUserMap = new HashMap<String, String>();

	
	private static String userPath = "users.txt";

	
	private static boolean setUser = false;

	

	public synchronized String authenticate(String user, String password) {
		// Check if username and password are correct
		if (!userPasswordMap.containsKey(user))
			return null;
		if (!userPasswordMap.get(user).equals(password))
			return null;
		
		if(tokenUserMap.containsKey(user)) {
			return tokenUserMap.get(user);
		}
		String token = createHash(user, password);
		
		// Create new token in case of collision
		while(token != null && userTokenMap.containsKey(token)) {
			token = createHash(user, password);
		}
		
		userTokenMap.put(token, user);
		tokenUserMap.put(user, token);
		
		return token;
	}

	

	private String createHash(String user, String password) {
		// Create and save token using a hash of username, password and current
		// time
		String token = "";
		try {
			Date date = new Date();
			java.security.MessageDigest messageDigest = java.security.MessageDigest
					.getInstance("SHA-256");
			byte[] hashInput = (user + password + date.getTime()).getBytes();
			messageDigest.update(hashInput);

			byte[] hash = messageDigest.digest();

			// Pick 32 random characters for use in hash
			Random rand = new Random();
			for (int i = 0; i < 32; i++) {
				token += "" + hash[rand.nextInt(32)];
			}

			return token;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

	

	// New access methods with authentication
	 private ReturnContainer  createNewFile__wrappee__Benutzerkonten  (String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException();
		else
			return createNewFileImpl(name);
	}

	

	 private ReturnContainer  createNewFile__wrappee__Zugriffsbereiche  (String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			ReturnContainer container = createNewFile__wrappee__Benutzerkonten(userName + "/" + name, token);
			if (container instanceof OutputPathContainer) {
				OutputPathContainer pathContainer = (OutputPathContainer) container;
				pathContainer.path = pathContainer.path.substring(userName
						.length() + 1);
			} else if (container instanceof OutputIDPathContainer) {
				OutputIDPathContainer idPathContainer = (OutputIDPathContainer) container;
				idPathContainer.path = idPathContainer.path.substring(userName
						.length() + 1);

				if (tokenIDMap.containsKey(token)) {
					List<Integer> accessibleIDs = tokenIDMap.get(token);
					accessibleIDs.add(idPathContainer.id);
				} else {
					List<Integer> accessibleIDs = new ArrayList<Integer>();
					accessibleIDs.add(idPathContainer.id);
					tokenIDMap.put(token, accessibleIDs);
				}
			} else {
				OutputIDContainer idContainer = (OutputIDContainer) container;
				if (tokenIDMap.containsKey(token)) {
					List<Integer> accessibleIDs = tokenIDMap.get(token);
					accessibleIDs.add(idContainer.id);
				} else {
					List<Integer> accessibleIDs = new ArrayList<Integer>();
					accessibleIDs.add(idContainer.id);
					tokenIDMap.put(token, accessibleIDs);
				}
			}

			return container;
		} else {
			throw new AuthenticationException("Token not found!");
		}

	}

	
	public ReturnContainer createNewFile(String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		ReturnContainer container = createNewFile__wrappee__Zugriffsbereiche(name, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return container;
	}

	

	 private InputStream  getInputStream__wrappee__Benutzerkonten  (String path, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return getInputStreamImpl(path);
	}

	

	 private InputStream  getInputStream__wrappee__Zugriffsbereiche  (String path, String token)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			return getInputStream__wrappee__Benutzerkonten(userName + "/" + path, token);
		} else {
			throw new AuthenticationException("Token not found!");
		}
	}

	
	
	public InputStream getInputStream(String path, String token)
			throws IOException, RemoteException, AuthenticationException {
		InputStream in = getInputStream__wrappee__Zugriffsbereiche(path, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return in;
	}

	

	 private InputStream  getInputStream__wrappee__Benutzerkonten  (Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return getInputStreamImpl(ID);
	}

	

	 private InputStream  getInputStream__wrappee__Zugriffsbereiche  (Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);
		
		if(!tokenIDMap.containsKey(token)) {
			throw new AuthenticationException("Not authorized to access file!");
		} else {
			List<Integer> accessibleIDs = tokenIDMap.get(token);
			if(!accessibleIDs.contains(ID)) {
				throw new AuthenticationException("Not authorized to access file!");
			} else {
				return getInputStream__wrappee__Benutzerkonten(ID, token);
			}
		}
	}

	
	
	public InputStream getInputStream(Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		InputStream in = getInputStream__wrappee__Zugriffsbereiche(ID, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return in;
	}

	

	 private OutputStream  getOutputStream__wrappee__Benutzerkonten  (String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return getOutputStreamImpl(name);
	}

	

	 private OutputStream  getOutputStream__wrappee__Zugriffsbereiche  (String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			return getOutputStream__wrappee__Benutzerkonten(userName + "/" + name, token);
		} else {
			throw new AuthenticationException("Token not found!");
		}
	}

	
	
	public OutputStream getOutputStream(String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		OutputStream out = getOutputStream__wrappee__Zugriffsbereiche(name, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return out;
	}

	

	 private OutputStream  getOutputStream__wrappee__Benutzerkonten  (Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return getOutputStreamImpl(ID);
	}

	

	 private OutputStream  getOutputStream__wrappee__Zugriffsbereiche  (Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		if(!tokenIDMap.containsKey(token)) {
			throw new AuthenticationException("Not authorized to access file!");
		} else {
			List<Integer> accessibleIDs = tokenIDMap.get(token);
			if(!accessibleIDs.contains(ID)) {
				throw new AuthenticationException("Not authorized to access file!");
			} else {
				return getOutputStream__wrappee__Benutzerkonten(ID, token);
			}
		}
	}

	
	
	public OutputStream getOutputStream(Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		OutputStream out = getOutputStream__wrappee__Zugriffsbereiche(ID, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return out;
	}

	

	 private void  deleteFile__wrappee__Benutzerkonten  (Integer id, String token) throws IOException,
			RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			deleteFileImpl(id);
	}

	

	 private void  deleteFile__wrappee__Zugriffsbereiche  (Integer id, String token) throws IOException,
			RemoteException, AuthenticationException {
		if(!tokenIDMap.containsKey(token)) {
			throw new AuthenticationException("Not authorized to access file!");
		} else {
			List<Integer> accessibleIDs = tokenIDMap.get(token);
			if(!accessibleIDs.contains(id)) {
				throw new AuthenticationException("Not authorized to access file!");
			} else {
				deleteFile__wrappee__Benutzerkonten(id, token);
			}
		}
	}

	
	
	public void deleteFile(Integer id, String token) throws IOException,
	RemoteException, AuthenticationException {
		deleteFile__wrappee__Zugriffsbereiche(id, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
	}

	

	 private void  deleteFile__wrappee__Benutzerkonten  (String path, String token) throws IOException,
			RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			deleteFileImpl(path);
	}

	

	 private void  deleteFile__wrappee__Zugriffsbereiche  (String path, String token) throws IOException,
			RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			deleteFile__wrappee__Benutzerkonten(userName + "/" + path, token);
		} else {
			throw new AuthenticationException("Token not found!");
		}
	}

	
	
	public void deleteFile(String path, String token) throws IOException,
	RemoteException, AuthenticationException {
		deleteFile__wrappee__Zugriffsbereiche(path, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
	}

	

	public String[] listFiles(String token) throws RemoteException,
			AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return listFilesImpl();
	}

	

	 private boolean  commit__wrappee__Benutzerkonten  (Integer id, String token) throws RemoteException,
			AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return commitImpl(id);
	}

	

	public boolean commit(Integer id, String token) throws RemoteException,
			AuthenticationException {
		if(!tokenIDMap.containsKey(token)) {
			throw new AuthenticationException("Not authorized to access file!");
		} else {
			List<Integer> accessibleIDs = tokenIDMap.get(token);
			if(!accessibleIDs.contains(id)) {
				throw new AuthenticationException("Not authorized to access file!");
			} else {
				return commit__wrappee__Benutzerkonten(id, token);
			}
		}
	}

	

	 private boolean  commit__wrappee__Benutzerkonten  (String path, String token) throws RemoteException,
			AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return commitImpl(path);
	}

	

	public boolean commit(String path, String token) throws RemoteException,
			AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			return commit__wrappee__Benutzerkonten(userName + "/" + path, token);
		} else {
			throw new AuthenticationException("Token not found!");
		}
	}

	

	private String checkToken(String token) {
		if (!userTokenMap.containsKey(token))
			return null;
		else
			return userTokenMap.get(token);
	}

	

	private static Map<String, List<Integer>> tokenIDMap = new HashMap<String, List<Integer>>();

	

	public static String groupFile = "groups.txt";

	
	public static Map<String, Set<String>> groupsForUser = new HashMap<String, Set<String>>();

	
	public static Map<String, Set<String>> usersForGroup = new HashMap<String, Set<String>>();

	
	
	private static Map<String, Set<Integer>> groupIDMap = new HashMap<String, Set<Integer>>();

	
	
	 private ReturnContainer  createNewFile__wrappee__Gruppen  (String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		
		if(!checkGroup(name, user, group))
			throw new AuthenticationException("Token not found or user not in group!");
		
		ReturnContainer container = createNewFileImpl(group + "/" + name);
		if (container instanceof OutputPathContainer) {
			OutputPathContainer pathContainer = (OutputPathContainer) container;
			pathContainer.path = pathContainer.path.substring(group
					.length() + 1);
		} else if (container instanceof OutputIDPathContainer) {
			OutputIDPathContainer idPathContainer = (OutputIDPathContainer) container;
			idPathContainer.path = idPathContainer.path.substring(group
					.length() + 1);

			if (groupIDMap.containsKey(group)) {
				Set<Integer> accessibleIDs = groupIDMap.get(group);
				accessibleIDs.add(idPathContainer.id);
			} else {
				Set<Integer> accessibleIDs = new HashSet<Integer>();
				accessibleIDs.add(idPathContainer.id);
				groupIDMap.put(group, accessibleIDs);
			}
		} else {
			OutputIDContainer idContainer = (OutputIDContainer) container;
			if (groupIDMap.containsKey(group)) {
				Set<Integer> accessibleIDs = groupIDMap.get(group);
				accessibleIDs.add(idContainer.id);
			} else {
				Set<Integer> accessibleIDs = new HashSet<Integer>();
				accessibleIDs.add(idContainer.id);
				groupIDMap.put(group, accessibleIDs);
			}
		}
		
		return container;
	}

	
	public ReturnContainer createNewFile(String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		ReturnContainer container = createNewFile__wrappee__Gruppen(name, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return container;
	}

	
	
	 private InputStream  getInputStream__wrappee__Gruppen  (String path, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if(!checkGroup(path, userName, group))
			throw new AuthenticationException("Token not found or user not in group!");
		
		return getInputStreamImpl(group + "/" + path);
	}

	
	
	public InputStream getInputStream(String path, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		InputStream in = getInputStream__wrappee__Gruppen(path, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return in;
	}

	

	 private InputStream  getInputStream__wrappee__Gruppen  (Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		
		if(!checkGroup(ID, userName, group))
			throw new AuthenticationException("Token not found or user not in group!");
		
		return getInputStreamImpl(ID);
	}

	
	
	public InputStream getInputStream(Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		InputStream in = getInputStream__wrappee__Gruppen(ID, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return in;
	}

	

	 private OutputStream  getOutputStream__wrappee__Gruppen  (String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if(!checkGroup(name, userName, group))
			throw new AuthenticationException("Token not found or user not in group!");
		
		return getOutputStreamImpl(group + "/" + name);
	}

	
	
	public OutputStream getOutputStream(String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		OutputStream out = getOutputStream__wrappee__Gruppen(name, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return out;
	}

	

	 private OutputStream  getOutputStream__wrappee__Gruppen  (Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if(!checkGroup(ID, userName, group))
			throw new AuthenticationException("Token not found or user not in group!");
		
		return getOutputStreamImpl(ID);
	}

	
	
	public OutputStream getOutputStream(Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		OutputStream out = getOutputStream__wrappee__Gruppen(ID, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return out;
	}

	

	 private void  deleteFile__wrappee__Gruppen  (Integer id, String token, String group) throws IOException,
			RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if(!checkGroup(id, userName, group))
			throw new AuthenticationException("Token not found or user not in group!");
		
		deleteFileImpl(id);
	}

	
	
	public void deleteFile(Integer id, String token, String group) throws IOException,
	RemoteException, AuthenticationException {
		deleteFile__wrappee__Gruppen(id, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
	}

	

	 private void  deleteFile__wrappee__Gruppen  (String path, String token, String group) throws IOException,
			RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if(!checkGroup(path, userName, group))
			throw new AuthenticationException("Token not found or user not in group!");
		
		deleteFileImpl(group + "/" + path);
	}

	private boolean checkGroup(String path, String user, String group) {
		if(user == null)
			return false;
		
		if(!groupsForUser.containsKey(user)) 
			return false;
		
		Set<String> groups = groupsForUser.get(user);
		if(!groups.contains(group)) 
			return false;
		
		return true;
	}
	
	private boolean checkGroup(Integer id, String user, String group) {
		if (user == null)
			return false;

		if(!groupsForUser.containsKey(user)) 
			return false;
		
		Set<String> groups = groupsForUser.get(user);
		
		if(!groups.contains(group))
			return false;
		
		if(!groupIDMap.containsKey(group))
			return false;
		
		Set<Integer> ids = groupIDMap.get(group);
		
		if(!ids.contains(id))
			return false;
		
		return true;
	}
	
	
	public void deleteFile(String path, String token, String group) throws IOException,
	RemoteException, AuthenticationException {
		deleteFile__wrappee__Gruppen(path, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
	}

	

	public boolean commit(Integer id, String token, String group) throws RemoteException,
			AuthenticationException {
		String userName = checkToken(token);

		if (userName == null)
			throw new AuthenticationException("Token not found!");

		if(!groupsForUser.containsKey(userName)) 
			throw new AuthenticationException("User not in group!");
		
		Set<String> groups = groupsForUser.get(userName);
		
		if(!groups.contains(group))
			throw new AuthenticationException("User not in group!");
		
		if(!groupIDMap.containsKey(group))
			throw new AuthenticationException("File not accessible for group!");
		
		Set<Integer> ids = groupIDMap.get(group);
		
		if(!ids.contains(id))
			throw new AuthenticationException("File not accessible for group!");
		
		return commitImpl(id);
	}

	

	public boolean commit(String path, String token, String group) throws RemoteException,
			AuthenticationException {
		String userName = checkToken(token);

		if (userName == null)
			throw new AuthenticationException("Token not found!");

		if(!groupsForUser.containsKey(userName)) 
			throw new AuthenticationException("User not in group!");
		
		Set<String> groups = groupsForUser.get(userName);
		
		if(!groups.contains(group))
			throw new AuthenticationException("User not in group!");
		
		return commitImpl(group + "/" + path);
	}

	

	// As no new classes can be declared, use inner class
	 

	// As no new classes can be declared, use inner class
	class  DefectCountPlugin  extends StatisticsPlugin {
		
		private int defectCount = 0;

		

		public void fileAdded(String path) {
		}

		

		public void fileChanged(String path) {
		}

		

		public void fileDeleted(String path) {
		}

		

		public void printCurrentStats(String outputPath) {
			try {
				PrintWriter writer = new PrintWriter(new BufferedWriter(
						new FileWriter("statistics.txt", true)));
				writer.println("Current defect count: " + defectCount);
				writer.close();
			} catch (IOException e) {

			}
		}

		

		@Override
		public void defectFound() {
			defectCount++;
		}


	}

	

	private static boolean pluginAdded = false;


}
