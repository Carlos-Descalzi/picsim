package sim.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileFilter;

import sim.io.HexReader;
import sim.mcu.Microcontroller;
import sim.mcu.MicrocontrollerEvent;
import sim.mcu.MicrocontrollerListener;
import sim.mcu.Program;
import sim.mcu.p16f84a.Pic16f84a;
import sim.ui.data.DataViewer;
import sim.ui.program.ProgramViewer;
import ar.com.da.swing.actions.ActionFactory;
import ar.com.da.swing.actions.ActionMethod;

public class MainFrame 
	extends JFrame
	implements MicrocontrollerListener{

	private static final long serialVersionUID = 8876584517269342361L;

	public static void main(String[] argv){
		
		String lnfClassName = Preferences.userNodeForPackage(MainFrame.class).node("ui").get("lookandfeel", "");
		
		if (!"".equals(lnfClassName)){
			try {
				UIManager.setLookAndFeel(lnfClassName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}
	private static final FileFilter HEX_FILE_FILTER = new FileFilter() {
		
		@Override
		public String getDescription() {
			return "HEX Files";
		}
		
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().endsWith(".hex");
		}
	};

	private Action openAction = ActionFactory.create(this,"openFile");
	private Action resetAction = ActionFactory.create(this,"reset");
	private Action stepOverAction = ActionFactory.create(this,"stepOver");
	private Action stepIntoAction = ActionFactory.create(this,"stepInto");
	private Action stepReturnAction = ActionFactory.create(this,"stepOut");
	private Action runToLineAction = ActionFactory.create(this,"runToLine");
	private Action runAction = ActionFactory.create(this,"run");
	private Action runStepped = ActionFactory.create(this,"runStepped");
	private Action stopAction = ActionFactory.create(this,"stop");
	private Action exitAction = ActionFactory.create(this,"exit");
	private Action preferencesAction = ActionFactory.create(this,"showPreferencesDialog");
	private MemoryViewer memoryViewer = new MemoryViewer();
	private ProgramViewer programViewer = new ProgramViewer();
	private RegisterViewer stackViewer = new RegisterViewer();
	private IoModule ioModule = new IoModule();
	private BreakpointViewer breakpointViewer = new BreakpointViewer();
	private MessagesViewer messagesViewer = new MessagesViewer();
	private DataViewer dataViewer = new DataViewer();
	private JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
	private JDesktopPane debugDesktop = new JDesktopPane();
	private Microcontroller mcu;
	private Program currentProgram = null;
	
	public MainFrame(){
		setUpUI();
		setupMenuBar();
		setupToolBar();
		setupBindings();
		setMcu(new Pic16f84a());
		debugDesktop.getDesktopManager().activateFrame(programViewer);
		updateActionsState();
	}
	private void updateActionsState() {
		boolean hasProgram = currentProgram != null;
		boolean running = mcu != null && mcu.isRunning();
		resetAction.setEnabled(hasProgram);
		stepOverAction.setEnabled(hasProgram && !running);
		stepIntoAction.setEnabled(hasProgram && !running);
		stepReturnAction.setEnabled(hasProgram && !running);
		runToLineAction.setEnabled(hasProgram && !running);
		runAction.setEnabled(hasProgram && !running);
		stopAction.setEnabled(hasProgram && running);
		runStepped.setEnabled(hasProgram && !running);
	}

	private void setupBindings() {

		for (JInternalFrame frame:new JInternalFrame[]{memoryViewer,programViewer,stackViewer,ioModule,breakpointViewer}){
			frame.getActionMap().put("open", openAction);
			frame.getActionMap().put("reset", resetAction);
			frame.getActionMap().put("stop", stopAction);
			frame.getActionMap().put("stepOver", stepOverAction);
			frame.getActionMap().put("stepInto", stepIntoAction);
			frame.getActionMap().put("stepReturn", stepReturnAction);
			frame.getActionMap().put("runToLine", runToLineAction);
			frame.getActionMap().put("run", runAction);
			frame.getActionMap().put("exit", exitAction);
			
			frame.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "run");
			frame.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "stepInto");
			frame.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "stepOver");
			frame.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "stepReturn");
			frame.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), "runToLine");
			
			frame.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0), "stop");
			frame.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "reset");
			frame.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "open");
			frame.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK), "exit");
		}
		
		
	}

	private void setUpUI(){
		setSize(getToolkit().getScreenSize());
		setLayout(new BorderLayout());
		add(debugDesktop,BorderLayout.CENTER);
		
		FrameListener listener = new FrameListener();
		
		for (JInternalFrame frame:new JInternalFrame[]{
				memoryViewer,stackViewer,programViewer,ioModule,breakpointViewer,messagesViewer,dataViewer}){
			debugDesktop.add(frame);
			restoreState(frame);
			frame.setVisible(true);
			frame.addInternalFrameListener(listener);
			frame.addComponentListener(listener);
		}

		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);

	}

	private void restoreState(JInternalFrame frame) {
		
		Preferences prefs = Preferences.userNodeForPackage(getClass())
			.node("windows");
		
		try {
			if (prefs.nodeExists(frame.getClass().getSimpleName())){
				prefs = prefs.node(frame.getClass().getSimpleName());
				
				String state = prefs.get("state","");
				
				try {
					if ("iconified".equals(state)){
						frame.setIcon(true);
					} else if ("maximized".equals(state)){
						frame.setMaximum(true);
					}
				} catch (Exception ex){}
				
				int x = prefs.getInt("x", Integer.MIN_VALUE);
				int y = prefs.getInt("y", Integer.MIN_VALUE);
				int w = prefs.getInt("w", Integer.MIN_VALUE);
				int h = prefs.getInt("h", Integer.MIN_VALUE);
				
				if (x !=Integer.MIN_VALUE && y != Integer.MIN_VALUE){
					frame.setLocation(x,y);
				}
				
				if (w != Integer.MIN_VALUE && h != Integer.MIN_VALUE){
					frame.setSize(w,h);
				}
			}
		} catch (BackingStoreException e) {
		}
		
	}

	private void setupToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.add(openAction);
		toolBar.addSeparator();
		toolBar.add(resetAction);
		toolBar.add(stepOverAction);
		toolBar.add(stepIntoAction);
		toolBar.add(stepReturnAction);
		toolBar.add(runToLineAction);
		toolBar.add(runAction);
		toolBar.add(runStepped);
		toolBar.add(stopAction);
		add(toolBar,BorderLayout.NORTH);
	}

	private void setupMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		addToMenu(fileMenu,openAction);
		fileMenu.addSeparator();
		addToMenu(fileMenu,exitAction);
		
		JMenu debugMenu = new JMenu("Debug");
		menuBar.add(debugMenu);
		addToMenu(debugMenu,resetAction);
		addToMenu(debugMenu,stepOverAction);
		addToMenu(debugMenu,stepIntoAction);
		addToMenu(debugMenu,stepReturnAction);
		addToMenu(debugMenu,runToLineAction);
		addToMenu(debugMenu,runAction);
		addToMenu(debugMenu,stopAction);
		
		JMenu optionsMenu = new JMenu("Options");
		menuBar.add(optionsMenu);
		addToMenu(optionsMenu,preferencesAction);
		
		setJMenuBar(menuBar);
	}
	
	private void addToMenu(JMenu menu,Action action){
		JMenuItem item = menu.add(action);
		Object mnemonic = action.getValue(Action.MNEMONIC_KEY);
		if (mnemonic instanceof Integer){
			item.setMnemonic((Integer)mnemonic);	
		} else if (mnemonic instanceof Character){
			item.setMnemonic((Character)mnemonic);
		}
	}
	
	@ActionMethod(name="Reset",icon="/reset.png",accelerator="F11")
	public void reset() {
		mcu.reset();
	}
	
	@ActionMethod(name="Step Over",icon="/step-over.png",accelerator="F7")
	public void stepOver() {
		mcu.stepOver();
	}

	@ActionMethod(name="Step Into",icon="/step-into.png",accelerator="F6")
	public void stepInto() {
		mcu.step();
	}

	@ActionMethod(name="Step return",icon="/step-return.png",accelerator="F8")
	public void stepOut() {
		mcu.stepOut();
	}
	
	@ActionMethod(name="Run to line",icon="/run-to-line.png",accelerator="F9")
	public void runToLine() {
		mcu.runToPc(programViewer.getCurrentLine());
	}
	
	@ActionMethod(name="Run",icon="/run.png",accelerator="F5")
	public void run() {
		mcu.run();
	}
	
	@ActionMethod(name="Run Stepped",icon="/run-stepped.png")
	public void runStepped() {
		mcu.runStepByStep();
	}
	
	@ActionMethod(name="Stop",icon="/stop.png")
	public void stop() {
		mcu.stop();
	}
	
	@ActionMethod(name="Open")
	public void openFile(){
		fileChooser.setFileFilter(HEX_FILE_FILTER);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
			File file = fileChooser.getSelectedFile();
			try {
				Program program = new HexReader().read(file);
				setProgram(program);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setProgram(Program program){
		currentProgram = program;
		mcu.setProgram(program);
		updateActionsState();
	}
	
	public Microcontroller getMcu() {
		return mcu;
	}

	public void setMcu(Microcontroller mcu) {
		if (this.mcu != null){
			this.mcu.removeListener(this);
		}
		this.mcu = mcu;
		if (this.mcu != null){
			this.mcu.addListener(this);
		}
		memoryViewer.setMcu(mcu);
		programViewer.setMcu(mcu);
		stackViewer.setMcu(mcu);
		ioModule.setMcu(mcu);
		breakpointViewer.setMcu(mcu);
		messagesViewer.setMcu(mcu);
		dataViewer.setMcu(mcu);
	}
	@Override
	public void breakpointReached(MicrocontrollerEvent event) {
	}
	@Override
	public void breakpointAdded(MicrocontrollerEvent event) {
	}
	@Override
	public void breakpointRemoved(MicrocontrollerEvent event) {
	}
	@Override
	public void breakpointChanged(MicrocontrollerEvent event) {
	}
	@Override
	public void registerChanged(MicrocontrollerEvent event) {
	}
	@Override
	public void executionStarted(MicrocontrollerEvent event) {
		updateActionsState();
	}
	@Override
	public void executionFinished(MicrocontrollerEvent event) {
		updateActionsState();
	}
	@Override
	public void programLoaded(MicrocontrollerEvent event) {
	}
	

	private class FrameListener implements ComponentListener, InternalFrameListener {

		private void saveState(Object source, boolean iconified, boolean maximized, Dimension size, Point position){
			
			Preferences prefs = Preferences.userNodeForPackage(getClass())
				.node("windows")
				.node(source.getClass().getSimpleName());
			
			prefs.put("state", iconified ? "iconified" : maximized ? "maximized" : "normal");
			
			prefs.putInt("x", position.x);
			prefs.putInt("y", position.y);
			prefs.putInt("w", size.width);
			prefs.putInt("h", size.height);
			
		}

		@Override
		public void internalFrameIconified(InternalFrameEvent e) {
			saveState(e.getSource(), true, false, e.getInternalFrame().getSize(), e.getInternalFrame().getLocation());
		}

		@Override
		public void internalFrameDeiconified(InternalFrameEvent e) {
			saveState(e.getSource(), false, false, e.getInternalFrame().getSize(), e.getInternalFrame().getLocation());
		}

		@Override
		public void componentResized(ComponentEvent e) {
			JInternalFrame frame = (JInternalFrame)e.getSource();
			saveState(frame, false, false, frame.getSize(), frame.getLocation());
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			JInternalFrame frame = (JInternalFrame)e.getSource();
			saveState(frame, false, false, frame.getSize(), frame.getLocation());
		}
		
		@Override
		public void internalFrameOpened(InternalFrameEvent e) {	}
		@Override
		public void internalFrameClosing(InternalFrameEvent e) { }
		@Override
		public void internalFrameClosed(InternalFrameEvent e) { }
		@Override
		public void internalFrameActivated(InternalFrameEvent e) {	}
		@Override
		public void internalFrameDeactivated(InternalFrameEvent e) { }
		@Override
		public void componentShown(ComponentEvent e) { }
		@Override
		public void componentHidden(ComponentEvent e) {	}
		
	}

	@ActionMethod(name="Preferences")
	public void showPreferencesDialog(){
		new OptionsDialog(MainFrame.this).setVisible(true);
	}

	
	@ActionMethod(name="Build All")
	public void buildAll(){
		
	}
	
	@ActionMethod(name="Build")
	public void build(){
		
	}
	
	@ActionMethod(name="Clean All")
	public void cleanAll(){
		
	}
	
	@ActionMethod(name="Exit")
	public void exit(){
		System.exit(0);
	}
	@Override
	public void stackOverflowDetected(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void watchdogActivated(MicrocontrollerEvent event) {
		// TODO Auto-generated method stub
		
	}
}
