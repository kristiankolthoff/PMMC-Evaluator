package de.unima.ki.pmmc.evaluator.generator;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;

public interface Binder {

	
	public AlignmentBinding bind(List<Alignment> a1, List<Alignment> a2);
}
