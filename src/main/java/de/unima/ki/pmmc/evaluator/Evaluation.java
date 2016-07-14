package de.unima.ki.pmmc.evaluator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReader;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
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



public class Evaluation {
	
	private Result goldstandard;
	private List<Result> results;
	private double threshold;
	private List<Renderer> renderVals;
	
	public static final double THRESHOLD_ZERO = 0.0;
	public static final double THRESHOLD_LOW = 0.375;
	public static final double THRESHOLD_MEDIUM = 0.5;
	public static final double THRESHOLD_HIGH = 0.75;
	
	public Evaluation(List<Renderer> renderVals, List<Result> results, Result goldstandard) {
		this.renderVals = renderVals;
		this.results = results;
		this.goldstandard = goldstandard;
	}
	
	public Evaluation(Result goldstandard) {
		this.renderVals = new ArrayList<>();
		this.results = new ArrayList<>();
		this.goldstandard = goldstandard;
	}
	
	public Evaluation() {
		this.renderVals = new ArrayList<>();
		this.results = new ArrayList<>();
	}
	
	public Evaluation run() {
		return this;
	}
	
	public Evaluation addRenderer(Renderer renderer) {
		this.renderVals.add(renderer);
		return this;
	}
	
	public Evaluation removeRenderer(Renderer renderer) {
		for (int i = 0; i < renderVals.size(); i++) {
			if(renderVals.get(i).getClass().getName().equals(renderer.getClass().getName())) {
				renderVals.remove(i);
				return this;
			}
		}
		return this;
	}
	
	public Evaluation addResult(Result result) {
		//TODO check wether result matches
		results.add(result);
		return this;
	}
	
	public Evaluation removeResult(Result result) {
		for (int i = 0; i < results.size(); i++) {
			if(results.get(i).equals(result)) {
				results.remove(i);
				return this;
			}
		}
		return this;
	}
	
	public double getThreshold() {
		return threshold;
	}

	public Evaluation setThreshold(double threshold) {
		this.threshold = threshold;
		return this;
	}

	public Result getGoldStandard() {
		return goldstandard;
	}

	public Evaluation setGoldstandard(Result goldstandard) {
		this.goldstandard = goldstandard;
		return this;
	}
	
	
	public List<Result> getResults() {
		return results;
	}

	public Evaluation setResults(List<Result> results) {
		this.results = results;
		return this;
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, 
			IOException, AlignmentException, CorrespondenceException {
//		Result goldstandard = new Result();
//		Evaluation evalution = new Evaluation(goldstandard)
//									.addRenderer(new HTMLTableNBRenderer("", true))
//									.addRenderer(new DebugRenderer(""))
//									.addRenderer(new JavaFXRenderer(""))
//									.run();
//
//		
		
		/**
		 * Define multiple directories with matcher alginments.
		 * First entry of the array should be the directory of the reference alignment.
		 */
		final double THRESHOLD = 0.000;
		final boolean SHOW_IN_BROWSER = true;
		final String OUTPUT_PATH = "src/main/resources/data/evaluation/mes-nonbinary-new-gs-000.html";
		final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
		final String END_DIR = "/dataset1";
		final String[] dirs = new String[] {
				"src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts",
				RESULTS_PATH + "AML-PM" + END_DIR,
				RESULTS_PATH + "BPLangMatch" + END_DIR,
				RESULTS_PATH + "KnoMa-Proc" + END_DIR,
				RESULTS_PATH + "Know-Match-SSS" + END_DIR,
				RESULTS_PATH + "Match-SSS" + END_DIR,
				RESULTS_PATH + "OPBOT" + END_DIR,
				RESULTS_PATH + "pPalm-DS" + END_DIR,
				RESULTS_PATH + "RMM-NHCM" + END_DIR,
				RESULTS_PATH + "RMM-NLM" + END_DIR,
				RESULTS_PATH + "RMM-SMSL" + END_DIR,
				RESULTS_PATH + "RMM-VM2" + END_DIR,
				RESULTS_PATH + "TripleS" + END_DIR
		};
		final String[] mappingInfo = new String[] {
				"AML-PM",
				"BPLangMatch",
				"KnoMa-Proc",
				"Know-Match-SSS",
				"Match-SSS",
				"OPBOT",
				"pPalm-DS",
				"RMM-NHCM",
				"RMM-NLM",
				"RMM-VM2",
				"RMM-SMSL",
				"TripleS"
		};
		/**
		 * Read all alignments from gold standard and matchers 
		 */
		final AlignmentReader alignReader = new AlignmentReaderXml();
		List<List<Alignment>> alignments = new ArrayList<>();
		for (int i = 0; i < dirs.length; i++) {
			List<Alignment> aligns = new ArrayList<>();
			Files.walk(Paths.get(dirs[i])).forEach(filePath -> {
				if(Files.isRegularFile(filePath)) {
					try {
						System.out.println(filePath.toString());
						Alignment a = alignReader.getAlignment(filePath.toString());
						if(filePath.toString().contains("dataset1_goldstandard_experts")) {
							a.applyThreshold(THRESHOLD);							
						}
						aligns.add(a);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
		});
			alignments.add(aligns);
		}
		/**
		 * Compute typecharacteristcs for each alignment
		 */
		List<List<Characteristic>> characteristics = new ArrayList<>();
		for (int i = 1; i < alignments.size(); i++) {
			List<Alignment> aligns = alignments.get(i);
			List<Characteristic> tcharacteristics = new ArrayList<>();
			for (int j = 0; j < aligns.size(); j++) {
				try {
				Alignment mapping = aligns.get(j);
				Alignment reference = alignments.get(0).get(j);
				Characteristic tc = new Characteristic(mapping, reference);
				tc.setAllowZeros(false);
				tcharacteristics.add(tc);
				} catch(IndexOutOfBoundsException ex) {
					System.err.println("Number of reference and matcher "
							+ "alignments unequal : " + ex.getMessage());
				}
			}
			characteristics.add(tcharacteristics);
		}
		/**
		 * Annotate collection of characteristics to obtain typecharacteristics
		 */
		List<Model> models = loadModels();
		List<List<TypeCharacteristic>> tCharacteristics = new ArrayList<>();
		Annotator annotator = new Annotator(models);
		for(List<Characteristic> chars : characteristics) {
			tCharacteristics.add(annotator.annotateCharacteristics(chars));
		}
		/**
		 * Render alignments to HTML evaluation summary page
		 */
		Renderer renderer = new HTMLTableNBRenderer(OUTPUT_PATH, SHOW_IN_BROWSER);
		for (int i = 0; i < characteristics.size(); i++) {
			renderer.render(characteristics.get(i), mappingInfo[i]);
		}
		renderer.flush();
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
