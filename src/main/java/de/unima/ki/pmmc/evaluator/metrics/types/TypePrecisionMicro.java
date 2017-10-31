package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypePrecisionMicro extends AbstractTypeMetric {

	public TypePrecisionMicro(CorrespondenceType ...types) {
		super(types);
	}
	
	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMicro(characteristics, 
				c -> {return (double) c.getTP(types).size();}, 
				c -> {return (double) (c.getTP(types).size() + c.getFP().size());});
	}

	@Override
	public String getName() {
		return "prec-micro";
	}

}
