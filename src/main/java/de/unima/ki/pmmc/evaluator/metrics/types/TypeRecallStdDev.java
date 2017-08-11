package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeRecallStdDev implements Metric {

	private CorrespondenceType type;
	
	public TypeRecallStdDev(CorrespondenceType type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics, 
				new TypeRecallMacro(type)::compute, 
				c -> {return c.getRecall(type);});
	}

	@Override
	public String getName() {
		return "rec-std-dev-" + type.getName();
	}

}
