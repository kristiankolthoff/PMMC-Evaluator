package de.unima.ki.pmmc.evaluator.metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;

public class TypeCharacteristic extends Characteristic {

	/**
	 * Partition of correspondences into correspondence types 
	 * of the reference alignment from the characteristic
	 */
	private Map<CorrespondenceType, List<Correspondence>> corresponendencesGold;
	/**
	 * Partition of correspondences into correspondence types 
	 * of the matcher alignment from the characteristic
	 */
	private Map<CorrespondenceType, List<Correspondence>> correspondencesMatcher;
	/**
	 * Partition of correspondences into correspondence types 
	 * of the correct alignment from the characteristic
	 */
	private Map<CorrespondenceType, List<Correspondence>> correspondencesCorrect;
	
	public TypeCharacteristic(Alignment mapping, Alignment reference) {
		super(mapping, reference);
		this.init(mapping, reference);
	}
	
	private void init(Alignment mapping, Alignment reference) {
		this.correspondencesCorrect = new HashMap<>();
		this.correspondencesMatcher = new HashMap<>();
		this.corresponendencesGold = new HashMap<>();
	}

	public double getPrecision(CorrespondenceType type) {
		return correspondencesCorrect.size() / correspondencesMatcher.size();
	}
	
	public double getRecall(CorrespondenceType type) {
		return correspondencesCorrect.size() / corresponendencesGold.size();
	}
	
}
