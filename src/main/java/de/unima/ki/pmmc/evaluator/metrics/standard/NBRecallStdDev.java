package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;
/**
 * Computes the standard deviation of the non-binary recall over
 * a list of characteristics. As a reference for the average value,
 * the non-binary macro recall is used.
 * @param characteristics the characteristics to compute the standard deviation of the non-binary recall
 * @return standard deviation of non-binary recall
 */
public class NBRecallStdDev implements Metric{

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics, 
				new NBRecallMacro()::compute, 
				c -> {return c.getNBRecall();});
	}

	@Override
	public String getName() {
		return "nb-rec-std-dev";
	}

}
