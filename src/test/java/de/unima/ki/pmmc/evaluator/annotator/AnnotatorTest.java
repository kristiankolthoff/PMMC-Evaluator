package de.unima.ki.pmmc.evaluator.annotator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.model.Model;
import de.unima.ki.pmmc.evaluator.model.parser.Parser;
import de.unima.ki.pmmc.evaluator.model.parser.ParserFactory;

public class AnnotatorTest {

	private Annotator annotator;
	private List<Model> models;
	
	@Before
	public void init() throws ParserConfigurationException, SAXException, IOException {
		String[] modelIds = new String[]{
				"Cologne",
				"TU_Munich",
		};
		this.models = new ArrayList<>();
		Parser bpmnParser = ParserFactory.getParser(Parser.TYPE_BPMN);
		for (int i = 0; i < modelIds.length - 1; i++) {
			String sourceId = modelIds[i];
			Model model = bpmnParser.parse("src/main/resources/data/dataset1/models/" + sourceId + ".bpmn");
			this.models.add(model);
		}
		this.annotator = new Annotator(models);
	}
	
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
		Correspondence c = new Correspondence("id1", "id2");
		this.annotator = new Annotator(models);
		assertEquals(CorrespondenceType.TRIVIAL, this.annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.TRIVIALcorrespondences
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
		Correspondence c = new Correspondence("id1", "id2");
		this.annotator = new Annotator(models);
//		assertEquals(CorrespondenceType.TRIVIAL, this.annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.ONE_WORD_SIMILAR correspondences
	 */
	@Test
	public void annotateCTOWSTest() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Sending letter of acceptance"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Acceptance received"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("test#id1", "test#id2");
		this.annotator = new Annotator(models);
//		assertEquals(CorrespondenceType.ONE_WORD_SIMILAR, this.annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.DIFFICULT_SIMILAR_VERB correspondences
	 */
	@Test
	public void annotateCTDSVTest() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Check documents"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Check application complete"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("id1", "id2");
		this.annotator = new Annotator(models);
//		assertEquals(CorrespondenceType.DIFFICULT_SIMILAR_VERB, this.annotator.annotateCorrespondence(c));
	}
	
	//TODO Two verbs similar, still CorrespondenceType.DIFFICULT_SIMILAR_VERB?
//	@Test
//	public void annotateCTDSV2Test() {
//		List<Model> models = new ArrayList<>();
//		Model m1 = new Model();
//		m1.addActivity(new Activity("id1", "Check documents and complete interview"));
//		Model m2 = new Model();
//		m2.addActivity(new Activity("id2", "Check application and complete remaining tasks"));
//		models.add(m1);
//		models.add(m2);
//		Correspondence c = new Correspondence("id1", "id2");
//		this.annotator = new Annotator(models);
//		assertEquals(CorrespondenceType.DIFFICULT_SIMILAR_VERB, this.annotator.annotateCorrespondence(c));
//	}
	
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
		Correspondence c = new Correspondence("id1", "id2");
		this.annotator = new Annotator(models);
		assertEquals(CorrespondenceType.MISC, this.annotator.annotateCorrespondence(c));
	}
	
	/**
	 * Recognize CorrespondenceType.DIFFICULT correspondences
	 */
	@Test
	public void annotateCTDifficult1Test() {
		List<Model> models = new ArrayList<>();
		Model m1 = new Model();
		m1.addActivity(new Activity("id1", "Apply at university"));
		Model m2 = new Model();
		m2.addActivity(new Activity("id2", "Complete online interview"));
		models.add(m1);
		models.add(m2);
		Correspondence c = new Correspondence("id1", "id2");
		this.annotator = new Annotator(models);
//		assertEquals(CorrespondenceType.DIFFICULT, this.annotator.annotateCorrespondence(c));
	}
	
	//TODO recognize context of words using turboparser
//	@Test
//	public void annotateCTDifficult2Test() {
//		List<Model> models = new ArrayList<>();
//		Model m1 = new Model();
//		m1.addActivity(new Activity("id1", "Apply online"));
//		Model m2 = new Model();
//		m2.addActivity(new Activity("id2", "Complete online interview"));
//		models.add(m1);
//		models.add(m2);
//		Correspondence c = new Correspondence("id1", "id2");
//		this.annotator = new Annotator(models);
//		assertEquals(CorrespondenceType.DIFFICULT, this.annotator.annotateCorrespondence(c));
//	}
	
}
