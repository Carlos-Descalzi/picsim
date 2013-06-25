package sim.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.Set;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import sim.mcu.MemoryEvent;
import sim.mcu.MemoryListener;
import sim.mcu.Microcontroller;
import sim.mcu.MicrocontrollerEvent;
import sim.mcu.MicrocontrollerListener;
import sim.mcu.Symbol;

public class MemoryViewer 
	extends JInternalFrame implements MicrocontrollerListener{

	private static final long serialVersionUID = 9034983465407930359L;

	private final JTable table = new JTable();
	
	public MemoryViewer(){
		super("Memory",true,false,true,true);

		setSize(400,300);

		JScrollPane scroll = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll,BorderLayout.CENTER);
		scroll.setViewportView(table);
		table.setAutoCreateColumnsFromModel(false);
		
		TableColumn c0 = new TableColumn(0);
		c0.setHeaderValue("Pos");
		c0.setMaxWidth(50);
		c0.setCellRenderer(new HexRenderer(4));
		table.addColumn(c0);
		
		TableColumn c1 = new TableColumn(1);
		c1.setHeaderValue("Register");
		c1.setCellRenderer(new TextRenderer());
		table.addColumn(c1);
		
		TableColumn c2 = new TableColumn(2);
		c2.setHeaderValue("Label");
		c2.setCellRenderer(new TextRenderer());
		table.addColumn(c2);
				
		TableColumn c3 = new TableColumn(3);
		c3.setHeaderValue("HEX");
		c3.setMaxWidth(60);
		c3.setCellRenderer(new HexRenderer(2));
		table.addColumn(c3);
		
		TableColumn c4 = new TableColumn(3);
		c4.setHeaderValue("DEC");
		c4.setMaxWidth(60);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.RIGHT);
		renderer.setFont(new Font("Courier",Font.PLAIN,11));
		c4.setCellRenderer(renderer);
		
		table.addColumn(c4);
		
		TableColumn c5 = new TableColumn(3);
		c5.setHeaderValue("Bits");
		c5.setMaxWidth(180);
		c5.setCellRenderer(new BitSetRenderer());
		c5.setCellEditor(new BitSetEditor());
		table.addColumn(c5);
		
		setUp();
		
	}
	
	public void setUp(){
	}
	
	private class MemoryModel extends AbstractTableModel 
		implements MemoryListener{

		private static final long serialVersionUID = -2053871589184770941L;
		private String[] symbols;
		private String[] registers;
		public MemoryModel(int size){
			mcu.getDataMemory().addListener(this);
			loadSymbols();
			loadRegisters();
		}
		private void loadSymbols() {
			
			symbols = new String[mcu.getDataMemory().getLength()];
			for (int i=0;i<symbols.length;symbols[i++] = "");
			Set<Symbol> mcuSymbols = mcu.getSymbols();
			
			for (int i=0;i<symbols.length;i++){
				for (Symbol s:mcuSymbols){
					if (s.getValue() == i){
						if (symbols[i].length() > 0){
							symbols[i]+="/";
						}
						symbols[i] += s.getSymbol();
					}
				}
			}
		}
		private void loadRegisters() {
			registers = new String[mcu.getDataMemory().getLength()];
			
			Set<Symbol> mcuRegisters = mcu.getRegisterNames();
			for (int i=0;i<registers.length;i++){
				for (Symbol s:mcuRegisters){
					if (s.getValue() == i){
						registers[i] = s.getSymbol();
						break;
					}
				}
			}
		}
		@Override
		public int getRowCount() {
			return mcu.getDataMemory().getLength();
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex){
			case 0: return Integer.class;
			case 1: return String.class;
			case 2: return String.class;
			case 3: return Short.class;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch (columnIndex){
			case 0: return rowIndex;
			case 1: return registers[rowIndex];
			case 2: return symbols[rowIndex];
			case 3: return mcu.getDataMemory().getAbsoluteAddressValue(rowIndex);
			}
			return null;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			mcu.getDataMemory().setAbsoluteAddressValue(rowIndex,((Number)aValue).byteValue());
			fireTableChanged(new TableModelEvent(this,rowIndex, rowIndex,TableModelEvent.ALL_COLUMNS,TableModelEvent.UPDATE));
		}
		@Override
		public void addressValueChanged(MemoryEvent event) {
			int address = event.getAddress();
			fireTableChanged(new TableModelEvent(this,address, address, TableModelEvent.ALL_COLUMNS ,TableModelEvent.UPDATE));
		}
		@Override
		public void memoryCleared(MemoryEvent event) {
			fireTableChanged(new TableModelEvent(this,0, mcu.getDataMemory().getLength(), TableModelEvent.ALL_COLUMNS ,TableModelEvent.UPDATE));
		}
		
	}
	
	private Microcontroller mcu;

	public void setMcu(Microcontroller mcu) {
		this.mcu = mcu;
		table.setModel(new MemoryModel(0x100));
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
		table.setModel(new MemoryModel(0x100));
		
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
