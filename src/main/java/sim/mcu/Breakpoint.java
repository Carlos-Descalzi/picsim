package sim.mcu;

import java.io.Serializable;

public interface Breakpoint 
	extends Serializable{

	public boolean isEnabled();
	public int getPc();
	
}