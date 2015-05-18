package application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.Remote;

public interface IFileHosterServer extends Remote {
	ReturnContainer createNewFile(String name) throws IOException;
	InputStream getInputStream(String path) throws IOException;
	InputStream getInputStream(Integer ID) throws IOException;
	OutputStream getOutputStream(String name) throws IOException;
	OutputStream getOutputStream(Integer ID) throws IOException;
}
