package sim.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import sim.ui.util.UiUtils;

public class BreakpointEditor 
	extends JDialog{

	private static final long serialVersionUID = -217098828289216669L;

	private Action okAction = new AbstractAction("Ok"){

		private static final long serialVersionUID = 2572843902340346553L;

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
		
	};
	
	private Action cancelAction = new AbstractAction("Cancel"){

		private static final long serialVersionUID = 5224568882850494348L;

		@Override
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
		
	};
	
	private JCheckBox enabled = new JCheckBox();
	private JCheckBox conditional = new JCheckBox();
	private JTable conditions = new JTable();
	public BreakpointEditor(Frame owner){
		super(owner,"Breakpoint properties",true);
		setSize(400,300);
		UiUtils.center(this);
		setLayout(new BorderLayout());
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(buttonsPanel,BorderLayout.SOUTH);
		buttonsPanel.add(new JButton(okAction));
		buttonsPanel.add(new JButton(cancelAction));
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		add(mainPanel,BorderLayout.CENTER);
		
		buildConditionsTable();
		
		mainPanel.add(new JLabel("Enabled"),new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.VERTICAL,new Insets(0,0,0,0),0,0));
		mainPanel.add(enabled,new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		mainPanel.add(new JLabel("Conditional"),new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.VERTICAL,new Insets(0,0,0,0),0,0));
		mainPanel.add(conditional,new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
		mainPanel.add(new JScrollPane(conditions),new GridBagConstraints(1,2,2,1,1,1,GridBagConstraints.SOUTH,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
	}
	
	private void buildConditionsTable() {
		conditions.setAutoCreateColumnsFromModel(false);
		
		TableColumn t0 = new TableColumn(0);
		conditions.addColumn(t0);
		TableColumn t1 = new TableColumn(1);
		conditions.addColumn(t1);
		TableColumn t2 = new TableColumn(2);
		conditions.addColumn(t2);
		TableColumn t3 = new TableColumn(3);
		conditions.addColumn(t3);
	}
}
