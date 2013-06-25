package sim.ui.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import ar.com.da.swing.FormLayout;

public class PanelBuilder {

	private LayoutManager layout;
	private JPanel panel;
	
	private PanelBuilder(){
		panel = new JPanel();
	}
	
	public static PanelBuilder newFormLayout(){
		PanelBuilder builder = new PanelBuilder();
		builder.panel.setLayout(new FormLayout());
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
	
	public PanelBuilder add(Component component,Dimension preferredSize,String constraints){
		component.setPreferredSize(preferredSize);
		panel.add(component,constraints);
		return this;
	}
	
	public PanelBuilder addToForm(Component component, String label){
		((FormLayout)panel.getLayout()).add(component, label, panel);
		return this;
	}
	
	public JPanel getPanel(){
		return panel;
	}
}
