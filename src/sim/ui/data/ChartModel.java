package sim.ui.data;

public interface ChartModel {

	public int getLenght();
	
	public int getLineCount();
	
	public String getLineLabel(int lineIndex);
	
	public boolean[] getValues(int slice);
	
	public void addListener(ChartModelListener listener);
	
	public void removeListener(ChartModelListener listener);
}
