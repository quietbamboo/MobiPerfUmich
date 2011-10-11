/****************************
 *
 * @Date: Oct 7, 2011
 * @Time: 2:26:55 PM
 * @Author: Junxian Huang
 *
 ****************************/
package com.mobiperf.lte.chart;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Average temperature demo chart.
 */
public class CubicChart extends AbstractChart {
	
	public static double[] index = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
	public double[] tp;
	public double[] rtt;
	
	public CubicChart(double[] tp, double[] rtt){
		this.rtt = new double[index.length];
		this.tp = new double[index.length];
		
		for(int i = 0; i < index.length; i++){
			this.rtt[i] = Double.MIN_VALUE;
			this.tp[i] = Double.MIN_VALUE;
		}
		
		for(int i = 1; i <= rtt.length; i++){
			this.rtt[index.length - i] = rtt[rtt.length - i];
		}
		for(int i = 1; i <= tp.length; i++){
			this.tp[index.length - i] = tp[tp.length - i];
		}
	}
	
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Average temperature";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The average temperature in 4 Greek islands (cubic line chart)";
	}

	public Intent execute(Context context){
		return null;
	}

	public GraphicalView getGraphView(Context context) {
		
		String[] titles = new String[] { "Throughput (kbps)" };
		List<double[]> x = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			x.add(index);
		}
		List<double[]> values = new ArrayList<double[]>();
		values.add(tp);
		int[] colors = new int[] { Color.YELLOW, Color.GREEN };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
				PointStyle.TRIANGLE, PointStyle.SQUARE };
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer(2);
		setRenderer(renderer, colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		}
		setChartSettings(renderer, "Network Performance", "Experiment NO.", "Throughput (kbps)", 0.5, 12.5, 0, 32,
				Color.LTGRAY, Color.LTGRAY);
		renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setZoomButtonsVisible(true);
		renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });

		renderer.setYTitle("Latency (ms)", 1);
		renderer.setYAxisAlign(Align.RIGHT, 1);
		renderer.setYLabelsAlign(Align.LEFT, 1);
		XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
		values.clear();
		values.add(rtt);
		addXYSeries(dataset, new String[] { "Latency (ms)" }, x, values, 1);

		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
			seriesRenderer.setDisplayChartValues(true);
		}
		GraphicalView graphView = ChartFactory.getCubeLineChartView(context,
				dataset, renderer, 0.2f);
		return graphView;
	}



}
