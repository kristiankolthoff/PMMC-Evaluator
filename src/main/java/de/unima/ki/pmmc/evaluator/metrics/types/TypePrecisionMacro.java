package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypePrecisionMacro implements Metric {

	private CorrespondenceType type;
	
	
	public TypePrecisionMacro(CorrespondenceType type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, c -> {return c.getPrecision(type);});
	}

	@Override
	public String getName() {
		return "prec-macro-" + type.getName();
	}

}
