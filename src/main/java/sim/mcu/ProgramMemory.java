package sim.mcu;


public interface ProgramMemory {
	public int getLenght();
	public int getWord(int address);
	public String getCode(int address);
	public void setProgram(Program program);
	public void addListener(ProgramMemoryListener listener);
	public void removeListener(ProgramMemoryListener listener);
}
