package faulttolerance;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;

public abstract class ChecksumPlugin {
	public abstract CheckedInputStream getInputStream(int id, InputStream in);
	public abstract CheckedInputStream getInputStream(String path, InputStream in);
	
	public abstract CheckedOutputStream getOutputStream(int id, OutputStream out);
	public abstract CheckedOutputStream getOutputStream(String path, OutputStream out);
	
	public abstract boolean check(int id);
	public abstract boolean check(String path);
	
	public abstract void delete(int id);
	public abstract void delete(String path);
}
