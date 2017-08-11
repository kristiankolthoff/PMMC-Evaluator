package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Computes the standard deviation of the f-measure over
 * a list of characteristics. As a reference for the average value,
 * the macro f-measure is used.
 */
public class FMeasureStdDev implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics, new FMeasureMacro()::compute, 
				c -> {return c.getFMeasure();});
	}

	@Override
	public String getName() {
		return "fm-std-dev";
	}

}
