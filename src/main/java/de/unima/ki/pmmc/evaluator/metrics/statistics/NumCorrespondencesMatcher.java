package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class NumCorrespondencesMatcher implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return characteristics.stream()
				.mapToInt(c -> {return c.getAlignmentMapping().size();})
				.sum();
	}

	@Override
	public String getName() {
		return "#c-m";
	}

}
