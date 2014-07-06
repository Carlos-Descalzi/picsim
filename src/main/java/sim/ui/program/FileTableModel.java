package sim.ui.program;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import sim.mcu.Microcontroller;
import sim.ui.CodeLine;

public class FileTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3461239481012313013L;

	private Microcontroller mcu;
	private PcEntry entry = new PcEntry();
	private Map<Short,CodeLine> executableLinesByPc = new LinkedHashMap<Short,CodeLine>();
	private Map<Integer,CodeLine> executableLinesByLine = new LinkedHashMap<Integer,CodeLine>();
	public FileTableModel(Microcontroller mcu){
		this.mcu = mcu;
		
		Map<Short,List<CodeLine>> linesByPc = new LinkedHashMap<Short,List<CodeLine>>();
		
		if (mcu != null && mcu.getProgram() != null && mcu.getProgram().isCodeAvailable()){
			
			for (CodeLine line:mcu.getProgram().getCode()){
				if (line.getAddress() == null){
					continue;
				}
				
				List<CodeLine> lines = linesByPc.get(line.getAddress());
				
				if (lines == null){
					lines = new ArrayList<CodeLine>();
					linesByPc.put(line.getAddress(), lines);
				}
				
				lines.add(line);
			}
			
			for (Map.Entry<Short,List<CodeLine>> entry:linesByPc.entrySet()){
				List<CodeLine> lines = entry.getValue();
				CodeLine lastLine = lines.get(lines.size()-1);
				executableLinesByPc.put(entry.getKey(),lastLine);
				executableLinesByLine.put(lastLine.getLineNumber(),lastLine);
			}
			
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0 && mcu != null && mcu.getProgram() != null && mcu.getProgram().isCodeAvailable()){
			CodeLine code = mcu.getProgram().getCode().get(rowIndex);
			return code.getAddress() != null;
		}
		return false;
	}

	@Override
	public int getRowCount() {
		return mcu.getProgram() == null ? 0 : mcu.getProgram().isCodeAvailable() ? mcu.getProgram().getCode().size() : 0;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		CodeLine line = mcu.getProgram().getCode().get(rowIndex);

		entry.setPc(line.getAddress());
		entry.setLine(line.getLineNumber());
		if (line.getAddress() != null){
			entry.setBreakpoint(mcu.isBreakpoint(line.getAddress()));
			if (entry.isBreakpoint()){
				entry.setBreakpointEnabled(mcu.isBreakpointEnabled(line.getAddress()));
			}
		}
		switch (columnIndex){
		case 0: return entry;
		case 1: return line.getAddress();
		case 2: return line.getCode();
		}
		return null;
	}
	
	public int getExecutableLineForPc(int pc){
		if (!executableLinesByPc.isEmpty()){
			CodeLine line = executableLinesByPc.get((short)pc);
			
			if (line != null){
				return line.getLineNumber();
			}
		}

		return -1;
	}
	
	public static class PcEntry {
		private Short pc;
		private int line;
		private boolean breakpoint;
		private boolean breakpointEnabled;
		
		public int getLine() {
			return line;
		}

		public void setLine(int line) {
			this.line = line;
		}

		public Short getPc() {
			return pc;
		}

		public void setPc(Short pc) {
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

	public Short getPcAtRow(int row) {
		CodeLine line = executableLinesByLine.get(row+1);
		
		if (line != null){
			return line.getAddress();
		}
		
		return null;
	}
}