package de.unima.ki.pmmc.evaluator.generator;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Metric;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroup;

public class MetricGroupBinding extends MetricGroup {

	private List<MetricBinding> bindings;

	public MetricGroupBinding(String name, List<Metric> metrics) {
		super(name, metrics);
	}

	public MetricGroupBinding(String name, Metric... metrics) {
		super(name, metrics);
	}

	public MetricGroupBinding(String name, String info, List<MetricBinding> bindings) {
		super(name, info);
		this.bindings = bindings;
	}

	public MetricGroupBinding(String name) {
		super(name);
	}

	public List<MetricBinding> getBindings() {
		return bindings;
	}

	public void setBindings(List<MetricBinding> bindings) {
		this.bindings = bindings;
	}
	
	public void addMetricBinding(MetricBinding binding) {
		this.bindings.add(binding);
	}
	
	@Override
	public int size() {
		return bindings.size();
	}

}
