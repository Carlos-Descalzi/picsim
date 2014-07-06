package sim.ui.data;

import java.util.ArrayList;
import java.util.List;

import sim.mcu.Microcontroller;
import sim.mcu.Port;

public class DataCapturer implements Runnable {
	
	private long delay = 20;
	private Thread thread;
	private List<boolean[]> values = new ArrayList<boolean[]>();
	private Microcontroller mcu;
	private final boolean[] dummySlice;
	private BaseModel realTimeModel = new BaseModel() {
		
		@Override
		public boolean[] getValues(int slice) {
			if (slice >= values.size()){
				return dummySlice;
			} else if (values.size() < 50){
				return values.get(slice);
			} else {
				return values.subList(values.size()-50, values.size()).get(slice);
			}
		}
		
		@Override
		public int getLenght() {
			return 50;
		}
	};
	private BaseModel historyModel = new BaseModel(){
		@Override
		public boolean[] getValues(int slice) {
			if (slice >= values.size()){
				return dummySlice;
			} 
			return values.get(slice);
		}
		
		@Override
		public int getLenght() {
			return Math.max(values.size(), 50);
		}
	};
	public DataCapturer(Microcontroller mcu){
		this.mcu = mcu;
		dummySlice = new boolean[mcu.getPorts().length];
	}
	
	public void start(){
		thread = new Thread(this);
		thread.start();
		values.clear();
	}
	
	public void stop(){
		thread.interrupt();
		try {
			thread.join();
		}catch (Exception ex){
		}
	}
	
	public void run(){
		while (true){
			for (int i=0;i<50;i++){
				values.add(capture());
				try {
					Thread.sleep(delay);
				}catch (InterruptedException ex){
					realTimeModel.fireModelChanged();
					historyModel.fireModelChanged();
					return;
				}
			}
			realTimeModel.fireModelChanged();
			historyModel.fireModelChanged();
		}
	}

	private abstract class BaseModel extends AbstractChartModel {

		@Override
		public String getLineLabel(int lineIndex) {
			return mcu.getPorts()[lineIndex].getName();
		}
		
		@Override
		public int getLineCount() {
			return mcu.getPorts().length;
		}
	}
	
	public ChartModel getRealTimeModel(){
		return realTimeModel;
	}
	
	public ChartModel getHistoryModel(){
		return historyModel;
	}
	
	public void clear(){
		values.clear();
		realTimeModel.fireModelChanged();
		historyModel.fireModelChanged();
	}
	
	private boolean[] capture() {
		Port[] ports = mcu.getPorts();
		boolean[] values = new boolean[ports.length];
		
		for (int i=0;i<values.length;i++){
			values[i] = ports[i].getState();
		}
		
		return values;
	}


}