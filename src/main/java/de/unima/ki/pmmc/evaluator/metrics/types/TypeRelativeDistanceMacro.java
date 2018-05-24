package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeRelativeDistanceMacro implements Metric {

	private CorrespondenceType[] types;
	private boolean normalize;
	
	public TypeRelativeDistanceMacro(boolean normalize, CorrespondenceType... types) {
		this.normalize = normalize;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, 
						c -> {return c.getRelativeDistance(normalize,types);});
	}

	@Override
	public String getName() {
		return "type-rel-dist-mac";
	}

}
