package de.unima.ki.pmmc.evaluator;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.data.Result;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.handler.HTMLHandler;
import de.unima.ki.pmmc.evaluator.handler.JSONHandler;
import de.unima.ki.pmmc.evaluator.handler.LaTexHandler;
import de.unima.ki.pmmc.evaluator.handler.LaTexHandlerType;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.utils.GSPartitioner;
import edu.stanford.nlp.parser.metrics.Eval;

public class PMMCEvaluation {

	private Evaluator evaluator;
	
	public final boolean SHOW_IN_BROWSER = true;
	public final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
	public final String GOLDSTANDARD_OLD_PATH = "src/main/resources/data/dataset1/goldstandard/";
	public final String GOLDSTANDARD_OLD_SUB_PATH = "src/main/resources/data/dataset1-sub/goldstandard/";
	public final String GOLDSTANDARD_NEW_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts/";
	public final String GOLDSTANDARD_NEW_ADAPTED_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts_adapted/";
	public final String GOLDSTANDARD_NEW_ADAPTED_PARTS_PATH = "src/main/resources/data/dataset1/goldstandard_experts_heiner/";
	public final String RESULTS_PATH = "src/main/resources/data/results/OAEI16/";
	public final String MODELS_PATH = "src/main/resources/data/dataset1/models/";
	
	public PMMCEvaluation() {
	}
	
	public void run() throws CorrespondenceException, IOException, 
								ParserConfigurationException, SAXException {
//		for (int i = 1; i <9; i++) {
//			System.out.println("Running Goldstandard n" + i);
//			GSPartitioner partitioner = new GSPartitioner();
//			System.out.println("Loading all goldstandards...");
//			List<Result> goldstandards = partitioner.getKGoldstandardsAsResult(i);
//			System.out.println(goldstandards.get(0).sizeOfCorrespondences());
//			System.out.println(goldstandards.size());
//			System.out.println("Finished loading...");
			Evaluator.Builder builder = new Evaluator.Builder().
					setGoldstandardPath(GOLDSTANDARD_NEW_ADAPTED_PATH).
					setMatchersRootPath(RESULTS_PATH).
	//				addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/").
					setOutputPath(OUTPUT_PATH).
					setOutputName("oaei16-new-gs").
					setAlignmentReader(new AlignmentReaderXml()).
					setParser(Parser.TYPE_BPMN).
					setModelsRootPath(MODELS_PATH).
					addThreshold(Evaluator.THRESHOLD_ZERO).
//					addThreshold(Evaluator.THRESHOLD_LOW).
//					addThreshold(Evaluator.THRESHOLD_MEDIUM).
//					addThreshold(Evaluator.THRESHOLD_HIGH).
					setDebugOn(true).
					setTagCTOn(true).
					addHandler(new HTMLHandler(SHOW_IN_BROWSER));
//					addHandler(new LaTexHandler()).
//					addHandler(new LaTexHandlerType());
//			for(Result goldstandard : goldstandards) {
//				builder.addGoldstandard(goldstandard);
//			}
			this.evaluator = builder.build();
			this.evaluator.run();
//		}
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, 
													IOException, AlignmentException, CorrespondenceException {
		PMMCEvaluation evaluation = new PMMCEvaluation();
		evaluation.run();
	}
}
