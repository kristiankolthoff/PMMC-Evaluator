package de.unima.ki.pmmc.evaluator.generator;

import java.util.ArrayList;
import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;

public class AlignmentBinder {

	private List<Binder> binders;
	
	public AlignmentBinder() {
		this.binders = new ArrayList<>();
		this.binders.add(new BinderName());
	}
	
	public AlignmentBinder(List<Binder> binders) {
		this.binders = binders;
	}
	
	public void addBinder(Binder binder) {
		binders.add(binder);
	}
	
	public AlignmentBinding bind(List<Alignment> aligns1, List<Alignment> aligns2) {
		List<AlignmentBinding> bindings = new ArrayList<>();
		for(Binder binder : binders) {
			AlignmentBinding binding = binder.bind(aligns1, aligns2);
			bindings.add(binding);
		}
		return (bindings.isEmpty()) ? new AlignmentBinding() : bindings.get(0);
	}
}
