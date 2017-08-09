package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MinimumConfidence implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return characteristics.stream()
				.flatMap(c -> {return c.getAlignmentMapping().getCorrespondences().stream();})
				.mapToDouble(corres -> {return corres.getConfidence();})
				.filter(value -> {return value > 0;}).min().getAsDouble();
	}

	@Override
	public String getName() {
		return "min-conf";
	}

}
