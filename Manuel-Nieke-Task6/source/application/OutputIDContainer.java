package application; 

import java.io.Serializable; 

/**
 * A return container if identification via ID is selected.
 * @author Manuel Nieke
 *
 */
public  class  OutputIDContainer  extends ReturnContainer  implements Serializable {
	
	public Integer id;

	
	
	public OutputIDContainer(Integer id) {
		super();
		this.id = id;
	}


}
