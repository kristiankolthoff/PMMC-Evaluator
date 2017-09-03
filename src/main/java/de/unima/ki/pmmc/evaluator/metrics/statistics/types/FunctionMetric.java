package de.unima.ki.pmmc.evaluator.metrics.statistics.types;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class FunctionMetric implements Metric {

	private BiFunction<List<Characteristic>, CorrespondenceType[], Double> function;
	private CorrespondenceType[] types;
	private Optional<String> name;

	public FunctionMetric(BiFunction<List<Characteristic>, CorrespondenceType[], Double> function, String name, CorrespondenceType... types) {
		this.function = function;
		this.name = Optional.of(name);
		this.types = types;
	}
	
	public FunctionMetric(BiFunction<List<Characteristic>, CorrespondenceType[], Double> function, CorrespondenceType... types) {
		this.function = function;
		this.name = Optional.empty();
		this.types = types;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return function.apply(characteristics, types);
	}

	@Override
	public String getName() {
		return name.isPresent() ? name.get() : "cust-func";
	}

}
