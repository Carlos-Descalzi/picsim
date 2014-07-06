package sim.ui.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelBuilder {

	private JPanel panel;
	private int rowCount;
	private PanelBuilder(){
		panel = new JPanel();
		rowCount = 0;
	}
	
	public static PanelBuilder newFormLayout(){
		PanelBuilder builder = new PanelBuilder();
		builder.panel.setLayout(new GridBagLayout());
		return builder;
	}
	
	public static PanelBuilder newBorderLayout(){
		PanelBuilder builder = new PanelBuilder();
		builder.panel.setLayout(new BorderLayout());
		return builder;
	}
	
	public PanelBuilder add(Component component,String constraints){
		panel.add(component,constraints);
		return this;
	}
	
	public PanelBuilder addAtCenter(Component component){
		return add(component,BorderLayout.CENTER);
	}
	
	public PanelBuilder addAtNorth(Component component){
		return add(component,BorderLayout.NORTH);
	}
	
	public PanelBuilder addAtSouth(Component component){
		return add(component,BorderLayout.SOUTH);
	}
	
	public PanelBuilder addAtEast(Component component){
		return add(component,BorderLayout.EAST);
	}
	
	public PanelBuilder addAtWest(Component component){
		return add(component,BorderLayout.WEST);
	}

	public PanelBuilder addToForm(Component component, String label){
		JLabel jLabel = new JLabel(label);
		panel.add(jLabel,new GridBagConstraints(0, rowCount, 1, 1, 0.3, 0.7, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
		panel.add(component,new GridBagConstraints(1, rowCount, 1, 1, 0.3, 0.7, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2,2,2,2), 0, 0));
		rowCount++;
		return this;
	}
	
	public JPanel getPanel(){
		return panel;
	}
}
