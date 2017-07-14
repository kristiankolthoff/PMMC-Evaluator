package de.unima.ki.pmmc.evaluator.metrics;

import java.util.List;
import java.util.Map;

import de.unima.ki.pmmc.evaluator.Option;

public class NBPrecisionMacro implements Metric {

	@Override
	public double compute(List<? extends Characteristic> characteristics, Map<Option, Object> params) {
		boolean normalize = (boolean) params.get(Option.NORMALIZE);
//		return Characteristic.computeMacro(characteristics, c -> {return c.getNBPrecision();});
		return 0;
	}

}
