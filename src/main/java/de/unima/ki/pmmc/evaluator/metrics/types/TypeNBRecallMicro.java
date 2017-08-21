package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeNBRecallMicro implements Metric {

	private CorrespondenceType[] type;
	
	
	public TypeNBRecallMicro(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMicro(characteristics, 
				c -> {return c.getConfSumCorrect(type);}, 
				c -> {return c.getConfSumReference(type);});
	}

	@Override
	public String getName() {
		return "nb-rec-mic";
	}

}
