import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import application.FileHosterServer;


public privileged aspect Administratorkonten {
	HashMap<String, String> adminPasswordMap = new HashMap<String, String>();
	HashMap<String, String> adminTokenMap = new HashMap<String, String>();
	HashMap<String, String> tokenAdminMap = new HashMap<String, String>();

	pointcut startExecution(): execution (private static boolean application.FileHosterServer.start());
	pointcut authentication(String user, String password, FileHosterServer t): 
		execution (public String application.FileHosterServer.authenticate(String, String)) 
		&& args(user, password) && target(t);
	pointcut tokenCheck(String token) : execution(private String checkToken(String)) && args(token);
	pointcut groupCheck(String user) : execution(private boolean FileHosterServer.checkGroup(..)) && args(*,user,*);

	after(): startExecution() {
		try {
			File file = new File("admins.txt");
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\t");
				String user = tokens[0];
				String password = tokens[1];
				adminPasswordMap.put(user,
						password);
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Error while reading admin accounts.");
		} catch (ArrayIndexOutOfBoundsException e) {
		}
	}

	String around(String user, String password, FileHosterServer t): authentication(user, password, t) {
		// Check if username and password are correct
		if (!adminPasswordMap.containsKey(user))
			return proceed(user, password, t);
		if (!adminPasswordMap.get(user).equals(password))
			return null;

		if (tokenAdminMap.containsKey(user)) {
			return tokenAdminMap.get(user);
		}
		String token = t.createHash(user, password);

		// Create new token in case of collision
		while (token != null && adminTokenMap.containsKey(token)) {
			token = t.createHash(user, password);
		}

		adminTokenMap.put(token, user);
		tokenAdminMap.put(user, token);

		return token;
	}
	
	
	String around(String token): tokenCheck(token) {
		// Skip file if user is owner of file if is admin
		if (!adminTokenMap.containsKey(token))
			return proceed(token);
		else
			return adminTokenMap.get(token);
	}
	
	
	boolean around(String user): groupCheck(user) {
		// Skip check if user is in group if is admin
		if (!tokenAdminMap.containsKey(user))
			return proceed(user);
		else
			return true;
	}
}