package sim.mcu;

import java.util.List;
import java.util.Set;


/**
 * Interface to be implemented by different versions of microcontrollers.
 * @author Carlos E. Descalzi.
 *
 */
public interface Microcontroller {
	/**
	 * Reset the microcontroller
	 */
	public void reset();
	/**
	 * Start running the microcontroller. It will stop when a stack overflow is detected
	 * ,on a breakpoint or by explicit call of {@link #stop()}. 
	 * Events are disabled during the run, and enabled back when is stopped.
	 */
	public void run();
	/**
	 * Starts running at a 1/2 hz. This operation doesn't disable events. 
	 */
	public void runStepByStep();
	/**
	 * Runs a single step.
	 */
	public void step();
	/**
	 * Stops the microcontroller.
	 */
	public void stop();
	/**
	 * Determines if the microcontroller is running.
	 * @return <b>true</b> if it is running.
	 */
	public boolean isRunning();
	/**
	 * Runs up to a given program memory location, events are disabled meanwhile.
	 * @param pc the program memory location where is required to stop.
	 */
	public void runToPc(int pc);
	/**
	 * Steps over an instruction, if it is a CALL it will run over it unless there is a breakpoint
	 * in the call.
	 */
	public void stepOver();
	/**
	 * Runs until the code returns back from a CALL.
	 */
	public void stepOut();
	/**
	 * Adds a breakpoint at a given program memory location.
	 * @param position the program memory location.
	 */
	public void addBreakpoint(int position);
	/**
	 * Removes a breakpoint from a given program memory location.
	 * @param position the program memory location.
	 */
	public void removeBreakpoint(int position);
	/**
	 * Enables/disables a breakpoint in a given program memory location.
	 * @param position the program memory location.
	 * @param enabled <b>true</b> to enable.
	 */
	public void setBreakpointEnabled(int position,boolean enabled);
	/**
	 * Determines if a breakpoint is enabled at a given program memory position.
	 * @param position the program memory position.
	 * @return <b>true</b> if exists a breakpoint and is enabled.
	 */
	public boolean isBreakpointEnabled(int position);
	/**
	 * Determines if exists a breakpoint at a given program memory location.
	 * @param position the program memory position.
	 * @return <b>true</b> if exists a breakpoint in the given program memory location.
	 */
	public boolean isBreakpoint(int position);
	/**
	 * Returns a list of existing breakpoints with their state.
	 * @return a list a breakpoints.
	 */
	public List<Breakpoint> getBreakpoints();
	/**
	 * Sets a program
	 * @param program the program to store in memory.
	 */
	public void setProgram(Program program);
	/**
	 * Returns the current program
	 * @return the current program, or <b>null</b> if there is no program set.
	 */
	public Program getProgram();
	/**
	 * Returns the mnemonic representing a given opcode.
	 * @param instruction the opcode.
	 * @return the mnemonic for the given opcode.
	 */
	public String getMnemonic(int instruction);
	/**
	 * Returns a reference to the program memory.
	 * @return the program memory.
	 */
	public ProgramMemory getProgramMemory();
	/**
	 * Returns a reference to the data memory.
	 * @return the data memory.
	 */
	public DataMemory getDataMemory();
	/**
	 * Returns a set of labels defined in the program and their memory locations.
	 * @return a set of labels.
	 */
	public Set<Symbol> getSymbols();
	/**
	 * Returns a set of known register names.
	 * @return a set of known register names.
	 */
	public Set<Symbol> getRegisterNames();
	/**
	 * Adds an event listener
	 * @param listener the event listener to add.
	 */
	public void addListener(MicrocontrollerListener listener);
	/**
	 * Removes an event listener
	 * @param listener the event listener to remove
	 */
	public void removeListener(MicrocontrollerListener listener);
	/**
	 * Returns the program counter value.
	 * @return the program counter value, this is PCLATH << 8 | PCL.
	 */
	public int getPc();
	public byte getW();
	/**
	 * Returns the STATUS word.
	 * @return the STATUS word.
	 */
	public short getStatus();
	/**
	 * Returns the stack segment
	 * @return the stack segment
	 */
	public int[] getStack();
	/**
	 * Returns the names of implemented status flags
	 * @return the names of implemented status flags
	 */
	public String[] getStatusFlags();
	/**
	 * Returns the IO ports existing in the microcontroller
	 * @return the IO ports existing in the microcontroller
	 */
	public Port[] getPorts();
	/**
	 * 
	 * @return
	 */
	public int getClkOut();
	/**
	 * Sets the working clock speed.
	 * @param speed the clock speed in Hz.
	 */
	public void setSpeed(long speed);
}
