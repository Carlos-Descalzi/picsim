package sim.mcu;


public class BreakpointImpl 
	implements Breakpoint{

	private static final long serialVersionUID = 6517082514937186997L;

	private boolean enabled;
	private int pc;
	
	public BreakpointImpl(){}
	
	public BreakpointImpl(int pc){
		enabled = true;
		this.pc = pc;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	
}