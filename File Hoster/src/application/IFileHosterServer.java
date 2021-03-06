package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;

import notification.INotificationListener;

public interface IFileHosterServer extends Remote {
	/**
	 * This method is called to create a new file on the server. It needs to be called in order to
	 * upload content.
	 * @param name the name for the newly created file
	 * @return depending on the configuration either the path, the id or both
	 * @throws IOException if something went wrong during file creation
	 */
	ReturnContainer createNewFile(String name) throws IOException,RemoteException;
	
	/**
	 * Gets the input stream for the file with the given path.
	 * @param path the path to the file that should be retrieved
	 * @return an input stream for retrieving data from the uploaded file
	 * @throws IOException if something went wring during reading
	 */
	InputStream getInputStream(String path) throws IOException,RemoteException;
	
	/**
	 * Gets the input stream for the file with the given id.
	 * @param path the path to the file that should be retrieved
	 * @return an input stream for retrieving data from the uploaded file
	 * @throws IOException if something went wring during reading
	 */
	InputStream getInputStream(Integer ID) throws IOException,RemoteException;
	
	/**
	 * Gets the output stream for the file with the given path.
	 * @param name the path to the file that should be uploaded
	 * @return an output stream for sending data to the server
	 * @throws IOException if something went wring during writing
	 */
	OutputStream getOutputStream(String name) throws IOException,RemoteException;
	
	/**
	 * Gets the output stream for the file with the given id.
	 * @param name the path to the file that should be uploaded
	 * @return an output stream for sending data to the server
	 * @throws IOException if something went wring during writing
	 */
	OutputStream getOutputStream(Integer ID) throws IOException,RemoteException;
	
	/**
	 * Deletes the file identified by the id.
	 * @param id the id of the file to be deleted.
	 * @throws IOException if something went wrong during deletion.
	 */
	void deleteFile(Integer id) throws IOException,RemoteException;
	
	/**
	 * Deletes the file identified by the path.
	 * @param path the path of the file to be deleted.
	 * @throws IOException if something went wrong during deletion.
	 */
	void deleteFile(String path) throws IOException,RemoteException;
	
	/**
	 * Lists all saved files.
	 * @return an array containing either ids, paths or id/path pairs.
	 */
	String[] listFiles() throws RemoteException;
	
	boolean commit(Integer id) throws RemoteException;
	boolean commit(String path) throws RemoteException;
	
	//#ifdef Benachrichtigung
//@	public void registerListener(INotificationListener listener, Integer id) throws RemoteException;
//@	
//@	public void registerListener(INotificationListener listener, String file) throws RemoteException;
	//#endif
}
