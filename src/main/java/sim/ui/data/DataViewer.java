package sim.ui.data;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import sim.mcu.Microcontroller;
import sim.mcu.MicrocontrollerEvent;
import sim.mcu.MicrocontrollerListener;
import sim.mcu.Port;
import sim.ui.util.PanelBuilder;

@SuppressWarnings("serial")
public class DataViewer extends JInternalFrame 
	implements MicrocontrollerListener{

	private final DataChart realTimeChart = new DataChart();
	private final DataChart historyChart = new DataChart();
	private JScrollPane scroller = new JScrollPane(historyChart, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,  JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	private JTabbedPane tabbedPane = new JTabbedPane();
	private DataCapturer dataCapturer;
	public DataViewer(){
		super("IO Timeline",true,false,true,true);
		setLayout(new BorderLayout());
		add(tabbedPane);
		historyChart.setFixedSize(true);
		realTimeChart.setFixedSize(false);
		tabbedPane.add("Real time",buildRealTimeView());
		tabbedPane.add("Log",buildLogView());
		setSize(600,400);
	}
	
	private JPanel buildRealTimeView(){
		return PanelBuilder.newBorderLayout()
			.addAtCenter(realTimeChart)
			.getPanel();
	}
	
	private JPanel buildLogView(){
		return PanelBuilder.newBorderLayout()
			.addAtCenter(scroller)
			.getPanel();
	}
	
	private Microcontroller mcu;
	
	public void setMcu(Microcontroller mcu) {
		this.mcu = mcu;
		Port[] ports = mcu.getPorts();
		String[] labels = new String[ports.length];
		
		for (int i=0;i<labels.length;i++){
			labels[i] = ports[i].getName();
		}
		dataCapturer = new DataCapturer(mcu);
		realTimeChart.setChartModel(dataCapturer.getRealTimeModel());
		historyChart.setChartModel(dataCapturer.getHistoryModel());
		this.mcu.addListener(this);
	}

	
	private void startCapture(){
		dataCapturer.start();
	}
	
	private void stopCapture(){
		dataCapturer.stop();
	}
	@Override
	public void breakpointReached(MicrocontrollerEvent event) {
		stopCapture();
	}

	@Override
	public void breakpointAdded(MicrocontrollerEvent event) {}
	@Override
	public void breakpointRemoved(MicrocontrollerEvent event) {}
	@Override
	public void breakpointChanged(MicrocontrollerEvent event) {}

	@Override
	public void registerChanged(MicrocontrollerEvent event) {}

	@Override
	public void executionStarted(MicrocontrollerEvent event) {
		startCapture();
	}

	@Override
	public void executionFinished(MicrocontrollerEvent event) {
		stopCapture();
	}

	@Override
	public void programLoaded(MicrocontrollerEvent event) {
	}

	@Override
	public void stackOverflowDetected(MicrocontrollerEvent event) {
		stopCapture();
	}

	@Override
	public void watchdogActivated(MicrocontrollerEvent event) {
		stopCapture();
	}
}
