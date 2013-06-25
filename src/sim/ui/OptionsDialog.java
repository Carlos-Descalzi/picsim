package sim.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import sim.ui.util.PanelBuilder;
import sim.ui.util.UiUtils;
import ar.com.da.swing.actions.ActionFactory;
import ar.com.da.swing.actions.ActionMethod;

public class OptionsDialog extends JDialog {

	private static final long serialVersionUID = 4788553211974733844L;

	private JComboBox lookAndFeel = new JComboBox();
	private Action okAction = ActionFactory.create(this, "ok");
	private Action cancelAction = ActionFactory.create(this,"cancel");
	
	public OptionsDialog(Frame owner){
		super(owner,true);
		setLayout(new BorderLayout());
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(buttonsPanel,BorderLayout.SOUTH);
		
		buttonsPanel.add(new JButton(okAction));
		buttonsPanel.add(new JButton(cancelAction));
		
		JTabbedPane tabs = new JTabbedPane();
		add(tabs,BorderLayout.CENTER);
		
		tabs.addTab("UI", createPreferencesPanel());
		setSize(500,400);
		UiUtils.center(this);
	}
	
	
	private JPanel createPreferencesPanel() {
		
		ListCellRenderer renderer = new DefaultListCellRenderer(){
			private static final long serialVersionUID = -4114933821275189614L;
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				LookAndFeelInfo info = (LookAndFeelInfo)value;
				return super.getListCellRendererComponent(list, info.getName(), index, isSelected,
						cellHasFocus);
			}
		};
		
		
		lookAndFeel.setModel(new DefaultComboBoxModel(UIManager.getInstalledLookAndFeels()));
		lookAndFeel.setRenderer(renderer);

		JPanel uiOptions = PanelBuilder.newFormLayout()
			.addToForm(lookAndFeel, "Look&Feel")
			.getPanel();

		return uiOptions;
	}
	
	@ActionMethod(name="Ok")
	public void ok(){
		apply();
		setVisible(false);
	}
	
	@ActionMethod(name="Cancel")
	public void cancel(){
		setVisible(false);
	}
	
	private void apply(){
		saveUiOptions();
	}
	
	private void saveUiOptions() {
		LookAndFeelInfo info = (LookAndFeelInfo)lookAndFeel.getSelectedItem();
		Preferences.userNodeForPackage(getClass()).node("ui").put("lookandfeel", info.getClassName());
		try {
			UIManager.setLookAndFeel(info.getClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
