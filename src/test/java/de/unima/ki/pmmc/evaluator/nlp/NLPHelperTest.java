package de.unima.ki.pmmc.evaluator.nlp;


import static org.junit.Assert.*;

import java.util.List;

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
		List<String> stems = NLPHelper.getStemmedTokens("sending bachelors documents to universities");
		assertEquals("send", stems.get(0));
		assertEquals("bachelor", stems.get(1));
		assertEquals("document", stems.get(2));
		assertEquals("university", stems.get(4));
	}
	
	
	@Test
	public void getWordStemsWaitingTest() {
		List<String> stems = NLPHelper.getStemmedTokens("waiting for certificates");
		assertEquals("wait", stems.get(0));
		assertEquals("certificate", stems.get(2));
	}
	
	@Test
	public void getWordStemsRejectionTest() {
		List<String> stems1 = NLPHelper.getStemmedTokens("rejected");
		List<String> stems2 = NLPHelper.getStemmedTokens("rejection");
		System.out.println(stems1);
		System.out.println(stems2);
		assertEquals("reject", stems1.get(0));
		assertEquals("reject", stems2.get(0));
	}
	
	@Test
	public void getStemmedSentenceTest() {
		String stemmed = NLPHelper.getStemmedString("sending bachelors documents to universities");
		assertEquals("send bachelor document to university", stemmed);
	}
	
	
	public static void main(String[] args) {
		
		String x = "run";
		String x1 = x.substring(0, x.length() - 1);
		System.out.println(x1);
		System.out.println(x1 + "e");


		System.out.println(NLPHelper.getPOS("documents"));
		
	}

}
