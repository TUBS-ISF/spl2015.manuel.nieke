import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import application.FileHosterServer;

public aspect BenutzerAenderung {

	pointcut userChange(String user): execution(* FileHosterServer.addUser(..)) && args(user,*);
	pointcut groupChange(String user, String group) : 
		execution(* FileHosterServer.addUserToGroup(..)) && args(user, group);
	
	after(String user): userChange(user) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("log/log.txt", true)));
			writer.println("User added: " + user);
			writer.close();
		} catch (IOException e) {
		}
	}
	
	after(String user, String group): groupChange(user, group) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("log/log.txt", true)));
			writer.println("User " + user + " added to group " + group);
			writer.close();
		} catch (IOException e) {
		}
	}
}