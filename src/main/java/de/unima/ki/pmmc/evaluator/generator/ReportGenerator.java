package de.unima.ki.pmmc.evaluator.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.unima.ki.pmmc.evaluator.Configuration;
import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.data.Evaluation;
import de.unima.ki.pmmc.evaluator.data.GoldstandardGroup;
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
	
	public Evaluation generate(List<GoldstandardGroup> groups, 
			List<Solution> matchers, List<Double> thresholds) throws CorrespondenceException {
		Evaluation evaluation = new Evaluation();
		List<Report> reportPerThresholdAndGroup = new ArrayList<>();
		for(double threshold : thresholds) {
			for(GoldstandardGroup group : groups) {
				List<Solution> currGoldstandards = group.getGoldstandards(threshold);
				//For each matcher solution, compute complete characteristic list over
				//all available goldstandards
				for(Solution matcher : matchers) {
					List<Characteristic> characteristics = computeCharacteristics(currGoldstandards, matcher);
					Report report = computeMetrics(characteristics, matcher);
					report.setGroup(group);
					report.setGoldstandards(currGoldstandards);
					reportPerThresholdAndGroup.add(report);
				}
			}
		}
		Collections.sort(reportPerThresholdAndGroup, configuration.getSortingComparator());
		evaluation.addReports(reportPerThresholdAndGroup);
		return evaluation;
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
			MetricGroupBinding groupBinding = new MetricGroupBinding(group);
			for(Metric metric : group) {
				double result = metric.compute(characteristics);
				MetricBinding binding = new MetricBinding(metric, result);
				groupBinding.addMetricBinding(binding);
			}
			report.addBinding(groupBinding);
		}
		return report;
	}
}
