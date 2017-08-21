package de.unima.ki.pmmc.evaluator.metrics.types;


import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeNBPrecisionMicro implements Metric{

	private CorrespondenceType[] type;
	
	
	public TypeNBPrecisionMicro(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMicro(characteristics, 
				c -> {return c.getConfSumCorrect(type);}, 
				c -> {return ((double)c.getFP(type).size() + c.getConfSumCorrect(type));});
	}

	@Override
	public String getName() {
		return "nb-pre-mic";
	}

}
