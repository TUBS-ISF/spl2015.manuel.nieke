import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import application.FileHosterServer;

public privileged aspect Benutzeranzahl {
	
	pointcut commit() : execution(* FileHosterServer.commit(..));
	
	after(): commit() {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("statistics.txt", true)));
			writer.println("Currently registered users: " + (FileHosterServer.userPasswordMap.size()
					+ Administratorkonten.aspectOf().adminPasswordMap.size()));
			writer.close();
		} catch (IOException e) {
		}
	}
}