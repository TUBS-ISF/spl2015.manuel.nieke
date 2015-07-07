package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.naming.AuthenticationException;

/**
 * 
 * This class is used to implement token based authentication and user management
 *
 */

public class FileHosterServer {
	private static Map<String, String> userPasswordMap = new HashMap<String, String>();
	private static Map<String, String> userTokenMap = new HashMap<String, String>();
	private static Map<String, String> tokenUserMap = new HashMap<String, String>();
	private static String userPath = "users.txt";
	private static boolean setUser = false;

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
		original(argsArray);
	}

	private static void printHelpText() {
		original();
		System.out
				.println("\t-u <file>: The file from which users and passwords should be read (default: users.txt)");
	}

	private static boolean start() {
		if (!original())
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

	// Old methods without authentication now throw exception
	public ReturnContainer createNewFile(String name) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	public InputStream getInputStream(String path) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	public InputStream getInputStream(Integer ID) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	public OutputStream getOutputStream(String name) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	public OutputStream getOutputStream(Integer ID) throws IOException,
			RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	public void deleteFile(Integer id) throws IOException, RemoteException,
			AuthenticationException {
		throw new AuthenticationException();
	}

	public void deleteFile(String path) throws IOException, RemoteException,
			AuthenticationException {
		throw new AuthenticationException();
	}

	public String[] listFiles() throws RemoteException, AuthenticationException {
		throw new AuthenticationException();
	}

	public boolean commit(Integer id) throws RemoteException,
			AuthenticationException {
		throw new AuthenticationException();
	}

	public boolean commit(String path) throws RemoteException,
			AuthenticationException {
		throw new AuthenticationException();
	}

	// New access methods with authentication
	public ReturnContainer createNewFile(String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException();
		else
			return createNewFileImpl(name);
	}

	public InputStream getInputStream(String path, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return getInputStreamImpl(path);
	}

	public InputStream getInputStream(Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return getInputStreamImpl(ID);
	}

	public OutputStream getOutputStream(String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return getOutputStreamImpl(name);
	}

	public OutputStream getOutputStream(Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return getOutputStreamImpl(ID);
	}

	public void deleteFile(Integer id, String token) throws IOException,
			RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			deleteFileImpl(id);
	}

	public void deleteFile(String path, String token) throws IOException,
			RemoteException, AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			deleteFileImpl(path);
	}

	public String[] listFiles(String token) throws RemoteException,
			AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return listFilesImpl();
	}

	public boolean commit(Integer id, String token) throws RemoteException,
			AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return commitImpl(id);
	}

	public boolean commit(String path, String token) throws RemoteException,
			AuthenticationException {
		String user = checkToken(token);
		if(user == null)
			throw new AuthenticationException("Token not found!");
		else 
			return commitImpl(path);
	}

	private String checkToken(String token) {
		if (!userTokenMap.containsKey(token))
			return null;
		else
			return userTokenMap.get(token);
	}
}