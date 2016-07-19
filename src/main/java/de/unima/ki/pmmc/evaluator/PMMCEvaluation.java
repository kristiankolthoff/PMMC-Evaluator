package de.unima.ki.pmmc.evaluator;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.handler.ConsoleHandler;
import de.unima.ki.pmmc.evaluator.handler.HTMLHandler;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;

public class PMMCEvaluation {

	private Evaluator evaluator;
	
	public final boolean SHOW_IN_BROWSER = true;
	public final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
	public final String GOLDSTANDARD_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts";
	public final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
	public final String MODELS_PATH = "src/main/resources/data/dataset1/models/";
	
	public PMMCEvaluation() throws IOException {
		this.evaluator = new Evaluator.Builder().
				setGoldstandardPath(GOLDSTANDARD_PATH).
				setMatchersRootPath(RESULTS_PATH).
				setOutputPath(OUTPUT_PATH).
				setOutputName("result").
				setAlignmentReader(new AlignmentReaderXml()).
				setParser(Parser.TYPE_BPMN).
				setModelsRootPath(MODELS_PATH).
				setTagCTOn(false).
				addThreshold(Evaluator.THRESHOLD_ZERO).
				addThreshold(0.99).
				addHandler(new ConsoleHandler()).
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
