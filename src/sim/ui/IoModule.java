package sim.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import sim.mcu.Microcontroller;
import sim.mcu.Port;
import sim.mcu.PortEvent;
import sim.mcu.PortListener;

public class IoModule 
	extends JInternalFrame 
	implements PortListener{

	private static final long serialVersionUID = -143067327032105338L;

	private JMenu optionsMenu = new JMenu("Options");
	public IoModule() {
		super("IO",true,false,true,true);
		setSize(150,450);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(optionsMenu);
		setJMenuBar(menuBar);
	}

	private Module[] modules;
	private JPanel[] slots;
	
	public void setMcu(Microcontroller mcu){
		Port[] ports = mcu.getPorts();
		slots = new JPanel[ports.length];
		modules = new Module[ports.length];
		setLayout(new GridLayout(slots.length,1));
		for (int i=0;i<ports.length;i++){
			ports[i].addOutputListener(this);
			slots[i] = new JPanel();
			JLabel l = new JLabel(ports[i].getName());
			l.setPreferredSize(new Dimension(30,22));
			slots[i].add(l);
			
			if (ports[i].getType() == Port.Type.INPUT){
				Module s = createModule(ports[i]);
				s.setPort(ports[i]);
				slots[i].add(s);
				modules[i] = s;
			} else if (ports[i].getType() == Port.Type.OUTPUT){
				Led led = new Led();
				led.setPort(ports[i]);
				slots[i].add(led);
				modules[i] = led;
			} 
			optionsMenu.add(createMenu(ports[i],modules[i].getClass().getSimpleName()));
			add(slots[i]);
		}
		
	}
	
	public abstract class Module extends JComponent {

		private static final long serialVersionUID = -5702712592313763329L;

		protected Port port;
		
		public void setPort(Port port){
			this.port = port;
		}
		
		public Port getPort(){
			return port;
		}
	}
	
	public class Switch extends Module {

		private static final long serialVersionUID = -4841999419704127507L;

		public Switch(){
			setPreferredSize(new Dimension(40,20));
			addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					port.toggle();
					repaint();
				}
			});
		}
		
		protected void paintComponent(Graphics g){
			
			int h = getHeight()/2;
			int w = getWidth()/4;
			
			if (port != null && port.getState()){
				g.drawLine(0,h,w*4,h);
			} else {
				g.drawLine(0,h,w,h);
				g.drawLine(w,h,w*3,0);
				g.drawLine(w*3,h,w*4,h);
			}
			
		}
		
	}
	public class Button extends Module {

		private static final long serialVersionUID = -4841999419704127507L;

		public Button(){
			setPreferredSize(new Dimension(40,20));
			addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					port.setState(true);
					repaint();
				}
				public void mouseReleased(MouseEvent e){
					port.setState(false);
					repaint();
				}
			});
		}
		
		protected void paintComponent(Graphics g){
			
			int h = getHeight()/2;
			int w = getWidth()/4;
			
			if (port != null && port.getState()){
				g.drawLine(0,h,w*4,h);
			} else {
				g.drawLine(0,h,w,h);
				g.drawLine(w,h,w*3,0);
				g.drawLine(w*3,h,w*4,h);
			}
			
		}
		
	}
	
	public class Led extends Module 
		implements PortListener{

		private static final long serialVersionUID = -1242433564892429658L;

		public Led(){
			setPreferredSize(new Dimension(40,20));
		}
		
		public void setPort(Port port) {
			if (this.port != null){
				this.port.removeOutputListener(this);
			}
			this.port = port;
			if (this.port != null){
				this.port.addOutputListener(this);
			}
		}

		protected void paintComponent(Graphics g){
			
			int radius = Math.min(getWidth()-1, getHeight()-1)/2;
			
			int h = getHeight()/2;
			int w = getWidth()/2;
			
			g.setColor(port != null && port.getState() ? Color.RED : Color.GRAY);
			g.fillOval(w-radius, h-radius, radius*2, radius*2);
			g.setColor(Color.BLACK);
			g.drawOval(w-radius, h-radius, radius*2, radius*2);
		}

		@Override
		public void portStateChanged(PortEvent event) {
			repaint();
		}

		@Override
		public void portDirectionChanged(PortEvent event) {
			
		}
	}

	@Override
	public void portStateChanged(PortEvent event) {
	}

	@Override
	public void portDirectionChanged(PortEvent event) {
		Port port = (Port)event.getSource();
		for (int i=0;i<modules.length;i++){
			if (modules[i].getPort() == port){
				slots[i].remove(modules[i]);
				modules[i].setPort(null);
				if (port.getType() == Port.Type.INPUT){
					modules[i] = createModule(port);
				} else {
					modules[i] = new Led();
				}
				modules[i].setPort(port);
				slots[i].add(modules[i]);
				
				optionsMenu.remove(i);
				optionsMenu.insert(createMenu(port,modules[i].getClass().getSimpleName()), i);
				
			}
		}
		validate();
		repaint();
	}
	
	private class SetOutputTypeAction extends AbstractAction {

		private static final long serialVersionUID = 6705543871996396400L;

		private String type;
		private Port port;
		
		public SetOutputTypeAction(Port port,String type, String name){
			super(name);
			this.type = type;
			this.port = port;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			for (int i=0;i<modules.length;i++){
				if (modules[i].getPort() == port){
					modules[i].setPort(null);
					slots[i].remove(modules[i]);
					modules[i] = createModule(type);
					modules[i].setPort(port);
					slots[i].add(modules[i]);
					slots[i].invalidate();
					slots[i].repaint();
					break;
				}
			}
			validate();
			storePortType(port.getName(),type);
		}
		
	}

	private JMenuItem createMenu(Port port, String current) {
		JMenu item = new JMenu(port.getName());
		
		if (port.getType() == Port.Type.INPUT){
			
			ButtonGroup group = new ButtonGroup();
			
			JRadioButtonMenuItem item1 = new JRadioButtonMenuItem(new SetOutputTypeAction(port,"switch","Switch"));
			item1.setSelected("Switch".equals(current));
			JRadioButtonMenuItem item2 = new JRadioButtonMenuItem(new SetOutputTypeAction(port,"button","Button"));
			item2.setSelected("Button".equals(current));
			group.add(item1);
			group.add(item2);
			
			item.add(item1);
			item.add(item2);
		}
		
		return item;
	}
	private Module createModule(String type) {
		if ("button".equals(type)){
			return new Button();
		} else if ("switch".equals(type)){
			return new Switch();
		}
		return null;
	}
	
	private Module createModule(Port port){
		String defaultType = getPortType(port.getName(),"switch");
		return createModule(defaultType);
	}
	
	private void storePortType(String portName, String portType){
		Preferences.userNodeForPackage(getClass()).node("io").put(portName+".type", portType);
	}
	
	private String getPortType(String portName, String defaultType){
		return Preferences.userNodeForPackage(getClass()).node("io").get(portName+".type", defaultType);
	}
}
