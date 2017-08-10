package de.unima.ki.pmmc.evaluator;


import de.unima.ki.pmmc.evaluator.metrics.MetricGroup;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroupFactory;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionStdDev;
import de.unima.ki.pmmc.evaluator.metrics.statistics.MinimumConfidence;

public class PMMCNewEvaluationAdmission {

	private Configuration.Builder builder;
	
	public final boolean SHOW_IN_BROWSER = true;
	public final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
	public final String GOLDSTANDARD_OLD_PATH = "src/main/resources/data/dataset1/goldstandard/";
	public final String GOLDSTANDARD_OLD_SUB_PATH = "src/main/resources/data/dataset1-sub/goldstandard/";
	public final String GOLDSTANDARD_NEW_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts/";
	public final String GOLDSTANDARD_NEW_ADAPTED_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts_adapted/";
	public final String GOLDSTANDARD_NEW_ADAPTED_PARTS_PATH = "src/main/resources/data/dataset1/goldstandard_experts_heiner/";
	public final String RESULTS_PATH = "src/main/resources/data/results/OAEI16/";
	public final String MODELS_PATH = "src/main/resources/data/dataset1/models/";
	
	public PMMCNewEvaluationAdmission() {
		this.init();
	}
	
	private void init() {
		MetricGroupFactory factory = MetricGroupFactory.getInstance();
		builder = new Configuration.Builder().
				addMetricGroup(new MetricGroup("recall", "recall info")
						.addMetric(new PrecisionMicro())
						.addMetric(new PrecisionMacro())
						.addMetric(new PrecisionStdDev()))
				.addMetricGroup(factory.create(MetricGroup.PRECISION_GROUP))
				.addMetric(new PrecisionMacro())
				.addMetrics("min-conf", new MinimumConfidence())
				.persistToFile(true);
	}
	
	public static void main(String[] args) {
		PMMCNewEvaluationAdmission eval = new PMMCNewEvaluationAdmission();
		eval.oldGoldstandardExperiment();
	}
	
	public void oldGoldstandardExperiment() {
		builder.addGoldstandardPath("").
				addThreshold(Evaluator.THRESHOLD_ZERO).
				addThreshold(Evaluator.THRESHOLD_HIGH);
		Evaluator evaluator = new Evaluator(builder.build());
	}
	
	public void newGoldstandardExperiment() {
		builder.addGoldstandardPath("").
		addThreshold(Evaluator.THRESHOLD_ZERO).
		addThreshold(Evaluator.THRESHOLD_HIGH);
		Evaluator evaluator = new Evaluator(builder.build());
	}
}
