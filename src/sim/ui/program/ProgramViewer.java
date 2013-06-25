package sim.ui.program;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import sim.mcu.Microcontroller;
import sim.mcu.MicrocontrollerEvent;
import sim.mcu.MicrocontrollerListener;

public class ProgramViewer extends JInternalFrame 
	implements MicrocontrollerListener{

	private static final long serialVersionUID = 6782229927208628361L;

	private final DissasemblyView dissasemblyTable = new DissasemblyView();
	private final SourceCodeView fileTable = new SourceCodeView();
	private Microcontroller mcu;
	
	JTabbedPane tabs = new JTabbedPane();
	
	public ProgramViewer(){
		super("Program",true,false,true,true);
		setSize(600,500);
		
		add(tabs,BorderLayout.CENTER);
		tabs.addTab("Dissasembly",new JScrollPane(dissasemblyTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		tabs.addTab("Source",new JScrollPane(fileTable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
	}

	
	public void setMcu(Microcontroller mcu) {
		this.mcu = mcu;
		dissasemblyTable.setMcu(mcu);
		fileTable.setMcu(mcu);
		this.mcu.addListener(this);
	}
	
	@Override
	public void breakpointReached(MicrocontrollerEvent event) {}

	@Override
	public void registerChanged(MicrocontrollerEvent event) {
	}

	@Override
	public void breakpointAdded(MicrocontrollerEvent event) {
	}

	@Override
	public void breakpointRemoved(MicrocontrollerEvent event) {
	}

	@Override
	public void breakpointChanged(MicrocontrollerEvent event) {
	}

	public int getCurrentLine(){
		return dissasemblyTable.getSelectedRow();
	}

	@Override
	public void executionStarted(MicrocontrollerEvent event) {}

	@Override
	public void executionFinished(MicrocontrollerEvent event) {}

	@Override
	public void programLoaded(MicrocontrollerEvent event) {
	}


	@Override
	public void stackOverflowDetected(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void watchdogActivated(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}
}
