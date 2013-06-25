package sim.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import sim.HexUtil;
import sim.mcu.Microcontroller;
import sim.mcu.MicrocontrollerEvent;
import sim.mcu.MicrocontrollerListener;

public class RegisterViewer 
	extends JInternalFrame 
	implements MicrocontrollerListener{

	private static final long serialVersionUID = 1032449205785914004L;

	private BitSetRenderer status = new BitSetRenderer();
	private JLabel pc = new JLabel();
	private JLabel w = new JLabel();
	private JLabel clk = new JLabel();
	private JTable table = new JTable();
	private Microcontroller mcu;
	
	public RegisterViewer(){
		super("Registers",false,false,true,true);
		
		pc.setHorizontalAlignment(JLabel.RIGHT);
		pc.setFont(new Font("Courier",Font.PLAIN,12));
		w.setHorizontalAlignment(JLabel.RIGHT);
		w.setFont(new Font("Courier",Font.PLAIN,12));
		clk.setHorizontalAlignment(JLabel.RIGHT);
		clk.setFont(new Font("Courier",Font.PLAIN,12));

		setSize(200,250);
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.add(new JLabel("ST"),
			new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		panel.add(status,
			new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		
		panel.add(new JLabel("PC"),
			new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		panel.add(pc,
			new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

		panel.add(new JLabel("W"),
			new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		panel.add(w,
			new GridBagConstraints(1,2,1,1,1,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));


		panel.add(new JLabel("CLK"),
			new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		panel.add(clk,
			new GridBagConstraints(1,3,1,1,1,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));

		table.setAutoCreateColumnsFromModel(false);
		
		TableColumn c0 = new TableColumn(0);
		c0.setMaxWidth(30);
		c0.setResizable(false);
		c0.setHeaderValue("Pos");
		table.addColumn(c0);

		TableColumn c1 = new TableColumn(1);
		c1.setHeaderValue("Value");
		c1.setCellRenderer(new HexRenderer(4));
		table.addColumn(c1);
		
		panel.add(new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),
			new GridBagConstraints(0,4,2,1,1,1,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		
		add(panel,BorderLayout.CENTER);
	}

	public void setMcu(Microcontroller mcu) {
		this.mcu = mcu;
		this.mcu.addListener(this);
		this.table.setModel(new StackTableModel());
		this.status.setBitNames(mcu.getStatusFlags());
		update();
	}

	private void update() {
		status.setValue(mcu.getStatus());
		w.setText(HexUtil.toHex(mcu.getW()));
		pc.setText(HexUtil.toHex(mcu.getPc(),4));
		clk.setText(HexUtil.toHex(mcu.getClkOut(),2));
		((StackTableModel)table.getModel()).update();
	}
	
	private class StackTableModel 
		extends AbstractTableModel{

		private static final long serialVersionUID = -2111030872901426928L;

		public void update(){
			fireTableChanged(new TableModelEvent(this,0,mcu.getStack().length,1,TableModelEvent.UPDATE));
		}

		@Override
		public int getRowCount() {
			return mcu.getStack().length;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0){
				return rowIndex;
			}
			return mcu.getStack()[rowIndex];
		}
	}

	@Override
	public void breakpointReached(MicrocontrollerEvent event) {}

	@Override
	public void registerChanged(MicrocontrollerEvent event) {
		update();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void watchdogActivated(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}

}
