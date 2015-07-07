package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import javax.naming.AuthenticationException;

/**
 * TODO description
 */
public class FileHosterServer {
	public ReturnContainer createNewFile(String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		ReturnContainer container = original(name, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return container;
	}
	
	public InputStream getInputStream(String path, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		InputStream in = original(path, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return in;
	}
	
	public InputStream getInputStream(Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		InputStream in = original(ID, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return in;
	}
	
	public OutputStream getOutputStream(String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		OutputStream out = original(name, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return out;
	}
	
	public OutputStream getOutputStream(Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException {
		OutputStream out = original(ID, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
		return out;
	}
	
	public void deleteFile(Integer id, String token, String group) throws IOException,
	RemoteException, AuthenticationException {
		original(id, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
	}
	
	public void deleteFile(String path, String token, String group) throws IOException,
	RemoteException, AuthenticationException {
		original(path, token, group);
		String userName = checkToken(token);
		
		appendLog("by user" + userName + " in group " + group);
	}
}