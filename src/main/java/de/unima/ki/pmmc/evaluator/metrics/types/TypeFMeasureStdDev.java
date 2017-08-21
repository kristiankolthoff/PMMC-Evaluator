package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeFMeasureStdDev implements Metric{

	private CorrespondenceType[] type;
	
	public TypeFMeasureStdDev(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeStdDev(characteristics,
				new TypeFMeasureMacro(type)::compute, 
				c -> {return c.getFMeasure(type);});
	}

	@Override
	public String getName() {
		return "fm-std-dev";
	}

}
