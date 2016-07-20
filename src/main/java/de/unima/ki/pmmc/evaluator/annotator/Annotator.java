package de.unima.ki.pmmc.evaluator.annotator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.TypeCharacteristic;
import de.unima.ki.pmmc.evaluator.model.Activity;
import de.unima.ki.pmmc.evaluator.model.Model;
/**
 * The <code>Annotator</code> is responsible for annotating or tagging a
 * <code>Correspondence</code> with is appropriate <code>CorrespondenceType</code>
 * tag. Therefore, a chain of <code>CTMatcher</code> is applied, starting at the
 * low level types and ending in the high level types. That is, first TRIVIAL type
 * correspondences are annotated, and if the trivial <code>CTMatcher</code> can not
 * generate and identify the currently annotating <code>Correspondence</code> as TRIVIAL,
 * the <code>Annotator</code> delegates the <code>Correspondence</code> to the next higher
 * level.
 */
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
	
	/**
	 * Initializes the activity id to activity label map
	 */
	private void initModelMap() {
		for(Model m : this.models) {
			for(Activity a : m.getActivities()) {
				this.idCache.put(a.getId(), a.getLabel());
			}
		}
	}
	
	/**
	 * Given a <code>Correspondence</code>, computing the appropriate
	 * <code>CorrespondenceType</code> based on a collection of <code>
	 * CTMatcher</code>s.
	 * @param correspondence the correspondence which should be annotated
	 * @return the appropriate <code>CorrespondenceType</code> if available,
	 * and <code>CorrespondenceType.DEFAULT</code> if not allowed elements of the
	 * models where used to generate the <code>Correspondece</code>
	 */
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
	
	/**
	 * Annotates each <code>Correspondence</code> of the provided
	 * <code>Alignment</code> and returns the new <code>Alignment</code>
	 * with annotations.
	 * @param alignment the alignment to be annotated
	 * @return alignment with annotated correspondences
	 */
	public Alignment annotateAlignment(Alignment alignment) {
		Alignment finalAlign = new Alignment();
		for(Correspondence c : alignment) {
			c.setType(annotateCorrespondence(c));
			finalAlign.add(c);
		}
		return finalAlign;
	}
	
	/**
	  Annotates each <code>Correspondence</code> of each <code>Alignment</code> provided by
	 * the <code>Characteristic</code> and returns the new <code>TypeCharacteristic</code>.
	 * @param characteristic the characteristic to be annotated
	 * @return typecharacteristic
	 */
	public TypeCharacteristic annotateCharacteristic(Characteristic characteristic) 
			throws CorrespondenceException {
		Alignment alignmentReference = annotateAlignment(characteristic.getAlignmentReference());
		Alignment alignmentMapping = annotateAlignment(characteristic.getAlignmentMapping());
		return new TypeCharacteristic(alignmentMapping, alignmentReference);
	}
	
	/**
	  Annotates each <code>Correspondence</code> of each <code>Alignment</code> provided by
	 * each <code>Characteristic</code> and returns the new collection of <code>TypeCharacteristic</code>s.
	 * @param characteristics the characteristics to be annotated
	 * @return typecharacteristics
	 */
	public List<TypeCharacteristic> annotateCharacteristics(List<Characteristic> characteristics) 
			throws CorrespondenceException {
		List<TypeCharacteristic> vals = new ArrayList<>();
		for(Characteristic c : characteristics) {
			vals.add(annotateCharacteristic(c));
		}
		return vals;
	}
}
