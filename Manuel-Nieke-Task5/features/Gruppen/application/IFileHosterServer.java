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
	public ReturnContainer createNewFile(String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException;

	public InputStream getInputStream(String path, String token, String group)
			throws IOException, RemoteException, AuthenticationException;

	public InputStream getInputStream(Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException;

	public OutputStream getOutputStream(String name, String token, String group)
			throws IOException, RemoteException, AuthenticationException;

	public OutputStream getOutputStream(Integer ID, String token, String group)
			throws IOException, RemoteException, AuthenticationException;

	public void deleteFile(Integer id, String token, String group)
			throws IOException, RemoteException, AuthenticationException;

	public void deleteFile(String path, String token, String group)
			throws IOException, RemoteException, AuthenticationException;

	public boolean commit(Integer id, String token, String group)
			throws RemoteException, AuthenticationException;

	public boolean commit(String path, String token, String group)
			throws RemoteException, AuthenticationException;
}