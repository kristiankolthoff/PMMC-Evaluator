package de.unima.ki.pmmc.evaluator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.annotator.Annotator;
import de.unima.ki.pmmc.evaluator.data.Evaluation;
import de.unima.ki.pmmc.evaluator.data.Report;
import de.unima.ki.pmmc.evaluator.data.Solution;
import de.unima.ki.pmmc.evaluator.data.ValidationReport;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.generator.ReportGenerator;
import de.unima.ki.pmmc.evaluator.handler.ReportHandler;
import de.unima.ki.pmmc.evaluator.loader.Loader;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.validation.SizeValidator;
import de.unima.ki.pmmc.evaluator.validation.Validator;

/**
 * Starts the evaluation process and allows to easily set up
 * the evaluation.
 */
public class Evaluator {
	
	private Configuration configuration;
	/**
	 * Paths to goldstandard alignments, multiple matchers
	 * root path and root path to the used models
	 */
	private List<String> goldstandardPaths;
	private Optional<String> matchersRootPath;
	private String modelsRootPath;
	/**
	 * Output path and prefix for generated output files
	 */
	private String outputPath;
	private String outputName;
	/**
	 * Loaded result for goldstandard
	 */
	private Map<Double, List<Solution>> goldstandards;
	/**
	 * Contains all specified paths to matchers
	 */
	private List<String> matcherPaths;
	private List<Double> thresholds;
	private boolean debugOn;
	private boolean tagCTOn;
	/**
	 * Loaded results list. One result for
	 * each single matcher
	 */
	private List<Model> models;
	private List<Solution> matcherSolutions;
	/**
	 * The result handlers which should 
	 * process the generated results
	 */
	private List<ReportHandler> handler;
	private AlignmentReader alignmentReader;
	/**
	 * Functions providing Correspondence, Alignment 
	 * and Result transformations
	 */
	private List<Function<Report, Report>> transformationsResult;
	private List<Function<Alignment, Alignment>> transformationsAlignment;
	private List<Function<Correspondence, Correspondence>> transformationsCorrespondence;
	/**
	 * Predicates for filtering Correspondences,
	 * Alignments and Results
	 */
	private List<Predicate<Report>> filterResult;
	private List<Predicate<Alignment>> filterAlignment;
	private List<Predicate<Correspondence>> filterCorrespondence;
	private Consumer<String> flowListener;
	private Annotator annotator;
	private Parser parser;
	private Loader loader;
	private Validator validator;
	private ReportGenerator generator;
	private List<Throwable> errors;

	/**
	 * Important default thresholds used often
	 */
	public static final double THRESHOLD_ZERO = 0.0;
	public static final double THRESHOLD_LOW = 0.375;
	public static final double THRESHOLD_MEDIUM = 0.5;
	public static final double THRESHOLD_HIGH = 0.75;

	private static final String GOLDSTANDARD_NAME = "goldstandard";
	private static final String SEPERATOR = "-";
	private static final Logger LOG = Logger.getLogger(Evaluator.class.getName());
	
	public Evaluator(Configuration configuration) {
		this.configuration = configuration;
		this.transformationsAlignment = configuration.getTransformationsAlignment();
		this.transformationsCorrespondence = configuration.getTransformationsCorrespondence();
		this.transformationsResult = configuration.getTransformationsResult();
		this.filterAlignment = configuration.getFilterAlignment();
		this.filterCorrespondence = configuration.getFilterCorrespondence();
		this.filterResult = configuration.getFilterResult();
		this.modelsRootPath = configuration.getModelsRootPath();
		this.parser = configuration.getParser();
		this.loader = new Loader(configuration.getAlignmentReader());
		this.thresholds = configuration.getThresholds();
		this.goldstandardPaths = configuration.getGoldstandardPaths();
		this.outputName = configuration.getOutputName();
		this.outputPath = configuration.getOutputPath();
		this.validator = new SizeValidator();
		this.flowListener = configuration.getFlowListener();
		this.goldstandards = new HashMap<>();
		this.matcherPaths = configuration.getMatcherPaths();
		this.matchersRootPath = configuration.getMatchersRootPath();
		this.generator = new ReportGenerator(configuration);
		this.handler = configuration.getHandler();
		this.debugOn = configuration.isDebugOn();
		this.tagCTOn = configuration.isCtTagOn();
	}
	
	
	/**
	 * Runs the evaluation process using the defined configurations.
	 * Given a root path for the matcher results, automatically detects and
	 * extracts the alignments it can find compared to the goldstandard alignments.
	 * Applies transformations to every <code>Correspondence</code>, <code>Alignment</code>
	 * and <code>Result</code>. If configured, also computes the <code>CorrespondenceType</code>
	 * for each <code>Correspondence</code>. Finally ships the <code>Result</code>s to 
	 * each <code>ResultHandler</code>.
	 * @return same <code>Evaluator</code> instance to allow method chaining
	 * @throws IOException
	 * @throws CorrespondenceException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public Evaluation run() throws ParserConfigurationException, SAXException, IOException, CorrespondenceException {
		log("------------Evaluator------------");
		this.models = loadModels(this.modelsRootPath);
		if(!thresholds.isEmpty()) {
			//Load all goldstandard solutions for all thresholds
			for(double threshold : thresholds) {
				List<Solution> golds = new ArrayList<>();
				for(String goldstandardPath : goldstandardPaths) {
					Solution gs = loader.load(GOLDSTANDARD_NAME, goldstandardPath, threshold);
					golds.add(gs);
				}
				goldstandards.put(threshold, golds);
			}
			
		} else {
			//Load all goldstandards solutions without threshold
			List<Solution> golds = new ArrayList<>();
			for(String goldstandardPath : goldstandardPaths) {
				Solution gs = loader.load(GOLDSTANDARD_NAME, goldstandardPath, THRESHOLD_ZERO);
				golds.add(gs);
			}
			goldstandards.put(THRESHOLD_ZERO, golds);
		}
		//Load matcher solutions
		matcherSolutions = loader.loadAll(matchersRootPath, matcherPaths, THRESHOLD_ZERO);
		//Validate matcher solutions against goldstandard
		ValidationReport report = validate();
		if(!report.isOk()) {
			System.err.println(report.getInfoMessage());
			System.exit(0);
		}
		if(tagCTOn) {
			this.annotator = new Annotator(this.models);
//			log("CTTagging...");
//			this.goldstandard = applyCTAnnotationGS(this.goldstandard);
//			this.mapResult = applyCTAnnotation(this.mapResult);
		} else {
			for(Solution matcher : matcherSolutions) {
				defaultTypes(matcher);
			}
			for(List<Solution> solutions : goldstandards.values()) {
				for(Solution solution : solutions) {
					defaultTypes(solution);
				}
			}
		}

		//Generate reports
		Map<Double, List<Report>> reports = generator.generate(goldstandards, matcherSolutions);
		//Distribute reports to the ReportHandlers
		for(ReportHandler handler : this.handler) {
			handler.setFlowListener(this.flowListener);
		}
		for(Map.Entry<Double, List<Report>> e : reports.entrySet()) {
			for(ReportHandler handler : this.handler) {
				handler.setMappingInfo(getFinalOutputName(this.outputName, e.getKey()));
				handler.setOutputPath(Paths.get(this.outputPath));
				handler.open();
				handler.receive(e.getValue());
				handler.close();
			}
		}
		Evaluation evaluation = new Evaluation("", new Date(), reports);
		log("Finished tasks...");
		return evaluation;
	}

	private ValidationReport validate() {
		List<Solution> goldstandards = this.goldstandards.get(THRESHOLD_ZERO);
		Solution goldstandard = goldstandards.get(0);
		for(Solution matcher : matcherSolutions) {
			ValidationReport report = validator.validate(goldstandard, matcher);
			if(!report.isOk()) {
				return report;
			}
		}
		return new ValidationReport();
	}
	
	private String getFinalOutputName(String prefix, double threshold) {
		return prefix + SEPERATOR
				+ "t" + String.valueOf(threshold).replace(".", "");
	}
	
	
	private void defaultTypes(Solution solution) {
		for(Alignment a : solution) {
			for(Correspondence c : a) {
				c.setType(CorrespondenceType.DEFAULT);
			}
		}
	}

	/**
	 * Runs the evaluation process using the defined configurations.
	 * Given a root path for the matcher results, automatically detects and
	 * extracts the alignments it can find compared to the goldstandard alignments.
	 * Applies transformations to every <code>Correspondence</code>, <code>Alignment</code>
	 * and <code>Result</code>. If configured, also computes the <code>CorrespondenceType</code>
	 * for each <code>Correspondence</code>. Finally ships the <code>Result</code>s to 
	 * each <code>ResultHandler</code>.
	 * @return same <code>Evaluator</code> instance to allow method chaining
	 * @throws IOException
	 * @throws CorrespondenceException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
//	public Evaluator run() throws IOException, CorrespondenceException, 
//									ParserConfigurationException, SAXException {
//		log("------------Evaluator------------");
//		this.models = loadModels(this.modelsRootPath);
//		if(!thresholds.isEmpty()) {
//			for(double threshold : thresholds) {
//				this.goldstandard.put(threshold,loadResult(this.goldstandardPath, GOLDSTANDARD_NAME, threshold));
//				log("Goldstandard found at : " + this.goldstandard.get(threshold).getPath() + 
//						this.goldstandard.get(threshold).getName() + " [" + this.goldstandard.get(threshold).size() + "]");
//				this.mapResult.put(threshold, this.searchForResults(this.matchersRootPath, THRESHOLD_ZERO));
//			}
//		} else {
//			this.goldstandard.put(THRESHOLD_ZERO,loadResult(this.goldstandardPath, GOLDSTANDARD_NAME, THRESHOLD_ZERO));
//			log("Goldstandard found at : " + this.goldstandard.get(THRESHOLD_ZERO).getPath() + 
//					this.goldstandard.get(THRESHOLD_ZERO).getName() + " [" + this.goldstandard.get(THRESHOLD_ZERO).size() + "]");
//			searchForResults(this.matchersRootPath, THRESHOLD_ZERO);
//		}
//		this.mapResult = applyTransformationsCorrespondence(this.mapResult, this.transformationsCorrespondence);
//		this.mapResult = applyTransformationsAlignment(this.mapResult, this.transformationsAlignment);
//		this.mapResult = applyTransformationsResults(this.mapResult, this.transformationsResult);
//		this.mapResult = applyFilterCorrespondence(this.mapResult, this.filterCorrespondence);
//		this.mapResult = applyFilterAlignment(this.mapResult, this.filterAlignment);
//		this.mapResult = applyFilterResults(this.mapResult, this.filterResult);
//		if(tagCTOn) {
//			this.annotator = new Annotator(this.models);
//			log("CTTagging...");
//			this.goldstandard = applyCTAnnotationGS(this.goldstandard);
//			this.mapResult = applyCTAnnotation(this.mapResult);
//		}
//		this.mapResult = computeMetrics(this.mapResult);
//		for(ResultHandler handler : this.handler) {
//			handler.setFlowListener(this.flowListener);
//		}
//		for(Map.Entry<Double, List<Result>> e : this.mapResult.entrySet()) {
//			for(ResultHandler handler : this.handler) {
//				handler.setMappingInfo(getFinalOutputName(this.outputName, e.getKey()));
//				handler.setOutputPath(Paths.get(this.outputPath));
//				handler.open();
//				handler.receive(e.getValue());
//				handler.close();
//			}
//		}
//		log("Finished tasks...");
//		return this;
//	}
	 
//	private Result applyCTAnnotation(Result result) {
//		for(Alignment alignment : result) {
//			alignment = this.annotator.annotateAlignment(alignment);
//		}
//		return result;
//	}
	
//	private Map<Double, Result> applyCTAnnotationGS(Map<Double, Result> results) {
//		for(Result result : results.values()) {
//			result = applyCTAnnotation(result);
//		}
//		return results;
//	}
//	
//	private Map<Double, List<Result>> applyCTAnnotation(Map<Double, List<Result>> results) {
//		for(List<Result> vals : results.values()) {
//			for(Result result : vals) {
//				result = applyCTAnnotation(result);
//			}
//		}
//		return results;
//	}
	
//	private Map<Double, List<Result>> computeMetrics(Map<Double, List<Result>> results) 
//			throws CorrespondenceException {
//		log("Computing metrics...");
//		for(double threshold : this.thresholds) {
//			List<Result> currResults = results.get(threshold);
//			for(Result result : currResults) {
//				result.computeCharacteristics(this.goldstandard.get(threshold), this.tagCTOn);
//			}
//		}
//		return results;
//	}
	
//	private Map<Double, List<Result>> applyTransformationsResults(Map<Double, List<Result>> results,
//			List<Function<Result,Result>> transformations) {
//		log("Applying Transformation to Results [" + transformations.size() + "]");
//		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
//			for(Result result : e.getValue()) {
//				for(Function<Result, Result> trans : transformations) {
//					result = trans.apply(result);
//				}
//			}
//		}
//		return results;
//	}
//	
//	private Map<Double, List<Result>> applyTransformationsCorrespondence(Map<Double, List<Result>> results,
//			List<Function<Correspondence, Correspondence>> transformations) {
//		log("Applying Transformation to Correspondences [" + transformations.size() + "]");
//		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
//			for(Result result : e.getValue()) {
//				for(Alignment alignment : result) {
//					for(Correspondence c : alignment) {
//						for(Function<Correspondence, Correspondence> trans : transformations) {
//							c = trans.apply(c);
//						}
//					}
//				}
//			}
//		}
//		return results;
//	}
//	
//	private Map<Double, List<Result>> applyTransformationsAlignment(Map<Double, List<Result>> results,
//			List<Function<Alignment, Alignment>> transformations) {
//		log("Applying Transformation to Alignments [" + transformations.size() + "]");
//		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
//			for(Result result : e.getValue()) {
//				for(Alignment alignment : result) {
//					for(Function<Alignment, Alignment> trans : transformations) {
//						alignment = trans.apply(alignment);
//					}
//				}
//			}
//		}
//		return results;
//	}
//	
//	private Map<Double, List<Result>> applyFilterCorrespondence(Map<Double, List<Result>> results,
//			List<Predicate<Correspondence>> filters) {
//		log("Applying Filter to Result [" + filters.size() + "]");
//		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
//			for (int i = 0; i < e.getValue().size(); i++) {
//				for (int j = 0; j < e.getValue().get(i).getAlignments().size(); j++) {
//					Alignment a = e.getValue().get(i).getAlignments().get(j);
//					for (int k = 0; k < a.size(); k++) {
//						for(Predicate<Correspondence> filter : filters) {
//							if(filter.test(a.get(k))) {
//								a.getCorrespondences().remove(k);
//							}
//						}
//					}
//				}
//			}
//		}
//		return results;
//	}
//	
//	
//	private Map<Double, List<Result>> applyFilterAlignment(Map<Double, List<Result>> results,
//			List<Predicate<Alignment>> filters) {
//		log("Applying Filter to Result [" + filters.size() + "]");
//		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
//			for (int i = 0; i < e.getValue().size(); i++) {
//				for (int j = 0; j < e.getValue().get(i).getAlignments().size(); j++) {
//					for(Predicate<Alignment> filter : filters) {
//						if(filter.test(e.getValue().get(i).getAlignments().get(j))) {
//							e.getValue().get(i).getAlignments().remove(j);
//						}
//					}
//				}
//			}
//		}
//		return results;
//	}
//	
//	private Map<Double, List<Result>> applyFilterResults(Map<Double, List<Result>> results,
//			List<Predicate<Result>> filters) {
//		log("Applying Filter to Result [" + filters.size() + "]");
//		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
//			for (int i = 0; i < e.getValue().size(); i++) {
//				for(Predicate<Result> filter : filters) {
//					if(filter.test(e.getValue().get(i))) {
//						e.getValue().remove(i);
//					}
//				}
//			}
//		}
//		return results;
//	}
//	
//	private String getFinalOutputName(String prefix, double threshold) {
//		return prefix + SEPERATOR
//				+ "t" + String.valueOf(threshold).replace(".", "");
//	}
	
	private List<Model> loadModels(String path) throws ParserConfigurationException, SAXException, IOException {
		log("Loading models from " + path);
		List<Model> models = new ArrayList<>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
			if(Files.isRegularFile(filePath)) {
				try {
					String p = filePath.getFileName().toString();
					if(p.contains(Parser.TYPE_BPMN) ||
							p.contains(Parser.TYPE_EPML) || p.contains(Parser.TYPE_PNML)) {
						Model model = parser.parse(filePath.toString());
						model.setName(p.split("\\.")[0]);
						models.add(model);
						log("Found model at " + filePath);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return models;
	}

	private void log(String info) {
		if(debugOn) {
			LOG.info(info);
		}
	}
	
	public List<String> getGoldstandardPath() {
		return goldstandardPaths;
	}

	public Optional<String> getMatchersRootPath() {
		return matchersRootPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public String getOutputName() {
		return outputName;
	}

	public List<String> getMatcherPaths() {
		return matcherPaths;
	}

	public List<Double> getThresholds() {
		return thresholds;
	}

	public List<ReportHandler> getRenderVals() {
		return handler;
	}

	public boolean isDebugOn() {
		return debugOn;
	}

	public boolean isTagCTOn() {
		return tagCTOn;
	}

	public AlignmentReader getAlignmentReader() {
		return alignmentReader;
	}

	public List<Function<Report, Report>> getTransformationsResult() {
		return transformationsResult;
	}

	public List<Function<Correspondence, Correspondence>> getTransformationsCorrespondence() {
		return transformationsCorrespondence;
	}
	
	public Annotator getAnnotator() {
		return annotator;
	}

	public Parser getParser() {
		return parser;
	}
	
	public List<Throwable> getErrors() {
		return errors;
	}
	
	public boolean areErrorsOccured() {
		return !errors.isEmpty();
	}
	
	public int getNumberOfErrors() {
		return errors.size();
	}

	public List<Function<Alignment, Alignment>> getTransformationsAlignment() {
		return transformationsAlignment;
	}

	public void setTransformationsAlignment(List<Function<Alignment, Alignment>> transformationsAlignment) {
		this.transformationsAlignment = transformationsAlignment;
	}
	
	public List<Model> getModels() {
		return models;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
}