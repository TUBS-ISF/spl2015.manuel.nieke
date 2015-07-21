package application; 

import java.io.Serializable; 
/**
 * A return container if identification via path is selected.
 * @author Manuel Nieke
 *
 */
public  class  OutputPathContainer  extends ReturnContainer  implements Serializable {
	
	public String path;

	
	
	public OutputPathContainer(String path) {
		super();
		this.path = path;
	}


}
