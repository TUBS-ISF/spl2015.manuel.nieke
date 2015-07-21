import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public aspect FehlerhafteAuthentifizierung {
	private int failedAuthCounter = 0;
	
	pointcut authentication(): execution(public String authenticate(..));
	pointcut commit(): execution(public boolean commit(..));
	
	String around(): authentication() {
		// If result is null authentication failed
		String result = proceed();
		
		if(result == null)
			failedAuthCounter++;
		
		return result;
	}
	
	after(): commit() {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("statistics.txt", true)));
			writer.println("Current failed authentications: " + failedAuthCounter);
			writer.close();
		} catch (IOException e) {
		}
	}
	
}