package de.unima.ki.pmmc.evaluator.utils.reader;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;

public interface GSReader {

	public Alignment nextAlignment();
	
	public String getName();
}
