package sim.mcu;

import java.util.EventListener;

public interface MemoryListener 
	extends EventListener{

	public void addressValueChanged(MemoryEvent event);
	public void memoryCleared(MemoryEvent event);
}
