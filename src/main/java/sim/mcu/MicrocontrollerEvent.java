package sim.mcu;

import java.util.EventObject;


public class MicrocontrollerEvent extends EventObject {

	private static final long serialVersionUID = 8606860667867780803L;

	private long clockTicks;
	private Breakpoint breakpoint;
	private Program program;
	
	public MicrocontrollerEvent(Object source,long clockTicks,Breakpoint breakpoint) {
		this(source,clockTicks);
		this.breakpoint = breakpoint;
	}
	
	public MicrocontrollerEvent(Object source,long clockTicks,Program program) {
		this(source,clockTicks);
		this.program = program;
	}
	
	public MicrocontrollerEvent(Object source,long clockTicks) {
		super(source);
		this.clockTicks = clockTicks;
	}

	public Breakpoint getBreakpoint() {
		return breakpoint;
	}

	public Program getProgram() {
		return program;
	}

	public long getClockTicks() {
		return clockTicks;
	}

}
