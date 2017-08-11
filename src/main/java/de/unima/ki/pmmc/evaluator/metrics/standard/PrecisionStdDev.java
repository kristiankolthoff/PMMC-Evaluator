package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Computes the standard deviation of the precision over
 * a list of characteristics. As a reference for the average value,
 * the macro precision is used.
 */
public class PrecisionStdDev implements Metric{

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics, 
				new PrecisionMacro()::compute, 
				c -> {return c.getPrecision();});
	}

	@Override
	public String getName() {
		return "prec-std-dev";
	}

	
}
