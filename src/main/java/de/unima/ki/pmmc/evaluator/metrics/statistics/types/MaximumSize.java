package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;
import java.util.OptionalInt;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MaximumSize implements Metric {

	private CorrespondenceType[] types;
	
	
	public MaximumSize(CorrespondenceType[] types) {
		this.types = types;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		OptionalInt value = characteristics.stream()
				.mapToInt(c ->  {return c.getAlignmentMapping(types).size();}).max();
		return value.isPresent() ? value.getAsInt() : 0d;
	}

	@Override
	public String getName() {
		return "max-size";
	}

}
