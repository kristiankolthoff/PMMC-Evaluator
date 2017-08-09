package de.unima.ki.pmmc.evaluator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.unima.ki.pmmc.evaluator.annotator.Annotator;
import de.unima.ki.pmmc.evaluator.data.Report;
import de.unima.ki.pmmc.evaluator.data.Result;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.handler.ResultHandler;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.model.parser.ParserFactory;

/**
 * Starts the evaluation process and allows to easily set up
 * the evaluation.
 */
public class Evaluator {
	
	/**
	 * Paths to goldstandard alignments, multiple matchers
	 * root path and root path to the used models
	 */
	private String goldstandardPath;
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
	private Map<Double, Result> goldstandard;
	/**
	 * Contains all specified paths to matchers
	 */
	private List<String> matcherPaths;
	private List<Double> thresholds;
	/**
	 * Current matcher path and name as well as
	 * alignment which is currently read
	 */
	private String currMatcherName;
	private String currMatcherPath;
	private List<Alignment> currAlignments;
	private boolean debugOn;
	private boolean tagCTOn;
	/**
	 * Loaded results list. One result for
	 * each single matcher
	 */
	private List<Model> models;
	private List<Result> results;
	private Map<Double, List<Result>> mapResult;
	/**
	 * The result handlers which should 
	 * process the generated results
	 */
	private List<ResultHandler> handler;
	private AlignmentReader alignmentReader;
	/**
	 * Functions providing Correspondence, Alignment 
	 * and Result transformations
	 */
	private List<Function<Result, Result>> transformationsResult;
	private List<Function<Alignment, Alignment>> transformationsAlignment;
	private List<Function<Correspondence, Correspondence>> transformationsCorrespondence;
	/**
	 * Predicates for filtering Correspondences,
	 * Alignments and Results
	 */
	private List<Predicate<Result>> filterResult;
	private List<Predicate<Alignment>> filterAlignment;
	private List<Predicate<Correspondence>> filterCorrespondence;
	private Consumer<String> flowListener;
	private Annotator annotator;
	private Parser parser;
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
	
	public Evaluator(String goldstandardPath, Optional<String> matchersRootPath,
			String modelsRootPath, String outputPath, String outputName, Result goldstandard,
			List<String> matcherPaths, List<Result> results,
			List<Double> thresholds, List<ResultHandler> handler,
			boolean debugOn, boolean tagCTOn,
			Consumer<String> flowListener,
			AlignmentReader alignmentReader,
			List<Function<Result, Result>> transResult,
			List<Function<Correspondence, Correspondence>> transCorr,
			List<Function<Alignment, Alignment>> transAlign,
			List<Predicate<Result>> filterResult,
			List<Predicate<Correspondence>> filterCorrespondence,
			List<Predicate<Alignment>> filterAlignment,
			Parser parser) {
		this.goldstandardPath = goldstandardPath;
		this.matchersRootPath = matchersRootPath;
		this.modelsRootPath = modelsRootPath;
		this.outputPath = outputPath;
		this.outputName = outputName;
		this.goldstandard = new HashMap<>();
		if(goldstandard != null) {
			this.goldstandard.put(THRESHOLD_ZERO, goldstandard);
		}
		this.matcherPaths = matcherPaths;
		this.results = results;
		this.thresholds = thresholds;
		this.handler = handler;
		this.debugOn = debugOn;
		this.tagCTOn = tagCTOn;
		this.flowListener = flowListener;
		this.alignmentReader = alignmentReader;
		this.transformationsResult = transResult;
		this.transformationsCorrespondence = transCorr;
		this.transformationsAlignment = transAlign;
		this.filterResult = filterResult;
		this.filterCorrespondence = filterCorrespondence;
		this.filterAlignment = filterAlignment;
		this.parser = parser;
		this.currAlignments = new ArrayList<>();
		this.mapResult = new HashMap<>();
	}
	
	public Evaluator(Configuration configuration) {
		
	}
	
	public Report dorun() {
		
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
	public Evaluator run() throws IOException, CorrespondenceException, 
									ParserConfigurationException, SAXException {
		log("------------Evaluator------------");
		this.models = loadModels(this.modelsRootPath);
		if(!thresholds.isEmpty()) {
			for(double threshold : thresholds) {
				this.goldstandard.put(threshold,loadResult(this.goldstandardPath, GOLDSTANDARD_NAME, threshold));
				log("Goldstandard found at : " + this.goldstandard.get(threshold).getPath() + 
						this.goldstandard.get(threshold).getName() + " [" + this.goldstandard.get(threshold).size() + "]");
				this.mapResult.put(threshold, this.searchForResults(this.matchersRootPath, THRESHOLD_ZERO));
			}
		} else {
			this.goldstandard.put(THRESHOLD_ZERO,loadResult(this.goldstandardPath, GOLDSTANDARD_NAME, THRESHOLD_ZERO));
			log("Goldstandard found at : " + this.goldstandard.get(THRESHOLD_ZERO).getPath() + 
					this.goldstandard.get(THRESHOLD_ZERO).getName() + " [" + this.goldstandard.get(THRESHOLD_ZERO).size() + "]");
			searchForResults(this.matchersRootPath, THRESHOLD_ZERO);
		}
		this.mapResult = applyTransformationsCorrespondence(this.mapResult, this.transformationsCorrespondence);
		this.mapResult = applyTransformationsAlignment(this.mapResult, this.transformationsAlignment);
		this.mapResult = applyTransformationsResults(this.mapResult, this.transformationsResult);
		this.mapResult = applyFilterCorrespondence(this.mapResult, this.filterCorrespondence);
		this.mapResult = applyFilterAlignment(this.mapResult, this.filterAlignment);
		this.mapResult = applyFilterResults(this.mapResult, this.filterResult);
		if(tagCTOn) {
			this.annotator = new Annotator(this.models);
			log("CTTagging...");
			this.goldstandard = applyCTAnnotationGS(this.goldstandard);
			this.mapResult = applyCTAnnotation(this.mapResult);
		}
		this.mapResult = computeMetrics(this.mapResult);
		for(ResultHandler handler : this.handler) {
			handler.setFlowListener(this.flowListener);
		}
		for(Map.Entry<Double, List<Result>> e : this.mapResult.entrySet()) {
			for(ResultHandler handler : this.handler) {
				handler.setMappingInfo(getFinalOutputName(this.outputName, e.getKey()));
				handler.setOutputPath(Paths.get(this.outputPath));
				handler.open();
				handler.receive(e.getValue());
				handler.close();
			}
		}
		log("Finished tasks...");
		return this;
	}
	 
	private Result applyCTAnnotation(Result result) {
		for(Alignment alignment : result) {
			alignment = this.annotator.annotateAlignment(alignment);
		}
		return result;
	}
	
	private Map<Double, Result> applyCTAnnotationGS(Map<Double, Result> results) {
		for(Result result : results.values()) {
			result = applyCTAnnotation(result);
		}
		return results;
	}
	
	private Map<Double, List<Result>> applyCTAnnotation(Map<Double, List<Result>> results) {
		for(List<Result> vals : results.values()) {
			for(Result result : vals) {
				result = applyCTAnnotation(result);
			}
		}
		return results;
	}
	
	private Map<Double, List<Result>> computeMetrics(Map<Double, List<Result>> results) 
			throws CorrespondenceException {
		log("Computing metrics...");
		for(double threshold : this.thresholds) {
			List<Result> currResults = results.get(threshold);
			for(Result result : currResults) {
				result.computeCharacteristics(this.goldstandard.get(threshold), this.tagCTOn);
			}
		}
		return results;
	}
	
	private Map<Double, List<Result>> applyTransformationsResults(Map<Double, List<Result>> results,
			List<Function<Result,Result>> transformations) {
		log("Applying Transformation to Results [" + transformations.size() + "]");
		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
			for(Result result : e.getValue()) {
				for(Function<Result, Result> trans : transformations) {
					result = trans.apply(result);
				}
			}
		}
		return results;
	}
	
	private Map<Double, List<Result>> applyTransformationsCorrespondence(Map<Double, List<Result>> results,
			List<Function<Correspondence, Correspondence>> transformations) {
		log("Applying Transformation to Correspondences [" + transformations.size() + "]");
		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
			for(Result result : e.getValue()) {
				for(Alignment alignment : result) {
					for(Correspondence c : alignment) {
						for(Function<Correspondence, Correspondence> trans : transformations) {
							c = trans.apply(c);
						}
					}
				}
			}
		}
		return results;
	}
	
	private Map<Double, List<Result>> applyTransformationsAlignment(Map<Double, List<Result>> results,
			List<Function<Alignment, Alignment>> transformations) {
		log("Applying Transformation to Alignments [" + transformations.size() + "]");
		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
			for(Result result : e.getValue()) {
				for(Alignment alignment : result) {
					for(Function<Alignment, Alignment> trans : transformations) {
						alignment = trans.apply(alignment);
					}
				}
			}
		}
		return results;
	}
	
	private Map<Double, List<Result>> applyFilterCorrespondence(Map<Double, List<Result>> results,
			List<Predicate<Correspondence>> filters) {
		log("Applying Filter to Result [" + filters.size() + "]");
		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
			for (int i = 0; i < e.getValue().size(); i++) {
				for (int j = 0; j < e.getValue().get(i).getAlignments().size(); j++) {
					Alignment a = e.getValue().get(i).getAlignments().get(j);
					for (int k = 0; k < a.size(); k++) {
						for(Predicate<Correspondence> filter : filters) {
							if(filter.test(a.get(k))) {
								a.getCorrespondences().remove(k);
							}
						}
					}
				}
			}
		}
		return results;
	}
	
	
	private Map<Double, List<Result>> applyFilterAlignment(Map<Double, List<Result>> results,
			List<Predicate<Alignment>> filters) {
		log("Applying Filter to Result [" + filters.size() + "]");
		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
			for (int i = 0; i < e.getValue().size(); i++) {
				for (int j = 0; j < e.getValue().get(i).getAlignments().size(); j++) {
					for(Predicate<Alignment> filter : filters) {
						if(filter.test(e.getValue().get(i).getAlignments().get(j))) {
							e.getValue().get(i).getAlignments().remove(j);
						}
					}
				}
			}
		}
		return results;
	}
	
	private Map<Double, List<Result>> applyFilterResults(Map<Double, List<Result>> results,
			List<Predicate<Result>> filters) {
		log("Applying Filter to Result [" + filters.size() + "]");
		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
			for (int i = 0; i < e.getValue().size(); i++) {
				for(Predicate<Result> filter : filters) {
					if(filter.test(e.getValue().get(i))) {
						e.getValue().remove(i);
					}
				}
			}
		}
		return results;
	}
	
	private String getFinalOutputName(String prefix, double threshold) {
		return prefix + SEPERATOR
				+ "t" + String.valueOf(threshold).replace(".", "");
	}
	
	private List<Model> loadModels(String path) throws ParserConfigurationException, SAXException, IOException {
		log("Loading models from " + path);
		List<Model> models = new ArrayList<>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
			if(Files.isRegularFile(filePath)) {
				try {
					String p = filePath.getFileName().toString();
					if(p.contains(Parser.TYPE_BPMN) ||
							p.contains(Parser.TYPE_EPK) || p.contains(Parser.TYPE_PNML)) {
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
	
	
	private Result loadResult(String path, String name, double threshold) throws IOException {
		List<Alignment> alignments = new ArrayList<>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
				if(Files.isRegularFile(filePath)) {
					try {
						Alignment a = alignmentReader.getAlignment(filePath.toString());
						a.applyThreshold(threshold);
						List<Model> sourceTargetModels = findModels(filePath);
						//TODO what if list empty or only contains one model
						a.setSourceModel(sourceTargetModels.get(0));
						a.setTargetModel(sourceTargetModels.get(1));
						a.setName(filePath.getFileName().toString());
						alignments.add(a);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		});
		return new Result(name, path, threshold, alignments);
	}
	
	private String extractName(String path) {
		return path;
	}
	
	/**
	 * Automatically searches at a given path for matcher alignments
	 * to build up the <code>Result</code> collection.
	 * @param path the root path used as a starting point to search for results
	 * @throws IOException
	 */
	private List<Result> searchForResults(Optional<String> path, double threshold) throws IOException {
		log("Generate Results for threshold [" + threshold + "]");
		List<Result> results = new ArrayList<>();
		for(String matcherPath : this.matcherPaths) {
			results.add(loadResult(matcherPath, extractName(matcherPath), threshold));
		}
		if(path.isPresent()) {
			Files.walk(Paths.get(path.get())).forEach(filePath -> {
				if(isFileInGS(filePath.getFileName().toString())) {
					try {
						Alignment a = alignmentReader.getAlignment(filePath.toString());
						a.applyThreshold(threshold);
						a.setName(filePath.getFileName().toString());
						List<Model> sourceTargetModels = findModels(filePath);
						//TODO what if list empty or only contains one model
						a.setSourceModel(sourceTargetModels.get(0));
						a.setTargetModel(sourceTargetModels.get(1));
						currAlignments.add(a);
						currMatcherName = findName(filePath, path.get());
						currMatcherPath = filePath.getParent().toString();
					} catch (Exception e) {
						errors.add(e);
						e.printStackTrace();
					}
				} else if(!currAlignments.isEmpty()){
					//TODO make check with models to verify explicitly
					//Verify if currently read matcher alignment has the same size as goldstandard
					Result goldstandard = this.goldstandard.values().iterator().next();
					if(goldstandard.size() != currAlignments.size()) {
						throw new IllegalArgumentException("Goldstandard alignment size unequal to matcher alignment size at "
								+ currMatcherName + " GS: " + goldstandard.size() + " Matcher: " + currAlignments.size());
					}
					results.add(new Result(currMatcherName, currMatcherPath, threshold, currAlignments));
					log("[" + currMatcherName + "] @ " + currMatcherPath + " [" + currAlignments.size() + "]");
					currAlignments = new ArrayList<>();
				}
			});
		}
		return results;
	}
	
	private List<Model> findModels(Path path) {
		List<Model> models = new ArrayList<>();
		for(Model model : this.models) {
			if(path.toString().contains(model.getName())) {
				models.add(model);
			}
		}
		return models;
	}
	
	/**
	 * Given a file path and a root name, automatically
	 * extracts the name for a matcher as the directory
	 * chain from the root to the actual RDF <code>Alignment</code>
	 * files.
	 * @param filePath the file path to start searching from
	 * @param root the top most root path of the matcher
	 * @return the extracted matcher name for a <code>Result</code>
	 */
	private String findName(Path filePath, String root) {
		List<String> dirs = new ArrayList<>();
		while(filePath.getParent() != null && 
				!filePath.getFileName().toString()
				.equals(Paths.get(root).getFileName().toString())) {
			if(Files.isDirectory(filePath)) {
				dirs.add(filePath.getFileName().toString());
			}
			filePath = filePath.getParent();
		}
		String name = "";
		for (int i = dirs.size()-1; i >= 0; i--) {
			name += dirs.get(i);
			if(i != 0) {
				name += SEPERATOR;
			}
		}
		return name;
	}
	
	private boolean isFileInGS(String filename) {
		Result goldstandard = this.goldstandard.values().iterator().next();
		for(Alignment a : goldstandard) {
			if(a.getName().equals(filename)) {
				return true;
			}
		}
		return false;
	}
	
	private void log(String info) {
		if(debugOn) {
			LOG.info(info);
		}
	}
	
	public String getGoldstandardPath() {
		return goldstandardPath;
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

	public Result getGoldstandard(double threshold) {
		return goldstandard.get(threshold);
	}

	public List<String> getMatcherPaths() {
		return matcherPaths;
	}

	public List<Result> getResults() {
		return results;
	}

	public List<Double> getThresholds() {
		return thresholds;
	}

	public List<ResultHandler> getRenderVals() {
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

	public List<Function<Result, Result>> getTransformationsResult() {
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


	/**
	 * Allows to easily create and configure an <code>Evaluator</code>
	 * instance.
	 */
	public static class Builder {
		
		private String goldstandardPath;
		private Optional<String> matchersRootPath;
		private String modelsRootPath;
		private String outputPath;
		private String outputName;
		private Result goldstandard;
		private List<String> matcherPaths;
		private List<String> modelPaths;
		private List<Double> thresholds;
		private boolean debugOn;
		private boolean tagCTOn;
		private AlignmentReader alignmentReader;
		private List<Result> results;
		private List<ResultHandler> handler;
		private Consumer<String> flowListener;
		private List<Function<Result, Result>> transformationsResult;
		private List<Function<Correspondence, Correspondence>> transformationsCorrespondence;
		private List<Function<Alignment, Alignment>> transformationsAlignment;
		private List<Predicate<Result>> filterResult;
		private List<Predicate<Correspondence>> filterCorrespondence;
		private List<Predicate<Alignment>> filterAlignment;
		private Parser parser;
		
		public Builder() {
			this.matcherPaths = new ArrayList<>();
			this.modelPaths = new ArrayList<>();
			this.results = new ArrayList<>();
			this.thresholds = new ArrayList<>();
			this.handler = new ArrayList<>();
			this.transformationsResult = new ArrayList<>();
			this.transformationsCorrespondence = new ArrayList<>();
			this.transformationsAlignment = new ArrayList<>();
			this.filterResult = new ArrayList<>();
			this.filterCorrespondence = new ArrayList<>();
			this.filterAlignment = new ArrayList<>();
			this.matchersRootPath = Optional.empty();
			this.outputPath =  System.getProperty("user.dir");
			this.debugOn = false;
			this.tagCTOn = false;
			this.flowListener = s -> {};
			this.outputName = "result";
		}
		
		/**
		 * Adds a <code>ResultHandler</code> which is used
		 * as a sink for the computed <code>Result</code>s.
		 * @param handler the handler which should be added
		 * @return this
		 */
		public Builder addHandler(ResultHandler handler) {
			this.handler.add(handler);
			return this;
		}
		
		/**
		 * Removes a <code>ResultHandler</code> from the current
		 * collection of handlers.
		 * @param handler the handler which should be removed
		 * @return this
		 */
		public Builder removeHandler(ResultHandler handler) {
			for (int i = 0; i < this.handler.size(); i++) {
				if(this.handler.get(i).getClass().getName().equals(handler.getClass().getName())) {
					this.handler.remove(i);
					return this;
				}
			}
			return this;
		}
		
		/**
		 * Manually add a <code>Result</code> to the collection.
		 * @param result the result which should be added
		 * @return this
		 */
		public Builder addResult(Result result) {
			//Verify if currently read matcher alignment has the same size as goldstandard
			if(goldstandard.size() != result.size() && goldstandard == null) {
				throw new IllegalArgumentException("Goldstandard alignment size unequal to matcher alignment size at "
						+ result.getName() + "GS: " + goldstandard.size() + " Matcher: " + result.size());
			}
			results.add(result);
			return this;
		}
		
		/**
		 * Removes a <code>Result</code> from the collection.
		 * @param result the result which should be removed
		 * @return this
		 */
		public Builder removeResult(Result result) {
			for (int i = 0; i < results.size(); i++) {
				if(results.get(i).equals(result)) {
					results.remove(i);
					return this;
				}
			}
			return this;
		}
		
		/**
		 * Adds a root path to alignments of a matcher.
		 * The files within this folder should be actual
		 * RDF alignment files.
		 * @param path the root path to the alignments of a single matcher
		 * @return this
		 */
		public Builder addMatcherPath(String path) {
			this.matcherPaths.add(path);
			return this;
		}
		
		/**
		 * Removes a root path to alignments of a matcher from
		 * the collection.
		 * @param path the root path which should be removed
		 * @return this
		 */
		public Builder removeMatcherPath(String path) {
			for (int i = 0; i < matcherPaths.size(); i++) {
				if(matcherPaths.get(i).equals(path)) {
					matcherPaths.remove(i);
					return this;
				}
			}
			return this;
		}
		
		/**
		 * Adds the root path of the source folder
		 * containing the actual <code>Model</code>s
		 * which were used to generate the alignments.
		 * @param path the root path of the actual models
		 * @return this
		 */
		public Builder addModelPath(String path) {
			this.modelPaths.add(path);
			return this;
		}
		
		/**
		 * Removes the root path to the <code>Model</code>s.
		 * @param path the root path of the actual models
		 * @return this
		 */
		public Builder removeModelPath(String path) {
			for (int i = 0; i < modelPaths.size(); i++) {
				if(modelPaths.get(i).equals(path)) {
					modelPaths.remove(i);
					return this;
				}
			}
			return this;
		}
		
		/**
		 * If debug mode is on, prints log information to the console
		 * about the evaluation progress.
		 * @param debugOn if true logs progress, else silent
		 * @return this
		 */
		public Builder setDebugOn(boolean debugOn) {
			this.debugOn = debugOn;
			return this;
		}

		/**
		 * Adds custom <code>Result</code> transformation. This transformation
		 * is applied to each generated <code>Result</code> instance of a matcher.
		 * @param transformation which should be applied to the generated results
		 * @return this
		 */
		public Builder addTransformationToResult(Function<Result, Result> transformation) {
			this.transformationsResult.add(transformation);
			return this;
		}
		
		/**
		 * Adds custom <code>Correspondence</code> transformation. This transformation
		 * is applied to each generated <code>Correspondence</code> instances of a matcher.
		 * @param transformation which should be applied to the generated correspondences
		 * @return this
		 */
		public Builder addTransformationToCorrespondence(Function<Correspondence, Correspondence> transformation) {
			this.transformationsCorrespondence.add(transformation);
			return this;
		}
		
		/**
		 * Adds custom <code>Alignment</code> transformation. This transformation
		 * is applied to each generated <code>Alignment</code> instance of a matcher.
		 * @param transformation which should be applied to the generated alignments
		 * @return this
		 */
		public Builder addTransformationToAlignment(Function<Alignment, Alignment> transformation) {
			this.transformationsAlignment.add(transformation);
			return this;
		}
		
		/**
		 * Adds custom <code>Result</code> filter. This filter
		 * is applied to each generated <code>Result</code> instance of a matcher,
		 * and filters the <code>Result</code> based on the filter predicate.
		 * @param filter which should be applied to the generated results
		 * @return this
		 */
		public Builder addFilterToResult(Predicate<Result> filter) {
			this.filterResult.add(filter);
			return this;
		}
		
		/**
		 * Adds custom <code>Correspondence</code> filter. This filter
		 * is applied to each <code>Correspondence</code> instance of a matcher,
		 * and filters the <code>Correspondence</code> based on the filter predicate.
		 * @param filter which should be applied to the generated correspondences
		 * @return this
		 */
		public Builder addFilterToCorrespondence(Predicate<Correspondence> filter) {
			this.filterCorrespondence.add(filter);
			return this;
		}
		
		/**
		 * Adds custom <code>Alignment</code> filter. This filter
		 * is applied to each generated <code>Alignment</code> instance of a matcher,
		 * and filters the <code>Alignment</code> based on the filter predicate.
		 * @param filter which should be applied to the generated alignments
		 * @return this
		 */
		public Builder addFilterToAlignment(Predicate<Alignment> filter) {
			this.filterAlignment.add(filter);
			return this;
		}
		
		/**
		 * Set root path of goldstandard alignment
		 * @param goldstandardPath the path to the goldstandard alignments
		 * @return this
		 */
		public Builder setGoldstandardPath(String goldstandardPath) {
			this.goldstandardPath = goldstandardPath;
			return this;
		}
		
		/**
		 * Set root path for alignments of multiple matchers. Used
		 * to automatically extract the name of a matcher and its
		 * <code>Alignment</code>s corresponding to the <code>Alignment</code>s
		 * from the goldstandard.
		 * @param matchersPath root path to matcher alignments
		 * @return this
		 */
		public Builder setMatchersRootPath(String matchersPath) {
			this.matchersRootPath = Optional.of(matchersPath);
			return this;
		}
		
		/**
		 * Set root path to the acutal RDF models used for generating
		 * the alignments.
		 * @param modelsPath the path to the RDF files of the acutal <code>Model</code>s
		 * @return this
		 */
		public Builder setModelsRootPath(String modelsPath) {
			this.modelsRootPath = modelsPath;
			return this;
		}

		/**
		 * Sets the prefix of the generated <code>Result</code>s
		 * @param outputName the name
		 * @return this
		 */
		public Builder setOutputName(String outputName) {
			this.outputName = outputName;
			return this;
		}
		
		/**
		 * Used to process current evalation events.
		 * @param listener the listener which should be used
		 * @return this
		 */
		public Builder setFlowListener(Consumer<String> listener) {
			this.flowListener = listener;
			return this;
		}

		/**
		 * Sets the path for the output which should be
		 * used by corresponding <code>ResultHandler</code>s.
		 * @param outputPath the output path
		 * @return this
		 */
		public Builder setOutputPath(String outputPath) {
			this.outputPath = outputPath;
			return this;
		}

		public Builder setThresholds(List<Double> thresholds) {
			this.thresholds = thresholds;
			return this;
		}
		
		public Builder setThresholds(Double[] thresholds) {
			this.thresholds = Arrays.asList(thresholds);
			return this;
		}
		
		public Builder addThreshold(double threshold) {
			this.thresholds.add(threshold);
			return this;
		}
		
		public Builder removeThreshold(double threshold) {
			for (int i = 0; i < thresholds.size(); i++) {
				if(thresholds.get(i) == threshold) {
					thresholds.remove(i);
					return this;
				}
			}
			return this;
		}
		
		/**
		 * Specifies wether to annotate each <code>Correspondence</code>
		 * with its <code>CorrespondenceType</code>. If set to true,
		 * annotates <code>Correspondence</code>s and computes the <code>
		 * TypeCharacteristic</code> for each <code>Result</code>. Otherwise
		 * computes only the <code>Charateristics</code>. Note that if this is
		 * set to true, also <code>Parser</code> and root path to <code>Model</code>s
		 * is required.
		 * @param tagCTOn if true, annotates correspondences with their type
		 * @return this
		 */
		public Builder setTagCTOn(boolean tagCTOn) {
			this.tagCTOn = tagCTOn;
			return this;
		}

		/**
		 * Set the type of the <code>Parser</code> which should be used
		 * to read the model files.
		 * @param parsertype the parser type
		 * @return this
		 */
		public Builder setParser(String parsertype) {
			this.parser = ParserFactory.getParser(parsertype);
			return this;
		}

		public Builder setGoldstandard(Result goldstandard) {
			this.goldstandard = goldstandard;
			return this;
		}
		
		public Builder setResults(List<Result> results) {
			this.results = results;
			return this;
		}
		
		/**
		 * Set the <code>AlignmentReader</code> implementation that should
		 * be used to read the RDF alignments.
		 * @param alignmentReader the alignment reader for parsing RDF files
		 * @return this
		 */
		public Builder setAlignmentReader(AlignmentReader alignmentReader) {
			this.alignmentReader = alignmentReader;
			return this;
		}
		
		public Evaluator build() {
			return new Evaluator(this.goldstandardPath, 
					this.matchersRootPath,
					this.modelsRootPath,
					this.outputPath, 
					this.outputName, 
					this.goldstandard, 
					this.matcherPaths,
					this.results, 
					this.thresholds, 
					this.handler, 
					this.debugOn, 
					this.tagCTOn, 
					this.flowListener,
					this.alignmentReader, 
					this.transformationsResult, 
					this.transformationsCorrespondence, 
					this.transformationsAlignment,
					this.filterResult,
					this.filterCorrespondence,
					this.filterAlignment,
					this.parser);
		}
	}
	
}