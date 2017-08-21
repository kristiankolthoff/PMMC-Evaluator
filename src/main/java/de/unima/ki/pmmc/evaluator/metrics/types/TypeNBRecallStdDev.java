package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeNBRecallStdDev implements Metric{

	private CorrespondenceType[] type;
	
	
	public TypeNBRecallStdDev(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics, 
				new TypeNBRecallMacro(type)::compute, 
				c -> {return c.getNBRecall(type);});
	}

	@Override
	public String getName() {
		return "nb-rec-std-dev";
	}

}
