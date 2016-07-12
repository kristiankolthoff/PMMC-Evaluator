package de.unima.ki.pmmc.evaluator.metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;

public class TypeCharacteristic extends Characteristic {

	/**
	 * Partition of correspondences into correspondence types 
	 * of the reference alignment from the characteristic
	 */
	private Map<CorrespondenceType, Alignment> corresponendencesRef;
	/**
	 * Partition of correspondences into correspondence types 
	 * of the matcher alignment from the characteristic
	 */
	private Map<CorrespondenceType, Alignment> correspondencesMapping;
	/**
	 * Partition of correspondences into correspondence types 
	 * of the correct alignment from the characteristic
	 */
	private Map<CorrespondenceType, Alignment> correspondencesCorrect;
	
	public TypeCharacteristic(Alignment mapping, Alignment reference) throws CorrespondenceException {
		super(mapping, reference);
		this.init();
	}
	
	private void init() throws CorrespondenceException {
		this.corresponendencesRef = extractCTMap(getAlignmentReference());
		this.correspondencesMapping = extractCTMap(getAlignmentMapping());
		this.correspondencesCorrect = extractCTMap(getAlignmentCorrect());
	}
	
	private Map<CorrespondenceType, Alignment> extractCTMap(Alignment alignment) 
			throws CorrespondenceException {
		Map<CorrespondenceType, Alignment> vals = new HashMap<>();
		for(Correspondence c : alignment) {
			if(c.getCType().isPresent()) {
				if(vals.containsKey(c.getCType().get())) {
					Alignment align = vals.get(c.getCType().get());
					align.add(c);
					vals.put(c.getCType().get(), align);
				} else {
					Alignment align = new Alignment();
					align.add(c);
					vals.put(c.getCType().get(), align);
				}
			} else {
				throw new CorrespondenceException(CorrespondenceException.MISSING_TYPE_ANNOTATION, c.toString());
			}
		}
		return vals;
	}

	public double getPrecision(CorrespondenceType type) {
		return correspondencesCorrect.get(type).size() / (double) correspondencesMapping.get(type).size();
	}
	
	public double getRecall(CorrespondenceType type) {
		return correspondencesCorrect.get(type).size() / (double) corresponendencesRef.get(type).size();
	}
	
	public double getFMeasure(CorrespondenceType type) {
		return computeFFromPR(getPrecision(type), getRecall(type));
	}
	
	public Alignment getCTAlignmentRef(CorrespondenceType type) {
		return this.corresponendencesRef.get(type);
	}
	
	public Alignment getCTAlignmentMapping(CorrespondenceType type) {
		return this.correspondencesMapping.get(type);
	}
	
	public Alignment getCTAlignmentCorrect(CorrespondenceType type) {
		return this.correspondencesCorrect.get(type);
	}
	
	@Override
	public String toString() {
		return "TypeCharacteristic [corresponendencesRef="
				+ corresponendencesRef + ", correspondencesMapping="
				+ correspondencesMapping + ", correspondencesCorrect="
				+ correspondencesCorrect + "]";
	}
	
	/**
	 * Returns the macro recall for a given correspondence type for mutliple
	 * <code>TypeCharacteristc</code>s.
	 * @param characteristics - the characteristics to compute the macro recall from
	 * @param type - the correpsondence type
	 * @return macro recall
	 */
	public static double getRecallMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
		Objects.requireNonNull(characteristics);
		double sum = 0;
		int numOfOcc = 0;
		for(TypeCharacteristic c : characteristics) {
			double currRecall = c.getRecall(type);
			if(!Double.isNaN(currRecall)) {
				sum += c.getRecall(type);
				numOfOcc++;
			}
		}
		return sum / numOfOcc;
	}

	/**
	 * Returns the micro recall for a given correspondence type for multiple
	 * <code>TypeCharacteristic</code>s.
	 * @param characteristics - the characteristics to compute the macro recall from
	 * @param type - the correspondence type
	 * @return micro recall
	 */
	public static double getRecallMicro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
		Objects.requireNonNull(characteristics);
		int sumNumOfMatcher = 0;
		int sumNumOfGold = 0;
		for(TypeCharacteristic c : characteristics) {
			sumNumOfMatcher += c.getCTAlignmentCorrect(type).size();
			sumNumOfGold += c.getCTAlignmentRef(type).size();
		}
		return Characteristic.computeRecall(sumNumOfMatcher, sumNumOfGold);
	}
	
	/**
	 * Returns the standard deviation of the macro of a collection of <code>TypeCharacteristic</code>s
	 * @param characteristics - the characteristics to compute the macro recall standard deviation from
	 * @param type - the correspondence type
	 * @return standard deviation of recall
	 */
	public static double getRecallStdDev(List<TypeCharacteristic> characteristics, CorrespondenceType type) { 
		Objects.requireNonNull(characteristics);
		double avgMacro = TypeCharacteristic.getRecallMacro(characteristics, type);
		double dev = 0;
		int numOfOcc = 0;
		for(TypeCharacteristic c : characteristics) {
			double currRecall = c.getRecall(type);
			if(!Double.isNaN(currRecall)) {
				double currDev = Math.abs(currRecall - avgMacro);
				dev += Math.pow(currDev, 2);
				numOfOcc++;
			}
		}
		return Math.sqrt(dev/numOfOcc);
	}
	
	/**
	 * Compute the macro precision over a list of <code>TypeCharacteristics</code>. 
	 * The macro precision is the average of all the precision values of
	 * all characteristics. Note that this metric can be easily biased
	 * if the test sets are not equally large.
	 * @param characteristics - the characteristics to compute the macro precision from
	 * @param type - the correspondence type
	 * @return macro precision for a specific <code>CorrespondenceType</code>
	 */
	public static double getPrecisionMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
		Objects.requireNonNull(characteristics);
		double sum = 0;
		int numOfOcc = 0;
		for(TypeCharacteristic c : characteristics) {
			double currPrecision = c.getPrecision(type);
			if(!Double.isNaN(currPrecision)) {
				sum += currPrecision;
				numOfOcc++;
			}
		}
		return sum / numOfOcc;
	}
	
	/**
	 * Compute the micro precision over a list of <code>TypeCharacteristic</code>s. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the micro precision from
	 * @param type - the correpsondence type
	 * @return micro precision for a specific <code>CorrespondenceType</code>
	 */
	public static double getPrecisionMicro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
		Objects.requireNonNull(characteristics);
		int sumNumOfRulesCorrect = 0;
		int sumNumOfRulesMatcher = 0;
		for(TypeCharacteristic c : characteristics) {
			sumNumOfRulesCorrect += c.getCTAlignmentCorrect(type).size();
			sumNumOfRulesMatcher += c.getCTAlignmentMapping(type).size();
		}
		return sumNumOfRulesCorrect / (double)sumNumOfRulesMatcher;
	}
	
	/**
	 * Compute the standard deviation of the macro precision over a collection 
	 * of <code>TypeCharacteristic</code>s.
	 * @param characteristics - the charateristics to compute the macro precision standard deviation from
	 * @param type - the correspondence type
	 * @return macro precision standard deviation for a specific <code>CorrespondenceType</code>
	 */
	public static double getPrecisionStdDev(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
		double avgMacro = TypeCharacteristic.getPrecisionMacro(characteristics, type);
		double dev = 0;
		int numOfOcc = 0;
		for(TypeCharacteristic c : characteristics) {
			double currPrecision = c.getPrecision(type);
			if(!Double.isNaN(currPrecision)) {
				double currDev = Math.abs(currPrecision - avgMacro);
				dev += Math.pow(currDev, 2);	
				numOfOcc++;
			}
		}
		return Math.sqrt(dev/numOfOcc);
	}
	
	public double getRelativeDistance(CorrespondenceType type, boolean normalize) {
		List<Alignment> mappings = new ArrayList<>();
		List<Alignment> references = new ArrayList<>();
		mappings.add(correspondencesMapping.get(type));
		references.add(corresponendencesRef.get(type));
		return getRelativeDistance(mappings, references, normalize);
	}
	
	/**
	 * Computes the relative distance of the matcher alignments to the reference alignments 
	 * of the gold standard for a specific <code>CorrespondenceType</code>, based on a collection 
	 * of characteristics. First normalizes the  matcher alignments to a target scale, then computes 
	 * the sum of squared deviations of the confidence values of the matcher and the reference alignment.
	 * @param characteristics - the characteristics to compute the relative distance from
	 * @param type - the correspondence type
	 * @param normalize - state wether the confidences should be normalized
	 * @return the realtive distance for a given <code>CorrespondenceType</code>
	 */
	public static double getRelativeDistance(List<TypeCharacteristic> characteristics, CorrespondenceType type, 
			boolean normalize) {
		List<Alignment> mappings = new ArrayList<>();
		List<Alignment> references = new ArrayList<>();
		for(TypeCharacteristic c : characteristics) {
			mappings.add(c.getCTAlignmentMapping(type));
			references.add(c.getCTAlignmentRef(type));
		}
		return getRelativeDistance(mappings, references, normalize);
	}
	
}
