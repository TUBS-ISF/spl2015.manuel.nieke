package statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileCountPlugin extends StatisticsPlugin {

	private static FileCountPlugin instance;

	private FileCountPlugin() {
	};

	private int fileCount = 0;

	public static FileCountPlugin getInstance() {
		if (instance == null)
			instance = new FileCountPlugin();

		return instance;
	}

	@Override
	public void fileAdded(String path) {
		fileCount++;
	}

	@Override
	public void fileChanged(String path) {
	}

	@Override
	public void fileDeleted(String path) {
		fileCount--;
	}

	@Override
	public void printCurrentStats(String outputPath) {
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("statistics.txt", true)));
			writer.println("Current file count: " + fileCount);
			writer.close();
		} catch (IOException e) {

		}
	}

}
