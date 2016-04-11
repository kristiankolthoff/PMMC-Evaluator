package de.unima.ki.pmmc.evaluator.model.parser;


public class ParserFactory {

	public static Parser getParser(String type) {
		if(type.equals(Parser.TYPE_BPMN)) {
			return new BPMNParser();
		} else if(type.equals(Parser.TYPE_PNML)){
			return new PNMLParser();
		}
		throw new IllegalArgumentException();
	}
}
