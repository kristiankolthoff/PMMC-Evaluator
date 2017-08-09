package de.unima.ki.pmmc.evaluator.metrics;

import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionStdDev;

public class MetricGroupFactory {

	public MetricGroupFactory() {
		
	}
	
	public static MetricGroupFactory getInstance() {
		return new MetricGroupFactory();
	}
	
	public MetricGroup create(String group) {
		if(group.equals(MetricGroup.PRECISION_GROUP)) {
			return new MetricGroup("precision", 
					new PrecisionMicro(),
					new PrecisionMicro(),
					new PrecisionStdDev());
		}
		return new MetricGroup("");
	}
	
}
