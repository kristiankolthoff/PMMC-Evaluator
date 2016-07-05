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
	
	@Test
	public void matchCTOWSDefault2Test() {
		this.matcher = new CTMatcherOWS();
		final String label1 = "Send documents to the office";
		final String label2 = "Send CV to the university office";
		assertEquals(CorrespondenceType.ONE_WORD_SIMILAR, this.matcher.match(label1, label2));
	}
}
