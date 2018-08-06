package de.unima.ki.pmmc.evaluator.metrics.sorting;

import java.util.Comparator;
import java.util.NoSuchElementException;

import de.unima.ki.pmmc.evaluator.data.Report;
import de.unima.ki.pmmc.evaluator.generator.MetricBinding;
import de.unima.ki.pmmc.evaluator.generator.MetricGroupBinding;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MetricSort implements Comparator<Report> {

	private Metric metric;
	private boolean desc;
	
	public MetricSort(Metric metric, boolean desc) {
		this.metric = metric;
		this.desc = desc;
	}

	@Override
	public int compare(Report report1, Report report2) {
		double metricValue1 = getMetricValue(report1, metric);
		double metricValue2 = getMetricValue(report2, metric);
		int comparision = Double.compare(metricValue1, metricValue2);
		return desc ? -comparision : comparision;
//		double diff = metricValue1 - metricValue2;
//		if(diff < 0) {
//			if(desc) return 1;
//			else return -1;
//		}
//		else if(diff > 0) {
//			if(desc) return -1;
//			else return 1;
//		}
//		else return 0;
	}
	
	private double getMetricValue(Report report, Metric metric) {
		for(MetricGroupBinding metricGroupBinding : report.getBindings()) {
			for(MetricBinding metricBinding : metricGroupBinding) {
				if(metricBinding.getMetric().equals(metric)) {
					return metricBinding.getValue();
				}
			}
		}
		throw new NoSuchElementException("The metric " + metric.getName() + 
				" was not found in the configuration");
	}

}
