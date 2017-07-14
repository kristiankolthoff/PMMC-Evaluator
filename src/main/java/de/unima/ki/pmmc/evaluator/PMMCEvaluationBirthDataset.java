package de.unima.ki.pmmc.evaluator;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;
import de.unima.ki.pmmc.evaluator.exceptions.AlignmentException;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.handler.HTMLHandler;
import de.unima.ki.pmmc.evaluator.handler.LaTexHandler;
import de.unima.ki.pmmc.evaluator.handler.LaTexHandlerType;
import de.unima.ki.pmmc.evaluator.model.parser.PNMLParser;
import de.unima.ki.pmmc.evaluator.model.parser.PNMLParser2;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;

public class PMMCEvaluationBirthDataset {

	private Evaluator evaluator;
	
	public final boolean SHOW_IN_BROWSER = true;
	public final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
	public final String GOLDSTANDARD_PATH = "src/main/resources/data/dataset2/goldstandard/";
	public final String GOLDSTANDARD_NB_PATH = "src/main/resources/data/dataset2/new_gs_rdf/";
	public final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
	public final String MODELS_PATH = "src/main/resources/data/dataset2/models/";
	
	public PMMCEvaluationBirthDataset() throws IOException {
		this.evaluator = new Evaluator.Builder().
				setGoldstandardPath(GOLDSTANDARD_PATH).
				setMatchersRootPath(RESULTS_PATH).
//				addMatcherPath("src/main/resources/data/results/OAEI16/AML-PM/").
				setOutputPath(OUTPUT_PATH).
				setOutputName("birthCertificate").
				setAlignmentReader(new AlignmentReaderXml()).
				//Caution with selecting the correct the parser here
				setParser(new PNMLParser()).
				setModelsRootPath(MODELS_PATH).
				addThreshold(Evaluator.THRESHOLD_ZERO).
//				addThreshold(Evaluator.THRESHOLD_LOW).
//				addThreshold(Evaluator.THRESHOLD_MEDIUM).
//				addThreshold(Evaluator.THRESHOLD_HIGH).
				setDebugOn(true).
				setTagCTOn(true).
				addHandler(new HTMLHandler(SHOW_IN_BROWSER)).
//				addHandler(new LaTexHandler()).
//				addHandler(new LaTexHandlerType()).
				build();
	}
	
	public void run() throws CorrespondenceException, IOException, 
								ParserConfigurationException, SAXException {
		this.evaluator.run();
	}
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, 
													IOException, AlignmentException, CorrespondenceException {
		PMMCEvaluationBirthDataset evaluation = new PMMCEvaluationBirthDataset();
		evaluation.run();
	}
}
