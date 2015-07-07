package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.AuthenticationException;

import application.OutputIDContainer;
import application.OutputIDPathContainer;
import application.OutputPathContainer;

/**
 * TODO description
 */
public class FileHosterServer {

	public static String groupFile = "groups.txt";
	public static Map<String, Set<String>> groupsForUser = new HashMap<String, Set<String>>();
	public static Map<String, Set<String>> usersForGroup = new HashMap<String, Set<String>>();
	
	private static Map<String, Set<Integer>> groupIDMap = new HashMap<String, Set<Integer>>();

	private static boolean start() {
		if (!original())
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
	
	public ReturnContainer createNewFile(String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		String user = checkToken(token);
		
		if(user == null)
			throw new AuthenticationException("Token not found!");
		
		if(!groupsForUser.containsKey(user)) 
			throw new AuthenticationException("User not in group!");
		
		Set<String> groups = groupsForUser.get(user);
		if(!groups.contains(group)) 
			throw new AuthenticationException("User not in group!");
		
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
				Set<Integer> accessibleIDs = groupIDMap.get(token);
				accessibleIDs.add(idContainer.id);
			} else {
				Set<Integer> accessibleIDs = new HashSet<Integer>();
				accessibleIDs.add(idContainer.id);
				groupIDMap.put(group, accessibleIDs);
			}
		}
		
		return container;
	}
	
	public InputStream getInputStream(String path, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName == null)
			throw new AuthenticationException("Token not found!");

		if(!groupsForUser.containsKey(userName)) 
			throw new AuthenticationException("User not in group!");
		
		Set<String> groups = groupsForUser.get(userName);
		
		if(!groups.contains(group))
			throw new AuthenticationException("User not in group!");
		
		return getInputStreamImpl(group + "/" + path);
	}

	public InputStream getInputStream(Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
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
		
		if(!ids.contains(ID))
			throw new AuthenticationException("File not accessible for group!");
		
		return getInputStreamImpl(ID);
	}

	public OutputStream getOutputStream(String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName == null)
			throw new AuthenticationException("Token not found!");

		if(!groupsForUser.containsKey(userName)) 
			throw new AuthenticationException("User not in group!");
		
		Set<String> groups = groupsForUser.get(userName);
		
		if(!groups.contains(group))
			throw new AuthenticationException("User not in group!");
		
		return getOutputStreamImpl(group + "/" + name);
	}

	public OutputStream getOutputStream(Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
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
		
		if(!ids.contains(ID))
			throw new AuthenticationException("File not accessible for group!");
		
		return getOutputStreamImpl(ID);
	}

	public void deleteFile(Integer id, String token, String group) throws IOException,
			RemoteException, AuthenticationException {
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
		
		deleteFileImpl(id);
	}

	public void deleteFile(String path, String token, String group) throws IOException,
			RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName == null)
			throw new AuthenticationException("Token not found!");

		if(!groupsForUser.containsKey(userName)) 
			throw new AuthenticationException("User not in group!");
		
		Set<String> groups = groupsForUser.get(userName);
		
		if(!groups.contains(group))
			throw new AuthenticationException("User not in group!");
		
		deleteFileImpl(group + "/" + path);
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
}