package de.unima.ki.pmmc.evaluator.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.unima.ki.pmmc.evaluator.generator.MetricGroupBinding;

public class Report implements Iterable<MetricGroupBinding>{

	private List<MetricGroupBinding> bindings;
	private Solution solution;

	public Report() {
		this.bindings = new ArrayList<>();
	}
	
	public Report addBinding(MetricGroupBinding binding) {
		this.bindings.add(binding);
		return this;
	}
	
	public List<MetricGroupBinding> getBindings() {
		return bindings;
	}

	public void setBindings(List<MetricGroupBinding> bindings) {
		this.bindings = bindings;
	}

	@Override
	public Iterator<MetricGroupBinding> iterator() {
		return bindings.iterator();
	}

	public Solution getSolution() {
		return solution;
	}

	public void setSolution(Solution solution) {
		this.solution = solution;
	}

}
