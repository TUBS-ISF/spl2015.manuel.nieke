package statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class FileSizePlugin extends StatisticsPlugin {

	private static FileSizePlugin instance;

	private FileSizePlugin() {
	};

	private HashMap<String, Long> fileSizes = new HashMap<String, Long>();

	public static FileSizePlugin getInstance() {
		if (instance == null)
			instance = new FileSizePlugin();

		return instance;
	}

	@Override
	public void fileAdded(String path) {
	}

	@Override
	public void fileChanged(String path) {
		if (path != null) {
			File file = new File(path);
			fileSizes.put(path, file.length());
		}
	}

	@Override
	public void fileDeleted(String path) {
		fileSizes.remove(path);
	}

	@Override
	public void printCurrentStats(String outputPath) {
		Long fileSize = (long) 0;
		for (Long size : fileSizes.values()) {
			fileSize += size;
		}
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter("statistics.txt", true)));
			writer.println("Current size: " + fileSize + " bytes");
			writer.close();
		} catch (IOException e) {

		}
	}

}
