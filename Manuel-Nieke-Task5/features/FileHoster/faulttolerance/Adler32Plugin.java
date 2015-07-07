package faulttolerance;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;

public class Adler32Plugin extends ChecksumPlugin {

	private static Adler32Plugin instance;

	private HashMap<Integer, Adler32> idToChecksumOut = new HashMap<Integer, Adler32>();
	private HashMap<String, Adler32> pathToChecksumOut = new HashMap<String, Adler32>();

	private HashMap<Integer, Adler32> idToChecksumIn = new HashMap<Integer, Adler32>();
	private HashMap<String, Adler32> pathToChecksumIn = new HashMap<String, Adler32>();

	private Adler32Plugin() {
	};

	@Override
	public CheckedOutputStream getOutputStream(int id, OutputStream out) {
		Adler32 checksum = new Adler32();
		CheckedOutputStream outputStream = new CheckedOutputStream(out,
				checksum);

		idToChecksumOut.put(id, checksum);

		return outputStream;
	}

	@Override
	public CheckedOutputStream getOutputStream(String path, OutputStream out) {
		Adler32 checksum = new Adler32();
		CheckedOutputStream outputStream = new CheckedOutputStream(out,
				checksum);

		pathToChecksumOut.put(path, checksum);

		return outputStream;
	}

	@Override
	public boolean check(int id) {
		Adler32 in, out;
		if (idToChecksumIn.containsKey(id)
				&& idToChecksumOut.containsKey(id)) {
			in = idToChecksumIn.get(id);
			out = idToChecksumOut.get(id);
		} else {
			return true;
		}
		
		System.out.println("Checksum saved: " + out.getValue() + " Now: " + in.getValue());

		return in.getValue() == out.getValue();
	}

	@Override
	public boolean check(String path) {
		Adler32 in, out;
		if (pathToChecksumIn.containsKey(path)
				&& pathToChecksumOut.containsKey(path)) {
			in = pathToChecksumIn.get(path);
			out = pathToChecksumOut.get(path);
		} else {
			return true;
		}
		
		System.out.println("Checksum saved: " + out.getValue() + " Now: " + in.getValue());

		return in.getValue() == out.getValue();
	}

	public static ChecksumPlugin getInstance() {
		if (instance == null)
			instance = new Adler32Plugin();

		return instance;
	}

	@Override
	public CheckedInputStream getInputStream(int id, InputStream in) {
		Adler32 checksum = new Adler32();
		CheckedInputStream inputStream = new CheckedInputStream(in, checksum);

		idToChecksumIn.put(id, checksum);

		return inputStream;
	}

	@Override
	public CheckedInputStream getInputStream(String path, InputStream in) {
		Adler32 checksum = new Adler32();
		CheckedInputStream inputStream = new CheckedInputStream(in, checksum);

		pathToChecksumIn.put(path, checksum);

		return inputStream;
	}

	@Override
	public void delete(int id) {
		idToChecksumIn.remove(id);
		idToChecksumOut.remove(id);
	}

	@Override
	public void delete(String path) {
		pathToChecksumIn.remove(path);
		pathToChecksumOut.remove(path);
	}

}
