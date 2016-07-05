package de.unima.ki.pmmc.evaluator.annotator;

import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;

public class CTMatcherTrivial implements CTMatcher {

	private static final CorrespondenceType TYPE = CorrespondenceType.TRIVIAL;
	
	@Override
	public CorrespondenceType match(String label1, String label2, Correspondence c) {
		return (label1.equals(label2)) ? TYPE : CorrespondenceType.DEFAULT;
	}

}
