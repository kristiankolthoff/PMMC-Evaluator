package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;
import java.util.Optional;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class RecallMicro implements Metric {

	private Optional<String> name;
	
	public RecallMicro(String name) {
		this.name = Optional.of(name);
	}
	
	public RecallMicro() {
		this.name = Optional.empty();
	}
	
	@Override
	public double compute(List<Characteristic> characteristics) {
		return Metric.computeMicro(characteristics, 
				c -> {return (double) c.getTP().size();}, 
				c -> {return (double) (c.getTP().size() + c.getFN().size());});
	}

	@Override
	public String getName() {
		return name.isPresent() ? name.get() : "rec-mic";
	}

}
