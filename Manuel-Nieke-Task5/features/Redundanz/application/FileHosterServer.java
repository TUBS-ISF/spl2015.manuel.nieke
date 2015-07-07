package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.*;

public class FileHosterServer {
	private synchronized ReturnContainer createNewFileImpl(String name)
			throws IOException {
		ReturnContainer container = original(name);

		File file = new File("redundancy/" + name);
		file.mkdirs();

		Files.copy(Paths.get(name), Paths.get("redundancy/" + name),
				REPLACE_EXISTING);

		return container;
	}

	private boolean commitImpl(String path) {
		if (!original(path)) {
			// Restore file to last checkpoint
			try {
				Files.copy(Paths.get("redundancy/" + path), Paths.get(path),
						REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Error while writing to redundant file!");
			}
			return false;
		} else {
			// Update checkpoint
			try {
				Files.copy(Paths.get(path), Paths.get("redundancy/" + path),
						REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Error while writing to redundant file!");
			}

			return true;
		}
	}

	private boolean commitImpl(Integer id) {
		String path = idPathMap.get(id);
		if (!original(id)) {
			// Restore file to last checkpoint
			try {
				Files.copy(Paths.get("redundancy/" + path), Paths.get(path),
						REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Error while writing to redundant file!");
			}
			return false;
		} else {
			// Update checkpoint
			try {
				Files.copy(Paths.get(path), Paths.get("redundancy/" + path),
						REPLACE_EXISTING);
			} catch (IOException e) {
				System.out.println("Error while writing to redundant file!");
			}
			return true;
		}
	}
	
}