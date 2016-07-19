package de.unima.ki.pmmc.evaluator.annotator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.TypeCharacteristic;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.model.Model;

public class Annotator {

	/**
	 * Stores all kinds of different CTMatchers
	 */
	private List<CTMatcher> matchers;
	private List<Model> models;
	/**
	 * Cached IDs to actual labels
	 */
	private Map<String, String> idCache;
	
	private static final String SPLIT = "#";
	
	public Annotator(List<Model> models) {
		this.matchers = new ArrayList<>();
		this.models = models;
		this.matchers.add(new CTMatcherTrivial());
		this.matchers.add(new CTMatcherTrivialNorm());
		this.matchers.add(new CTMatcherOWS());
		this.matchers.add(new CTMatcherDSV());
		this.matchers.add(new CTMatcherMisc());
		this.idCache = new HashMap<>();
		this.initModelMap();
	}
	
	private void initModelMap() {
		for(Model m : this.models) {
			for(Activity a : m.getActivities()) {
				this.idCache.put(a.getId(), a.getLabel());
			}
		}
	}
	
	public CorrespondenceType annotateCorrespondence(Correspondence correspondence) {
		for(CTMatcher matcher : this.matchers) {
			String label1 = this.idCache.get(correspondence.getUri1().split(SPLIT)[1]);
			String label2 = this.idCache.get(correspondence.getUri2().split(SPLIT)[1]);
			//Matcher generated correspondence using not allowed events of the model
			if(label1 == null || label2 == null) {
				return CorrespondenceType.DEFAULT;
			} else {
				CorrespondenceType estCT = matcher.match(label1, label2);
				if(estCT != CorrespondenceType.DEFAULT) {
					return estCT;
				}
			}
		}
		return CorrespondenceType.DIFFICULT;
	}
	
	public Alignment annotateAlignment(Alignment alignment) {
		System.out.println("annotate " + alignment.getName());
		Alignment finalAlign = new Alignment();
		for(Correspondence c : alignment) {
			c.setType(annotateCorrespondence(c));
			finalAlign.add(c);
		}
		return finalAlign;
	}
	
	public TypeCharacteristic annotateCharacteristic(Characteristic characteristic) 
			throws CorrespondenceException {
		Alignment alignmentReference = annotateAlignment(characteristic.getAlignmentReference());
		Alignment alignmentMapping = annotateAlignment(characteristic.getAlignmentMapping());
		return new TypeCharacteristic(alignmentMapping, alignmentReference);
	}
	
	public List<TypeCharacteristic> annotateCharacteristics(List<Characteristic> characteristics) 
			throws CorrespondenceException {
		List<TypeCharacteristic> vals = new ArrayList<>();
		for(Characteristic c : characteristics) {
			vals.add(annotateCharacteristic(c));
		}
		return vals;
	}
}
