package application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import statistics.MemorySizePlugin;
import statistics.StatisticsPlugin;

/**
 * TODO description
 */
public class FileHosterServer {

	// As no new classes can be declared, use inner class
	class DefectCountPlugin extends StatisticsPlugin {
		private int defectCount = 0;

		public void fileAdded(String path) {
		}

		public void fileChanged(String path) {
		}

		public void fileDeleted(String path) {
		}

		public void printCurrentStats(String outputPath) {
			try {
				PrintWriter writer = new PrintWriter(new BufferedWriter(
						new FileWriter("statistics.txt", true)));
				writer.println("Current defect count: " + defectCount);
				writer.close();
			} catch (IOException e) {

			}
		}

		@Override
		public void defectFound() {
			defectCount++;
		}
	}

	private static boolean pluginAdded = false;

	private synchronized ReturnContainer createNewFileImpl(String name)
			throws IOException {
		ReturnContainer container = original(name);

		if (!pluginAdded) {
			statisticPlugins.add(new DefectCountPlugin());
			pluginAdded = true;
		}

		return container;
	}

	public boolean commitImpl(String path) {
		boolean successful = original(path);
		
		if(!successful) {
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.defectFound();
			}

		}
		
		return successful;
	}
	
	public boolean commitImpl(Integer id) {
		boolean successful = original(id);
		
		if(!successful) {
			for(StatisticsPlugin plugin : statisticPlugins) {
				plugin.defectFound();
			}

		}
		
		return successful;
	}
}