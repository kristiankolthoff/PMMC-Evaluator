package de.unima.ki.pmmc.evaluator.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MetricGroup implements Iterable<Metric>{
	
	private String name;
	private String info;
	private List<Metric> metrics;
	
	public static final String PRECISION_GROUP = "prec-group";
	public static final String RECALL_GROUP = "rec-group";
	public static final String F1_MEASURE_GROUP = "f1-group";
	
	public MetricGroup(String name, String info, List<Metric> metrics) {
		this.name = name;
		this.info = info;
		this.metrics = metrics;
	}
	
	public MetricGroup(String name, List<Metric> metrics) {
		this.name = name;
		this.metrics = metrics;
	}
	
	public MetricGroup(String name, Metric ...metrics) {
		this.name = name;
		this.metrics = Arrays.asList(metrics);
	}
	
	public MetricGroup(String name, String info) {
		this.name = name;
		this.info = info;
		this.metrics = new ArrayList<>();
	}
	
	public MetricGroup(String name) {
		this.name = name;
		this.metrics = new ArrayList<>();
	}
	
	public boolean isEmpty() {
		return this.metrics.isEmpty();
	}
	
	public MetricGroup addMetric(Metric metric) {
		metrics.add(metric);
		return this;
	}
	
	public MetricGroup addMetrics(Metric ...metrics) {
		this.metrics.addAll(Arrays.asList(metrics));
		return this;
	}
	
	public MetricGroup addMetrics(Collection<Metric> metrics) {
		this.metrics.addAll(metrics);
		return this;
	}
	
	public String getName() {
		return name;
	}

	public MetricGroup setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public Iterator<Metric> iterator() {
		return metrics.iterator();
	}

	public String getInfo() {
		return info;
	}

	public MetricGroup setInfo(String info) {
		this.info = info;
		return this;
	}
	
	public int size() {
		return metrics.size();
	}

	public MetricGroup addGoldstandard() {
		return null;
	}
}
