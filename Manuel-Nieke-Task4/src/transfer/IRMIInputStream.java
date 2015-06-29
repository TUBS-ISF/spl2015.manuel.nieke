package transfer;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * All files in this package are used in order to enable file transfer via Java-RMI.
 * See http://www.censhare.com/de/insight/uebersicht/artikel/file-streaming-using-java-rmi for
 * an explanation as to why this is necessary.
 *
 */
public interface IRMIInputStream extends Remote {

	public byte[] readBytes(int len) throws IOException, RemoteException;

	public int read() throws IOException, RemoteException;

	public void close() throws IOException, RemoteException;
}
