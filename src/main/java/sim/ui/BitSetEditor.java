package sim.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

public class BitSetEditor extends JPanel implements TableCellEditor, MouseListener{

	private static final long serialVersionUID = 6562630246698654122L;

	private Bit[] bits = new Bit[8];
	
	private class Bit extends JLabel {

		private static final long serialVersionUID = -8709364972038086812L;

		private int bit;
		private boolean set;
		public Bit(int bit){
			this.bit = bit;
			setOpaque(true);
		}
		public void clear(){
			set = false;
			setBackground(Color.WHITE);
		}
		public void set(){
			set = true;
			setBackground(Color.BLACK);
		}
		public void toggle(){
			set = !set;
		}
	}
	
	public BitSetEditor(){
		setOpaque(true);
		setLayout(new GridLayout(1,8));
		for (int i=0;i<8;i++){
			bits[i] = new Bit(7-i);
			bits[i].setOpaque(true);
			bits[i].addMouseListener(this);
			add(bits[i]);
		}
		
	}
	@Override
	public Object getCellEditorValue() {
		
		short value = 0;
		
		for (int i=0;i<8;i++){
			if (bits[i].set){
				value |= 1 << bits[i].bit;
			}
		}
		
		return value;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return (anEvent instanceof MouseEvent) 
			&& ((MouseEvent)anEvent).getID() == MouseEvent.MOUSE_PRESSED 
			&& ((MouseEvent)anEvent).getClickCount() == 1;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return (anEvent instanceof MouseEvent) 
		&& ((MouseEvent)anEvent).getID() == MouseEvent.MOUSE_PRESSED 
		&& ((MouseEvent)anEvent).getClickCount() == 1;
	}

	@Override
	public boolean stopCellEditing() {
		ChangeEvent event = new ChangeEvent(this);
		for (CellEditorListener l:new ArrayList<CellEditorListener>(listeners)){
			l.editingStopped(event);
		}
		return true;
	}

	@Override
	public void cancelCellEditing() {
		ChangeEvent event = new ChangeEvent(this);
		for (CellEditorListener l:new ArrayList<CellEditorListener>(listeners)){
			l.editingCanceled(event);
		}
	}

	private List<CellEditorListener> listeners = new ArrayList<CellEditorListener>();
	
	@Override
	public void addCellEditorListener(CellEditorListener l) {
		listeners.add(l);
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		
		short sValue = ((Number)value).shortValue();
		
		for (Bit bit:bits){
			if ((sValue & (1 << bit.bit))!= 0){
				bit.set();
			} else {
				bit.clear();
			}
		}
		return this;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		((Bit)e.getSource()).toggle();
		stopCellEditing();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
