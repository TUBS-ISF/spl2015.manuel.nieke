package application;

import java.io.Serializable;

/**
 * A return container if identification via both id and path is selected.
 * @author Manuel Nieke
 *
 */
public class OutputIDPathContainer extends ReturnContainer implements Serializable  {
	public Integer id;
	public String path;
	
	public OutputIDPathContainer(Integer id, String path) {
		super();
		this.id = id;
		this.path = path;
	}
}
