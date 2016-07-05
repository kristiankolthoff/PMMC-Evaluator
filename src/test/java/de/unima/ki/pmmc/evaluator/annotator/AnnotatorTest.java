package de.unima.ki.pmmc.evaluator.annotator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.Receiver;
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
	private Model modelCologne;
	
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
		this.modelCologne = this.models.get(0);
		this.annotator = new Annotator(models);
	}
	
}
