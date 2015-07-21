import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import application.FileHosterServer;

public aspect Antwortzeit {
	// Pointcut to get only "outermost" methods
	pointcut allCalls(): 
		execution(public * FileHosterServer.*(..)) && !execution(* FileHosterServer.*wrappee*(..));
	pointcut commit(): execution(* FileHosterServer.commit(..));
	
	List<Long> callDurations = new ArrayList<Long>();
	
	Object around(): allCalls() {
		Long startTime = new Date().getTime();
		
		Object result = proceed();
		
		Long duration = new Date().getTime() - startTime;
		callDurations.add(duration);
		
		return result;
	}
	
	after(): commit() {
		Long totalTime = 0L;
		for(Long duration: callDurations) {
			totalTime += duration;
		}
		
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("statistics.txt", true)));
			writer.println("Average method execution time: " + totalTime / callDurations.size() + "ms");
			writer.close();
		} catch (IOException e) {
		}
	}
	
	
}