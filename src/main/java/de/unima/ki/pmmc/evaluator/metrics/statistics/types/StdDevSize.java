package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;

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
		double sqsum =  characteristics.stream().
				mapToDouble(c -> {return Math.pow(c.getAlignmentMapping(types).size() - mean, 2);}).
				average().getAsDouble();
		return Math.sqrt(sqsum);
	}

	@Override
	public String getName() {
		return "std-dev-size";
	}

}
