package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeFracCorrespondencesGS implements Metric {

	private CorrespondenceType type;
	
	
	public TypeFracCorrespondencesGS(CorrespondenceType type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		double sum = new NumCorrespondencesGS().compute(characteristics);
		double typeSum = new TypeNumCorrespondencesGS(type).compute(characteristics);
		return (typeSum / sum) * 100;
	}

	@Override
	public String getName() {
		return "%-c-gs";
	}

}
