package de.unima.ki.pmmc.evaluator.generator;

import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MetricBinding {

	public Metric metric;
	public double value;
	
	
	public MetricBinding(Metric metric, double result) {
		this.metric = metric;
		this.value = result;
	}

	public Metric getMetric() {
		return metric;
	}


	public void setMetric(Metric metric) {
		this.metric = metric;
	}


	public double getValue() {
		return value;
	}


	public void setValue(double result) {
		this.value = result;
	}
	
	
}
