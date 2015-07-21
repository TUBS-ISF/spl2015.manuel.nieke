import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import application.FileHosterServer;
public privileged aspect Gruppengroesse {
	
	pointcut commit(): execution(* FileHosterServer.commit(..));
	
	after(): commit() {
		int groupCount = 0, userCount = 0;
		
		for(Set<String> groupMembers : FileHosterServer.groupsForUser.values()) {
			groupCount++;
			userCount += groupMembers.size();
		}
		
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("statistics.txt", true)));
			writer.println("Current number of groups: " + groupCount);
			writer.println("Average size per group: " + (userCount*1.0) / groupCount);
			writer.close();
		} catch (IOException e) {
		}
	}
}