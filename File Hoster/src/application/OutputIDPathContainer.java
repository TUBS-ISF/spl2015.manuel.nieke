package application;

import java.io.OutputStream;
import java.io.Serializable;

public class OutputIDPathContainer extends ReturnContainer implements Serializable  {
	public Integer id;
	public String path;
	
	public OutputIDPathContainer(Integer id, String path, OutputStream outputStream) {
		super();
		this.id = id;
		this.path = path;
		this.outputStream = outputStream;
	}
}
