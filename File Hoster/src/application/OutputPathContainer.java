package application;

import java.io.OutputStream;
import java.io.Serializable;

public class OutputPathContainer extends ReturnContainer implements Serializable {
	public String path;
	
	public OutputPathContainer(String path) {
		super();
		this.path = path;
	}
}
