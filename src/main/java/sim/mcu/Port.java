package sim.mcu;


/**
 * Represents an IO port pin.
 * 
 * @author Carlos E. Descalzi
 */
public interface Port {

	/**
	 * Port direction.
	 */
	public enum Type {
		INPUT,
		OUTPUT;
	}
	
	public String getName();

	public Type getType();

	public void addOutputListener(PortListener listener);
	
	public void removeOutputListener(PortListener listener);
	
	public void setState(boolean value);
	
	public boolean getState();

	public void toggle();
}
