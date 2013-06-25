package sim.mcu;

import java.util.EventListener;

public interface ProgramMemoryListener extends EventListener {

	public void memoryChanged(ProgramMemoryEvent event);
}
