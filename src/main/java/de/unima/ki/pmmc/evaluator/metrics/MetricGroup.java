package de.unima.ki.pmmc.evaluator.metrics;

import java.util.Arrays;
import java.util.List;

public enum MetricGroup {

	PRECISION("Precision", 
			Arrays.asList(new Metrics[]{Metrics.PRECISION_MICRO, Metrics.PRECISION_MACRO, Metrics.PRECISION_STD}));
	
	private String name;
	private List<Metrics> metrics;
	
	MetricGroup(String name, List<Metrics> metrics) {
		this.name = name;
		this.metrics = metrics;
	}
}
