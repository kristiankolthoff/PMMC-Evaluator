package de.unima.ki.pmmc.evaluator.metrics.statistics;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class FunctionMetric implements Metric {

	private Function<List<Characteristic>, Double> function;
	private Optional<String> name;

	public FunctionMetric(Function<List<Characteristic>, Double> function, String name) {
		this.function = function;
		this.name = Optional.of(name);
	}
	
	public FunctionMetric(Function<List<Characteristic>, Double> function) {
		this.function = function;
		this.name = Optional.empty();
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return function.apply(characteristics);
	}

	@Override
	public String getName() {
		return name.isPresent() ? name.get() : "cust-func";
	}

}
