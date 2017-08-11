package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class StdDevConfidence implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		double mean = new MeanConfidence().compute(characteristics);
		double sqsum =  characteristics.stream().
				flatMap(c -> {return c.getAlignmentMapping().getCorrespondences().stream();}).
				mapToDouble(corres -> {return Math.pow(corres.getConfidence() - mean, 2);}).
				average().getAsDouble();
		return Math.sqrt(sqsum);
	}

	@Override
	public String getName() {
		return "std-dev-conf";
	}

}
