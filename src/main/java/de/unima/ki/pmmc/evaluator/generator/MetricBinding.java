package de.unima.ki.pmmc.evaluator.generator;

import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MetricBinding {

	public Metric metric;
	public double result;
	
	
	public MetricBinding(Metric metric, double result) {
		this.metric = metric;
		this.result = result;
	}

	public Metric getMetric() {
		return metric;
	}


	public void setMetric(Metric metric) {
		this.metric = metric;
	}


	public double getResult() {
		return result;
	}


	public void setResult(double result) {
		this.result = result;
	}
	
	
}
