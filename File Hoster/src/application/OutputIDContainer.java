package application;

import java.io.OutputStream;
import java.io.Serializable;

public class OutputIDContainer extends ReturnContainer implements Serializable {
	public Integer id;
	
	public OutputIDContainer(Integer id) {
		super();
		this.id = id;
	}
	
}