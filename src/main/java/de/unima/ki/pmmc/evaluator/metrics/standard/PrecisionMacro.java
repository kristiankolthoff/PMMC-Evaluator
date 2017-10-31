package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Compute the macro precision over a list of characteristics. The
 * macro precision is the average of all the precision values of
 * all characteristics. Note that this metric can be easily biased
 * if the test sets are not equally large.
 */
public class PrecisionMacro implements Metric {

	
	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, c -> {return c.getPrecision();});
	}

	@Override
	public String getName() {
		return "prec-mac";
	}

}
