package de.unima.ki.pmmc.evaluator.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.MetricGroup;

public class MetricGroupBinding implements Iterable<MetricBinding>{

	private MetricGroup group;
	private List<MetricBinding> bindings;

	public MetricGroupBinding(MetricGroup group) {
		this.group = group;
		this.bindings = new ArrayList<>();
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
	
	public int size() {
		return bindings.size();
	}

	@Override
	public Iterator<MetricBinding> iterator() {
		return bindings.iterator();
	}

	public MetricGroup getGroup() {
		return group;
	}

	public void setGroup(MetricGroup group) {
		this.group = group;
	}

}
