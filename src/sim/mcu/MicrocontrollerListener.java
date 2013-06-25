package sim.mcu;

import java.util.EventListener;


public interface MicrocontrollerListener extends EventListener {

	public void breakpointReached(MicrocontrollerEvent event);
	public void breakpointAdded(MicrocontrollerEvent event);
	public void breakpointRemoved(MicrocontrollerEvent event);
	public void breakpointChanged(MicrocontrollerEvent event);
	public void registerChanged(MicrocontrollerEvent event);
	public void executionStarted(MicrocontrollerEvent event);
	public void executionFinished(MicrocontrollerEvent event);
	public void programLoaded(MicrocontrollerEvent event);
	public void stackOverflowDetected(MicrocontrollerEvent event);
	public void watchdogActivated(MicrocontrollerEvent event);
}
