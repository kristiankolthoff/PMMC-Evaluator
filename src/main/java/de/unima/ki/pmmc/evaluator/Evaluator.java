package de.unima.ki.pmmc.evaluator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
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
	private double threshold;
	private List<Double> thresholds;
	private List<Renderer> renderVals;
	private String currMatcherName;
	private boolean debugOn;
	private AlignmentReader alignmentReader;
	private Function<Result, Result> transResult;
	private Function<Correspondence, Correspondence> transCorr;

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
	
	public Evaluator(List<Renderer> renderVals, List<Result> results, Result goldstandard) {
		this.renderVals = renderVals;
		this.results = results;
		this.goldstandard = goldstandard;
	}
	
	public Evaluator(Result goldstandard) {
		init();
		this.goldstandard = goldstandard;
	}
	
	public Evaluator() {
		init();
	}
	
	private void init() {
		this.renderVals = new ArrayList<>();
		this.results = new ArrayList<>();
		this.thresholds = new ArrayList<>();
		this.matcherPaths = new ArrayList<>();
		this.debugOn = false;
		this.outputName = DEFAULT_NAME;
	}
	
	public Evaluator run() throws IOException {
		this.goldstandard = loadResult(this.goldstandardPath, GOLDSTANDARD_NAME);
		for(String matcherPath : this.matcherPaths) {
			results.add(loadResult(matcherPath, extractName(matcherPath)));
		}
		searchForResults(this.matchersRootPath);
		return this;
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
		List<Alignment> alignments = new ArrayList<>();
		Files.walk(Paths.get(path)).forEach(filePath -> {
			System.out.println(filePath + " " + filePath.getFileName());
			if(isFileInGS(filePath.getFileName().toString())) {
				try {
					Alignment a = alignmentReader.getAlignment(filePath.toString());
					System.out.println(a.toString());;
					a.setName(filePath.getFileName().toString());
					alignments.add(a);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if(!alignments.isEmpty()){
				//TODO recognize unequal alignment size to gs
				results.add(new Result(currMatcherName, filePath.toString(), alignments));
				currMatcherName = null;
				alignments.clear();
			} else if(currMatcherName == null && !filePath.getFileName().equals(Paths.get(path).getFileName())){
				currMatcherName = filePath.getFileName().toString();
			}
		});
		System.out.println();
	}
	
	private boolean isFileInGS(String filename) {
		for(Alignment a : this.goldstandard) {
			if(a.getName().equals(filename)) {
				return true;
			}
		}
		return false;
	}
	
	public Evaluator addRenderer(Renderer renderer) {
		System.out.println("addRenderer");
		this.renderVals.add(renderer);
		return this;
	}
	
	public Evaluator removeRenderer(Renderer renderer) {
		for (int i = 0; i < renderVals.size(); i++) {
			if(renderVals.get(i).getClass().getName().equals(renderer.getClass().getName())) {
				renderVals.remove(i);
				return this;
			}
		}
		return this;
	}
	
	public Evaluator addResult(Result result) {
		//TODO check wether result matches
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
	
	public boolean isDebugOn() {
		return debugOn;
	}

	public Evaluator setDebugOn(boolean debugOn) {
		this.debugOn = debugOn;
		return this;
	}

	public Function<Result, Result> getFunctionResult() {
		return transResult;
	}

	public Evaluator setTransformationResult(Function<Result, Result> transformation) {
		this.transResult = transformation;
		return this;
	}

	public Function<Correspondence, Correspondence> getTransformationCorr() {
		return transCorr;
	}

	public Evaluator setTransformationCorr(Function<Correspondence, Correspondence> transformation) {
		this.transCorr = transformation;
		return this;
	}

	public String getGoldstandardPath() {
		return goldstandardPath;
	}

	public Evaluator setGoldstandardPath(String goldstandardPath) {
		System.out.println("goldstandard");
		this.goldstandardPath = goldstandardPath;
		return this;
	}

	public String getMatchersPath() {
		return matchersRootPath;
	}

	public Evaluator setMatchersRootPath(String matchersPath) {
		System.out.println("setRoot");
		this.matchersRootPath = matchersPath;
		return this;
	}

	public String getOutputName() {
		return outputName;
	}

	public Evaluator setOutputName(String outputName) {
		this.outputName = outputName;
		return this;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public Evaluator setOutputPath(String outputPath) {
		System.out.println("ouput path");
		this.outputPath = outputPath;
		return this;
	}

	public List<Double> getThresholds() {
		return thresholds;
	}

	public Evaluator setThresholds(List<Double> thresholds) {
		this.thresholds = thresholds;
		return this;
	}
	
	public double getThreshold(int position) {
		return thresholds.get(position);
	}

	public double getThreshold() {
		return threshold;
	}

	public Evaluator setThreshold(double threshold) {
		this.threshold = threshold;
		return this;
	}

	public Result getGoldStandard() {
		return goldstandard;
	}

	public Evaluator setGoldstandard(Result goldstandard) {
		this.goldstandard = goldstandard;
		return this;
	}
	
	public List<Result> getResults() {
		return results;
	}

	public Evaluator setResults(List<Result> results) {
		this.results = results;
		return this;
	}
	
	public AlignmentReader getAlignmentReader() {
		return alignmentReader;
	}

	public Evaluator setAlignmentReader(AlignmentReader alignmentReader) {
		this.alignmentReader = alignmentReader;
		return this;
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, 
			IOException, AlignmentException, CorrespondenceException {
		final boolean SHOW_IN_BROWSER = true;
		final String OUTPUT_PATH = "src/main/resources/data/evaluation/mes-nonbinary-new-gs-000.html";
		final String GOLDSTANDARD_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts";
		final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
//		final String SINGLE_MATCHER_TEST_PATH = "src/main/resources/data/results/submitted-matchers/RMM-VM2/dataset1";
		@SuppressWarnings("unused")
		Evaluator evaluator = new Evaluator()
								.addRenderer(new HTMLTableNBRenderer(SHOW_IN_BROWSER))
								.setOutputPath(OUTPUT_PATH)
								.setOutputName("result")
								.setGoldstandardPath(GOLDSTANDARD_PATH)
								.setMatchersRootPath(RESULTS_PATH)
//								.addMatcherPath(SINGLE_MATCHER_TEST_PATH)
								.setAlignmentReader(new AlignmentReaderXml())
								.run();
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
