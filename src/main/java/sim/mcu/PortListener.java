package sim.mcu;

import java.util.EventListener;

public interface PortListener extends EventListener {

	public void portStateChanged(PortEvent event);
	public void portDirectionChanged(PortEvent event);
}
