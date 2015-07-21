package statistics; 

/**
 * TODO description
 */
public abstract   class  StatisticsPlugin {
	
	public abstract void fileAdded(String path);

	
	public abstract void fileChanged(String path);

	
	public abstract void fileDeleted(String path);

	
	public abstract void printCurrentStats(String outputPath);

	
	public void defectFound() {}


}
