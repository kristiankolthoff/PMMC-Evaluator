package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;
import java.util.OptionalDouble;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MaximumConfidence implements Metric {

	private CorrespondenceType[] types;
	
	
	public MaximumConfidence(CorrespondenceType... types) {
		this.types = types;
	}

	//TODO in case of unavailability, what to return instead of zero. Na?
	@Override
	public double compute(List<Characteristic> characteristics) {
		OptionalDouble result = characteristics.stream()
				.flatMap(c -> {return c.getAlignmentMapping(types).getCorrespondences().stream();})
				.mapToDouble(corres -> {return corres.getConfidence();})
				.max();
		return result.isPresent() ? result.getAsDouble() : 0.0;
	}

	@Override
	public String getName() {
		return "max-conf";
	}

}
