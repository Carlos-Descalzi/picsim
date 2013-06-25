package sim.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import sim.mcu.Breakpoint;
import sim.mcu.Microcontroller;
import sim.mcu.MicrocontrollerEvent;
import sim.mcu.MicrocontrollerListener;

public class BreakpointViewer extends JInternalFrame 
	implements MicrocontrollerListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7117959362866267152L;

	private BreakpointModel model = new BreakpointModel();
	private JTable table = new JTable();
	private Microcontroller mcu;
	private BreakpointEditor breakpointEditor = new BreakpointEditor(MainFrame.getFrames()[0]);
	private Action editAction = new AbstractAction("Edit"){

		private static final long serialVersionUID = -8388952138153755213L;

		@Override
		public void actionPerformed(ActionEvent e) {
			editBreakpoint();
		}
	};
	private Action removeAction = new AbstractAction("Remove"){

		private static final long serialVersionUID = -8952391815673327517L;

		@Override
		public void actionPerformed(ActionEvent e) {
			removeBreakpoint();
		}
		
	};
	public BreakpointViewer(){
		super("Breakpoints",true,false,false,true);
		setLayout(new BorderLayout());
		setSize(120,120);
		JToolBar toolBar = new JToolBar();
		
		
		table.setAutoCreateColumnsFromModel(false);
		
		TableColumn c0 = new TableColumn(0);
		c0.setHeaderValue("En.");
		c0.setMaxWidth(25);
		table.addColumn(c0);
		
		TableColumn c1 = new TableColumn(1);
		c1.setHeaderValue("PC");
		c1.setCellRenderer(new HexRenderer(4));
		table.addColumn(c1);
		
		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setColumnSelectionAllowed(false);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateActionsState();
			}
		});
		add(toolBar,BorderLayout.NORTH);
		add(new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
			BorderLayout.CENTER);
		
		toolBar.add(editAction);
		toolBar.add(removeAction);
	}
	
	protected void editBreakpoint() {
		breakpointEditor.setVisible(true);
	}
	
	protected void removeBreakpoint(){
		Breakpoint breakpoint = model.getBreakpoint(table.getSelectedRow());
		mcu.removeBreakpoint(breakpoint.getPc());
	}

	protected void updateActionsState() {
		
		boolean hasSelection = table.getSelectedRow() !=-1;
		
		editAction.setEnabled(hasSelection);
		removeAction.setEnabled(hasSelection);
	}

	public void setMcu(Microcontroller mcu){
		this.mcu = mcu;
		this.mcu.addListener(this);
		this.model.update();
	}
	
	private class BreakpointModel extends AbstractTableModel {

		private static final long serialVersionUID = -7564900851857723346L;

		public BreakpointModel(){
		}
		
		@Override
		public int getRowCount() {
			return mcu == null ? 0 : mcu.getBreakpoints().size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (mcu == null){
				return null;
			}
			Breakpoint breakpoint = mcu.getBreakpoints().get(rowIndex);
			
			switch (columnIndex){	
			case 0: return breakpoint.isEnabled();
			case 1: return breakpoint.getPc();
			}
			
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex){
			case 0: return Boolean.class;
			case 1: return Integer.class;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0;
		}

		public Breakpoint getBreakpoint(int row){
			return mcu.getBreakpoints().get(row);
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (mcu == null){
				return;
			}
			int position = mcu.getBreakpoints().get(rowIndex).getPc();
			
			mcu.setBreakpointEnabled(position,(Boolean)aValue);
		}
		
		private int oldRowCount = 0;
		public void update(){
			
			int rows = mcu.getBreakpoints().size();
			
			if (rows < oldRowCount){
				fireTableChanged(new TableModelEvent(this,0,rows-1,TableModelEvent.ALL_COLUMNS,TableModelEvent.UPDATE));
				fireTableChanged(new TableModelEvent(this,rows-1,oldRowCount-1,TableModelEvent.ALL_COLUMNS,TableModelEvent.DELETE));
			} else if (rows > oldRowCount){
				fireTableChanged(new TableModelEvent(this,0,oldRowCount-1,TableModelEvent.ALL_COLUMNS,TableModelEvent.UPDATE));
				fireTableChanged(new TableModelEvent(this,oldRowCount-1,rows-1,TableModelEvent.ALL_COLUMNS,TableModelEvent.INSERT));
			} else {
				fireTableChanged(new TableModelEvent(this,0,rows-1,TableModelEvent.ALL_COLUMNS,TableModelEvent.UPDATE));
			}
			
			oldRowCount = rows;
		}
		
	}

	@Override
	public void breakpointAdded(MicrocontrollerEvent event) {
		model.update();
	}

	@Override
	public void breakpointRemoved(MicrocontrollerEvent event) {
		model.update();
	}

	@Override
	public void breakpointChanged(MicrocontrollerEvent event) {
		model.update();
	}

	@Override
	public void breakpointReached(MicrocontrollerEvent event) {	}

	@Override
	public void registerChanged(MicrocontrollerEvent event) { }

	@Override
	public void executionStarted(MicrocontrollerEvent event) { }

	@Override
	public void executionFinished(MicrocontrollerEvent event) { }

	@Override
	public void programLoaded(MicrocontrollerEvent event) {	}

	@Override
	public void stackOverflowDetected(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void watchdogActivated(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}
}
