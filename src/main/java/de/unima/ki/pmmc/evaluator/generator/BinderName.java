package de.unima.ki.pmmc.evaluator.generator;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;

public class BinderName implements Binder {

	@Override
	public AlignmentBinding bind(List<Alignment> a1, List<Alignment> a2) {
		AlignmentBinding binding = new AlignmentBinding();
		for(Alignment alignment1 : a1) {
			for(Alignment alignment2 : a2) {
				if(alignment1.getName().equals(alignment2.getName())) {
					binding.addBinding(alignment1, alignment2);
				}
			}
		}
		return binding;
	}

}
