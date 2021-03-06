package sim.ui.program;

import static sim.ui.CommonResources.BREAKPOINT_DISABLED_ICON;
import static sim.ui.CommonResources.BREAKPOINT_ENABLED_ICON;
import static sim.ui.CommonResources.MONOSPACE_FONT;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import sim.mcu.Microcontroller;
import sim.mcu.MicrocontrollerEvent;
import sim.ui.HexRenderer;
import sim.ui.TextRenderer;
import sim.ui.program.ProgramTableModel.PcEntry;

public class DissasemblyView extends AbstractProgramView {

	private static final long serialVersionUID = -6982669106770435451L;
	
	public DissasemblyView(){
		setAutoCreateColumnsFromModel(false);
		setFont(MONOSPACE_FONT);
		
		TableColumn c0 = new TableColumn(0);
		c0.setHeaderValue("PC");
		c0.setCellRenderer(new RendererSelectionWrapper(new BreakpointSwitcher(4)));
		c0.setCellEditor(new BreakpointSwitcher(4));
		c0.setMaxWidth(70);
		
		addColumn(c0);
		
		TableColumn c1 = new TableColumn(1);
		c1.setHeaderValue("HEX");
		c1.setCellRenderer(new RendererSelectionWrapper(new HexRenderer(4)));
		c1.setMaxWidth(80);
		
		addColumn(c1);
		TableColumn c2 = new TableColumn(2);
		c2.setHeaderValue("Mnemonic");
		c2.setCellRenderer(new RendererSelectionWrapper(new TextRenderer()));
		addColumn(c2);
		getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setColumnSelectionAllowed(false);
	}
	
	private class RendererSelectionWrapper implements TableCellRenderer {
		private TableCellRenderer renderer;
		
		public RendererSelectionWrapper(TableCellRenderer renderer){
			this.renderer = renderer;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			Component c = renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			if (pc == row){
				c.setBackground(Color.GREEN);
			} else {
				if (isSelected){
					c.setBackground(table.getSelectionBackground());
				} else {
					c.setBackground(table.getBackground());
				}
			}
			return c;
		}
	}
	
	
	public void setMcu(Microcontroller mcu){
		super.setMcu(mcu);
		setModel(new ProgramTableModel(mcu));
	}
	
	
	private class BreakpointSwitcher extends HexRenderer implements TableCellEditor, MouseListener {

		private static final long serialVersionUID = 660316140211842960L;
		private final PcEntry entry = new PcEntry();
		
		public BreakpointSwitcher(int size) {
			super(size);
			addMouseListener(this);
		}

		@Override
		public Object getCellEditorValue() {
			return entry;
		}

		@Override
		public boolean isCellEditable(EventObject anEvent) {
			boolean editable = anEvent instanceof MouseEvent
				&& ((MouseEvent)anEvent).getID() == MouseEvent.MOUSE_PRESSED;
			return editable;
		}

		@Override
		public boolean shouldSelectCell(EventObject anEvent) {
			return true;
		}

		@Override
		public boolean stopCellEditing() {
			fireStopped();
			return true;
		}

		@Override
		public void cancelCellEditing() {
			fireCanceled();
		}

		private List<CellEditorListener> listeners = new ArrayList<CellEditorListener>();
		
		private void fireStopped(){
			ChangeEvent e = new ChangeEvent(this);
			for (CellEditorListener l:new ArrayList<CellEditorListener>(listeners)){
				l.editingStopped(e);
			}
		}
		
		private void fireCanceled(){
			ChangeEvent e = new ChangeEvent(this);
			for (CellEditorListener l:new ArrayList<CellEditorListener>(listeners)){
				l.editingCanceled(e);
			}
		}
		
		@Override
		public void addCellEditorListener(CellEditorListener l) {
			listeners.add(l);
		}

		@Override
		public void removeCellEditorListener(CellEditorListener l) {
			listeners.remove(l);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			this.entry.set((PcEntry)value);
			Component c = getTableCellRendererComponent(table, value, true, true, row, column);
			return c;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			JLabel component = (JLabel) super.getTableCellRendererComponent(
				table, ((PcEntry)value).getPc(), isSelected, hasFocus,
				row, column);
			
			if (((PcEntry)value).isBreakpoint()){
				if (((PcEntry)value).isBreakpointEnabled()){
					component.setIcon(BREAKPOINT_ENABLED_ICON);
				} else {
					component.setIcon(BREAKPOINT_DISABLED_ICON);
				}
			} else {
				component.setIcon(null);
			}
			
			
			return component;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1){
				entry.toggleBreakpoint();
				stopCellEditing();
			}
		}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		
	}


	@Override
	public void breakpointReached(MicrocontrollerEvent event) {}
	@Override
	public void breakpointAdded(MicrocontrollerEvent event) {
		repaint();
	}
	@Override
	public void breakpointRemoved(MicrocontrollerEvent event) {
		repaint();
	}
	@Override
	public void breakpointChanged(MicrocontrollerEvent event) {
		repaint();
	}
	@Override
	public void executionStarted(MicrocontrollerEvent event) {}
	@Override
	public void executionFinished(MicrocontrollerEvent event) {
		repaint();
		followProgamCounter();
	}
	@Override
	public void programLoaded(MicrocontrollerEvent event) {}
	@Override
	public void stackOverflowDetected(MicrocontrollerEvent event) {}
	@Override
	public void watchdogActivated(MicrocontrollerEvent event) {}
	
}
