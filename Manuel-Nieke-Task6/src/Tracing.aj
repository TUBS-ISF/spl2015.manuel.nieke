import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public aspect Tracing {
	pointcut allCalls(): execution(* *.*(..));

	before(): allCalls() {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("trace.txt", true)));
			writer.println("Calling: "
					+ thisJoinPoint.getSignature().toLongString());
			writer.close();
		} catch (IOException e) {
		}
	}

	after(): allCalls() {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("trace.txt", true)));
			writer.println("Returning: "
					+ thisJoinPoint.getSignature().toLongString());
			writer.close();
		} catch (IOException e) {
		}
	}
}
