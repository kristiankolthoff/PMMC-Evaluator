package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeNBRecallMacro implements Metric {

	private CorrespondenceType type;
	
	
	public TypeNBRecallMacro(CorrespondenceType type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, 
				c -> {return c.getNBRecall(type);});
	}

	@Override
	public String getName() {
		return "nb-rec-mac-" + type.getName();
	}

}
