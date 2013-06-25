package sim.mcu;

import java.util.EventObject;

public class MemoryEvent extends EventObject {

	private static final long serialVersionUID = -6213546017021529751L;

	private int address;
	private byte oldValue;
	private byte newValue;
	
	public MemoryEvent(Object source){
		this(source,0,(byte)0,(byte)0);
	}
	
	public MemoryEvent(Object source,int address, byte oldValue, byte newValue) {
		super(source);
		this.address = address;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public int getAddress() {
		return address;
	}

	public byte getOldValue() {
		return oldValue;
	}

	public byte getNewValue() {
		return newValue;
	}

}
