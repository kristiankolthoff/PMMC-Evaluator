package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeNBFMeasureMacro implements Metric {

	private CorrespondenceType[] type;
	
	
	public TypeNBFMeasureMacro(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacroStrict(characteristics, 
				c -> {return c.getNBFMeasure(type);});
	}

	@Override
	public String getName() {
		return "nb-fm-mac";
	}

}
