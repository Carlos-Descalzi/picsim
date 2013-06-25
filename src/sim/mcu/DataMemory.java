package sim.mcu;

public interface DataMemory {

	public int getLength();
	public byte getValue(int address);
	public void setValue(int address, byte value);
	public int getPc();
	public void setPc(int pc);
	public void incPc();
	
	public void addListener(MemoryListener listener);
	public void removeListener(MemoryListener listener);
	
	public byte getAbsoluteAddressValue(int address);
	
	public void setAbsoluteAddressValue(int address,byte value);

}
