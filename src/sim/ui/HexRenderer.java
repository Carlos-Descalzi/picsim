package sim.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import sim.HexUtil;

public class HexRenderer extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = 6851534336241239084L;

	private int size;
	public HexRenderer(int size){
		this.size = size;
		this.setOpaque(true);
		this.setHorizontalAlignment(JLabel.RIGHT);
		this.setFont(CommonResources.MONOSPACE_FONT_SMALLER);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if (isSelected){
			setBackground(table.getSelectionBackground());
			setForeground(table.getSelectionForeground());
			setOpaque(true);
		} else {
			setBackground(table.getBackground());
			setForeground(table.getForeground());
			setOpaque(false);
		}

		if (value == null){
			setText("");
		} else {
			int intValue = ((Number)value).intValue();
			setText(HexUtil.toHex(intValue,size));
		}
		return this;
	}

}
