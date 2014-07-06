package sim.ui.data;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractChartModel implements ChartModel {

	private final List<ChartModelListener> listeners = new ArrayList<>();

	protected void fireModelChanged(){
		ChartModelEvent event = new ChartModelEvent(this);
		for (ChartModelListener listener: listeners){
			listener.dataChanged(event);
		}
	}
	
	
	@Override
	public void addListener(ChartModelListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(ChartModelListener listener) {
		listeners.remove(listener);
	}

}
