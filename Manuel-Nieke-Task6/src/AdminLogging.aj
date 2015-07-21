import application.FileHosterServer;

public privileged aspect AdminLogging {
	pointcut checkExecution(String token, FileHosterServer t): execution(private String checkToken(String )) && args(token) && target(t);

	after(String token, FileHosterServer t): checkExecution(token,t) {
		if (Administratorkonten.aspectOf().adminTokenMap.containsKey(token)) {
			String adminName = Administratorkonten.aspectOf().adminTokenMap.get(token);
			t.appendLog("Admin access by admin " + adminName);
		}
	}
}