package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeFPMatcher implements Metric {

	private CorrespondenceType[] type;
	
	public TypeFPMatcher(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return characteristics.stream().mapToInt(c -> {return c.getFP(type).size();}).sum();
	}

	@Override
	public String getName() {
		return "FP";
	}

}
