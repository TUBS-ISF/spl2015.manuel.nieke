import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import application.FileHosterServer;

public privileged aspect GruppenErweiterbar {
	public void FileHosterServer.addUserToGroup(String user, String group) {
		// Read user groups from file
		if (usersForGroup.containsKey(group)) {
			Set<String> userSet = usersForGroup.get(group);
			userSet.add(user);
		} else {
			Set<String> userSet = new HashSet<String>();
			userSet.add(user);
			usersForGroup.put(group, userSet);
		}
		
		// Add group to group file
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(groupFile, true)));
			writer.println(group + "\t" + user);
			writer.close();
		} catch (IOException e) {
		}

	}

	public void application.IFileHosterServer.addUserToGroup(String name,
			String group) throws RemoteException {
	};
}