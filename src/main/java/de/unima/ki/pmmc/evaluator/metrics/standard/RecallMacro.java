package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Compute the macro recall over a list of characteristics. The
 * macro recall is the average of all the recall values of
 * all characteristics. Note that this metric can be easily biased
 * if the testsets are not equally large.
 */
public class RecallMacro implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, c -> {return c.getRecall();});
	}

	@Override
	public String getName() {
		return "rec-mac";
	}

}
