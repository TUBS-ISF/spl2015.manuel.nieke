package statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MemorySizePlugin extends StatisticsPlugin {

	private static MemorySizePlugin instance;

	private MemorySizePlugin() {
	};

	public static MemorySizePlugin getInstance() {
		if (instance == null)
			instance = new MemorySizePlugin();

		return instance;
	}

	@Override
	public void fileAdded(String path) {
	}

	@Override
	public void fileChanged(String path) {
	}

	@Override
	public void fileDeleted(String path) {
	}

	@Override
	public void printCurrentStats(String outputPath) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("statistics.txt", true)));
			writer.println("Current memory usage: "
					+ Runtime.getRuntime().totalMemory() + " bytes");
			writer.close();
		} catch (IOException e) {

		}
	}

}
