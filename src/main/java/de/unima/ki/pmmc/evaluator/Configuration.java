package de.unima.ki.pmmc.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Metric;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroup;
import de.unima.ki.pmmc.evaluator.metrics.Metrics;

public class Configuration {

	//TODO what about multiple goldstandards
	private List<CorrespondenceType> types;
	private Map<String, List<Metric>> metricGroups;
	
	
	public static class Builder {
		
		private List<CorrespondenceType> types;
		private List<Metrics> metrics;
		private Map<String, List<Metric>> metricGroupsMap;
		private Map<CorrespondenceType, List<Metric>> typeMetricGroups;
		private Map<Option, Object> params;
		private List<MetricGroup> metricGroups;
		private boolean persistToFile;
		
		public Builder() {
			this.types = new ArrayList<>();
			this.metrics = new ArrayList<>();
			this.metricGroupsMap = new HashMap<>();
			this.params = new HashMap<>();
			this.metricGroups = new ArrayList<>();
			this.typeMetricGroups = new HashMap<>();
		}
		
		public Builder addOption(Option option, Object value) {
			this.params.put(option, value);
			return this;
		}
		
		public Builder addMetric(Metrics metric) {
			metrics.add(metric);
			return this;
		}
		
		public Builder addMetrics(Metrics... metrics) {
			this.metrics.addAll(Arrays.asList(metrics));
			return this;
		}
		
		public Builder addMetricGroup(String group, Metric...metrics) {
			metricGroupsMap.put(group, Arrays.asList(metrics));
			return this;
		}
		
		public Builder addMetricGroup(MetricGroup metricGroup) {
			metricGroups.add(metricGroup);
			return this;
		}
		
		public Builder addTypeMetricGroup(CorrespondenceType type, Metric...metrics) {
			typeMetricGroups.put(type, Arrays.asList(metrics));
			return this;
		}
		
		//TODO Path path
		public Builder persistToFile(boolean persist) {
			this.persistToFile = persist;
			return this;
		}
		
		public Configuration build() {
			Configuration configuration = new Configuration();
			return configuration;
		}
	}
	
	public static void main(String[] args) {
		Configuration config = new Configuration.Builder().
				addOption(Option.NORMALIZE, true).
				addMetricGroup(MetricGroup.PRECISION).
				addMetric(Metrics.NB_RECALL_MACRO_GS).
				addMetric(Metrics.NB_FMEASURE_STD_GS).
				persistToFile(true).
				build();
				
				
	}
}
