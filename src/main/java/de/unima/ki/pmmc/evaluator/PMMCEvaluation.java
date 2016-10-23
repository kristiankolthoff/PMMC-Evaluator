package de.unima.ki.pmmc.evaluator;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.handler.HTMLHandler;
import de.unima.ki.pmmc.evaluator.handler.JSONHandler;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import edu.stanford.nlp.parser.metrics.Eval;

public class PMMCEvaluation {

	private Evaluator evaluator;
	
	public final boolean SHOW_IN_BROWSER = true;
	public final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
	public final String GOLDSTANDARD_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts/";
	public final String RESULTS_PATH = "src/main/resources/data/results/OAEI16/";
	public final String MODELS_PATH = "src/main/resources/data/dataset1/models/";
	
	public PMMCEvaluation() throws IOException {
		this.evaluator = new Evaluator.Builder().
				setGoldstandardPath(GOLDSTANDARD_PATH).
//				setMatchersRootPath(RESULTS_PATH).
				addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/").
				setOutputPath(OUTPUT_PATH).
				setOutputName("oaei16").
				setAlignmentReader(new AlignmentReaderXml()).
				setParser(Parser.TYPE_BPMN).
				setModelsRootPath(MODELS_PATH).
//				addThreshold(Evaluator.THRESHOLD_ZERO).
//				addThreshold(Evaluator.THRESHOLD_LOW).
//				addThreshold(Evaluator.THRESHOLD_MEDIUM).
				addThreshold(Evaluator.THRESHOLD_HIGH).
				setDebugOn(true).
				addHandler(new HTMLHandler(SHOW_IN_BROWSER)).
				build();
	}
	
	public void run() throws CorrespondenceException, IOException, 
								ParserConfigurationException, SAXException {
		this.evaluator.run();
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, 
													IOException, AlignmentException, CorrespondenceException {
		PMMCEvaluation evaluation = new PMMCEvaluation();
		evaluation.run();
	}
}
