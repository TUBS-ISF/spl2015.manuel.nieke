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
public  interface  IRMIOutputStream  extends Remote {
	

	public void write(int b) throws IOException, RemoteException;

	

	public void write(byte[] b, int off, int len) throws IOException,
			RemoteException;

	

	public void close() throws IOException, RemoteException;


}
