package de.unima.ki.pmmc.evaluator;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.data.Evaluation;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.handler.HTMLHandler;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroup;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroupFactory;
import de.unima.ki.pmmc.evaluator.metrics.standard.FMeasureMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.FMeasureMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.FMeasureStdDev;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBFMeasureMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBFMeasureMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBFMeasureStdDev;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBPrecisionMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBPrecisionMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBPrecisionStdDev;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBRecallMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBRecallMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.NBRecallStdDev;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.PrecisionStdDev;
import de.unima.ki.pmmc.evaluator.metrics.standard.RecallMacro;
import de.unima.ki.pmmc.evaluator.metrics.standard.RecallMicro;
import de.unima.ki.pmmc.evaluator.metrics.standard.RecallStdDev;
import de.unima.ki.pmmc.evaluator.metrics.statistics.FunctionMetric;
import de.unima.ki.pmmc.evaluator.metrics.statistics.MinimumConfidence;
import de.unima.ki.pmmc.evaluator.metrics.statistics.NumCorrespondencesMatcher;
import de.unima.ki.pmmc.evaluator.metrics.statistics.TypeFracCorrespondencesGS;
import de.unima.ki.pmmc.evaluator.metrics.statistics.TypeNumCorrespondencesGS;
import de.unima.ki.pmmc.evaluator.metrics.statistics.TypeNumCorrespondencesMatcher;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeFMeasureMacro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeNBFMeasureMacro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeNBPrecisionMacro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeNBRecallMacro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypePrecisionMacro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeRecallMacro;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;

public class PMMCNewEvaluationBirthDataset {

	private Configuration.Builder builder;
	
	public final boolean SHOW_IN_BROWSER = true;
	public final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
	public final String GOLDSTANDARD_PATH = "src/main/resources/data/dataset2/goldstandard/";
	public final String GOLDSTANDARD_NB_PATH = "src/main/resources/data/dataset2/new_gs_rdf/";
	public final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
	public final String MODELS_PATH = "src/main/resources/data/dataset2/models/";
	
	public PMMCNewEvaluationBirthDataset() throws IOException {
		this.init();
	}
	
	private void init() throws IOException {
		@SuppressWarnings("unused")
		MetricGroupFactory factory = MetricGroupFactory.getInstance();
		builder = new Configuration.Builder().
				addMetricGroup(new MetricGroup("Precision", "prec-info")
						.addMetric(new PrecisionMicro())
						.addMetric(new PrecisionMacro())
						.addMetric(new PrecisionStdDev()))
				.addMetricGroup(new MetricGroup("Recall", "rec-info")
						.addMetric(new RecallMicro())
						.addMetric(new RecallMacro())
						.addMetric(new RecallStdDev()))
				.addMetricGroup(new MetricGroup("F1-Measure")
						.addMetric(new FMeasureMicro())
						.addMetric(new FMeasureMacro())
						.addMetric(new FMeasureStdDev()))
				.addMetricGroup(new MetricGroup("Stats")
						.addMetric(new MinimumConfidence())
						.addMetric(new NumCorrespondencesMatcher())
						.addMetric(new FunctionMetric(list -> 
						{return (double) list.stream()
								.mapToInt(c -> {return c.getAlignmentCorrect().size();})
								.max().getAsInt();})));
				for(CorrespondenceType type : CorrespondenceType.values()) {
					builder.addMetricGroup(new MetricGroup(type.getName())
							.addMetric(new TypeNumCorrespondencesGS(type))
							.addMetric(new TypeFracCorrespondencesGS(type))
							.addMetric(new TypeNumCorrespondencesMatcher(type))
							.addMetric(new TypePrecisionMacro(type))
							.addMetric(new TypeRecallMacro(type))
							.addMetric(new TypeFMeasureMacro(type)));
				}
				builder.addHandler(new HTMLHandler(SHOW_IN_BROWSER))
//				.addMatcherPath("src/main/resources/data/results/OAEI17/br/AML/")
//				.addMatcherPath("src/main/resources/data/results/OAEI17/br/I-Match")
//				.addMatcherPath("src/main/resources/data/results/OAEI17/br/LogMap")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/BPLangMatch/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/KnoMa-Proc/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/Know-Match-SSS/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/Match-SSS/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/OPBOT/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/pPalm-DS/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-NHCM/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-NLM/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-SMSL/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-VM2/dataset2")
//				.addMatcherPath("src/main/resources/data/results/OAEI16/TripleS/dataset2")
				.setModelsRootPath(MODELS_PATH)
				.addThreshold(Evaluator.THRESHOLD_ZERO)
//				.addThreshold(Evaluator.THRESHOLD_LOW)
//				.addThreshold(Evaluator.THRESHOLD_MEDIUM)
//				.addThreshold(Evaluator.THRESHOLD_HIGH)
				.setAlignmentReader(new AlignmentReaderXml())
				.setOutputName("birth-old-gs")
				.setOutputPath(OUTPUT_PATH)
				.setParser(Parser.Type.PNML)
				.setCTTagOn(true)
				.setDebugOn(true)
				.persistToFile(true);
	}
	
	public static void main(String[] args) throws IOException {
		PMMCNewEvaluationBirthDataset eval = new PMMCNewEvaluationBirthDataset();
		eval.oldGoldstandardExperiment();
	}
	
	public void oldGoldstandardExperiment() {
//		builder.addGoldstandardGroup("birth-non-binary", GOLDSTANDARD_NB_PATH)
			   builder.addGoldstandardGroup("birth-binary", GOLDSTANDARD_PATH);
		Evaluator evaluator = new Evaluator(builder.build());
		try {
			Evaluation evaluation = evaluator.run();
			System.out.println(evaluation.getReports().size());
		} catch (CorrespondenceException | ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
}

