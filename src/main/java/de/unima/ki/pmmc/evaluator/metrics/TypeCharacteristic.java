//package de.unima.ki.pmmc.evaluator.metrics;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import de.unima.ki.pmmc.evaluator.alignment.Alignment;
//import de.unima.ki.pmmc.evaluator.alignment.Correspondence;
//import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
//import de.unima.ki.pmmc.evaluator.exceptions.CorrespondenceException;
//
//public class TypeCharacteristic extends Characteristic {
//
//	/**
//	 * Partition of correspondences into correspondence types 
//	 * of the reference alignment from the characteristic
//	 */
//	private Map<CorrespondenceType, Alignment> alignmentReference;
//	/**
//	 * Partition of correspondences into correspondence types 
//	 * of the matcher alignment from the characteristic
//	 */
//	private Map<CorrespondenceType, Alignment> alignmentMapping;
//	/**
//	 * Partition of correspondences into correspondence types 
//	 * of the correct alignment from the characteristic
//	 */
//	private Map<CorrespondenceType, Alignment> alignmentCorrect;
//	/**
//	 * Partition of correspondences into correspondence types 
//	 * of the cross product of the two models of the original alignment
//	 */
//	
//	public TypeCharacteristic(Alignment mapping, Alignment reference,
//			Alignment crossProduct) throws CorrespondenceException {
//		super(mapping, reference);
//		this.init();
//	}
//	
//	private void init() throws CorrespondenceException {
//		this.alignmentReference = extractCTMap(getAlignmentReference());
//		this.alignmentMapping = extractCTMap(getAlignmentMapping());
//		this.alignmentCorrect = extractCTMap(getAlignmentCorrect());
//	}
//	
//	private Map<CorrespondenceType, Alignment> extractCTMap(Alignment alignment) 
//			throws CorrespondenceException {
//		Map<CorrespondenceType, Alignment> vals = new HashMap<>();
//		for(CorrespondenceType type : CorrespondenceType.values()) {
//			vals.put(type, new Alignment());
//		}
//		for(Correspondence c : alignment) {
//			if(c.getCType().isPresent()) {
//				Alignment align = vals.get(c.getCType().get());
//				align.add(c);
//				vals.put(c.getCType().get(), align);
//			
//			} else {
//				throw new CorrespondenceException(CorrespondenceException.MISSING_TYPE_ANNOTATION, c.toString());
//			}
//		}
//		return vals;
//	}
//	
////	public Alignment getTP(CorrespondenceType type) {
////		return Alignment.newInstance(alignmentCorrect.get(type));
////	}
////	
////	
////	public Alignment getFP(CorrespondenceType type) {
////		return Alignment.newInstance(alignmentMapping.get(type).minus(alignmentCorrect.get(type)));
////	}
////	
////	public Alignment getFN(CorrespondenceType type) {
////		return Alignment.newInstance(alignmentReference.get(type).minus(alignmentMapping.get(type)));
////	}
////	
////
////	public double getPrecision(CorrespondenceType type) {
////		return alignmentCorrect.get(type).size() / (double) alignmentMapping.get(type).size();
////	}
////	
////	public double getNBPrecision(CorrespondenceType type) {
////		return getConfSumCorrect(type) / ((double)getFP(type).size() + getConfSumCorrect(type));
////	}
////	
////	public double getRecall(CorrespondenceType type) {
////		return alignmentCorrect.get(type).size() / (double) alignmentReference.get(type).size();
////	}
////	
////	public double getNBRecall(CorrespondenceType type) {
////		return getConfSumCorrect(type) / getConfSumReference(type);
////	}
////	
////	private double getConfSumReference(CorrespondenceType type) {
////		double sum = 0;
////		for(Correspondence cRef : this.alignmentReference.get(type)) {
////			sum += cRef.getConfidence();
////		}
////		return sum;
////	}
////	
////	private double getConfSumCorrect(CorrespondenceType type) {
////		double sum = 0;
////		for(Correspondence cRef : this.alignmentCorrect.get(type)) {
////			sum += cRef.getConfidence();
////		}
////		return sum;
////	}
////	
////	public double getFMeasure(CorrespondenceType type) {
////		return computeFFromPR(getPrecision(type), getRecall(type));
////	}
////	
////	public double getNBFMeasure(CorrespondenceType type) {
////		return computeFFromPR(getNBPrecision(type), getNBRecall(type));
////	}
////	
////	/**
////	 * Returns an <code>Alignment</code> corresponding to the
////	 * <code>CorrespondenceType</code>. That is, each <Code>Correspondence</code>
////	 * in the <code>Alignment</code> is of the specified type and belongs to the goldstandard.
////	 * @param type the type the <code>Correspondence</code>s should have
////	 * @return the <code>Alignment</code> containing <code>Correspondence</code>s corresponding to the type
////	 */
////	public Alignment getAlignmentReference(CorrespondenceType type) {
////		return this.alignmentReference.get(type);
////	}
////	
////	/**
////	 * Returns an <code>Alignment</code> corresponding to the
////	 * <code>CorrespondenceType</code>. That is, each <Code>Correspondence</code>
////	 * in the <code>Alignment</code> is of the specified type and is generated by the matcher.
////	 * @param type the type the <code>Correspondence</code>s should have
////	 * @return the <code>Alignment</code> containing <code>Correspondence</code>s corresponding to the type
////	 */
////	public Alignment getAlignmentMapping(CorrespondenceType type) {
////		return this.alignmentMapping.get(type);
////	}
////	
////	/**
////	 * Returns an <code>Alignment</code> corresponding to the
////	 * <code>CorrespondenceType</code>. That is, each <Code>Correspondence</code>
////	 * in the <code>Alignment</code> is of the specified type and is correctly
////	 * identified by the matcher.
////	 * @param type the type the <code>Correspondence</code>s should have
////	 * @return the <code>Alignment</code> containing <code>Correspondence</code>s corresponding to the type
////	 */
////	public Alignment getAlignmentCorrect(CorrespondenceType type) {
////		return this.alignmentCorrect.get(type);
////	}
////	
////	
////	@Override
////	public String toString() {
////		return "TypeCharacteristic [corresponendencesRef="
////				+ alignmentReference + ", correspondencesMapping="
////				+ alignmentMapping + ", correspondencesCorrect="
////				+ alignmentCorrect + "]";
////	}
//	
////	/**
////	 * General purpose method for computing micro summary
////	 * of values provided by the two function parameters. That is,
////	 * over a list of <code>TypeCharacteristic</code>s, this method computes
////	 * the division with the sum of single values of the first function in
////	 * the numerator, and the the sum of single values of the second function
////	 * in the denominator. This method avoids boilerplate code and follows DRY.
////	 * @param characteristics the typecharacteristics to compute the micro summary from
////	 * @param functionNum function for producing values from a single <code>TypeCharacteristic</code>
////	 * for the numerator sum
////	 * @param functionDenom function for producing values from a single <code>TypeCharacteristic</code>
////	 * for the denominator sum
////	 * @return sum{functionNumVals} / sum{functionDenomVals}
////	 */
////	private static double computeMicro(List<TypeCharacteristic> characteristics,
////			Function<TypeCharacteristic, Double> functionNum, Function<TypeCharacteristic, Double> functionDenom) {
////		double sumNum = 0;
////		double sumDenom = 0;
////		for(TypeCharacteristic c : characteristics) {
////			sumNum += functionNum.apply(c);
////			sumDenom += functionDenom.apply(c);
////		}
////		return sumNum / sumDenom;
////	}
//	
////	/**
////	 * General purpose method for computing macro summary of
////	 * values provided by the single input function. Simply sums up
////	 * the individual values provided by the function for each single
////	 * <code>TypeCharacteristic</code> and finally computes the average.
////	 * @param characteristics the typecharacteristics to compute the macro summary from
////	 * @param function function for producing values from a single <code>TypeCharacteristic</code>
////	 * @return the average as sum{functionVals} / numOfVals
////	 */
////	private static double computeMacro(List<TypeCharacteristic> characteristics,
////			Function<TypeCharacteristic, Double> function) {
////		double sum = 0;
////		int numOfOcc = 0;
////		for(TypeCharacteristic c : characteristics) {
////			double val = function.apply(c);
////			if(!Double.isNaN(val)) {
////				sum += val;
////				numOfOcc++;
////			}
////		}
////		double val = sum / numOfOcc;
////		return (Double.isNaN(val)) ? 0 : val;
////	}
//	
////	/**
////	 * Computes the standard deviation over a list of <code>TypeCharacteristic</code>s
////	 * based on values provided by the two input functions. The first function is
////	 * used to compute the average value for the <code>TypeCharacteristic</code> collection,
////	 * the second function provides the current value for a single <code>TypeCharacteristic</code>
////	 * @param characteristics the typecharacteristics to compute the standard deviation from
////	 * @param functionAvg function for producing the average value to compare the single/current
////	 * values against
////	 * @param functionSum function for producing the current value for a single <code>TypeCharacteristic</code>
////	 * @return the standard deviation based on the provided functions
////	 */
////	private static double computeStdDev(List<TypeCharacteristic> characteristics, 
////			BiFunction<List<TypeCharacteristic>, CorrespondenceType, Double> functionAvg, 
////			Function<TypeCharacteristic, Double> functionSum, CorrespondenceType type) {
////		double avgMacro = functionAvg.apply(characteristics, type);
////		double dev = 0;
////		int numOfOcc = 0;
////		for(TypeCharacteristic c : characteristics) {
////			double val = functionSum.apply(c);
////			if(!Double.isNaN(val)) {
////				double currDev = Math.abs(val - avgMacro);
////				dev += Math.pow(currDev, 2);
////				numOfOcc++;
////			}
////		}
////		return Math.sqrt(dev/numOfOcc);
////		
////	}
//	
////	public double getAccuracy(CorrespondenceType type) {
////		final int TP = getTP(type).size();
////		final int TN = getTN(type).size();
////		final int FP = getFP(type).size();
////		final int FN = getFN(type).size();
////		if(TP+TN+FP+FN <= 0) {
////			double test = (TP + TN) / (double)(TP + TN + FP + FN);
////			System.out.println("empty " + type);
////		}
////		return (TP + TN) / (double)(TP + TN + FP + FN);
////	}
//	
////	public static double getAccuracyMicro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
////		return computeMicro(characteristics, c -> {return (double)(c.getTN(type).size() + c.getTP(type).size());}, 
////				c -> {return (double)(c.getTN(type).size() + c.getTP(type).size() 
////						+ c.getFN(type).size() + c.getFP(type).size());});
////	}
////	
////	public static double getAccuracyMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
////		return computeMacro(characteristics, c -> {return c.getAccuracy(type);});
////	}
////	
////	public static double getAccuracyStdDev(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
////		return computeStdDev(characteristics, TypeCharacteristic::getAccuracyMacro, 
////				c -> {return c.getAccuracy(type);}, type);
////	}
//	
//	public static Map<CorrespondenceType, Integer> getMatcherTypeCount(List<TypeCharacteristic> characteristics) {
//		Map<CorrespondenceType, Integer> vals = new HashMap<>();
//		for(TypeCharacteristic c : characteristics) {
//			for(Map.Entry<CorrespondenceType, Alignment> e : c.alignmentMapping.entrySet()) {
//				if(vals.containsKey(e.getKey())) {
//					vals.put(e.getKey(), vals.get(e.getKey()) + e.getValue().size());
//				} else {
//					vals.put(e.getKey(), e.getValue().size());
//				}
//			}
//		}
//		return vals;
//	}
//	
//	public static int getFNSum(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return characteristics.
//				stream().
//				map((list) -> {return list.getFN(type).size();}).
//				reduce(0, (i,j) -> {return i+j;});
//	}
//	
//	public static int getFPSum(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return characteristics.
//				stream().
//				map((list) -> {return list.getFP(type).size();}).
//				reduce(0, (i,j) -> {return i+j;});
//	}
//	
//	/**
//	 * Computes the correlation between matcher and
//	 * reference <code>Alignment</code> for a given <code>CorrespondenceType</code>.
//	 * @param type the correspondence type to compute the correlation from
//	 * @param allowZeros allow zeros in correlation computation
//	 * @return correlation between matcher and goldstandard for a
//	 * specific correspondence type
//	 */
//	public double getCorrelation(CorrespondenceType type, boolean allowZeros) {
//		List<Alignment> mappings = new ArrayList<>();
//		List<Alignment> references = new ArrayList<>();
//		mappings.add(this.alignmentMapping.get(type));
//		references.add(this.alignmentReference.get(type));
//		return getCorrelation(mappings, references, allowZeros);
//	}
//	
//	/**
//	 * Computes the micro correlation across the <code>TypeCharacteristc</code>s.
//	 * @param characteristics the characteristics to compute the micro correlation from
//	 * @param allowZeros allow zeros in correlation computation
//	 * @return micro correlation of multiple <code>Characterisitic</code>s
//	 */
//	public static double getCorrelationMicro(List<TypeCharacteristic> characteristics, CorrespondenceType type, 
//			boolean allowZeros) {
//		List<Alignment> mappings = new ArrayList<>();
//		List<Alignment> references = new ArrayList<>();
//		for(TypeCharacteristic c : characteristics) {
//			mappings.add(c.getAlignmentMapping(type));
//			references.add(c.getAlignmentReference(type));
//		}
//		return getCorrelation(mappings, references, allowZeros);
//	}
//	
//	/**
//	 * Computes the macro correlation as the plain average of
//	 * the sum of correlations across the <code>Characteristc</code>s
//	 * based on the specified <code>CorrespondenceType</code>.
//	 * @param characteristics the type characteristics to compute the macro correlation from
//	 * @param allowZeros allow zeros in correlation computation
//	 * @return macro correlation of multiple <code>TypeCharacterisitic</code>s
//	 * for a specific <code>CorrespondenceType</code>
//	 */
//	public static double getCorrelationMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type,
//			boolean allowZeros) {
//		return computeMacro(characteristics, c -> {return c.getCorrelation(type, allowZeros);});
//	}
//	
//	/**
//	 * Returns the macro recall for a given correspondence type for mutliple
//	 * <code>TypeCharacteristc</code>s.
//	 * @param characteristics - the characteristics to compute the macro recall from
//	 * @param type - the correpsondence type
//	 * @return macro recall
//	 */
//	public static double getRecallMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMacro(characteristics, c -> {return c.getRecall(type);});
//	}
//	
//	public static double getNBRecallMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMacro(characteristics, c -> {return c.getNBRecall(type);});
//	}
//
//	/**
//	 * Returns the micro recall for a given correspondence type for multiple
//	 * <code>TypeCharacteristic</code>s.
//	 * @param characteristics - the characteristics to compute the macro recall from
//	 * @param type - the correspondence type
//	 * @return micro recall
//	 */
//	public static double getRecallMicro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMicro(characteristics, c -> {return (double) c.getAlignmentCorrect(type).size();}, 
//				c -> {return (double) c.getAlignmentReference(type).size();});
//	}
//	
//	public static double getNBRecallMicro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMicro(characteristics, c -> {return c.getConfSumCorrect(type);}, 
//				c -> {return c.getConfSumReference(type);});
//	}
//	
//	/**
//	 * Returns the standard deviation of the macro of a collection of <code>TypeCharacteristic</code>s
//	 * @param characteristics - the characteristics to compute the macro recall standard deviation from
//	 * @param type - the correspondence type
//	 * @return standard deviation of recall
//	 */
//	public static double getRecallStdDev(List<TypeCharacteristic> characteristics, CorrespondenceType type) { 
//		return computeStdDev(characteristics, TypeCharacteristic::getRecallMacro, c -> {return c.getRecall(type);}, type);
//	}
//	
//	/**
//	 * Compute the macro precision over a list of <code>TypeCharacteristics</code>. 
//	 * The macro precision is the average of all the precision values of
//	 * all characteristics. Note that this metric can be easily biased
//	 * if the test sets are not equally large.
//	 * @param characteristics - the characteristics to compute the macro precision from
//	 * @param type - the correspondence type
//	 * @return macro precision for a specific <code>CorrespondenceType</code>
//	 */
//	public static double getPrecisionMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMacro(characteristics, c -> {return c.getPrecision(type);});
//	}
//	
//	public static double getNBPrecisionMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMacro(characteristics, c -> {return c.getNBPrecision(type);});
//	}
//	
//	/**
//	 * Compute the micro precision over a list of <code>TypeCharacteristic</code>s. 
//	 * Avoids biasing the value by unequally large data sets.
//	 * @param characteristics - the characteristic to compute the micro precision from
//	 * @param type - the correpsondence type
//	 * @return micro precision for a specific <code>CorrespondenceType</code>
//	 */
//	public static double getPrecisionMicro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMicro(characteristics, c -> {return (double) c.getAlignmentCorrect(type).size();}, 
//				c -> {return (double) c.getAlignmentMapping(type).size();});
//	}
//	
//	public static double getNBPrecisionMicro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMicro(characteristics, c -> {return c.getConfSumCorrect(type);}, 
//				c -> {return ((double)c.getFP(type).size() + c.getConfSumCorrect(type));});
//	}
//	
//	/**
//	 * Compute the standard deviation of the macro precision over a collection 
//	 * of <code>TypeCharacteristic</code>s.
//	 * @param characteristics - the charateristics to compute the macro precision standard deviation from
//	 * @param type - the correspondence type
//	 * @return macro precision standard deviation for a specific <code>CorrespondenceType</code>
//	 */
//	public static double getPrecisionStdDev(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeStdDev(characteristics, TypeCharacteristic::getPrecisionMacro, c -> {return c.getPrecision(type);}, type);
//	}
//	
//	public static double getFMeasureMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMacro(characteristics, c -> {return c.getFMeasure(type);});
//	}
//	
//	public static double getNBFMeasureMacro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeMacro(characteristics, c -> {return c.getNBFMeasure(type);});
//	}
//	
//	public static double getFMeasureMicro(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		int sumNumOfMatcher = 0;
//		int sumNumOfGold = 0;
//		int sumNumOfCorrect = 0;
//		for(TypeCharacteristic c : characteristics) {
//			sumNumOfMatcher += c.getAlignmentMapping(type).size();
//			sumNumOfGold += c.getAlignmentReference(type).size();
//			sumNumOfCorrect += c.getAlignmentCorrect(type).size();
//		}
//		return Characteristic.computeFFromPR((sumNumOfCorrect / (double) sumNumOfMatcher), 
//				(sumNumOfCorrect / (double) sumNumOfGold));
//	}
//	
//	public static double getFMeasureStdDev(List<TypeCharacteristic> characteristics, CorrespondenceType type) {
//		return computeStdDev(characteristics, TypeCharacteristic::getFMeasureMacro, c -> {return c.getFMeasure();}, type);
//	}
//	
//	/**
//	 * Computes the relative distance of the matcher alignment to
//	 * the reference alignment of the gold standard, based on a <code>
//	 * CorrespondenceType</code>. First normalizes the matcher alignments to a 
//	 * target scale, then computes the sum of squared deviations of the confidence
//	 * values of the matcher and the reference alignment.
//	 * @param type - the correspondence type which should be used
//	 * @param normalize - specifies if the correspondence confidences should be normalized
//	 * @return realtive distance of matcher to reference alignment for the
//	 * specific <code>CorrespondenceType</code>
//	 */
//	public double getRelativeDistance(CorrespondenceType type, boolean normalize) {
//		List<Alignment> mappings = new ArrayList<>();
//		List<Alignment> references = new ArrayList<>();
//		mappings.add(alignmentMapping.get(type));
//		references.add(alignmentReference.get(type));
//		return getRelativeDistance(mappings, references, normalize);
//	}
//
//	/**
//	 * Computes the relative distance of the matcher alignments to the reference alignments 
//	 * of the gold standard for a specific <code>CorrespondenceType</code>, based on a collection 
//	 * of characteristics. First normalizes the  matcher alignments to a target scale, then computes 
//	 * the sum of squared deviations of the confidence values of the matcher and the reference alignment.
//	 * @param characteristics - the characteristics to compute the relative distance from
//	 * @param type - the correspondence type
//	 * @param normalize - state wether the confidences should be normalized
//	 * @return the realtive distance for a given <code>CorrespondenceType</code>
//	 */
//	public static double getRelativeDistance(List<TypeCharacteristic> characteristics, CorrespondenceType type, 
//			boolean normalize) {
//		List<Alignment> mappings = new ArrayList<>();
//		List<Alignment> references = new ArrayList<>();
//		for(TypeCharacteristic c : characteristics) {
//			mappings.add(c.getAlignmentMapping(type));
//			references.add(c.getAlignmentReference(type));
//		}
//		return getRelativeDistance(mappings, references, normalize);
//	}
//	
//}
