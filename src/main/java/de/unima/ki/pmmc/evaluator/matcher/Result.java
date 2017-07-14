package de.unima.ki.pmmc.evaluator.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
 * A <code>Result</code> represents a single matcher with its
 * <code>Alignment</code>s and either <code>Characteristic</code>s or
 * <code>TypeCharacteristc</code>s. Also it provides the matcher name
 * and the corresponding path of the input alignments.
 */
public class Result implements Iterable<Alignment>, Comparable<Result>{

	private String name;
	private String path;
	private double appliedThreshold;
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
	
	public Result(String name, String path, double appliedThreshold, List<Alignment> alignments) {
		this.name = name;
		this.path = path;
		this.appliedThreshold = appliedThreshold;
		this.alignments = alignments;
		this.characteristics = new ArrayList<>();
		this.tCharacteristics = new ArrayList<>();
	}
	
	public void applyThreshold(double threshold) {
		for(Alignment a : this.alignments) {
			a.applyThreshold(threshold);
		}
	}
	
	public void printStats() {
		Map<CorrespondenceType, Integer> vals = new HashMap<>();
		for(CorrespondenceType type : CorrespondenceType.values()) {
			vals.put(type, 0);
		}
		for(Alignment a : alignments) {
			for(Correspondence c : a) {
				int curr = vals.get(c.getCType().get());
				vals.put(c.getCType().get(), curr+1);
			}
		}
		int sum = 0;
		for(Map.Entry<CorrespondenceType, Integer> e : vals.entrySet()) {
			sum += e.getValue();
		}
		for(Map.Entry<CorrespondenceType, Integer> e : vals.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue() + " "+ (e.getValue()/(double)sum));
		}
	}
	
	//TODO create different class hierachy, result as interface and type and simple characteristic are implementations
	public void computeCharacteristics(List<Result> goldstandards, Map<String, Alignment> crossProduct, boolean typeOn) throws CorrespondenceException {
		for(Result result : goldstandards) {
			for(Alignment aRef : result) {
				for(Alignment aMatcher : this) {
					if(aRef.equals(aMatcher)) {
						if(typeOn) {
							Model sourceModel = aRef.getSourceModel();
							Model targetModel = aRef.getTargetModel();
							this.tCharacteristics.add(new TypeCharacteristic(aMatcher, aRef, 
									null));
	//						this.tCharacteristics.add(new TypeCharacteristic(aMatcher, aRef, 
	//								crossProduct.get(sourceModel.getName()+targetModel.getName())));
						} else {
							this.characteristics.add(new Characteristic(aMatcher, aRef));						
						}
					}
				}
			}
		}
		System.out.println(characteristics.size());
	}
	
	/**
	 * Computes the minimal confidence value of a <code>Correspondence</code> that this
	 * matcher contains in its <code>Alignment</code> collection.
	 * @return minimum confidence value
	 */
	public double minConf() {
		return this.alignments.stream().
				flatMap(alignment -> {return alignment.getCorrespondences().stream();}).
				mapToDouble(corres -> {return corres.getConfidence();}).
				filter(value -> {return value > 0;}).min().getAsDouble();
	}
	
	/**
	 * Computes the maximal confidence value of a <code>Correspondence</code> that this
	 * matcher contains in its <code>Alignment</code> collection.
	 * @return maximal confidence value
	 */
	public double maxConf() {
		return this.alignments.stream().
				flatMap(alignment -> {return alignment.getCorrespondences().stream();}).
				mapToDouble(corres -> {return corres.getConfidence();})
				.max().getAsDouble();
	}
	
	public double meanConf() {
		return this.alignments.stream().
				flatMap(alignment -> {return alignment.getCorrespondences().stream();}).
				mapToDouble(corres -> {return corres.getConfidence();}).average().getAsDouble();
	}
	
	public double stdDevConf() {
		double mean = meanConf();
		double sqsum =  this.alignments.stream().
				flatMap(alignment -> {return alignment.getCorrespondences().stream();}).
				mapToDouble(corres -> {return Math.pow(corres.getConfidence() - mean, 2);}).
				average().getAsDouble();
		return Math.sqrt(sqsum);
	}
	
	public int size() {
		return alignments.size();
	}
	
	public int sizeOfCorrespondences() {
		int size = 0;
		for(Alignment a : this.alignments) {
			size += a.size();
		}
		return size;
	}
	
	public double sumConfidences() {
		double sum = 0;
		for(Alignment a : this.alignments) {
			for(Correspondence c : a) {
				sum += c.getConfidence();
			}
		}
		return sum;
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
	
	public double getAppliedThreshold() {
		return appliedThreshold;
	}

	public void setAppliedThreshold(double appliedThreshold) {
		this.appliedThreshold = appliedThreshold;
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
		return "Result [name=" + name + ", path=" + path
				+ ", appliedThreshold=" + appliedThreshold + ", size=" + alignments.size() + "]";
	}

	@Override
	public int compareTo(Result that) {
		//TODO bug changing between t and characteristics
		double diff = Characteristic.getNBFMeasureMicro(that.getCharacteristics()) - Characteristic.getNBFMeasureMicro(this.characteristics);
		if(diff < 0) {
			return -1;
		} else if(diff > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	
}
