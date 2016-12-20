package de.unima.ki.pmmc.evaluator;

import java.io.IOException;

import org.junit.Test;

import de.unima.ki.pmmc.evaluator.alignment.AlignmentReaderXml;

public class EvaluatorTest {

	@Test
	public void evaluatorTest() throws IOException {
		final boolean SHOW_IN_BROWSER = true;
		final String OUTPUT_PATH = "src/main/resources/data/evaluation/mes-nonbinary-new-gs-000.html";
		final String GOLDSTANDARD_PATH = "src/main/resources/data/results/goldstandard/dataset1_goldstandard_experts";
		final String RESULTS_PATH = "src/main/resources/data/results/submitted-matchers/";
//		final String SINGLE_MATCHER_TEST_PATH = "src/main/resources/data/results/submitted-matchers/RMM-VM2/dataset1";
//		@SuppressWarnings("unused")
//		Evaluator evaluator = new Evaluator()
//								.addRenderer(new HTMLTableNBRenderer(SHOW_IN_BROWSER))
//								.setOutputPath(OUTPUT_PATH)
//								.setOutputName("result")
//								.setMatchersRootPath(RESULTS_PATH)
//								.setGoldstandardPath(GOLDSTANDARD_PATH)
////								.addMatcherPath(SINGLE_MATCHER_TEST_PATH)
//								.setAlignmentReader(new AlignmentReaderXml())
//								.run();
	}
}
