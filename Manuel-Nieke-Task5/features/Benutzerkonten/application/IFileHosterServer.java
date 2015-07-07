package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import javax.naming.AuthenticationException;

/**
 * TODO description
 */
public interface IFileHosterServer {
	// New method for authentication
	public String authenticate(String user, String password) throws RemoteException;
	
	// New methods for authenticated access
	ReturnContainer createNewFile(String name, String token) throws IOException,RemoteException, AuthenticationException;
	
	InputStream getInputStream(String path, String token) throws IOException,RemoteException, AuthenticationException;
	
	InputStream getInputStream(Integer ID, String token) throws IOException,RemoteException, AuthenticationException;
	
	OutputStream getOutputStream(String name, String token) throws IOException,RemoteException, AuthenticationException;
	
	OutputStream getOutputStream(Integer ID, String token) throws IOException,RemoteException, AuthenticationException;
	
	void deleteFile(Integer id, String token) throws IOException,RemoteException, AuthenticationException;
	
	void deleteFile(String path, String token) throws IOException,RemoteException, AuthenticationException;
	String[] listFiles(String token) throws RemoteException, AuthenticationException;
	
	boolean commit(Integer id, String token) throws RemoteException, AuthenticationException;
	boolean commit(String path, String token) throws RemoteException, AuthenticationException;
	
	// Old methods throw authentication exception
	ReturnContainer createNewFile(String name) throws IOException,RemoteException, AuthenticationException;
	
	InputStream getInputStream(String path) throws IOException,RemoteException, AuthenticationException;
	
	InputStream getInputStream(Integer ID) throws IOException,RemoteException, AuthenticationException;
	
	OutputStream getOutputStream(String name) throws IOException,RemoteException, AuthenticationException;
	
	OutputStream getOutputStream(Integer ID) throws IOException,RemoteException, AuthenticationException;
	
	void deleteFile(Integer id) throws IOException,RemoteException, AuthenticationException;
	
	void deleteFile(String path) throws IOException,RemoteException, AuthenticationException;
	String[] listFiles() throws RemoteException, AuthenticationException;
	
	boolean commit(Integer id) throws RemoteException, AuthenticationException;
	boolean commit(String path) throws RemoteException, AuthenticationException;
	
}