package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeNBFMeasureStdDev implements Metric{

	private CorrespondenceType[] type;
	
	
	public TypeNBFMeasureStdDev(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics,
				new TypeNBFMeasureMacro(type)::compute, 
				c -> {return c.getNBFMeasure(type);});
	}

	@Override
	public String getName() {
		return "nb-fm-std-dev";
	}

}
