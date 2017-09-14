package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;
import java.util.OptionalDouble;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class StdDevSize implements Metric {

	private CorrespondenceType[] types;
	
	
	public StdDevSize(CorrespondenceType... types) {
		this.types = types;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		double mean = new MeanSize(types).compute(characteristics);
		OptionalDouble sqsum =  characteristics.stream().
				mapToDouble(c -> {return Math.pow(c.getAlignmentMapping(types).size() - mean, 2);}).
				average();
		return sqsum.isPresent() ? Math.sqrt(sqsum.getAsDouble()) : 0.0;
	}

	@Override
	public String getName() {
		return "std-dev-size";
	}

}
