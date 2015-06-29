package faulttolerance;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;

public class CRC32Plugin extends ChecksumPlugin {

	private static CRC32Plugin instance;

	private HashMap<Integer, CRC32> idToChecksumOut = new HashMap<Integer, CRC32>();
	private HashMap<String, CRC32> pathToChecksumOut = new HashMap<String, CRC32>();

	private HashMap<Integer, CRC32> idToChecksumIn = new HashMap<Integer, CRC32>();
	private HashMap<String, CRC32> pathToChecksumIn = new HashMap<String, CRC32>();

	private CRC32Plugin() {
	};

	@Override
	public CheckedOutputStream getOutputStream(int id, OutputStream out) {
		CRC32 checksum = new CRC32();
		CheckedOutputStream outputStream = new CheckedOutputStream(out,
				checksum);

		idToChecksumOut.put(id, checksum);

		return outputStream;
	}

	@Override
	public CheckedOutputStream getOutputStream(String path, OutputStream out) {
		CRC32 checksum = new CRC32();
		CheckedOutputStream outputStream = new CheckedOutputStream(out,
				checksum);

		pathToChecksumOut.put(path, checksum);

		return outputStream;
	}

	@Override
	public boolean check(int id) {
		CRC32 in, out;
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
		CRC32 in, out;
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
			instance = new CRC32Plugin();

		return instance;
	}

	@Override
	public CheckedInputStream getInputStream(int id, InputStream in) {
		CRC32 checksum = new CRC32();
		CheckedInputStream inputStream = new CheckedInputStream(in, checksum);

		idToChecksumIn.put(id, checksum);

		return inputStream;
	}

	@Override
	public CheckedInputStream getInputStream(String path, InputStream in) {
		CRC32 checksum = new CRC32();
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
