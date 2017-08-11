package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypePrecisionStdDev implements Metric {

	public CorrespondenceType type;
	
	
	public TypePrecisionStdDev(CorrespondenceType type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics, new TypePrecisionMacro(type)::compute, 
				c -> {return c.getPrecision(type);});
	}

	@Override
	public String getName() {
		return "prec-std-dev" + type.getName();
	}

}
