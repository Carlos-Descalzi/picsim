package sim.ui.data;

import ar.com.da.swing.event.EventSupport;

public abstract class AbstractChartModel implements ChartModel {

	private final EventSupport<ChartModelListener> listeners = EventSupport.createSupport(ChartModelListener.class);

	protected void fireModelChanged(){
		listeners.getProxy().dataChanged(new ChartModelEvent(this));
	}
	
	
	@Override
	public void addListener(ChartModelListener listener) {
		listeners.addListener(listener);
	}

	@Override
	public void removeListener(ChartModelListener listener) {
		listeners.removeListener(listener);
	}

}
