package de.unima.ki.pmmc.evaluator.alignment;

import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;


public enum CorrespondenceType {

	TRIVIAL ("trivial"),
	DIFFICULT_NO_WORD_IDENT ("no-word-ident"),
	DIFFICULT_VERB_IDENT ("verb-ident"),
	ONE_WORD_IDENT ("one-word-ident"),
	MISC ("misc"),
	DEFAULT ("default");
	
	private String name;
	
	CorrespondenceType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static String[] getNames() {
		String[] names = new String[CorrespondenceType.values().length];
		CorrespondenceType[] types = CorrespondenceType.values();
		for (int i = 0; i < types.length; i++) {
			names[i] = types[i].getName();
		}
		return names;
	}
	
	public static CorrespondenceType getValue(String type) throws CorrespondenceException {
		CorrespondenceType[] types = CorrespondenceType.values();
		for(CorrespondenceType cType : types) {
			if(cType.getName().equals(type)) {
				return cType;
			}
		}
		throw new CorrespondenceException(CorrespondenceException.UNSUPPORTED_TYPE);
	}
	
	public static boolean isSupported(String type) {
		CorrespondenceType[] types = CorrespondenceType.values();
		for(CorrespondenceType cType : types) {
			if(cType.getName().equals(type)) {
				return true;
			}
		}
		return false;
	}
	
}
