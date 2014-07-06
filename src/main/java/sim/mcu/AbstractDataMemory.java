package sim.mcu;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractDataMemory implements DataMemory {

	private List<MemoryListener> listeners = new ArrayList<MemoryListener>();
	
	private boolean eventsEnabled = true;
	
	public boolean isEventsEnabled() {
		return eventsEnabled;
	}

	public void setEventsEnabled(boolean eventsEnabled) {
		this.eventsEnabled = eventsEnabled;
	}

	@Override
	public void addListener(MemoryListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(MemoryListener listener) {
		listeners.remove(listener);
	}
	public void fireMemoryCleared() {
		
		if (eventsEnabled){
			MemoryEvent event = new MemoryEvent(this);
			for (MemoryListener listener:new ArrayList<MemoryListener>(listeners)){
				listener.memoryCleared(event);
			}
		}
	}

	protected void fireAddressValueChanged(int address, byte oldValue, byte newValue) {
		
		if (eventsEnabled){
			MemoryEvent event = new MemoryEvent(this,address,oldValue,newValue);
			for (MemoryListener listener:new ArrayList<MemoryListener>(listeners)){
				listener.addressValueChanged(event);
			}
		}
	}

}
