package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Compute the macro f measure over a list of characteristics. 
 * Avoids biasing the value by unequally large data sets.
 */
public class NBFMeasureMacro implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, c -> {return c.getNBFMeasure();});
	}

	@Override
	public String getName() {
		return "nb-fm-mac";
	}

}
