package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Compute the micro recall over a list of characteristics. 
 * Avoids biasing the value by unequally large data sets.
 */
public class NBRecallMicro implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMicro(characteristics, 
				c -> {return c.getConfSumCorrect();}, 
				c -> {return c.getConfSumReference();});
	}

	@Override
	public String getName() {
		return "nb-rec-mic";
	}

}
