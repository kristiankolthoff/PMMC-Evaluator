package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeNumCorrespondencesGS implements Metric {

	private CorrespondenceType type;


	public TypeNumCorrespondencesGS(CorrespondenceType type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return characteristics.stream()
				.mapToInt(c -> {return c.getAlignmentReference(type).size();})
				.sum();
	}

	@Override
	public String getName() {
		return "#c-gs-" + type.getName();
	}

}
