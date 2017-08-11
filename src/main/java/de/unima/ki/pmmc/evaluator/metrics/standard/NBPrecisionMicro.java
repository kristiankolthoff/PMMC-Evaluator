package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class NBPrecisionMicro implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMicro(characteristics, 
				c -> {return c.getConfSumCorrect();}, 
				c -> {return c.getFP().size() + c.getConfSumCorrect();});
	}

	@Override
	public String getName() {
		return null;
	}

}
