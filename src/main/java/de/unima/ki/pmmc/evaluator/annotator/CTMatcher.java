package de.unima.ki.pmmc.evaluator.annotator;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;

@FunctionalInterface
public interface CTMatcher {

	public CorrespondenceType match(String label1, String label2);
}
