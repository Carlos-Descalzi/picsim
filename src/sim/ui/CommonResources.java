package sim.ui;

import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class CommonResources {
	public static final Font MONOSPACE_FONT = new Font("Courier",Font.PLAIN,12);
	public static final Font MONOSPACE_FONT_SMALLER = new Font("Courier",Font.PLAIN,11);
	public static final Icon BREAKPOINT_ENABLED_ICON = new ImageIcon(CommonResources.class.getResource("/breakpoint-enabled.png")); 
	public static final Icon BREAKPOINT_DISABLED_ICON = new ImageIcon(CommonResources.class.getResource("/breakpoint-disabled.png")); 
}
