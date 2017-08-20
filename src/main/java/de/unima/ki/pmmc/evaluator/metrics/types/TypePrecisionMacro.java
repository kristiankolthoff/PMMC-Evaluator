package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypePrecisionMacro implements Metric {

	private List<CorrespondenceType> type;
	
	
	public TypePrecisionMacro(CorrespondenceType type) {
		this.type = new ArrayList<>();
		this.type.add(type);
	}
	
	public TypePrecisionMacro(CorrespondenceType ...types) {
		this.type = Arrays.asList(types);
	}
	
	public TypePrecisionMacro(List<CorrespondenceType> types) {
		this.type = types;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, c -> {return c.getPrecision(type);});
	}

	@Override
	public String getName() {
		return "prec-macro-type";
	}

}
