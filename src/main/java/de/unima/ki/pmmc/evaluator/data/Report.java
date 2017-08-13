package de.unima.ki.pmmc.evaluator.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.unima.ki.pmmc.evaluator.generator.MetricGroupBinding;

public class Report implements Iterable<MetricGroupBinding>{

	private List<MetricGroupBinding> bindings;
	private GoldstandardGroup group;
	private Solution matcher;
	private List<Solution> goldstandard;

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

	public Solution getMatcher() {
		return matcher;
	}

	public void setSolution(Solution solution) {
		this.matcher = solution;
	}

	public List<Solution> getGoldstandards() {
		return goldstandard;
	}

	public void setGoldstandards(List<Solution> goldstandard) {
		this.goldstandard = goldstandard;
	}
	
	public double getTreshold() {
		return this.goldstandard.get(0).getThreshold();
	}

	public GoldstandardGroup getGroup() {
		return group;
	}

	public void setGroup(GoldstandardGroup group) {
		this.group = group;
	}

}
