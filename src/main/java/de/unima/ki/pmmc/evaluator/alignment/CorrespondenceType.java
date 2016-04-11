package de.unima.ki.pmmc.evaluator.alignment;

import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;


public enum CorrespondenceType {

	DIFFICULT ("difficult"),
	DIFFICULT_SIMILAR_VERB ("difficult-similar-verb"),
	ONE_WORD_SIMILAR ("one-word-similar"),
	TRIVIAL ("trivial"),
	TRIVIAL_BASIC_NORM ("trivial-after-basic-normalization"),
	TRIVIAL_EXTENDED_NORM ("trivial-after-extended-normalization"),
	MISC ("misc");
	
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
