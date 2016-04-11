package de.unima.ki.pmmc.evaluator.model.parser;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.model.Model;


public interface Parser {
	
	public static final String TYPE_BPMN = "bpmn";
	public static final String TYPE_PNML = "pnml";
	public static final String TYPE_EPK = "epk";
	
	public Model parse(String filepath) throws ParserConfigurationException, SAXException, IOException;

}
