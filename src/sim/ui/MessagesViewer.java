package sim.ui;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import sim.mcu.Microcontroller;
import sim.mcu.MicrocontrollerEvent;
import sim.mcu.MicrocontrollerListener;

public class MessagesViewer extends JInternalFrame 
	implements MicrocontrollerListener{

	private static final long serialVersionUID = 7670322417574471977L;

	private JTextArea text = new JTextArea();
	
	private Microcontroller mcu;
	
	public MessagesViewer(){
		super("Messages",true,false,false,true);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(text,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),BorderLayout.CENTER);
		setSize(500,100);
	}
	
	public void setMcu(Microcontroller mcu){
		this.mcu = mcu;
		this.mcu.addListener(this);
	}

	@Override
	public void breakpointReached(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void breakpointAdded(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void breakpointRemoved(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void breakpointChanged(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void registerChanged(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executionStarted(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void executionFinished(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void programLoaded(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stackOverflowDetected(MicrocontrollerEvent event) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				text.append("Stack overflow detected!\n");
			}
		});
	}

	@Override
	public void watchdogActivated(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}
}
