package de.unima.ki.pmmc.evaluator.data;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;

public class MatcherResult extends Solution {

	private List<Characteristic> characteristics;
	
	public MatcherResult(String name, String path, double threshold, 
			List<Alignment> alignments, List<Characteristic> characteristics) {
		super(name, path, threshold, alignments);
		this.setCharacteristics(characteristics);
	}

	public List<Characteristic> getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(List<Characteristic> characteristics) {
		this.characteristics = characteristics;
	}

}
