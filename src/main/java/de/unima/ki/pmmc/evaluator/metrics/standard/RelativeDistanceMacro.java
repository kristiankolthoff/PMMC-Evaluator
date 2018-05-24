package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class RelativeDistanceMacro implements Metric{

	private boolean normalize;
	
	public RelativeDistanceMacro(boolean normalize) {
		this.normalize = normalize;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMacro(characteristics, 
						c -> {return c.getRelativeDistance(normalize);});
	}

	@Override
	public String getName() {
		return "rel-dist-mac";
	}

}
