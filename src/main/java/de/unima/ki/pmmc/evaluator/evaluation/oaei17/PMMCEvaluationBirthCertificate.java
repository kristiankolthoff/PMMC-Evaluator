package de.unima.ki.pmmc.evaluator.evaluation.oaei17;


import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.Configuration;
import de.unima.ki.pmmc.evaluator.Evaluator;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.data.Evaluation;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.handler.HTMLHandler;
import de.unima.ki.pmmc.evaluator.handler.LaTexHandler;
import de.unima.ki.pmmc.evaluator.handler.LaTexHandlerType;
import de.unima.ki.pmmc.evaluator.metrics.Metric;
import de.unima.ki.pmmc.evaluator.metrics.MetricGroup;
import de.unima.ki.pmmc.evaluator.metrics.sorting.MetricSort;
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
import de.unima.ki.pmmc.evaluator.nlp.NLPHelper;

public class PMMCEvaluationBirthCertificate {

	public final boolean SHOW_IN_BROWSER = true;
	public final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
	public final String GOLDSTANDARD_PATH = "src/main/resources/data/dataset2/goldstandard/";
	public final String GOLDSTANDARD_NB_PATH = "src/main/resources/data/dataset2/new_gs_rdf/";
	public final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
	public final String MODELS_PATH = "src/main/resources/data/dataset2/models/";
	
	private static final Logger LOG = Logger.getLogger(PMMCEvaluationAdmission.class.getName());
	
	public PMMCEvaluationBirthCertificate() {}
	
	private Configuration.Builder createBuilder() throws IOException {
		Configuration.Builder builder = new Configuration.Builder()
				.addMatcherPath("src/main/resources/data/results/OAEI17/br/AML/")
				.addMatcherPath("src/main/resources/data/results/OAEI17/br/I-Match")
				.addMatcherPath("src/main/resources/data/results/OAEI17/br/LogMap")
				.addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/BPLangMatch/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/DKP")
				.addMatcherPath("src/main/resources/data/results/OAEI16/DKP-lite")
				.addMatcherPath("src/main/resources/data/results/OAEI16/KnoMa-Proc/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/Know-Match-SSS/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/Match-SSS/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/OPBOT/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/pPalm-DS/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-NHCM/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-NLM/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-SMSL/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/RMM-VM2/dataset2")
				.addMatcherPath("src/main/resources/data/results/OAEI16/TripleS/dataset2")
				.setModelsRootPath(MODELS_PATH)
				.setAlignmentReader(new AlignmentReaderXml())
				.setOutputPath(OUTPUT_PATH)
				.setParser(Parser.Type.PNML)
				.setCTTagOn(true)
				.addThreshold(Evaluator.THRESHOLD_ZERO)
				.addThreshold(Evaluator.THRESHOLD_LOW)
				.addThreshold(Evaluator.THRESHOLD_MEDIUM)
				.addThreshold(Evaluator.THRESHOLD_HIGH)
				.setDebugOn(true)
				.setPathMaxentTagger(NLPHelper.TAGGER_BIDIR_DIRECTORY)
				.setPathWordnet(NLPHelper.WORDNET_DIRECTORY)
				.persistToFile(true);
		return builder;
	}
	
	private Configuration.Builder createHTMLConfigurationBuilder() throws IOException {
		Configuration.Builder builder = createBuilder();
		builder.addMetricGroup(new MetricGroup("Precision")
				.addMetric(new PrecisionMicro())
				.addMetric(new PrecisionMacro())
				.addMetric(new PrecisionStdDev()))
	   .addMetricGroup(new MetricGroup("Recall")
				.addMetric(new RecallMicro())
				.addMetric(new RecallMacro())
				.addMetric(new RecallStdDev()))
	   .addMetricGroup(new MetricGroup("F1-Measure")
				.addMetric(new FMeasureMicro())
				.addMetric(new FMeasureMacro())
				.addMetric(new FMeasureStdDev()))
	   .addMetricGroup(new MetricGroup("Stats")
			    .addMetric(new MinimumConfidence())
			    .addMetric(new NumCorrespondencesMatcher()));
		for(CorrespondenceType type : CorrespondenceType.values()) {
			builder.addMetricGroup(new MetricGroup(type.getName())
					.addMetric(new TypeNumCorrespondencesGS(type))
					.addMetric(new TypeFracCorrespondencesGS(type))
					.addMetric(new TypeNumCorrespondencesMatcher(type))
					.addMetric(new TypePrecisionMacro(type))
					.addMetric(new TypeRecallMacro(type))
					.addMetric(new TypeFMeasureMacro(type)));
		}
		return builder;
	}
	
	public static void main(String[] args) throws IOException, CorrespondenceException, 
				ParserConfigurationException, SAXException {
		PMMCEvaluationBirthCertificate birthCertificate = new PMMCEvaluationBirthCertificate();
		//Run all evaluations for the binary birthCertificate goldstandard
		birthCertificate.runBinaryGSEvaluationHTML();
		birthCertificate.runBinaryGSEvaluationLaTex();
		birthCertificate.runBinaryGSEvaluationLaTexTypes();
		//Run all evaluations for the non-binary birthCertificate goldstandard
		birthCertificate.runNonBinaryGSEvaluationHTML();
		birthCertificate.runNonBinaryGSEvaluationLaTex();
		birthCertificate.runNonBinaryGSEvaluationLaTexTypes();
	}
	
	public void runBinaryGSEvaluationHTML() throws IOException, CorrespondenceException, 
				ParserConfigurationException, SAXException {
		Configuration.Builder builder = createHTMLConfigurationBuilder();
		builder.addHandler(new HTMLHandler(SHOW_IN_BROWSER))
			   .setOutputName("oaei17-birth-certificate-binary")
		       .addGoldstandardGroup("birth-certificate-binary", GOLDSTANDARD_PATH);
		Configuration configuration = builder.build();
		Evaluator evaluator = new Evaluator(configuration);
		Evaluation evaluation = evaluator.run();
		LOG.info("binaryGSEvaluationHTML : #reports = " + evaluation.getReports().size());
	}
	
	public void runBinaryGSEvaluationLaTex() throws IOException, CorrespondenceException,
				ParserConfigurationException, SAXException {
		Configuration.Builder builder = createBuilder();
		Metric sortMetric = new FMeasureMicro();
		builder.addMetricGroup(new MetricGroup("Precision")
						.addMetric(new PrecisionMicro())
						.addMetric(new PrecisionMacro()))
			   .addMetricGroup(new MetricGroup("Recall")
						.addMetric(new RecallMicro())
						.addMetric(new RecallMacro()))
			   .addMetricGroup(new MetricGroup("F1-Measure")
						.addMetric(sortMetric)
						.addMetric(new FMeasureMacro()));
		builder.addHandler(new LaTexHandler())
		   	   .setOutputName("oaei17-birth-certificate-binary")
	           .addGoldstandardGroup("birth-certificate-binary", GOLDSTANDARD_PATH)
		       .setSortingOrder(new MetricSort(sortMetric, true));
		Configuration configuration = builder.build();
		Evaluator evaluator = new Evaluator(configuration);
		Evaluation evaluation = evaluator.run();
		LOG.info("binaryGSEvaluationLaTex : #reports = " + evaluation.getReports().size());
	}
	
	public void runBinaryGSEvaluationLaTexTypes() throws IOException, CorrespondenceException, 
				ParserConfigurationException, SAXException {
		Configuration.Builder builder = createBuilder();
		CorrespondenceType[] excludedTypes = CorrespondenceType.valuesWithout(CorrespondenceType.DEFAULT);
		for(CorrespondenceType type : excludedTypes) {
			builder.addMetricGroup(new MetricGroup(type.getName())
					.addMetric(new TypePrecisionMacro(type))
					.addMetric(new TypeRecallMacro(type))
					.addMetric(new TypeFMeasureMacro(type)));
		}
		builder.addHandler(new LaTexHandlerType())
		       .setOutputName("oaei17-birth-certificate-binary-types")
	           .addGoldstandardGroup("birth-certificate-binary", GOLDSTANDARD_PATH);
		Configuration configuration = builder.build();
		Evaluator evaluator = new Evaluator(configuration);
		Evaluation evaluation = evaluator.run();
		LOG.info("binaryGSEvaluationLaTexTypes : #reports = " + evaluation.getReports().size());
	}
	
	
	
	public void runNonBinaryGSEvaluationHTML() throws IOException, CorrespondenceException, 
			ParserConfigurationException, SAXException {
		Configuration.Builder builder = createBuilder();
		builder.addMetricGroup(new MetricGroup("Precision")
				.addMetric(new NBPrecisionMicro())
				.addMetric(new NBPrecisionMacro())
				.addMetric(new NBPrecisionStdDev()))
	   .addMetricGroup(new MetricGroup("Recall")
				.addMetric(new NBRecallMicro())
				.addMetric(new NBRecallMacro())
				.addMetric(new NBRecallStdDev()))
	   .addMetricGroup(new MetricGroup("F1-Measure")
				.addMetric(new NBFMeasureMicro())
				.addMetric(new NBFMeasureMacro())
				.addMetric(new NBFMeasureStdDev()))
	   .addMetricGroup(new MetricGroup("Stats")
			    .addMetric(new MinimumConfidence())
			    .addMetric(new NumCorrespondencesMatcher()));
		for(CorrespondenceType type : CorrespondenceType.values()) {
			builder.addMetricGroup(new MetricGroup(type.getName())
					.addMetric(new TypeNumCorrespondencesGS(type))
					.addMetric(new TypeFracCorrespondencesGS(type))
					.addMetric(new TypeNumCorrespondencesMatcher(type))
					.addMetric(new TypeNBPrecisionMacro(type))
					.addMetric(new TypeNBRecallMacro(type))
					.addMetric(new TypeNBFMeasureMacro(type)));
		}
		builder.addHandler(new HTMLHandler(SHOW_IN_BROWSER))
		   .setOutputName("oaei17-birth-certificate-non-binary")
		   .addGoldstandardGroup("birth-certificate-non-binary", GOLDSTANDARD_NB_PATH);
		Configuration configuration = builder.build();
		Evaluator evaluator = new Evaluator(configuration);
		Evaluation evaluation = evaluator.run();
		LOG.info("nonBinaryGSEvaluationHTML : #reports = " + evaluation.getReports().size());
	}
	
	public void runNonBinaryGSEvaluationLaTex() throws IOException, CorrespondenceException,
			ParserConfigurationException, SAXException {
		Configuration.Builder builder = createBuilder();
		Metric sortMetric = new NBFMeasureMicro();
		builder.addMetricGroup(new MetricGroup("Precision")
					.addMetric(new NBPrecisionMicro())
					.addMetric(new NBPrecisionMacro()))
		   .addMetricGroup(new MetricGroup("Recall")
					.addMetric(new NBRecallMicro())
					.addMetric(new NBRecallMacro()))
		   .addMetricGroup(new MetricGroup("F1-Measure")
					.addMetric(sortMetric)
					.addMetric(new NBFMeasureMacro()));
		builder.addHandler(new LaTexHandler())
			.setOutputName("oaei17-birth-certificate-non-binary")
		   .addGoldstandardGroup("birth-certificate-non-binary", GOLDSTANDARD_NB_PATH)
		   .setSortingOrder(new MetricSort(sortMetric, true));
		Configuration configuration = builder.build();
		Evaluator evaluator = new Evaluator(configuration);
		Evaluation evaluation = evaluator.run();
		LOG.info("nonBinaryGSEvaluationLaTex : #reports = " + evaluation.getReports().size());
	}
	
	
	public void runNonBinaryGSEvaluationLaTexTypes() throws IOException, CorrespondenceException, 
			ParserConfigurationException, SAXException {
		Configuration.Builder builder = createBuilder();
		CorrespondenceType[] excludedTypes = CorrespondenceType.valuesWithout(CorrespondenceType.DEFAULT);
		for(CorrespondenceType type : excludedTypes) {
		builder.addMetricGroup(new MetricGroup(type.getName())
				.addMetric(new TypeNBPrecisionMacro(type))
				.addMetric(new TypeNBRecallMacro(type))
				.addMetric(new TypeNBFMeasureMacro(type)));
		}
		builder.addHandler(new LaTexHandlerType())
		.setOutputName("oaei17-birth-certificate-non-binary-types")
		   .addGoldstandardGroup("birth-certificate-non-binary", GOLDSTANDARD_NB_PATH);
		Configuration configuration = builder.build();
		Evaluator evaluator = new Evaluator(configuration);
		Evaluation evaluation = evaluator.run();
		LOG.info("nonBinaryGSEvaluationLaTexTypes : #reports = " + evaluation.getReports().size());
	}
	
}
