package sim.ui.util;

import java.awt.Dimension;
import java.awt.Window;

public class UiUtils {

	public static void center(Window window){
		Dimension size = window.getToolkit().getScreenSize();
		
		window.setLocation((size.width-window.getWidth())/2,(size.height-window.getHeight())/2);
	}
}
