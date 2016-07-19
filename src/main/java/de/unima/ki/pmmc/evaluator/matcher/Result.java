package de.unima.ki.pmmc.evaluator.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.TypeCharacteristic;
/**
 * A <code>Result</code> represents a single matcher with its
 * <code>Alignment</code>s and either <code>Characteristic</code>s or
 * <code>TypeCharacteristc</code>s. Also it provides the matcher name
 * and the corresponding path of the input alignments.
 */
public class Result implements Iterable<Alignment>{

	private String name;
	private String path;
	private List<Alignment> alignments;
	private List<Characteristic> characteristics;
	private List<TypeCharacteristic> tCharacteristics;
	
	public Result(String name, String path, List<Alignment> alignments,
			List<Characteristic> characteristics,
			List<TypeCharacteristic> tCharacteristics) {
		this.name = name;
		this.path = path;
		this.alignments = alignments;
		this.characteristics = characteristics;
		this.tCharacteristics = tCharacteristics;
	}

	public Result(String name, String path, List<Alignment> alignments) {
		this.name = name;
		this.path = path;
		this.alignments = alignments;
		this.characteristics = new ArrayList<>();
		this.tCharacteristics = new ArrayList<>();
	}
	
	public void applyThreshold(double threshold) {
		for(Alignment a : this.alignments) {
			a.applyThreshold(threshold);
		}
	}
	
	public void computeCharacteristics(Result goldstandard, boolean typeOn) throws CorrespondenceException {
		for(Alignment aRef : goldstandard) {
			for(Alignment aMatcher : this) {
				if(aRef.equals(aMatcher)) {
					if(typeOn) {
						this.tCharacteristics.add(new TypeCharacteristic(aMatcher, aRef));
					} else {
						this.characteristics.add(new Characteristic(aMatcher, aRef));						
					}
				}
			}
		}
	}
	
	/**
	 * Computes the minimal confidence value of a <code>Correspondence</code> that this
	 * matcher contains in its <code>Alignment</code> collection.
	 * @return minimum confidence value
	 */
	public double minConf() {
		List<Correspondence> cVals = new ArrayList<>();
		for(Alignment alignment : this.alignments) {
			for(Correspondence c : alignment) {
				cVals.add(c);
			}
		}
		return Collections.min(cVals).getConfidence();
	}
	
	/**
	 * Computes the maximal confidence value of a <code>Correspondence</code> that this
	 * matcher contains in its <code>Alignment</code> collection.
	 * @return maximal confidence value
	 */
	public double maxConf() {
		List<Correspondence> cVals = new ArrayList<>();
		for(Alignment alignment : this.alignments) {
			for(Correspondence c : alignment) {
				cVals.add(c);
			}
		}
		return Collections.max(cVals).getConfidence();
	}
	
	public int size() {
		return alignments.size();
	}
	
	public boolean isEmptyCharacteristics() {
		return this.characteristics.isEmpty();
	}
	
	public boolean isEmptyTypeCharacteristics() {
		return this.tCharacteristics.isEmpty();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Alignment> getAlignments() {
		return alignments;
	}

	public void setAlignments(List<Alignment> alignments) {
		this.alignments = alignments;
	}

	public List<Characteristic> getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(List<Characteristic> characteristics) {
		this.characteristics = characteristics;
	}

	public List<TypeCharacteristic> getTypeCharacteristics() {
		return tCharacteristics;
	}

	public void setTypeCharacteristics(List<TypeCharacteristic> tCharacteristics) {
		this.tCharacteristics = tCharacteristics;
	}
	
	public boolean isFLM() {
		return Characteristic.isFirstLineMatcher(this.characteristics);
	}

	@Override
	public Iterator<Alignment> iterator() {
		return alignments.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Result other = (Result) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Result [name=" + name + ", path=" + path + "]";
	}

}
