package de.unima.ki.pmmc.evaluator.annotator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.model.parser.ParserFactory;

public class AnnotatorTest {

	/**
	 * Recognize CorrespondenceType.TRIVIAL correspondences
	 */
	@Test
	public void annotateCTTrivialTest() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Upload CV and documents"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Upload CV and documents"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.TRIVIAL, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.TRIVIAL correspondences
	 */
	@Test
	public void annotateCTTrivialNormTest() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Sending documents"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Send document"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.TRIVIAL, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.TRIVIAL correspondences
	 */
	@Test
	public void annotateCTTrivialCapitalizedTest() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "send acceptance"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Send Acceptance"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.TRIVIAL, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.ONE_WORD_IDENT correspondences
	 */
	@Test
	public void annotateCTOWI1Test() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Sending letter of acceptance"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Acceptance received"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.ONE_WORD_IDENT, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.ONE_WORD_IDENT correspondences
	 */
	@Test
	public void annotateCTOWI2Test() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Send acceptance"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Acceptance"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.ONE_WORD_IDENT, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.DIFFICULT_VERB_IDENT correspondences
	 */
	@Test
	public void annotateCTDVITest() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Check documents"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Check application complete"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.DIFFICULT_VERB_IDENT, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.DIFFICULT_VERB_IDENT correspondences
	 */
	@Test
	public void annotateCTDVI2Test() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Send documents by post"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Send application"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.DIFFICULT_VERB_IDENT, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.DIFFICULT_VERB_IDENT correspondences
	 */
	@Test
	public void annotateCTDVI3Test() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Wait until results"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Waiting for the response"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.DIFFICULT_VERB_IDENT, annotator.annotateCorrespondence(c));
	}
	
	@Test @Ignore
	public void annotateCTDVITwoVerbsTest() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Check documents and complete interview"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Check application and complete remaining tasks"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.DIFFICULT_VERB_IDENT, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.DIFFICULT_NO_WORD_IDENT correspondences
	 */
	@Test
	public void annotateCTDifficultTest() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "apply online"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "complete application"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.DIFFICULT_NO_WORD_IDENT, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.DIFFICULT_NO_WORD_IDENT correspondences
	 */
	@Test
	public void annotateCTDifficult2Test() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Immatriculate"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "enrollment"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.DIFFICULT_NO_WORD_IDENT, annotator.annotateCorrespondence(c));
	}
	
	
	/**
	 * Recognize CorrespondenceType.MISC correspondences
	 */
	@Test
	public void annotateCTMiscTest() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Checking if complete"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Check application complete"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.MISC, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.MISC correspondences
	 */
	@Test
	public void annotateCTMisc2Test() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Invite applicant for appointment"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Invite applicant for interview"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.MISC, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.MISC correspondences
	 */
	@Test
	public void annotateCTMisc3Test() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Receiving acceptance letter"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "receive acceptance"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.MISC, annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.MISC correspondences
	 */
	@Test
	public void annotateCTMisc4Test() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Attach additional requirements"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Add additional requirements"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("http#id1", "http#id2");
		Annotator annotator = new Annotator(models);
		annotator = new Annotator(models);
		assertEquals(CorrespondenceType.MISC, annotator.annotateCorrespondence(c));
	}
	
}
