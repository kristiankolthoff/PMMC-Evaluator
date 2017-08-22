package de.unima.ki.pmmc.evaluator.model.parser;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.unima.ki.pmmc.evaluator.model.Model;


public interface Parser {
	
	public enum Type {
		
		BPMN("bpmn"),
		PNML("pnml"),
		PNML_2("pnml"),
		EPML("epml");
		
		private String suffix;
		
		Type(String suffix) {
			this.suffix = suffix;
		}

		public String getSuffix() {
			return suffix;
		}

		public void setSuffix(String suffix) {
			this.suffix = suffix;
		}
		
		
	}
//	public static final String TYPE_BPMN = "bpmn";
//	public static final String TYPE_PNML = "pnml";
//	public static final String TYPE_PNML_2 = "pnml";
//	public static final String TYPE_EPML = "epml";
	
	public Model parse(String filepath) throws ParserConfigurationException, SAXException, IOException;

}
