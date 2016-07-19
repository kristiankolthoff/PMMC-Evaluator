package de.unima.ki.pmmc.evaluator.annotator;

import static org.junit.Assert.*;

import org.junit.Test;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;

public class CTMatcherTest {

	private CTMatcher matcher;
	
	/**
	 * Recognize CorrespondenceType.TRIVIAL correspondences
	 */
	@Test
	public void matchCTTrivialTest() {
		this.matcher = new CTMatcherTrivial();
		final String label1 = "Send documents";
		final String label2 = "Send documents";
		assertEquals(CorrespondenceType.TRIVIAL, this.matcher.match(label1, label2));
	}
	
	@Test
	public void matchCTTrivialDefaultTest() {
		this.matcher = new CTMatcherTrivial();
		final String label1 = "Send documents";
		final String label2 = "Send documents to the office";
		assertEquals(CorrespondenceType.DEFAULT, this.matcher.match(label1, label2));
	}
	
	/**
	 * Recognize CorrespondenceType.TRIVIAL_NORM correspondences
	 */
	@Test
	public void matchCTTrivialNormTest() {
		this.matcher = new CTMatcherTrivialNorm();
		final String label1 = "Sending document";
		final String label2 = "Send documents";
		assertEquals(CorrespondenceType.TRIVIAL_NORM, this.matcher.match(label1, label2));
	}
	
	@Test
	public void matchCTTrivialNormDefaultTest() {
		this.matcher = new CTMatcherTrivialNorm();
		final String label1 = "Sending document to university office";
		final String label2 = "Send documents";
		assertEquals(CorrespondenceType.DEFAULT, this.matcher.match(label1, label2));
	}
	
	/**
	 * Recognize CorrespondenceType.ONE_WORD_SIMILAR correspondences
	 */
	@Test
	public void matchCTOWSTest() {
		this.matcher = new CTMatcherOWS();
		final String label1 = "Acceptance";
		final String label2 = "Send letter of acceptance";
		assertEquals(CorrespondenceType.ONE_WORD_SIMILAR, this.matcher.match(label1, label2));
	}
	
	@Test
	public void matchCTOWSDefault1Test() {
		this.matcher = new CTMatcherOWS();
		final String label1 = "Send rejection";
		final String label2 = "Send letter of acceptance";
		assertEquals(CorrespondenceType.DEFAULT, this.matcher.match(label1, label2));
	}
//	
//	@Test
//	public void matchCTOWSDefault2Test() {
//		this.matcher = new CTMatcherOWS();
//		final String label1 = "Send documents to the office";
//		final String label2 = "Send CV to the university office";
//		assertEquals(CorrespondenceType.ONE_WORD_SIMILAR, this.matcher.match(label1, label2));
//	}
	
	/**
	 * Recognize CorrespondenceType.DIFFICULT_SIMILAR_VERB correspondences
	 */
	@Test
	public void matchCTDSV1Test() {
		this.matcher = new CTMatcherDSV();
		final String label1 = "Send documents by post";
		final String label2 = "Send application";
		assertEquals(CorrespondenceType.DIFFICULT_SIMILAR_VERB, this.matcher.match(label1, label2));
	}
	
//	@Test
//	public void matchCTDSV2Test() {
//		this.matcher = new CTMatcherDSV();
//		final String label1 = "Send documents by post";
//		final String label2 = "sending application form and documents";
//		assertEquals(CorrespondenceType.DIFFICULT_SIMILAR_VERB, this.matcher.match(label1, label2));
//	}
	
	@Test
	public void matchCTDSVDefault1Test() {
		this.matcher = new CTMatcherDSV();
		final String label1 = "Apply online";
		final String label2 = "Complete online interview";
		assertEquals(CorrespondenceType.DEFAULT, this.matcher.match(label1, label2));
	}
	
	@Test
	public void matchCTDSVDefault2Test() {
		this.matcher = new CTMatcherDSV();
		final String label1 = "Send letter of rejection";
		final String label2 = "Reject applicant";
		assertEquals(CorrespondenceType.DEFAULT, this.matcher.match(label1, label2));
	}
	
	/**
	 * Recognize CorrespondenceType.MISC correspondences
	 */
	@Test
	public void matchCTMisc1Test() {
		this.matcher = new CTMatcherMisc();
		final String label1 = "Checking if complete";
		final String label2 = "Check application complete";
		assertEquals(CorrespondenceType.MISC, this.matcher.match(label1, label2));
	}
	
	@Test
	public void matchCTMisc2Test() {
		this.matcher = new CTMatcherMisc();
		final String label1 = "Compare and send application";
		final String label2 = "Send application";
		assertEquals(CorrespondenceType.MISC, this.matcher.match(label1, label2));
	}
	
	@Test
	public void matchCTMiscDefaultTest() {
		this.matcher = new CTMatcherMisc();
		final String label1 = "Compare and send results";
		final String label2 = "Send application";
		assertEquals(CorrespondenceType.DEFAULT, this.matcher.match(label1, label2));
	}
}
