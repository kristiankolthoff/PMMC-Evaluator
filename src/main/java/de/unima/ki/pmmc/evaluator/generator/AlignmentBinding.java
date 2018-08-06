package de.unima.ki.pmmc.evaluator.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import edu.stanford.nlp.util.Pair;

public class AlignmentBinding implements Iterable<Pair<Alignment, Alignment>>{

	private List<Pair<Alignment, Alignment>> binding;
	
	public AlignmentBinding() {
		this.binding = new ArrayList<>();
	}
	
	public void addBinding(Alignment a1, Alignment a2) {
		this.binding.add(new Pair<Alignment, Alignment>(a1, a2));
	}

	@Override
	public Iterator<Pair<Alignment, Alignment>> iterator() {
		return binding.iterator();
	}
}
