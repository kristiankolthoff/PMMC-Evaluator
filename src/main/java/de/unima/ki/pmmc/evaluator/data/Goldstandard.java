package de.unima.ki.pmmc.evaluator.data;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;

public class Goldstandard extends Solution {

	public Goldstandard(String name, String path, double threshold, List<Alignment> alignments) {
		super(name, path, threshold, alignments);
	}

}
