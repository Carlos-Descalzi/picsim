package sim.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;

public class BitSetRenderer extends JPanel implements TableCellRenderer {

	private static final long serialVersionUID = -7292773165941997555L;

	private final Bit[] bits = new Bit[8];

	private class Bit extends JPanel {

		private static final long serialVersionUID = 2328025747570911343L;

		private JLabel value = new JLabel();
		
		public Bit(){
			value.setOpaque(true);
			setLayout(new GridLayout(1,1));
			add(value);
		}
		
		public void setName(String name){
			removeAll();
			setLayout(new GridLayout(2,1));
			JLabel label = new JLabel(name);
			label.setFont(new Font("Courier",Font.PLAIN,10));
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setBorder(BOTTOM_BORDER);
			add(label);
			add(value);
		}
		
		public void set(boolean set){
			if (set) set(); else clear();
		}
		
		public void set(){
			value.setBackground(Color.BLACK);
		}
		
		public void clear(){
			value.setBackground(Color.WHITE);
		}
	}
	

	public BitSetRenderer(){
		
		setLayout(new GridLayout(1,8));
		for (int i=0;i<8;i++){
			bits[i] = new Bit();
			bits[i].setBorder(i < 7 ? THREE_FACES_BORDER : FULL_BORDER);
			add(bits[i]);
		}
	}
	
	public void setBitNames(String[] bitNames){
		for (int i=0;i<bitNames.length;i++){
			bits[i].setName(bitNames[i]);
		}
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		int intValue = ((Number)value).intValue();
		Color unsetColor = isSelected ? table.getSelectionBackground() : table.getBackground();
		setValue(intValue, unsetColor);
		
		return this;
	}
	
	public void setValue(int value){
		setValue(value,Color.WHITE);
	}
	
	private void setValue(int intValue, Color unsetColor) {
		for (int i=0;i<8;i++){
			bits[i].set((intValue & (1 << (7-i))) != 0);
		}
	}

	private static Border FULL_BORDER = BorderFactory.createLineBorder(Color.BLACK);
	private static Border THREE_FACES_BORDER = new Border() {
		
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width,
				int height) {
			g.drawLine(x, y, x+width-1, y);
			g.drawLine(x, y+height-1, x+width-1, y+height-1);
			g.drawLine(x, y, x, y+height-1);
		}
		
		@Override
		public boolean isBorderOpaque() {
			return false;
		}
		
		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(1,1,1,0);
		}
	};
	private static Border BOTTOM_BORDER = new Border() {
		
		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width,
				int height) {
			g.drawLine(x, y+height-1, x+width-1, y+height-1);
		}
		
		@Override
		public boolean isBorderOpaque() {
			return false;
		}
		
		@Override
		public Insets getBorderInsets(Component c) {
			return new Insets(0,0,1,0);
		}
	};


}
