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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.ecs.storage.Array;
import org.apache.ecs.xhtml.font;
import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.annotator.Annotator;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.TypeCharacteristic;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.model.parser.ParserFactory;
import de.unima.ki.pmmc.evaluator.renderer.DebugRenderer;
import de.unima.ki.pmmc.evaluator.renderer.HTMLTableNBRenderer;
import de.unima.ki.pmmc.evaluator.renderer.JavaFXRenderer;
import de.unima.ki.pmmc.evaluator.renderer.Renderer;



public class Evaluator {
	
	private String goldstandardPath;
	private String matchersRootPath;
	private String outputPath;
	private String outputName;
	private Result goldstandard;
	private List<String> matcherPaths;
	private List<Result> results;
	private List<Double> thresholds;
	private List<Renderer> renderVals;
	private String currMatcherName;
	private String currMatcherPath;
	private List<Alignment> currAlignments;
	private boolean debugOn;
	private boolean tagCTOn;
	private AlignmentReader alignmentReader;
	private Map<Double, List<Result>> mapResult;
	private List<Function<Result, Result>> transformationsResult;
	private List<Function<Correspondence, Correspondence>> transformationsCorrespondence;
	private Annotator annotator;
	private Parser parser;

	public static final double THRESHOLD_ZERO = 0.0;
	public static final double THRESHOLD_LOW = 0.375;
	public static final double THRESHOLD_MEDIUM = 0.5;
	public static final double THRESHOLD_HIGH = 0.75;
	public static final int METRIC_BINARY = 0;
	public static final int METRIC_NON_BINARY = 1;
	
	private static final String DEFAULT_NAME = "result";
	private static final String GOLDSTANDARD_NAME = "goldstandard";
	private static final String SEPERATOR = "-";
	private static final Logger LOGGER = Logger.getLogger(Evaluator.class.getName());
	
	public Evaluator(String goldstandardPath, String matchersRootPath,
			String outputPath, String outputName, Result goldstandard,
			List<String> matcherPaths, List<Result> results,
			List<Double> thresholds, List<Renderer> renderVals,
			boolean debugOn, boolean tagCTOn,
			AlignmentReader alignmentReader,
			List<Function<Result, Result>> transResult,
			List<Function<Correspondence, Correspondence>> transCorr,
			Annotator annotator, Parser parser) {
		this.goldstandardPath = goldstandardPath;
		this.matchersRootPath = matchersRootPath;
		this.outputPath = outputPath;
		this.outputName = outputName;
		this.goldstandard = goldstandard;
		this.matcherPaths = matcherPaths;
		this.results = results;
		this.thresholds = thresholds;
		this.renderVals = renderVals;
		this.debugOn = debugOn;
		this.tagCTOn = tagCTOn;
		this.alignmentReader = alignmentReader;
		this.transformationsResult = transResult;
		this.transformationsCorrespondence = transCorr;
		this.annotator = annotator;
		this.parser = parser;
	}

	public Evaluator run() throws IOException, CorrespondenceException {
		this.goldstandard = loadResult(this.goldstandardPath, GOLDSTANDARD_NAME);
		for(String matcherPath : this.matcherPaths) {
			results.add(loadResult(matcherPath, extractName(matcherPath)));
		}
		searchForResults(this.matchersRootPath);
		this.mapResult = applyThreshold();
		for(Map.Entry<Double, List<Result>> e : this.mapResult.entrySet()) {
			List<Result> currResults = e.getValue();
			for(Result result : currResults) {
				result.computeCharacteristics(this.goldstandard, this.tagCTOn);
				for(Renderer renderer : this.renderVals) {
					renderer.setFile(getFinalOutputName(outputPath, outputName, result.getName(), e.getKey()));
					if(this.tagCTOn) {
						renderer.render(result.getTypeCharacteristics(), "");
					} else {
						renderer.render(result.getCharacteristics(), "");
					}
					renderer.flush();
				}
			}
		}
		return this;
	}
	
	public Evaluator reset() {
		return this;
	}
	
	private String getFinalOutputName(String path, String prefix, String name, double threshold) {
		return path + SEPERATOR + prefix + SEPERATOR + name + SEPERATOR 
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

	public List<Renderer> getRenderVals() {
		return renderVals;
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


	public static class Builder {
		
		private String goldstandardPath;
		private String matchersRootPath;
		private String outputPath;
		private String outputName;
		private Result goldstandard;
		private List<String> matcherPaths;
		private List<Double> thresholds;
		private boolean debugOn;
		private boolean tagCTOn;
		private AlignmentReader alignmentReader;
		private List<Result> results;
		private List<Renderer> renderVals;
		private List<Function<Result, Result>> transformationsResult;
		private List<Function<Correspondence, Correspondence>> transformationsCorrespondence;
		private Annotator annotator;
		private Parser parser;
		
		public Builder() {
			this.matcherPaths = new ArrayList<>();
			this.results = new ArrayList<>();
			this.thresholds = new ArrayList<>();
			this.renderVals = new ArrayList<>();
			this.transformationsResult = new ArrayList<>();
			this.transformationsCorrespondence = new ArrayList<>();
		}
		
		public Builder addRenderer(Renderer renderer) {
			this.renderVals.add(renderer);
			return this;
		}
		
		public Builder removeRenderer(Renderer renderer) {
			for (int i = 0; i < renderVals.size(); i++) {
				if(renderVals.get(i).getClass().getName().equals(renderer.getClass().getName())) {
					renderVals.remove(i);
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

		public Builder setGoldstandardPath(String goldstandardPath) {
			this.goldstandardPath = goldstandardPath;
			return this;
		}

		public Builder setMatchersRootPath(String matchersPath) {
			this.matchersRootPath = matchersPath;
			return this;
		}

		public Builder setOutputName(String outputName) {
			this.outputName = outputName;
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
					this.outputPath, 
					this.outputName, 
					this.goldstandard, 
					this.matcherPaths, 
					this.results, 
					this.thresholds, 
					this.renderVals, 
					this.debugOn, 
					this.tagCTOn, 
					this.alignmentReader, 
					this.transformationsResult, 
					this.transformationsCorrespondence, 
					this.annotator, 
					this.parser);
		}
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, 
			IOException, AlignmentException, CorrespondenceException {
		final boolean SHOW_IN_BROWSER = true;
		final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
		final String GOLDSTANDARD_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts";
		final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
//		final String SINGLE_MATCHER_TEST_PATH = "src/main/resources/data/results/submitted-matchers/RMM-VM2/dataset1";
//		@SuppressWarnings("unused")
//		Evaluator evaluator = new Evaluator()
//								.addRenderer(new HTMLTableNBRenderer(SHOW_IN_BROWSER))
//								.setOutputPath(OUTPUT_PATH)
//								.setOutputName("result")
//								.setGoldstandardPath(GOLDSTANDARD_PATH)
//								.setMatchersRootPath(RESULTS_PATH)
////								.addMatcherPath(SINGLE_MATCHER_TEST_PATH)
//								.addThreshold(THRESHOLD_MEDIUM)
//								.addThreshold(THRESHOLD_LOW)
//								.setAlignmentReader(new AlignmentReaderXml())
//								.run();
//
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
	
	public static List<Model> loadModels() throws ParserConfigurationException, SAXException, IOException {
		final String[] modelIds = new String[]{
				"Cologne",
				"Frankfurt",
				"FU_Berlin",
				"Hohenheim",
				"IIS_Erlangen",
				"Muenster",
				"Potsdam",
				"TU_Munich",
				"Wuerzburg"
		};
		List<Model> models = new ArrayList<>();
		Parser bpmnParser = ParserFactory.getParser(Parser.TYPE_BPMN);
		for (int i = 0; i < modelIds.length - 1; i++) {
			String sourceId = modelIds[i];
			Model model = bpmnParser.parse("src/main/resources/data/dataset1/models/" + sourceId + ".bpmn");
			models.add(model);
		}
		return models;
	}

}
