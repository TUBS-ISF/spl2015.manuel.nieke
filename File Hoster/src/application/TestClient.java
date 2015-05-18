package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TestClient {

	

	public static void main(String[] args) {
		try {
			Registry registry = LocateRegistry.getRegistry("localhost");
			IFileHosterServer server = (IFileHosterServer) registry
					.lookup("FileHosterServer");
			File testFile = new File("client/test.txt");
			new File("client").mkdir();

			PrintWriter writer = new PrintWriter(testFile, "UTF-8");
			writer.println("The first line");
			writer.println("The second line");
			writer.close();
			testFile.createNewFile();

			ReturnContainer container = server.createNewFile("test.txt");
			OutputStream outputStream = null;

			if (container instanceof OutputPathContainer) {
				outputStream = server
						.getOutputStream(((OutputPathContainer) container).path);
			} else if (container instanceof OutputIDContainer) {
				outputStream = server
						.getOutputStream(((OutputIDContainer) container).id);
			}

			FileHosterServer.copy(new FileInputStream(testFile), outputStream);
			InputStream inputStream = null;
			if (container instanceof OutputPathContainer) {
				inputStream = server
						.getInputStream(((OutputPathContainer) container).path);
			} else if (container instanceof OutputIDContainer) {
				inputStream = server
						.getInputStream(((OutputIDContainer) container).id);
			}

			File returnedFile = new File("client/returned.txt");
			FileHosterServer.copy(inputStream, new FileOutputStream(returnedFile));
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

}
