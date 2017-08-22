package de.unima.ki.pmmc.evaluator.model.parser;


public class ParserFactory {

	public static Parser getParser(Parser.Type type) {
		if(type.equals(Parser.Type.BPMN)) {
			return new BPMNParser();
		} else if(type.equals(Parser.Type.PNML)){
			System.out.println("parser");
			return new PNMLParser();
		} else if(type.equals(Parser.Type.EPML)) {
			return new EPMLParser();
			
		} else if(type.equals(Parser.Type.PNML_2)) {
			System.out.println("parser2");
			return new PNMLParser2();
		}
		throw new IllegalArgumentException();
	}
}
