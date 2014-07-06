package sim.mcu;

import java.util.ArrayList;
import java.util.List;

public class PortImpl 
	implements Port{

	private boolean state;
	private int pin;
	
	public PortImpl(String name,int pin,Type type){
		this.name = name;
		this.pin = pin;
		this.type = type;
	}
	
	private String name;
	private Type type;

	public int getPin(){
		return pin;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		Type oldType = this.type;
		this.type = type;
		
		if (oldType != type){
			notifyDirectionChanged();
		}
	}

	private List<PortListener> outputListeners = new ArrayList<PortListener>();
	
	public void addOutputListener(PortListener listener){
		outputListeners.add(listener);
	}
	
	public void removeOutputListener(PortListener listener){
		outputListeners.remove(listener);
	}

	private List<PortListener> inputListeners = new ArrayList<PortListener>();
	
	public void addInputListener(PortListener listener){
		inputListeners.add(listener);
	}
	
	public void removeInputListener(PortListener listener){
		inputListeners.remove(listener);
	}
	
	public void setOutput(boolean value){
		state = value;
		PortEvent event = new PortEvent(this);
		for (PortListener listener:new ArrayList<PortListener>(outputListeners)){
			listener.portStateChanged(event);
		}
	}

	@Override
	public void setState(boolean value) {
		state = value;
		PortEvent event = new PortEvent(this);
		for (PortListener listener:new ArrayList<PortListener>(inputListeners)){
			listener.portStateChanged(event);
		}
	}
	
	public void toggle(){
		setState(!getState());
	}
	
	public boolean getState(){
		return state;
	}

	public void notifyDirectionChanged() {
		PortEvent event = new PortEvent(this);
		for (PortListener listener:new ArrayList<PortListener>(outputListeners)){
			listener.portDirectionChanged(event);
		}
		
	}
}
