package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class NumFN implements Metric {

	private CorrespondenceType[] types;
	
	
	public NumFN(CorrespondenceType... types) {
		this.types = types;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return characteristics.stream()
				.mapToInt(c -> {return c.getFN(types).size();}).sum();
	}

	@Override
	public String getName() {
		return "num-fn";
	}

}
