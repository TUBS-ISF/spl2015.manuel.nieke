package application; 

import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileOutputStream; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.PrintWriter; 
import java.rmi.registry.LocateRegistry; 
import java.rmi.registry.Registry; 

import application.IFileHosterServer; 
import notification.INotificationListener; 
import notification.NotificationListenerImpl; 

public  class  TestClient {
	

	public static void main(String[] args) {
		try {
			System.setProperty("javax.net.ssl.keyStore", "keystore");
			System.setProperty("javax.net.ssl.keyStorePassword", "123456");
			System.setProperty("javax.net.ssl.trustStore", "truststore");
			System.setProperty("javax.net.ssl.trustStorePassword", "123456");

			Registry registry = LocateRegistry.getRegistry("localhost", 5000);
			IFileHosterServer server = (IFileHosterServer) registry
					.lookup("FileHosterServer");
			File testFile = new File("client/test.txt");
			new File("client").mkdir();

			// #ifdef Benachrichtigung
			// @ INotificationListener listener = new
			// NotificationListenerImpl(6000);
			// @ server.registerListener(listener, 0);
			// #endif

			PrintWriter writer = new PrintWriter(testFile, "UTF-8");
			writer.println("The first line");
			writer.println("The second line");
			writer.close();
			testFile.createNewFile();

			String token = server
					.authenticate("exampleAdmin", "examplePassword");

			ReturnContainer container = server.createNewFile("test.txt", token);
			OutputStream outputStream = null;

			if (container instanceof OutputPathContainer) {
				outputStream = server.getOutputStream(
						((OutputPathContainer) container).path, token);
			} else if (container instanceof OutputIDContainer) {
				outputStream = server.getOutputStream(
						((OutputIDContainer) container).id, token);
			} else if (container instanceof OutputIDPathContainer) {
				outputStream = server.getOutputStream(
						((OutputIDPathContainer) container).id, token);
			}

			FileHosterServer.copy(new FileInputStream(testFile), outputStream);
			InputStream inputStream = null;
			if (container instanceof OutputPathContainer) {
				inputStream = server.getInputStream(
						((OutputPathContainer) container).path, token);
			} else if (container instanceof OutputIDContainer) {
				inputStream = server.getInputStream(
						((OutputIDContainer) container).id, token);
			} else if (container instanceof OutputIDPathContainer) {
				inputStream = server.getInputStream(
						((OutputIDPathContainer) container).id, token);
			}

			File returnedFile = new File("client/returned.txt");
			FileHosterServer.copy(inputStream, new FileOutputStream(
					returnedFile));

			if (container instanceof OutputPathContainer) {
				System.out.println(server.commit(
						((OutputPathContainer) container).path, token));
			} else if (container instanceof OutputIDContainer) {
				System.out.println(server.commit(
						((OutputIDContainer) container).id, token));
			} else if (container instanceof OutputIDPathContainer) {
				System.out.println(server.commit(
						((OutputIDPathContainer) container).id, token));
			}
			
			token = server
					.authenticate("exampleUser2", "examplePassword2");

			container = server.createNewFile("test.txt", token,
					"exampleGroup2");
			
			token = server
					.authenticate("exampleAdmin", "examplePassword");

			if (container instanceof OutputPathContainer) {
				server.getOutputStream(((OutputPathContainer) container).path,
						token, "exampleGroup2");
			} else if (container instanceof OutputIDContainer) {
				server.getOutputStream(((OutputIDContainer) container).id,
						token, "exampleGroup2");
			} else if (container instanceof OutputIDPathContainer) {
				server.getOutputStream(((OutputIDPathContainer) container).id,
						token, "exampleGroup2");
			}
			
			// putting this in should throw an exception
			/*
			if (container instanceof OutputPathContainer) {
				server.getOutputStream(((OutputPathContainer) container).path,
						token);
			} else if (container instanceof OutputIDContainer) {
				server.getOutputStream(((OutputIDContainer) container).id,
						token);
			} else if (container instanceof OutputIDPathContainer) {
				server.getOutputStream(((OutputIDPathContainer) container).id,
						token);
			}*/
			
			//server.addUser("exampleUser5", "examplePassword5");

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}


}
