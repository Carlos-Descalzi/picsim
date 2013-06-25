package sim.ui.data;

import java.util.EventListener;

public interface ChartModelListener extends EventListener {

	public void dataChanged(ChartModelEvent event);
}
