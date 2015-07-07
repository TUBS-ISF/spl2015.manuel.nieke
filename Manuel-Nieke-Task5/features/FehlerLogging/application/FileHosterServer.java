package application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileHosterServer {

	public boolean commitImpl(String path) {
		boolean result = original(path);

		if (!result) {
			appendLog("Defect discovered while reading file " + path + ".");
		}

		return result;
	}

	public boolean commitImpl(Integer id) {
		boolean result = original(id);

		if (!result) {
			appendLog("Defect discovered while reading file " + id + ".");
		}

		return result;
	}
}