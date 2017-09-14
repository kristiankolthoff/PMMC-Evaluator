package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;
import java.util.OptionalDouble;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class StdDevConfidence implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		double mean = new MeanConfidence().compute(characteristics);
		OptionalDouble sqsum =  characteristics.stream().
				flatMap(c -> {return c.getAlignmentMapping().getCorrespondences().stream();}).
				mapToDouble(corres -> {return Math.pow(corres.getConfidence() - mean, 2);}).
				average();
		return sqsum.isPresent() ? Math.sqrt(sqsum.getAsDouble()) : 0.0;
	}

	@Override
	public String getName() {
		return "std-dev-conf";
	}

}
