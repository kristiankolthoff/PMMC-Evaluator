package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MinimumConfidence implements Metric {

	private CorrespondenceType[] types;
	
	
	public MinimumConfidence(CorrespondenceType... types) {
		this.types = types;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return characteristics.stream()
				.flatMap(c -> {return c.getAlignmentMapping(types).getCorrespondences().stream();})
				.mapToDouble(corres -> {return corres.getConfidence();})
				.filter(value -> {return value > 0;}).min().getAsDouble();
	}

	@Override
	public String getName() {
		return "min-conf";
	}

}
