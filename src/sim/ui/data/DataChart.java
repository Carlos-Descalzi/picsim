package sim.ui.data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class DataChart 
	extends JComponent 
	implements ChartModelListener {

	private static final long serialVersionUID = -5626434991464808859L;

	private boolean fixedSize = true;
	private ChartModel chartModel;

	public boolean isFixedSize() {
		return fixedSize;
	}

	public void setFixedSize(boolean fixedSize) {
		this.fixedSize = fixedSize;
	}

	public void setChartModel(ChartModel chartModel){
		if (this.chartModel != null){
			this.chartModel.removeListener(this);
		}
		this.chartModel = chartModel;
		if (this.chartModel != null){
			this.chartModel.addListener(this);
		}
	}
	
	public ChartModel getChartModel(){
		return chartModel;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setColor(getBackground());
		g2d.fillRect(0,0, getWidth(),getHeight());
		
		if (chartModel == null){
			return;
		}

		final int lineCount = chartModel.getLineCount();
		final int length = chartModel.getLenght();
		final float lineHeight = getHeight() / lineCount;
		final float lineOff = lineHeight - 1;
		final float lineOn = 2;
		final float sliceWidth = fixedSize ? 20 : getWidth() / (float)length;
		
		g2d.setColor(new Color(0.7f,0.7f,0.7f));
		for (int i=0;i<lineCount;i++){
			if (i % 2 == 0){
				g2d.fillRect(0, (int)(i*lineHeight), getWidth(), (int)lineHeight);
			}
		}
		g.setColor(getForeground());
		
		int start = 0;

		for (int i=0;i<lineCount;i++){
			String label = chartModel.getLineLabel(i);
			int labelWidth = g.getFontMetrics().stringWidth(label);
			start = Math.max(start,labelWidth);
			g.drawString(label, 0, (int)(lineHeight*i+lineOff));
		}

		g.setColor(new Color(0.4f,0.4f,0.4f));
		
		Stroke stroke = ((Graphics2D)g).getStroke();
		
		((Graphics2D)g).setStroke(new BasicStroke(0.1f,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_BEVEL,0,new float[]{2,2},1));
		
		for (float i=start;i<getWidth();i+=sliceWidth){
			g.drawLine((int)i,0,(int)i,getHeight());
		}
		
		((Graphics2D)g).setStroke(stroke);
		
		g.setColor(getForeground());
		
		float x = start;

		((Graphics2D)g).setStroke(new BasicStroke(2f));
		
		for (int i=0;i<length;i++){
			boolean[] slice = chartModel.getValues(i);
			
			for (int j=0;j<lineCount;j++){
				if ((i == 0 && slice[j]) || (i > 0 && (chartModel.getValues(i-1)[j] ^ slice[j]))){
					g.drawLine((int)x, (int)(lineHeight*j+lineOff),(int) x, (int)(lineHeight*j+lineOn));
				} 
				if (slice[j]){
					g.drawLine((int)x, (int)(lineHeight*j+lineOn),(int)(x+sliceWidth),(int)(lineHeight*j+lineOn));
				} else {
					g.drawLine((int)x, (int)(lineHeight*j+lineOff),(int)(x+sliceWidth),(int)(lineHeight*j+lineOff));
				}
			}
			
			x+=sliceWidth;
		}
		
	}

	@Override
	public void dataChanged(ChartModelEvent event) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				if (isShowing()){
					repaint();
				}
				if (fixedSize){
					setPreferredSize(new Dimension(chartModel.getLenght() * 20,chartModel.getLineCount() * 20));
					validate();
				}
			}
		});
	}
	
}
