package transfer; 

import java.io.IOException; 
import java.io.InputStream; 
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject; 

/**
 * All files in this package are used in order to enable file transfer via Java-RMI.
 * See http://www.censhare.com/de/insight/uebersicht/artikel/file-streaming-using-java-rmi for
 * an explanation as to why this is necessary.
 *
 */
public  class  RMIInputStreamImpl  implements IRMIInputStream {
	
	private InputStream in;

	
	private byte[] b;

	

	public RMIInputStreamImpl(InputStream in) throws IOException {
		this.in = in;
		UnicastRemoteObject.exportObject(this, 1099);
	}

	

	public RMIInputStreamImpl(InputStream in, int port) throws IOException {
		this.in = in;
		UnicastRemoteObject.exportObject(this, port);
	}

	

	public byte[] readBytes(int len) throws IOException, RemoteException {
		if(b == null || b.length != len)
			b = new byte[len];
		
		int len2 = in.read(b);
		if(len2 < 0) 
			return null;
		
		if(len2 != len) {
			byte[] b2 = new byte[len2];
			System.arraycopy(b,  0,  b2,  0,  len2);
			return b2;
		} else
			return b;
	}

	

	public int read() throws IOException, RemoteException {
		return in.read();
	}

	

	public void close() throws IOException, RemoteException {
		in.close();
	}


}
