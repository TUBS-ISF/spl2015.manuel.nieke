package statistics; 

import java.io.BufferedWriter; 
import java.io.File; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.PrintWriter; 

public  class  AccessCountPlugin  extends StatisticsPlugin {
	

	private static AccessCountPlugin instance;

	

	private AccessCountPlugin() {
	}

	;

	

	private int accessCount = 0;

	

	public static AccessCountPlugin getInstance() {
		if (instance == null)
			instance = new AccessCountPlugin();

		return instance;
	}

	

	@Override
	public void fileAdded(String path) {
	}

	

	@Override
	public void fileChanged(String path) {
		accessCount++;
	}

	

	@Override
	public void fileDeleted(String path) {
	}

	

	@Override
	public void printCurrentStats(String outputPath) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("statistics.txt", true)));
			writer.println("Current access count: " + accessCount);
			writer.close();
		} catch (IOException e) {

		}
	}


}
