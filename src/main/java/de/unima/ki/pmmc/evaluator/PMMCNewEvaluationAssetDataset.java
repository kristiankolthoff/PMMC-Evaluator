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

public class PMMCNewEvaluationAssetDataset {

	private Configuration.Builder builder;
	
	public final boolean SHOW_IN_BROWSER = true;
	public final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
	public final String GOLDSTANDARD_PATH = "src/main/resources/data/dataset3/goldstandard/";
	public final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
	public final String MODELS_PATH = "src/main/resources/data/dataset3/models/";
	
	public PMMCNewEvaluationAssetDataset() throws IOException {
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
				.addMatcherPath(RESULTS_PATH + "/AML-PM/dataset3/")
				.addMatcherPath(RESULTS_PATH + "/BPLangMatch/dataset3/")
				.addMatcherPath(RESULTS_PATH + "/KnoMa-Proc/dataset3/")
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
		PMMCNewEvaluationAssetDataset eval = new PMMCNewEvaluationAssetDataset();
		eval.oldGoldstandardExperiment();
	}
	
	public void oldGoldstandardExperiment() {
		builder.addGoldstandardGroup("asset", GOLDSTANDARD_PATH);
//				.addGoldstandardGroup("admission old sub", GOLDSTANDARD_OLD_SUB_PATH);
		Evaluator evaluator = new Evaluator(builder.build());
		try {
			Evaluation evaluation = evaluator.run();
		} catch (CorrespondenceException | ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
}
