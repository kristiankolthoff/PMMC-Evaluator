package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class PrecisionMicro implements Metric{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMicro(characteristics, 
				c -> {return (double) c.getTP().size();}, 
				c -> {return (double) (c.getTP().size() + c.getFP().size());});
	}

	@Override
	public String getName() {
		return "prec-micro";
	}

}
