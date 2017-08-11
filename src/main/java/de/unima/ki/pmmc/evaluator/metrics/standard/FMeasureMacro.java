package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class FMeasureMacro implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, c -> {return c.getFMeasure();});
	}

	@Override
	public String getName() {
		return "fm-mac";
	}

}
