package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class StdDevSize implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		double mean = new MeanSize().compute(characteristics);
		double sqsum =  characteristics.stream().
				mapToDouble(c -> {return Math.pow(c.getAlignmentMapping().size() - mean, 2);}).
				average().getAsDouble();
		return Math.sqrt(sqsum);
	}

	@Override
	public String getName() {
		return "std-dev-size";
	}

}
