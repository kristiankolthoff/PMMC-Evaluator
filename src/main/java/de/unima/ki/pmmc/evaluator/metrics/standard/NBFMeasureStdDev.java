package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Computes the standard deviation of the non-binary f-measure over
 * a list of characteristics. As a reference for the average value,
 * the non-binary f-measure is used.
 */
public class NBFMeasureStdDev implements Metric{

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics, 
				new NBFMeasureMacro()::compute,
				c -> {return c.getNBFMeasure();});
	}

	@Override
	public String getName() {
		return "nb-fm-std-dev";
	}

}
