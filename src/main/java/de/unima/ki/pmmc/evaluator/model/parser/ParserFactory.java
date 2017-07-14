package de.unima.ki.pmmc.evaluator.model.parser;


public class ParserFactory {

	public static Parser getParser(String type) {
		if(type.equals(Parser.TYPE_BPMN)) {
			return new BPMNParser();
		} else if(type.equals(Parser.TYPE_PNML)){
			System.out.println("parser");
			return new PNMLParser();
		} else if(type.equals(Parser.TYPE_EPML)) {
			return new EPMLParser();
			
		} else if(type.equals(Parser.TYPE_PNML_2)) {
			System.out.println("parser2");
			return new PNMLParser2();
		}
		throw new IllegalArgumentException();
	}
}
