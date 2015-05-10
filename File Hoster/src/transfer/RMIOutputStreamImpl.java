package transfer;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIOutputStreamImpl implements IRMIOutputStream {

	private OutputStream out;

	public RMIOutputStreamImpl(OutputStream out) throws IOException {
		this.out = out;
		UnicastRemoteObject.exportObject(this, 1099);
	}

	public RMIOutputStreamImpl(OutputStream out, int port) throws IOException {
		this.out = out;
		UnicastRemoteObject.exportObject(this, port);
	}

	public void write(int b) throws IOException, RemoteException {
		out.write(b);

	}

	public void write(byte[] b, int off, int len) throws IOException,
			RemoteException {
		out.write(b, off, len);
	}

	public void close() throws IOException, RemoteException {
		out.close();
	}

}
