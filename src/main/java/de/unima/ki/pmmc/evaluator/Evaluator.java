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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.annotator.Annotator;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.handler.ConsoleHandler;
import de.unima.ki.pmmc.evaluator.handler.ResultHandler;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.model.parser.ParserFactory;


/**
 * Starts the evaluation process and allows to easily set up
 * the evaluation.
 */
public class Evaluator {
	
	private String goldstandardPath;
	private String matchersRootPath;
	private String modelsRootPath;
	private String outputPath;
	private String outputName;
	private Result goldstandard;
	private List<String> matcherPaths;
	private List<Double> thresholds;
	private String currMatcherName;
	private String currMatcherPath;
	private List<Alignment> currAlignments;
	private boolean debugOn;
	private boolean tagCTOn;
	private List<Result> results;
	private List<ResultHandler> handler;
	private AlignmentReader alignmentReader;
	private Map<Double, List<Result>> mapResult;
	private List<Function<Result, Result>> transformationsResult;
	private List<Function<Alignment, Alignment>> transformationsAlignment;
	private List<Function<Correspondence, Correspondence>> transformationsCorrespondence;
	private Consumer<String> flowListener;
	private Annotator annotator;
	private Parser parser;

	public static final double THRESHOLD_ZERO = 0.0;
	public static final double THRESHOLD_LOW = 0.375;
	public static final double THRESHOLD_MEDIUM = 0.5;
	public static final double THRESHOLD_HIGH = 0.75;
	public static final int METRIC_BINARY = 0;
	public static final int METRIC_NON_BINARY = 1;
	
	private static final String GOLDSTANDARD_NAME = "goldstandard";
	private static final String SEPERATOR = "-";
	private static final Logger LOG = Logger.getLogger(Evaluator.class.getName());
	
	public Evaluator(String goldstandardPath, String matchersRootPath,
			String modelsRootPath, String outputPath, String outputName, Result goldstandard,
			List<String> matcherPaths, List<Result> results,
			List<Double> thresholds, List<ResultHandler> handler,
			boolean debugOn, boolean tagCTOn,
			Consumer<String> flowListener,
			AlignmentReader alignmentReader,
			List<Function<Result, Result>> transResult,
			List<Function<Correspondence, Correspondence>> transCorr,
			List<Function<Alignment, Alignment>> transAlign,
			Parser parser) {
		this.goldstandardPath = goldstandardPath;
		this.matchersRootPath = matchersRootPath;
		this.modelsRootPath = modelsRootPath;
		this.outputPath = outputPath;
		this.outputName = outputName;
		this.goldstandard = goldstandard;
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
		this.parser = parser;
		this.currAlignments = new ArrayList<>();
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
		this.goldstandard = loadResult(this.goldstandardPath, GOLDSTANDARD_NAME);
		for(String matcherPath : this.matcherPaths) {
			results.add(loadResult(matcherPath, extractName(matcherPath)));
		}
		searchForResults(this.matchersRootPath);
		this.mapResult = applyThreshold();
		this.mapResult = applyTransformationsCorrespondence(this.mapResult, this.transformationsCorrespondence);
		this.mapResult = applyTransformationsAlignment(this.mapResult, this.transformationsAlignment);
		this.mapResult = applyTransformationsResults(this.mapResult, this.transformationsResult);
		if(tagCTOn) {
			this.annotator = new Annotator(loadModels(this.modelsRootPath));
			this.goldstandard = applyCTAnnotation(this.goldstandard);
			this.mapResult = applyCTAnnotation(this.mapResult);
		}
		this.mapResult = computeMetrics(this.mapResult);
		for(ResultHandler handler : this.handler) {
			handler.open();
			handler.setFlowListener(this.flowListener);
		}
		for(Map.Entry<Double, List<Result>> e : this.mapResult.entrySet()) {
			for(ResultHandler handler : this.handler) {
				handler.setMappingInfo(getFinalOutputName(this.outputName, e.getKey()));
				handler.setOutputPath(Paths.get(this.outputPath));
				handler.receive(e.getValue());
			}
		}
		for(ResultHandler handler : this.handler) {
			handler.close();
		}
		return this;
	}
	
	private Result applyCTAnnotation(Result result) {
		for(Alignment alignment : result) {
			alignment = this.annotator.annotateAlignment(alignment);
		}
		return result;
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
		for(Map.Entry<Double, List<Result>> e : results.entrySet()) {
			List<Result> currResults = e.getValue();
			for(Result result : currResults) {
				result.computeCharacteristics(this.goldstandard, this.tagCTOn);
			}
		}
		return results;
	}
	
	private Map<Double, List<Result>> applyTransformationsResults(Map<Double, List<Result>> results,
			List<Function<Result,Result>> transformations) {
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
	
	public Evaluator reset() {
		return this;
	}
	
	private String getFinalOutputName(String prefix, double threshold) {
		return prefix + SEPERATOR
				+ "t" + String.valueOf(threshold).replace(".", "");
	}
	
	private Map<Double, List<Result>> applyThreshold() {
		Map<Double, List<Result>> vals = new HashMap<>();
		if(!thresholds.isEmpty()) {
			for(double t : thresholds) {
				List<Result> tmpResults = getResults();
				for(Result result : tmpResults) {
					result.applyThreshold(t);
				}
				vals.put(t, tmpResults);
			}
		} else {
			vals.put(THRESHOLD_ZERO, getResults());
		}
		return vals;
	}
	
	private List<Model> loadModels(String path) throws ParserConfigurationException, SAXException, IOException {
		List<Model> models = new ArrayList<>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
			if(Files.isRegularFile(filePath)) {
				try {
					String p = filePath.getFileName().toString();
					if(p.contains(Parser.TYPE_BPMN) ||
							p.contains(Parser.TYPE_EPK) || p.contains(Parser.TYPE_PNML)) {
						models.add(parser.parse(filePath.toString()));			
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return models;
	}
	
	
	private Result loadResult(String path, String name) throws IOException {
		List<Alignment> alignments = new ArrayList<>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
				if(Files.isRegularFile(filePath)) {
					try {
						Alignment a = alignmentReader.getAlignment(filePath.toString());
						a.setName(filePath.getFileName().toString());
						alignments.add(a);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		});
		return new Result(name, path, alignments);
	}
	
	private String extractName(String path) {
		return path;
	}
	
	private void searchForResults(String path) throws IOException {
		Files.walk(Paths.get(path)).forEach(filePath -> {
			if(isFileInGS(filePath.getFileName().toString())) {
				try {
					Alignment a = alignmentReader.getAlignment(filePath.toString());
					a.setName(filePath.getFileName().toString());
					currAlignments.add(a);
					currMatcherName = findName(filePath, path);
					currMatcherPath = filePath.getParent().toString();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(!currAlignments.isEmpty()){
				//Verify if currently read matcher alignment has the same size as goldstandard
				if(goldstandard.size() != currAlignments.size()) {
					throw new IllegalArgumentException("Goldstandard alignment size unequal to matcher alignment size at "
							+ currMatcherName + "GS: " + goldstandard.size() + " Matcher: " + currAlignments.size());
				}
				results.add(new Result(currMatcherName, currMatcherPath, currAlignments));
				currAlignments = new ArrayList<>();
			}
		});
	}
	
	private String findName(Path filePath, String root) {
		List<String> dirs = new ArrayList<>();
		while(filePath.getParent() != null && 
				!filePath.getFileName().toString()
				.equals(Paths.get(root).getFileName().toString())) {
			if(Files.isDirectory(filePath)) {
				dirs.add(filePath.getFileName().toString());
			}
			filePath = filePath.getParent();
			System.out.println(filePath.getFileName().toString());
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
		for(Alignment a : this.goldstandard) {
			if(a.getName().equals(filename)) {
				return true;
			}
		}
		return false;
	}
	
	public String getGoldstandardPath() {
		return goldstandardPath;
	}

	public String getMatchersRootPath() {
		return matchersRootPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public String getOutputName() {
		return outputName;
	}

	public Result getGoldstandard() {
		return goldstandard;
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

	public Map<Double, List<Result>> getMapResult() {
		return mapResult;
	}
	
	public List<Result> getResults(double threshold) {
		return this.mapResult.get(threshold);
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
	
	public Evaluator addHandler(ResultHandler handler) {
		this.handler.add(handler);
		return this;
	}
	
	public Evaluator removeHandler(ResultHandler handler) {
		for (int i = 0; i < this.handler.size(); i++) {
			if(this.handler.get(i).getClass().getName().equals(handler.getClass().getName())) {
				this.handler.remove(i);
				return this;
			}
		}
		return this;
	}
	
	public Evaluator addResult(Result result) {
		//Verify if currently read matcher alignment has the same size as goldstandard
		if(goldstandard.size() != result.size() && goldstandard == null) {
			throw new IllegalArgumentException("Goldstandard alignment size unequal to matcher alignment size at "
					+ result.getName() + "GS: " + goldstandard.size() + " Matcher: " + result.size());
		}
		results.add(result);
		return this;
	}
	
	public Evaluator removeResult(Result result) {
		for (int i = 0; i < results.size(); i++) {
			if(results.get(i).equals(result)) {
				results.remove(i);
				return this;
			}
		}
		return this;
	}
	
	public Evaluator addMatcherPath(String path) {
		this.matcherPaths.add(path);
		return this;
	}
	
	public Evaluator removeMatcherPath(String path) {
		for (int i = 0; i < matcherPaths.size(); i++) {
			if(matcherPaths.get(i).equals(path)) {
				matcherPaths.remove(i);
				return this;
			}
		}
		return this;
	}
	
	public Evaluator setDebugOn(boolean debugOn) {
		this.debugOn = debugOn;
		return this;
	}

	public Evaluator addTransformationToResult(Function<Result, Result> transformation) {
		this.transformationsResult.add(transformation);
		return this;
	}
	
	public Evaluator addTransformationToCorrespondence(Function<Correspondence, Correspondence> transformation) {
		this.transformationsCorrespondence.add(transformation);
		return this;
	}
	
	public Evaluator addTransformationToAlignment(Function<Alignment, Alignment> transformation) {
		this.transformationsAlignment.add(transformation);
		return this;
	}

	public Evaluator setGoldstandardPath(String goldstandardPath) {
		this.goldstandardPath = goldstandardPath;
		return this;
	}

	public Evaluator setMatchersRootPath(String matchersPath) {
		this.matchersRootPath = matchersPath;
		return this;
	}

	public Evaluator setOutputName(String outputName) {
		this.outputName = outputName;
		return this;
	}

	public Evaluator setOutputPath(String outputPath) {
		this.outputPath = outputPath;
		return this;
	}

	public Evaluator setThresholds(List<Double> thresholds) {
		this.thresholds = thresholds;
		return this;
	}
	
	public Evaluator setThresholds(Double[] thresholds) {
		this.thresholds = Arrays.asList(thresholds);
		return this;
	}
	
	public Evaluator addThreshold(double threshold) {
		this.thresholds.add(threshold);
		return this;
	}
	
	public Evaluator removeThreshold(double threshold) {
		for (int i = 0; i < thresholds.size(); i++) {
			if(thresholds.get(i) == threshold) {
				thresholds.remove(i);
				return this;
			}
		}
		return this;
	}
	
	public Evaluator setTagCTOn(boolean tagCTOn) {
		this.tagCTOn = tagCTOn;
		return this;
	}

	public Evaluator setParser(String parsertype) {
		this.parser = ParserFactory.getParser(parsertype);
		return this;
	}

	public Evaluator setGoldstandard(Result goldstandard) {
		this.goldstandard = goldstandard;
		return this;
	}
	
	public Evaluator setResults(List<Result> results) {
		this.results = results;
		return this;
	}
	
	public Evaluator setAlignmentReader(AlignmentReader alignmentReader) {
		this.alignmentReader = alignmentReader;
		return this;
	}
	
	public List<Function<Alignment, Alignment>> getTransformationsAlignment() {
		return transformationsAlignment;
	}

	public void setTransformationsAlignment(List<Function<Alignment, Alignment>> transformationsAlignment) {
		this.transformationsAlignment = transformationsAlignment;
	}


	public static class Builder {
		
		private String goldstandardPath;
		private String matchersRootPath;
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
			this.outputPath =  System.getProperty("user.dir");
			this.debugOn = false;
			this.tagCTOn = false;
			this.flowListener = s -> {};
			this.outputName = "result";
		}
		
		public Builder addHandler(ResultHandler handler) {
			this.handler.add(handler);
			return this;
		}
		
		public Builder removeHandler(ResultHandler handler) {
			for (int i = 0; i < this.handler.size(); i++) {
				if(this.handler.get(i).getClass().getName().equals(handler.getClass().getName())) {
					this.handler.remove(i);
					return this;
				}
			}
			return this;
		}
		
		public Builder addResult(Result result) {
			//Verify if currently read matcher alignment has the same size as goldstandard
			if(goldstandard.size() != result.size() && goldstandard == null) {
				throw new IllegalArgumentException("Goldstandard alignment size unequal to matcher alignment size at "
						+ result.getName() + "GS: " + goldstandard.size() + " Matcher: " + result.size());
			}
			results.add(result);
			return this;
		}
		
		public Builder removeResult(Result result) {
			for (int i = 0; i < results.size(); i++) {
				if(results.get(i).equals(result)) {
					results.remove(i);
					return this;
				}
			}
			return this;
		}
		
		public Builder addMatcherPath(String path) {
			this.matcherPaths.add(path);
			return this;
		}
		
		public Builder removeMatcherPath(String path) {
			for (int i = 0; i < matcherPaths.size(); i++) {
				if(matcherPaths.get(i).equals(path)) {
					matcherPaths.remove(i);
					return this;
				}
			}
			return this;
		}
		
		public Builder addModelPath(String path) {
			this.modelPaths.add(path);
			return this;
		}
		
		public Builder removeModelPath(String path) {
			for (int i = 0; i < modelPaths.size(); i++) {
				if(modelPaths.get(i).equals(path)) {
					modelPaths.remove(i);
					return this;
				}
			}
			return this;
		}
		
		public Builder setDebugOn(boolean debugOn) {
			this.debugOn = debugOn;
			return this;
		}

		public Builder addTransformationToResult(Function<Result, Result> transformation) {
			this.transformationsResult.add(transformation);
			return this;
		}
		
		public Builder addTransformationToCorrespondence(Function<Correspondence, Correspondence> transformation) {
			this.transformationsCorrespondence.add(transformation);
			return this;
		}
		
		public Builder addTransformationToAlignment(Function<Alignment, Alignment> transformation) {
			this.transformationsAlignment.add(transformation);
			return this;
		}
		
		public Builder setGoldstandardPath(String goldstandardPath) {
			this.goldstandardPath = goldstandardPath;
			return this;
		}

		public Builder setMatchersRootPath(String matchersPath) {
			this.matchersRootPath = matchersPath;
			return this;
		}
		
		public Builder setModelsRootPath(String modelsPath) {
			this.modelsRootPath = modelsPath;
			return this;
		}

		public Builder setOutputName(String outputName) {
			this.outputName = outputName;
			return this;
		}
		
		public Builder setFlowListener(Consumer<String> listener) {
			this.flowListener = listener;
			return this;
		}

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
		
		public Builder setTagCTOn(boolean tagCTOn) {
			this.tagCTOn = tagCTOn;
			return this;
		}

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
					this.parser);
		}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, 
			IOException, AlignmentException, CorrespondenceException {
		final boolean SHOW_IN_BROWSER = true;
		final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
		final String GOLDSTANDARD_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts";
		final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
		final String MODELS_PATH = "src/main/resources/data/dataset1/models/";
//		final String SINGLE_MATCHER_TEST_PATH = "src/main/resources/data/results/submitted-matchers/RMM-VM2/dataset1";
		Evaluator evaluator = new Evaluator.Builder().
								addHandler(new ConsoleHandler()).
								setOutputPath(OUTPUT_PATH).
								setOutputName("result").
								setParser(Parser.TYPE_BPMN).
								setGoldstandardPath(GOLDSTANDARD_PATH).
								setMatchersRootPath(RESULTS_PATH).
								setModelsRootPath(MODELS_PATH).
//								addMatcherPath(SINGLE_MATCHER_TEST_PATH).
								setTagCTOn(true).
								addThreshold(THRESHOLD_ZERO).
								addThreshold(0.99).
								setAlignmentReader(new AlignmentReaderXml()).
								addTransformationToResult(r -> {
									return r;}).
								addTransformationToCorrespondence(c -> {
									return c;
								}).
								
								build();
		evaluator.run();
//
//		/**
//		 * Define multiple directories with matcher alginments.
//		 * First entry of the array should be the directory of the reference alignment.
//		 */
//		final String OUTPUT_PATH = "src/main/resources/data/evaluation/mes-nonbinary-new-gs-000.html";
//		final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
//		final String END_DIR = "/dataset1";

//		/**
//		 * Read all alignments from gold standard and matchers 
//		 */
//		final AlignmentReader alignReader = new AlignmentReaderXml();
//		List<List<Alignment>> alignments = new ArrayList<>();
//		for (int i = 0; i < dirs.length; i++) {
//			List<Alignment> aligns = new ArrayList<>();
//			Files.walk(Paths.get(dirs[i])).forEach(filePath -> {
//				if(Files.isRegularFile(filePath)) {
//					try {
//						System.out.println(filePath.toString());
//						Alignment a = alignReader.getAlignment(filePath.toString());
//						if(filePath.toString().contains("dataset1_goldstandard_experts")) {
//							a.applyThreshold(THRESHOLD);							
//						}
//						aligns.add(a);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//		});
//			alignments.add(aligns);
//		}
//		/**
//		 * Compute typecharacteristcs for each alignment
//		 */
//		List<List<Characteristic>> characteristics = new ArrayList<>();
//		for (int i = 1; i < alignments.size(); i++) {
//			List<Alignment> aligns = alignments.get(i);
//			List<Characteristic> tcharacteristics = new ArrayList<>();
//			for (int j = 0; j < aligns.size(); j++) {
//				try {
//				Alignment mapping = aligns.get(j);
//				Alignment reference = alignments.get(0).get(j);
//				Characteristic tc = new Characteristic(mapping, reference);
//				tc.setAllowZeros(false);
//				tcharacteristics.add(tc);
//				} catch(IndexOutOfBoundsException ex) {
//					System.err.println("Number of reference and matcher "
//							+ "alignments unequal : " + ex.getMessage());
//				}
//			}
//			characteristics.add(tcharacteristics);
//		}
//		/**
//		 * Annotate collection of characteristics to obtain typecharacteristics
//		 */
////		List<Model> models = loadModels();
////		List<List<TypeCharacteristic>> tCharacteristics = new ArrayList<>();
////		Annotator annotator = new Annotator(models);
////		for(List<Characteristic> chars : characteristics) {
////			tCharacteristics.add(annotator.annotateCharacteristics(chars));
////		}
//		/**
//		 * Render alignments to HTML evaluation summary page
//		 */
//		Renderer renderer = new HTMLTableNBRenderer(OUTPUT_PATH, SHOW_IN_BROWSER);
//		for (int i = 0; i < characteristics.size(); i++) {
//			renderer.render(characteristics.get(i), mappingInfo[i]);
//		}
//		renderer.flush();
	}
	
}
