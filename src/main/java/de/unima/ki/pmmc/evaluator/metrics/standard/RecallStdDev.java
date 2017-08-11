package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Computes the standard deviation of the recall over
 * a list of characteristics. As a reference for the average value,
 * the macro recall is used.
 */
public class RecallStdDev implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics, 
				new RecallMacro()::compute, 
				c -> {return c.getRecall();});
	}

	@Override
	public String getName() {
		return "rec-std-dev";
	}

}
