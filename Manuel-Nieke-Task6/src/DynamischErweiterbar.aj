import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import javax.management.openmbean.KeyAlreadyExistsException;

public privileged aspect DynamischErweiterbar {
	public void application.FileHosterServer.addUser(String name,
			String password) throws KeyAlreadyExistsException {
		if (userPasswordMap.containsKey(name))
			throw new KeyAlreadyExistsException("User already exists!");

		// Add user to current map and to user file.
		userPasswordMap.put(name, password);
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(userPath, true)));
			writer.println(name + "\t" + password);
			writer.close();
		} catch (IOException e) {
		}
	}

	// Add empty method implementation to interface (method declaration cannot
	// be sliced?)
	public void application.IFileHosterServer.addUser(String name,
			String password) throws KeyAlreadyExistsException, RemoteException {
	};
}