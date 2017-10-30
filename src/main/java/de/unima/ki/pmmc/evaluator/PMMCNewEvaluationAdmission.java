package de.unima.ki.pmmc.evaluator;


import java.io.IOException;
import java.util.Arrays;

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
import de.unima.ki.pmmc.evaluator.metrics.statistics.NumCorrespondencesGS;
import de.unima.ki.pmmc.evaluator.metrics.statistics.NumCorrespondencesMatcher;
import de.unima.ki.pmmc.evaluator.metrics.statistics.TypeFracCorrespondencesGS;
import de.unima.ki.pmmc.evaluator.metrics.statistics.TypeNumCorrespondencesGS;
import de.unima.ki.pmmc.evaluator.metrics.statistics.TypeNumCorrespondencesMatcher;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeNBFMeasureMacro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeNBFMeasureMicro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeNBPrecisionMacro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeNBRecallMacro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypeNBRecallMicro;
import de.unima.ki.pmmc.evaluator.metrics.types.TypePrecisionMacro;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;
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
		CorrespondenceType[] excludedValues = CorrespondenceType
					.valuesWithout(CorrespondenceType.TRIVIAL, CorrespondenceType.TRIVIAL_NORM);
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
						.addMetric(new NumCorrespondencesGS())
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
							.addMetric(new TypeNBPrecisionMacro(type))
							.addMetric(new TypeNBRecallMacro(type))
							.addMetric(new TypeNBFMeasureMacro(type)));
				}
				builder.addHandler(new HTMLHandler(SHOW_IN_BROWSER));
				builder.addMatcherPath("src/main/resources/data/results/OAEI17/ua/AML/")
				.addMatcherPath("src/main/resources/data/results/OAEI17/ua/I-Match")
				.addMatcherPath("src/main/resources/data/results/OAEI17/ua/LogMap")
				.addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/BPLangMatch/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/DKP")
				.addMatcherPath("src/main/resources/data/results/OAEI16/DKP-lite")
				.addMatcherPath("src/main/resources/data/results/OAEI16/KnoMa-Proc/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/Know-Match-SSS/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/Match-SSS/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/OPBOT/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/pPalm-DS/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-NHCM/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-NLM/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-SMSL/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-VM2/dataset1")
				.addMatcherPath("src/main/resources/data/results/OAEI16/TripleS/dataset1")
				.setModelsRootPath(MODELS_PATH)
				.setAlignmentReader(new AlignmentReaderXml())
				.setOutputName("oaei17-new-gs")
				.setOutputPath(OUTPUT_PATH)
				.setParser(Parser.Type.BPMN)
				.setCTTagOn(true)
				.addThreshold(Evaluator.THRESHOLD_ZERO)
//				.addThreshold(Evaluator.THRESHOLD_LOW)
//				.addThreshold(Evaluator.THRESHOLD_MEDIUM)
//				.addThreshold(Evaluator.THRESHOLD_HIGH)
				.setDebugOn(true)
				.setPathMaxentTagger(NLPHelper.TAGGER_BIDIR_DIRECTORY)
				.setPathWordnet(NLPHelper.WORDNET_DIRECTORY)
				.persistToFile(true);
	}
	
	public static void main(String[] args) throws IOException {
		PMMCNewEvaluationAdmission eval = new PMMCNewEvaluationAdmission();
		eval.oldGoldstandardExperiment();
	}
	
	public void oldGoldstandardExperiment() {
//		builder.addGoldstandardGroup("admission-non-binary", GOLDSTANDARD_NEW_ADAPTED_PATH)
//				.addGoldstandardGroup("admission-binary-sub", GOLDSTANDARD_OLD_SUB_PATH)
				builder.addGoldstandardGroup("admission-binary", GOLDSTANDARD_OLD_PATH);
		Evaluator evaluator = new Evaluator(builder.build());
		try {
			@SuppressWarnings("unused")
			Evaluation evaluation = evaluator.run();
		} catch (CorrespondenceException | ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}
	
}
