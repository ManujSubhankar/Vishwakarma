package edu.sssihl.mca.vishwakarma.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IBarSeries;
import org.swtchart.ISeries.SeriesType;

/**
 * An example for bar chart.
 */
public class ChartDrawer {

	private double[] ySeries;
	private String[] xSeries;
	private String title= "";
	
	public void setYSeries(int[] ySeries) {
		this.ySeries= new double[ySeries.length];
		for(int i= 0; i < ySeries.length; i++)
			this.ySeries[i]= ySeries[i];
	}
	
	public void setYSeries(double[] ySeries) {
		this.ySeries= ySeries;
		System.out.println(ySeries);
	}
	
	public void setXSeries(String[] xSeries) {
		this.xSeries= xSeries;
	}
	
	public void setTitle(String title) {
		this.title= title;
	}
	

	/**
	 * create the chart.
	 * 
	 * @param parent
	 *            The parent composite
	 * @return The created chart
	 */
	public Chart createChart(Composite parent) {

		// create a chart
		Chart chart = new Chart(parent, SWT.NONE);

		// set titles
		chart.getAxisSet().getXAxis(0).getTitle().setText("Data Points");
		chart.getAxisSet().getYAxis(0).getTitle().setText("Amplitude");
		
		chart.getAxisSet().getXAxis(0).setCategorySeries(xSeries);
		chart.getAxisSet().getXAxis(0).enableCategory(true);
		// create bar series
		IBarSeries barSeries = (IBarSeries) chart.getSeriesSet().createSeries(
				SeriesType.BAR, "Metrics Violations");
		barSeries.setYSeries(ySeries);
		//barSeries.setXSeries(new double[] {1,2,3,4,5,6});


		// adjust the axis range
		chart.getAxisSet().adjustRange();
		chart.getTitle().setText(title);

		return chart;
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Bar Chart");
		shell.setSize(500, 400);
		shell.setLayout(new FillLayout());
		ChartDrawer ch= new ChartDrawer();
		ch.setYSeries(new double[] { 0.2, 1.1, 1.9 });
		ch.setXSeries(new String[] {"asd","asdas","qwe"});
		ch.createChart(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
