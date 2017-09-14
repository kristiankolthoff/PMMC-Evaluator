package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;
import java.util.OptionalDouble;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MaximumConfidence implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		OptionalDouble result = characteristics.stream()
				.flatMap(c -> {return c.getAlignmentMapping().getCorrespondences().stream();})
				.mapToDouble(corres -> {return corres.getConfidence();})
				.max();
		return result.isPresent() ? result.getAsDouble() : 0.0;
	}

	@Override
	public String getName() {
		return "max-conf";
	}

}
