package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class StdDevConfidence implements Metric {

	private CorrespondenceType[] types;
	
	
	public StdDevConfidence(CorrespondenceType... types) {
		this.types = types;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		double mean = new MeanConfidence(types).compute(characteristics);
		double sqsum =  characteristics.stream().
				flatMap(c -> {return c.getAlignmentMapping(types).getCorrespondences().stream();}).
				mapToDouble(corres -> {return Math.pow(corres.getConfidence() - mean, 2);}).
				average().getAsDouble();
		return Math.sqrt(sqsum);
	}

	@Override
	public String getName() {
		return "std-dev-conf";
	}

}
