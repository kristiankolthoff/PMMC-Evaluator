package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeRecallMacro implements Metric{

	private CorrespondenceType type;
	
	public TypeRecallMacro(CorrespondenceType type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, 
				c -> {return c.getRecall(type);});
	}

	@Override
	public String getName() {
		return "rec-mac-" + type.getName();
	}

}
