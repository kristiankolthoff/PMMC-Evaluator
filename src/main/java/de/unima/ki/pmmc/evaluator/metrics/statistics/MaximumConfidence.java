package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MaximumConfidence implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return characteristics.stream()
				.flatMap(c -> {return c.getAlignmentMapping().getCorrespondences().stream();})
				.mapToDouble(corres -> {return corres.getConfidence();})
				.max().getAsDouble();
	}

	@Override
	public String getName() {
		return "max-conf";
	}

}
