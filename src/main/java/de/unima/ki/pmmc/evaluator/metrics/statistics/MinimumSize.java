package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;
import java.util.OptionalInt;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MinimumSize implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		OptionalInt value = characteristics.stream()
				.mapToInt(c ->  {return c.getAlignmentMapping().size();}).min();
		return value.isPresent() ? value.getAsInt() : 0d;
	}

	@Override
	public String getName() {
		return "min-size";
	}

}
