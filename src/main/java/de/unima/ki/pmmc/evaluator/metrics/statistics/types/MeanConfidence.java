package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class MeanConfidence implements Metric {

	private CorrespondenceType[] types;
	
	
	public MeanConfidence(CorrespondenceType... types) {
		this.types = types;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return characteristics.stream().
				flatMap(c -> {return c.getAlignmentMapping(types).getCorrespondences().stream();}).
				mapToDouble(corres -> {return corres.getConfidence();}).average().getAsDouble();
	}

	@Override
	public String getName() {
		return "mean-conf";
	}

}
