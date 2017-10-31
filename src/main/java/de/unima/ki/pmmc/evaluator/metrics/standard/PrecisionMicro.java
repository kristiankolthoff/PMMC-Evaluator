package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

/**
 * Compute the micro precision over a list of characteristics. 
 * Avoids biasing the value by unequally large data sets.
 */
public class PrecisionMicro implements Metric{

	private String name;
	
	public PrecisionMicro(String name) {
		this.name = name;
	}
	
	public PrecisionMicro() {}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMicro(characteristics, 
				c -> {return (double) c.getTP().size();}, 
				c -> {return (double) (c.getTP().size() + c.getFP().size());});
	}

	@Override
	public String getName() {
		return name != null ? name : "prec-mic";
	}

}
