package sim.ui.program;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import sim.mcu.Microcontroller;
import sim.mcu.ProgramMemoryEvent;
import sim.mcu.ProgramMemoryListener;

public class ProgramTableModel 
	extends AbstractTableModel 
	implements ProgramMemoryListener{

	private static final long serialVersionUID = 2426568392805954048L;

	private String[] mnemonics;

	private final PcEntry pcEntry = new PcEntry();
	private Microcontroller mcu;
	public ProgramTableModel(Microcontroller mcu){
		this.mcu = mcu;
		mnemonics = new String[mcu.getProgramMemory().getLenght()];
		mcu.getProgramMemory().addListener(this);
	}
	
	public void update(){
		int rows = mcu.getProgramMemory().getLenght();
		
		mnemonics = new String[mcu.getProgramMemory().getLenght()];
		
		for (int i=0;i<mnemonics.length;i++){
			mnemonics[i] = mcu.getMnemonic(mcu.getProgramMemory().getWord((short)i));
		}
		
		fireTableChanged(new TableModelEvent(this,0,rows,TableModelEvent.ALL_COLUMNS,TableModelEvent.UPDATE));
	}
	@Override
	public int getRowCount() {
		return mcu.getProgramMemory().getLenght();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PcEntry entry = (PcEntry)aValue;
		if (entry.isBreakpoint()){
			mcu.addBreakpoint(entry.getPc());
		} else {
			mcu.removeBreakpoint(entry.getPc());
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		
		if (columnIndex == 0){
			pcEntry.setPc(rowIndex);
			pcEntry.setBreakpoint(mcu.isBreakpoint(rowIndex));
			if (pcEntry.isBreakpoint()){
				pcEntry.setBreakpointEnabled(mcu.isBreakpointEnabled(rowIndex));
			} else {
				pcEntry.setBreakpointEnabled(false);
			}
			return pcEntry;
		} else if (columnIndex == 1){
			return mcu.getProgramMemory().getWord((short)rowIndex);
		} else {
			return mnemonics[rowIndex];
		}
	}

	@Override
	public void memoryChanged(ProgramMemoryEvent event) {
		update();
	}
	public static class PcEntry {
		private int pc;
		private boolean breakpoint;
		private boolean breakpointEnabled;
		
		public int getPc() {
			return pc;
		}

		public void setPc(int pc) {
			this.pc = pc;
		}

		public boolean isBreakpoint() {
			return breakpoint;
		}

		public void setBreakpoint(boolean breakpoint) {
			this.breakpoint = breakpoint;
		}

		public boolean isBreakpointEnabled() {
			return breakpointEnabled;
		}

		public void setBreakpointEnabled(boolean breakpointEnabled) {
			this.breakpointEnabled = breakpointEnabled;
		}

		public void set(PcEntry value) {
			pc = value.pc;
			breakpoint = value.breakpoint;
			breakpointEnabled = value.breakpointEnabled;
		}

		public void toggleBreakpoint() {
			breakpoint = !breakpoint;
		}
		
	}
}