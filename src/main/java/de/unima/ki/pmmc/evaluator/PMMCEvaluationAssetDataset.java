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
import de.unima.ki.pmmc.evaluator.model.parser.Parser;

public class PMMCEvaluationAssetDataset {

	private Evaluator evaluator;
	
	public final boolean SHOW_IN_BROWSER = true;
	public final String OUTPUT_PATH = "src/main/resources/data/evaluation/";
	public final String GOLDSTANDARD_PATH = "src/main/resources/data/dataset3/goldstandard/";
	public final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
	public final String MODELS_PATH = "src/main/resources/data/dataset3/models/";
	
	public PMMCEvaluationAssetDataset() throws IOException {
		this.evaluator = new Evaluator.Builder().
				setGoldstandardPath(GOLDSTANDARD_PATH).
				setMatchersRootPath(RESULTS_PATH).
				addMatcherPath("src/main/resources/data/results/submitted-matchers/TripleS/dataset3/").
				setOutputPath(OUTPUT_PATH).
				setOutputName("assetmanagement").
				setAlignmentReader(new AlignmentReaderXml()).
				setParser(Parser.TYPE_EPML).
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
		PMMCEvaluationAssetDataset evaluation = new PMMCEvaluationAssetDataset();
		evaluation.run();
	}
}
