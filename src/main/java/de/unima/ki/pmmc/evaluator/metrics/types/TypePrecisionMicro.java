package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypePrecisionMicro implements Metric {

	private CorrespondenceType[] type;
	
	
	public TypePrecisionMicro(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMicro(characteristics, 
				c -> {return (double) c.getTP(type).size();}, 
				c -> {return (double) (c.getTP(type).size() + c.getFP().size());});
	}

	@Override
	public String getName() {
		return "prec-micro";
	}

}
