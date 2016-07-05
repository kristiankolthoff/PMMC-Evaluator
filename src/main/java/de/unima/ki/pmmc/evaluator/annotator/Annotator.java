package de.unima.ki.pmmc.evaluator.annotator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.TypeCharacteristic;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.model.Model;

public class Annotator {

	/**
	 * Matcher for identifying TRIVIAL correspondences
	 */
	private CTMatcher matcherTrivial;
	private List<CTMatcher> allMatchers;
	private List<Model> models;
	/**
	 * Cached IDs to actual labels
	 */
	private Map<String, String> idCache;
	
	public Annotator(List<Model> models) {
		this.allMatchers = new ArrayList<>();
		this.models = models;
		this.matcherTrivial = new CTMatcherTrivial();
		this.allMatchers.add(matcherTrivial);
		this.initModelMap();
	}
	
	private void initModelMap() {
		//TODO activities, intermediateevent....
		for(Model m : this.models) {
			for(Activity a : m.getActivities()) {
				this.idCache.put(a.getId(), a.getLabel());
			}
		}
	}
	
	public CorrespondenceType receiveCType(Correspondence correspondence) {
		for(CTMatcher matcher : this.allMatchers) {
			String label1 = this.idCache.get(correspondence.getUri1());
			String label2 = this.idCache.get(correspondence.getUri2());
			CorrespondenceType estCT = matcher.match(label1, label2, correspondence);
			if(estCT != CorrespondenceType.DEFAULT) {
				return estCT;
			}
		}
		return null;
	}
	
	public Alignment annotateAlignment(Alignment alignment) {
		Alignment finalAlign = new Alignment();
		for(Correspondence c : alignment) {
			c.setType(Optional.ofNullable(receiveCType(c)));
			finalAlign.add(c);
		}
		return finalAlign;
	}
	
	public TypeCharacteristic annotateCharacteristic(Characteristic characteristic) {
		Alignment alignmentReference = annotateAlignment(characteristic.getAlignmentReference());
		Alignment alignmentMapping = annotateAlignment(characteristic.getAlignmentMapping());
		return new TypeCharacteristic(alignmentMapping, alignmentReference);
	}
	
	public List<TypeCharacteristic> annotateCharacteristics(List<Characteristic> characteristics) {
		List<TypeCharacteristic> vals = new ArrayList<>();
		for(Characteristic c : characteristics) {
			vals.add(annotateCharacteristic(c));
		}
		return vals;
	}
}
