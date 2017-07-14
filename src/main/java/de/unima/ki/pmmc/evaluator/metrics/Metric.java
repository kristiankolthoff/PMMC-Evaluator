package de.unima.ki.pmmc.evaluator.metrics;

import java.util.List;
import java.util.Map;

import de.unima.ki.pmmc.evaluator.Option;

@FunctionalInterface
public interface Metric {

	public double compute(List<? extends Characteristic> characteristics, Map<Option, Object> params);
}
