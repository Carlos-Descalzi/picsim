package sim.mcu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingUtilities;



public abstract class AbstractMicrocontroller implements Microcontroller {

	protected byte w;
	
	protected final Map<Integer,BreakpointImpl> breakpoints = new TreeMap<Integer,BreakpointImpl>();

	private long speed = 12000000;

	protected int clock;

	private List<MicrocontrollerListener> listeners = new ArrayList<>();

	private Runner runner;

	public void setSpeed(long speed){
		this.speed = speed;
	}

	private boolean eventsEnabled = true;

	public boolean isEventsEnabled() {
		return eventsEnabled;
	}

	public void setEventsEnabled(boolean eventsEnabled) {
		this.eventsEnabled = eventsEnabled;
		doSetEventsEnabled(eventsEnabled);
	}

	private class Runner extends Thread {
		private long interval = 1000;
		private int pc;
		private boolean running = true;
		private boolean stopOnReturn = false;
		private boolean disableEvents;
		public Runner(long interval,int pc, boolean disableEvents, boolean stopOnReturn){
			this.interval = interval;
			this.pc = pc;
			this.disableEvents = disableEvents;
			this.stopOnReturn = stopOnReturn;
		}
		
		public void run(){
			
			fireExecutionStarted();
			
			if (disableEvents){
				setEventsEnabled(false);
			}
			while (!breakpoints.containsKey(getPc()) && pc != getPc()){
				runStep();
				if (stopOnReturn && isReturnFromCall()){
					break;
				}
				if (interval >= 1){
					try {
							sleep(interval);
					} catch (InterruptedException ex){
						break;
					}
				} else {
					if (isInterrupted()){
						break;
					}
				}
			}
			running = false;
			
			if (disableEvents){
				setEventsEnabled(true);
			}
			SwingUtilities.invokeLater(new Runnable(){
				public void run(){
					if (disableEvents) notifyMcuStateChanged();
					fireExecutionFinished();
				}
			});
			
			
		}
		
		public boolean isRunning(){
			return running;
		}
	};
	
	@Override
	public void step() {
		runStep();
	}
	protected abstract boolean isReturnFromCall();

	public void stepOver(){
		if (isNextLineBranch()){
			runToPc(getPc()+1);
		} else {
			runStep();
		}
	}

	protected abstract boolean isNextLineBranch();

	public void stop(){
		if (isRunning()){
			runner.interrupt();
		}
	}
	
	@Override
	public void run() {
		if (!isRunning()){
			runner = new Runner(1000/speed,-1,true,false);
			runner.start();
		}
	}

	@Override
	public void runStepByStep() {
		if (!isRunning()){
			runner = new Runner(500,-1,false,false);
			runner.start();
		}
	}

	public boolean isRunning(){
		return runner != null && runner.isRunning();
	}
	
	public void runToPc(int pc){
		if (!isRunning()){
			runner = new Runner(1000/speed,pc,true,false);
			runner.start();
		}
	}
	
	public void stepOut(){
		if (!isRunning()){
			runner = new Runner(1000/speed,-1,true,true);
			runner.start();
		}
	}
	
	@Override
	public byte getW() {
		return w;
	}
	
	public void setW(byte w) {
		this.w = w;
	}

	@Override
	public void addBreakpoint(int position) {
		if (!breakpoints.containsKey(position)){
			BreakpointImpl breakpoint = new BreakpointImpl(position);
			breakpoints.put(position,new BreakpointImpl(position));
			fireBreakpointAdded(breakpoint);
		}
	}

	@Override
	public void removeBreakpoint(int position) {
		breakpoints.remove(position);
		fireBreakpointRemoved(breakpoints.remove(position));
	}

	@Override
	public void setBreakpointEnabled(int position, boolean enabled) {
		BreakpointImpl breakpoint = breakpoints.get(position); 
		breakpoint.setEnabled(enabled);
		fireBreakpointChanged(breakpoint);
	}
	
	@Override
	public boolean isBreakpointEnabled(int position) {
		return breakpoints.get(position).isEnabled();
	}
	
	public boolean isBreakpoint(int position){
		return breakpoints.containsKey(position);
	}
	
	@Override
	public List<Breakpoint> getBreakpoints() {
		return new ArrayList<Breakpoint>(breakpoints.values());
	}

	
	protected void fireStackOverflowDetected() {
		
		MicrocontrollerEvent event = new MicrocontrollerEvent(this,clock);
		
		for (MicrocontrollerListener listener: listeners){
			listener.stackOverflowDetected(event);
		}
	}

	protected void fireExecutionStarted(){
		MicrocontrollerEvent event = new MicrocontrollerEvent(this,clock);
		
		for (MicrocontrollerListener listener: listeners){
			listener.executionStarted(event);
		}
	}

	protected void fireExecutionFinished(){
		MicrocontrollerEvent event = new MicrocontrollerEvent(this,clock);
		
		for (MicrocontrollerListener listener: listeners){
			listener.executionFinished(event);
		}
	}
	
	protected void fireRegisterChanged(){
		MicrocontrollerEvent event = new MicrocontrollerEvent(this,clock);
		
		for (MicrocontrollerListener listener: listeners){
			listener.registerChanged(event);
		}
	}
	
	protected void fireBreakpointAdded(Breakpoint breakpoint){
		MicrocontrollerEvent event = new MicrocontrollerEvent(this,clock,breakpoint);
		
		for (MicrocontrollerListener listener: listeners){
			listener.breakpointAdded(event);
		}
		
	}

	protected void fireBreakpointRemoved(Breakpoint breakpoint){
		MicrocontrollerEvent event = new MicrocontrollerEvent(this,clock,breakpoint);
		
		for (MicrocontrollerListener listener: listeners){
			listener.breakpointRemoved(event);
		}

	}

	protected void fireBreakpointChanged(Breakpoint breakpoint){
		MicrocontrollerEvent event = new MicrocontrollerEvent(this,clock,breakpoint);
		
		for (MicrocontrollerListener listener: listeners){
			listener.breakpointChanged(event);
		}

	}
	
	protected void fireProgramLoaded(Program program){
		MicrocontrollerEvent event = new MicrocontrollerEvent(this,clock,program);
		
		for (MicrocontrollerListener listener: listeners){
			listener.programLoaded(event);
		}

	}
	
	@Override
	public void addListener(MicrocontrollerListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(MicrocontrollerListener listener) {
		listeners.remove(listener);
	}

	protected void doSetEventsEnabled(boolean enabled){
		
	}

	protected void notifyMcuStateChanged(){
		
	}
	
	protected abstract void runStep();

	public int getClkOut(){
		return clock & 0xFF;
	}
	
}
