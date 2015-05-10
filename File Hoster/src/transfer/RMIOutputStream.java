package transfer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public class RMIOutputStream extends OutputStream implements Serializable {

	IRMIOutputStream out;

	public RMIOutputStream(IRMIOutputStream out) {
		this.out = out;
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	public void close() throws IOException {
		out.close();
	}

}
