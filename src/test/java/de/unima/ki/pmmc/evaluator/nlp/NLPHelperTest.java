package de.unima.ki.pmmc.evaluator.nlp;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;


public class NLPHelperTest {

	@Test
	public void getPOSTest() {
		assertEquals("noun",NLPHelper.getPOS("documents").iterator().next().toString());
	}
	
	@Test
	public void sanitizeLabelsTest() {
		final String label = " Send letter of rejection   ";
		assertEquals("send letter of rejection", NLPHelper.getSanitizeLabel(label));
		final String label2 = "The student attends the university.";
		assertEquals("the student attends the university .", NLPHelper.getSanitizeLabel(label2));
		final String label3 = "Send letter of \nprovisional acceptance";
		assertEquals("send letter of provisional acceptance", NLPHelper.getSanitizeLabel(label3));
		final String label4 = "Attach bachelor's certificate";
		assertEquals("attach bachelor 's certificate", NLPHelper.getSanitizeLabel(label4));
		final String label5 = "Find Results IN the documents";
		assertEquals("find results in the documents", NLPHelper.getSanitizeLabel(label5));
	}
	
	@Test
	public void getWordStemsSendingTest() {
		List<String> stems = NLPHelper.getStemmedTokens("sending bachelors documents to universities", false);
		assertEquals("send", stems.get(0));
		assertEquals("bachelor", stems.get(1));
		assertEquals("document", stems.get(2));
		assertEquals("university", stems.get(4));
	}
	
	
	@Test
	public void getWordStemsWaitingTest() {
		List<String> stems = NLPHelper.getStemmedTokens("waiting for certificates", false);
		assertEquals("wait", stems.get(0));
		assertEquals("certificate", stems.get(2));
	}
	
//	@Test
//	public void getWordStemsRejectionTest() {
//		List<String> stems1 = NLPHelper.getStemmedTokens("rejected", false);
//		List<String> stems2 = NLPHelper.getStemmedTokens("rejection", false);
//		System.out.println(stems1);
//		System.out.println(stems2);
//		assertEquals("reject", stems1.get(0));
//		assertEquals("reject", stems2.get(0));
//	}
	
	@Test
	public void getStemmedSentenceTest() {
		String stemmed = NLPHelper.getStemmedString("sending bachelors documents to universities", false);
		assertEquals("send bachelor document to university", stemmed);
	}
	
	@Test
	public void getNGramsTest() {
		final String sentence = "We are";
		final int n = 2;
		Set<String> ngrams = NLPHelper.getNGrams(sentence, n);
		assertEquals(true, ngrams.contains("we"));
		assertEquals(true, ngrams.contains("ea"));
		assertEquals(true, ngrams.contains("ar"));
		assertEquals(true, ngrams.contains("re"));	
	}
	
	@Test
	public void getNGrams2Test() {
		final String sentence = "We are";
		final int n = 3;
		Set<String> ngrams = NLPHelper.getNGrams(sentence, n);
		assertEquals(true, ngrams.contains("wea"));
		assertEquals(true, ngrams.contains("ear"));
		assertEquals(true, ngrams.contains("are"));
	}
	
	@Test
	public void jaccardNGramSimilarityTest() {
		final String s1 = "hello";
		final String s2 = "heljiio";
		assertEquals(2/(double)8, NLPHelper.jaccardNGramSimilarity(s1, s2, 2), 0.01);
	}
	
	@Test
	public void jaccardSimilarityTest() {
		final String s1 = "check application complete";
		final String s2 = "check complete";
		assertEquals(2/(double)3, NLPHelper.jaccardSimilarity(s1, s2), 0.01);
	}
	
	
	public static void main(String[] args) {
		String x = "run";
		String x1 = x.substring(0, x.length() - 1);
		System.out.println(x1);
		System.out.println(x1 + "e");
		System.out.println(NLPHelper.getPOS("documents"));
	}

}
