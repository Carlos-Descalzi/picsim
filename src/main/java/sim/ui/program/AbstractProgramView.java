package sim.ui.program;

import java.awt.Rectangle;

import javax.swing.JTable;

import sim.mcu.Microcontroller;
import sim.mcu.MicrocontrollerEvent;
import sim.mcu.MicrocontrollerListener;

public abstract class AbstractProgramView extends JTable 
	implements MicrocontrollerListener{

	private static final long serialVersionUID = 1548080703442626107L;
	protected int pc;
	protected Microcontroller mcu;

	public void setMcu(Microcontroller mcu){
		this.mcu = mcu;
		mcu.addListener(this);
	}
	
	protected void followProgamCounter() {
		Rectangle rect = getVisibleRect();
		
		int rowHeight = getRowHeight();
		
		int rowTop = rowHeight * getRowForPc(pc);
		
		if (rowTop + rowHeight < rect.y || rowTop > rect.y+rect.height){
			
			rect.y = rowTop - 10;
			
			scrollRectToVisible(rect);
		}
	}
	
	protected int getRowForPc(int pc){
		return pc;
	}

	@Override
	public void registerChanged(MicrocontrollerEvent event) {
		pc = mcu.getPc();
		followProgamCounter();
		repaint();
	}
}
