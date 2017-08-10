package de.unima.ki.pmmc.evaluator.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unima.ki.pmmc.evaluator.Configuration;
import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.data.Report;
import de.unima.ki.pmmc.evaluator.data.Solution;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroup;

public class ReportGenerator {

	private Configuration configuration;
	
	public ReportGenerator(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public Map<Double, List<Report>> generate(Map<Double, List<Solution>> goldstandards, List<Solution> matchers) throws CorrespondenceException {
		Map<Double, List<Report>> reports = new HashMap<>();
		for(Map.Entry<Double, List<Solution>> e : goldstandards.entrySet()) {
			double currThreshold = e.getKey();
			List<Solution> currGoldstandards = e.getValue();
			//For each matcher solution, compute complete characteristic list over
			//all available goldstandards
			List<Report> reportPerThreshold = new ArrayList<>();
			for(Solution matcher : matchers) {
				List<Characteristic> characteristics = computeCharacteristics(currGoldstandards, matcher);
				reportPerThreshold.add(computeMetrics(characteristics, matcher));
			}
			reports.put(currThreshold, reportPerThreshold);
		}
		return reports;
	}
	
	private List<Characteristic> computeCharacteristics(List<Solution> goldstandards, Solution matcher) throws CorrespondenceException {
		List<Characteristic> characteristics = new ArrayList<>();
		for(Solution gs : goldstandards) {
			//TODO attentation : this is not safe, match alignments
			for (int i = 0; i < gs.getAlignments().size(); i++) {
				Alignment alignGS = gs.getAlignments().get(i);
				Alignment alignMatcher = matcher.getAlignments().get(i);
				characteristics.add(new Characteristic(alignMatcher, alignGS));
			}
		}
		return characteristics;
	}
	
	public Report computeMetrics(List<Characteristic> characteristics, Solution matcher) {
		Report report = new Report();
		report.setSolution(matcher);
		for(MetricGroup group : configuration) {
			MetricGroupBinding groupBinding = new MetricGroupBinding(group.getName());
			groupBinding.setInfo(group.getInfo());
			for(Metric metric : group) {
				double result = metric.compute(characteristics);
				MetricBinding binding = new MetricBinding(metric, result);
				groupBinding.addMetricBinding(binding);
			}
		}
		return report;
	}
}
