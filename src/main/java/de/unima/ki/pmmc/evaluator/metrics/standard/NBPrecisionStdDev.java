package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Computes the standard deviation of the non-binary precision over
 * a list of characteristics. As a reference for the average value,
 * the non-binary macro precision is used.
 */
public class NBPrecisionStdDev implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics, 
				new NBPrecisionMacro()::compute,
				c -> {return c.getNBPrecision();});
	}

	@Override
	public String getName() {
		return "nb-prec-std-dev";
	}

}
