package sim.ui.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public class ActionFactory {

	public static Action create(Object target, String method,String name, String accel, String icon){
		return new InvokerAction(name, accel, icon, target, method);
	}
	
	@SuppressWarnings("serial")
	private static class InvokerAction extends AbstractAction {
		private Object target;
		private String method;
		public InvokerAction(String name, String accel, String icon, Object target, String method){
			super(name);
			this.target = target;
			this.method = method;
			if (icon != null){
				putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(icon)));
			}
			if (accel != null){
				putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(accel));
			}
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				target.getClass().getMethod(method).invoke(target);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
}
