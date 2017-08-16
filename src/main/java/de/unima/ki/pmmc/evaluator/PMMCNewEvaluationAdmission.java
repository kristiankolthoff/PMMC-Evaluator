package de.unima.ki.pmmc.evaluator;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.data.Evaluation;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.handler.HTMLHandler;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroup;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroupFactory;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBFMeasureMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBFMeasureMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBFMeasureStdDev;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBPrecisionMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBPrecisionMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBPrecisionStdDev;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBRecallMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBRecallMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBRecallStdDev;
import de.unima.ki.pmmc.evaluator.metrics.statistics.FunctionMetric;
import de.unima.ki.pmmc.evaluator.metrics.statistics.MinimumConfidence;
import de.unima.ki.pmmc.evaluator.metrics.statistics.NumCorrespondences;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import edu.stanford.nlp.parser.metrics.Eval;

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
	
	public PMMCNewEvaluationAdmission() throws IOException {
		this.init();
	}
	
	private void init() throws IOException {
		@SuppressWarnings("unused")
		MetricGroupFactory factory = MetricGroupFactory.getInstance();
		builder = new Configuration.Builder().
				addMetricGroup(new MetricGroup("Precision", "prec-info")
						.addMetric(new NBPrecisionMicro())
						.addMetric(new NBPrecisionMacro())
						.addMetric(new NBPrecisionStdDev()))
				.addMetricGroup(new MetricGroup("Recall", "rec-info")
						.addMetric(new NBRecallMicro())
						.addMetric(new NBRecallMacro())
						.addMetric(new NBRecallStdDev()))
				.addMetricGroup(new MetricGroup("F1-Measure")
						.addMetric(new NBFMeasureMicro())
						.addMetric(new NBFMeasureMacro())
						.addMetric(new NBFMeasureStdDev()))
				.addMetricGroup(new MetricGroup("Stats")
						.addMetric(new MinimumConfidence())
						.addMetric(new NumCorrespondences())
						.addMetric(new FunctionMetric(list -> 
						{return (double) list.stream()
								.mapToInt(c -> {return c.getAlignmentCorrect().size();})
								.max().getAsInt();})))
				.addHandler(new HTMLHandler(SHOW_IN_BROWSER))
				.addMatcherPath("src/main/resources/data/results/OAEI16/AML/")
				.addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/dataset1/")
				.addMatcherPath("src/main/resources/data/results/OAEI16/BPLangMatch/dataset1/")
//				.setModelsRootPath(MODELS_PATH)
				.setAlignmentReader(new AlignmentReaderXml())
				.setOutputName("oaei16-new-gs")
				.setOutputPath(OUTPUT_PATH)
				.setParser(Parser.TYPE_BPMN)
				.setCTTagOn(false)
				.setDebugOn(true)
				.persistToFile(true);
	}
	
	public static void main(String[] args) throws IOException {
		PMMCNewEvaluationAdmission eval = new PMMCNewEvaluationAdmission();
		eval.oldGoldstandardExperiment();
	}
	
	public void oldGoldstandardExperiment() {
		builder.addGoldstandardGroup("admission new", GOLDSTANDARD_NEW_ADAPTED_PATH, 
				GOLDSTANDARD_NEW_ADAPTED_PATH);
//				.addGoldstandardGroup("admission old sub", GOLDSTANDARD_OLD_SUB_PATH);
		Evaluator evaluator = new Evaluator(builder.build());
		try {
			Evaluation evaluation = evaluator.run();
		} catch (CorrespondenceException | ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
}
