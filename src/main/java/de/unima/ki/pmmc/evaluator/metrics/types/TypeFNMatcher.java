package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeFNMatcher implements Metric {

	private CorrespondenceType[] type;
	
	public TypeFNMatcher(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return characteristics.stream().mapToInt(c -> {return c.getFN(type).size();}).sum();
	}

	@Override
	public String getName() {
		return "FN";
	}

}
