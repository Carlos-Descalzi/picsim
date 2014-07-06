package sim.mcu;

import java.util.ArrayList;
import java.util.List;


public abstract class AbstractProgramMemory implements ProgramMemory {

	private List<ProgramMemoryListener> listeners = new ArrayList<ProgramMemoryListener>();
	
	private boolean eventsEnabled = true;
	
	public boolean isEventsEnabled() {
		return eventsEnabled;
	}

	public void setEventsEnabled(boolean eventsEnabled) {
		this.eventsEnabled = eventsEnabled;
	}

	public void addListener(ProgramMemoryListener listener){
		listeners.add(listener);
	}
	
	public void removeListener(ProgramMemoryListener listener){
		listeners.remove(listener);
	}
	
	protected void fireMemoryChanged(){
		if (eventsEnabled){
			ProgramMemoryEvent event = new ProgramMemoryEvent(this);
			for (ProgramMemoryListener listener:listeners){
				listener.memoryChanged(event);
			}
		}
	}
}
