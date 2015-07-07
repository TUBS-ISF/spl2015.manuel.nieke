package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;

import application.OutputIDContainer;
import application.OutputIDPathContainer;
import application.OutputPathContainer;

/**
 * This class adds division of space for multiple users
 */
public class FileHosterServer {

	private static Map<String, List<Integer>> tokenIDMap = new HashMap<String, List<Integer>>();

	public ReturnContainer createNewFile(String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			ReturnContainer container = original(userName + "/" + name, token);
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

	public InputStream getInputStream(String path, String token)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			return original(userName + "/" + path, token);
		} else {
			throw new AuthenticationException("Token not found!");
		}
	}

	public InputStream getInputStream(Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);
		
		if(!tokenIDMap.containsKey(token)) {
			throw new AuthenticationException("Not authorized to access file!");
		} else {
			List<Integer> accessibleIDs = tokenIDMap.get(token);
			if(!accessibleIDs.contains(ID)) {
				throw new AuthenticationException("Not authorized to access file!");
			} else {
				return original(ID, token);
			}
		}
	}

	public OutputStream getOutputStream(String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			return original(userName + "/" + name, token);
		} else {
			throw new AuthenticationException("Token not found!");
		}
	}

	public OutputStream getOutputStream(Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		if(!tokenIDMap.containsKey(token)) {
			throw new AuthenticationException("Not authorized to access file!");
		} else {
			List<Integer> accessibleIDs = tokenIDMap.get(token);
			if(!accessibleIDs.contains(ID)) {
				throw new AuthenticationException("Not authorized to access file!");
			} else {
				return original(ID, token);
			}
		}
	}

	public void deleteFile(Integer id, String token) throws IOException,
			RemoteException, AuthenticationException {
		if(!tokenIDMap.containsKey(token)) {
			throw new AuthenticationException("Not authorized to access file!");
		} else {
			List<Integer> accessibleIDs = tokenIDMap.get(token);
			if(!accessibleIDs.contains(id)) {
				throw new AuthenticationException("Not authorized to access file!");
			} else {
				original(id, token);
			}
		}
	}

	public void deleteFile(String path, String token) throws IOException,
			RemoteException, AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			original(userName + "/" + path, token);
		} else {
			throw new AuthenticationException("Token not found!");
		}
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
				return original(id, token);
			}
		}
	}

	public boolean commit(String path, String token) throws RemoteException,
			AuthenticationException {
		String userName = checkToken(token);

		if (userName != null) {
			return original(userName + "/" + path, token);
		} else {
			throw new AuthenticationException("Token not found!");
		}
	}
}