package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import javax.naming.AuthenticationException;

public class FileHosterServer {
	public ReturnContainer createNewFile(String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		ReturnContainer container = original(name, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return container;
	}
	
	public InputStream getInputStream(String path, String token)
			throws IOException, RemoteException, AuthenticationException {
		InputStream in = original(path, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return in;
	}
	
	public InputStream getInputStream(Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		InputStream in = original(ID, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return in;
	}
	
	public OutputStream getOutputStream(String name, String token)
			throws IOException, RemoteException, AuthenticationException {
		OutputStream out = original(name, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return out;
	}
	
	public OutputStream getOutputStream(Integer ID, String token)
			throws IOException, RemoteException, AuthenticationException {
		OutputStream out = original(ID, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
		return out;
	}
	
	public void deleteFile(Integer id, String token) throws IOException,
	RemoteException, AuthenticationException {
		original(id, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
	}
	
	public void deleteFile(String path, String token) throws IOException,
	RemoteException, AuthenticationException {
		original(path, token);
		String userName = checkToken(token);
		
		appendLog("by user" + userName);
	}
}