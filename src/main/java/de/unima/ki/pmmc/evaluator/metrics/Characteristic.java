package de.unima.ki.pmmc.evaluator.metrics;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;

/**
* Characterises the relation between two mappings in terms of recall, precision and f-value.
*/
public class Characteristic {
	
	private Alignment alignmentMapping;
	private Alignment alignmentReference;
	private Alignment alignmentCorrect;
	private boolean allowZeros;
	/**
	* If set to false, it uses jeromes way of counting. 
	*/
	private static boolean strictEvaluation = false;
	
	public static final int NUM_OF_ANNOTATORS = 8;
	
	/**
	* Constructs a characteristic based by comparing two mappings.
	* 
	* @param mapping The mapping under discussion.
	* @param reference The reference mapping.
	* @throws ALCOMOException Thrown if the namespaces of the mappings differ.
	*/
	public Characteristic(Alignment mapping, Alignment reference) {
		Alignment correct = new Alignment();
		if (strictEvaluation) {
			for (Correspondence r : reference) {
				for (Correspondence m : mapping) {
					if (m.equals(r)) {
						correct.add(r);
					}
				}
			}
		}
		else {
			for (Correspondence r : reference) {
				for (Correspondence m : mapping) {
					if (m.getUri1().equals(r.getUri1()) && m.getUri2().equals(r.getUri2())) {
						correct.add(r);
					}
				}
			}			
		}
		this.alignmentReference = reference;
		this.alignmentMapping = mapping;
		this.alignmentCorrect = correct;
	}	
	
	/**
	* Joins this mapping with another mapping by summing up relevant characteristics
	* in absolute numbers. Larger matching problems are thus to a greater extent weighted.
	*  
	* @param c The other charcteristic.
	*/
	public void join(Characteristic c) {
		this.alignmentCorrect.join(c.getAlignmentCorrect());
		this.alignmentReference.join(c.getAlignmentReference());
		this.alignmentMapping.join(c.getAlignmentMapping());
	}

	/**
	* Computes the f-measure based on the
	* initilaizing <code>Alignment</code>s
	* @return the f-measure
	*/
	public double getFMeasure() {
		if ((this.getPrecision() == 0.0f) || (this.getRecall() == 0.0f)) {
			return 0.0f; 
		}
		return (2 * this.getPrecision() * this.getRecall()) / (this.getPrecision() + this.getRecall());
	}
	
	/**
	 * Computes the non-binary F-Measure using
	 * the non-binary precision as well as the non-binary recall.
	 * @return the non-binary f-measure
	 */
	public double getNBFMeasure() {
		if ((this.getNBPrecision() == 0.0f) || (this.getNBRecall() == 0.0f)) { return 0.0f; }
		return (2 * this.getNBPrecision() * this.getNBRecall()) / (this.getNBPrecision() + this.getNBRecall());
	}

	/**
	 * Computes the f-measure based on the provided
	 * precision and recall values.
	 * @param precision the precision
	 * @param recall the recall
	 * @return the f-measure
	 */
	public static double computeFFromPR(double precision, double recall) {
		if ((precision == 0.0f) || (recall == 0.0f)) { return 0.0f; }
		return (2 * precision * recall) / (precision + recall);
	}
	
	/**
	* Computes the precision based on the
	* specified <code>Alignment</code>s.
	* @return the precision
	*/
	public double getPrecision() {
		return (double)this.alignmentCorrect.size() /  (double)this.alignmentMapping.size();
	}

	/**
	* Computes the non-binary precision based on the
	* specified <code>Alignment</code>s as the sum of confidences
	* of the correct <code>Correspondence</code>s divided by the same sum plus
	* the number of false positives (= sum of confidences of <code>Correspondence</code>s
	* not found by the matcher).
	* @return the non-binary precision
	*/
	public double getNBPrecision() {
		return this.getConfSumCorrect() / ((double) this.getFP().size() + this.getConfSumCorrect());
	}
	
	/**
	* Computes the recall based on the
	* specified <code>Alignment</code>s.
	* @return the recall
	*/
	public double getRecall() {
		return (double)this.alignmentCorrect.size() /  (double) this.alignmentReference.size();
	}
	
	/**
	* Computes the non-binary precision based on the
	* specified <code>Alignment</code>s as the sum of confidences
	* of the correct <code>Correspondence</code>s divided by the sum of
	* confidences of the reference </code>Correspondence</code>s.
	* @return the non-binary precision
	*/
	public double getNBRecall() {
		return this.getConfSumCorrect() / this.getConfSumReference();
	}
	
	public int getNumOfRulesCorrect() {
		return this.alignmentCorrect.size();
	}

	public int getNumOfRulesReference() {
		return this.alignmentReference.size();
	}

	public int getNumOfRulesMatcher() {
		return this.alignmentMapping.size();
	}

	/**
	 * Returns the true positives based on the
	 * specified <code>Alignment</code>s.
	 * @return the true positives as an <code>Alignment</code>
	 */
	public Alignment getTP() {
		return this.alignmentCorrect;
	}
	
	/**
	 * Returns the false positives based on the
	 * specified <code>Alignment</code>s.
	 * @return the false positives as an <code>Alignment</code>
	 */
	public Alignment getFP() {
		return this.alignmentMapping.minus(this.alignmentCorrect);
	}
	
	/**
	 * Returns the true positives based on the
	 * specified <code>Alignment</code>s.
	 * @return the true positives as an <code>Alignment</code>
	 */
	public Alignment getFN() {
		return this.alignmentReference.minus(this.alignmentCorrect);
	}
	
	private double getConfSumReference() {
		double sum = 0;
		for(Correspondence cRef : this.alignmentReference) {
			sum += cRef.getConfidence();
		}
		return sum;
	}

	private double getConfSumCorrect() {
		double sum = 0;
		for(Correspondence cCorr : this.alignmentCorrect) {
			sum += cCorr.getConfidence();
		}
		return sum;
	}
	
	/**
	 * Computes the correlation between matcher and
	 * reference <code>Alignment</code>.
	 * @param allowZeros allow zeros in correlation computation
	 * @return correlation between matcher and goldstandard
	 */
	public double getCorrelation(boolean allowZeros) {
		List<Alignment> mappings = new ArrayList<>();
		List<Alignment> references = new ArrayList<>();
		mappings.add(this.alignmentMapping);
		references.add(this.alignmentReference);
		return getCorrelation(mappings, references, allowZeros);
	}
	
	public double getSpearmanRangCorrelation() {
		List<Alignment> mappings = new ArrayList<>();
		List<Alignment> references = new ArrayList<>();
		mappings.add(this.alignmentMapping);
		references.add(this.alignmentReference);
		return getSpearmanRangCorrelation(mappings, references);
	}
	
	/**
	 * Computes the correlation between the specified <code>Alignment</code>s.
	 * Note that this method assumes that the <code>Alignment</code>s of matcher 
	 * and reference are in the same order. If allowZeros is set, also the false
	 * positives (= correspondences missing on reference side) as well as the false 
	 * negatives (= correspondences missing on matcher side) are included into the
	 * computation. Otherwise only the correspondences present in the reference <code>Alignment</code>
	 * are used for computation.
	 * @param mappings the alignments generated by the matcher
	 * @param references the alignments of the goldstandard
	 * @param allowZeros if set to true, includes correspondences of type FP and FN into computation,
	 * else only correspondences present in the reference are used 
	 * @return correlation of matcher and reference alignments
	 */
	public static double getCorrelation(List<Alignment> mappings, List<Alignment> references, boolean allowZeros) {
		if(mappings.size() != references.size()) {
			throw new IllegalArgumentException("Mapping and reference alignment length unequal. Mapping: " + mappings.size()
					+ " Reference: " + references.size());
		}
		//Compute the averages
		double avgGold = 0, avgMapper = 0;
		int numRef = 0, numJoinAlign = 0;
		for(int i = 0; i < mappings.size(); i++) {
			Alignment alignRef = references.get(i);
			Alignment alignMap = mappings.get(i);
			Alignment joinAlign = Alignment.join(alignRef, alignMap);
			numJoinAlign += joinAlign.size();
			numRef += alignRef.size();
			avgGold += alignRef.getCorrespondences().stream().
								mapToDouble(cRef -> {return cRef.getConfidence();}).
								sum();
			avgMapper += alignMap.getCorrespondences().stream().
					filter(cMap -> {return (allowZeros || alignRef.contained(cMap));}).
					mapToDouble(cMap -> {return cMap.getConfidence();}).sum();
		}
		if(allowZeros) {
			avgGold /= numJoinAlign;
			avgMapper /= numJoinAlign;
		} else {
			avgGold /= numRef;
			avgMapper /= numRef;
		}
		//Compute correlation
		double sumDev = 0;
		double sumSqDevGold = 0;
		double sumSqDevMapper = 0;
		for(int i = 0; i < mappings.size(); i++) {
			Alignment alignRef = references.get(i);
			Alignment alignMap = mappings.get(i);
			Alignment joinAlign = Alignment.join(alignRef, alignMap);
			for(Correspondence cCurr : joinAlign) {
				double cRefConf = 0;
				double cMapConf = 0;
				for(Correspondence cCurr1 : alignRef) {
					if(cCurr.equals(cCurr1)) {
						cRefConf = cCurr1.getConfidence();
						break;
					}
				}
				for(Correspondence cCurr1 : alignMap) {
					if(cCurr.equals(cCurr1)) {
						cMapConf = cCurr1.getConfidence();
						break;
					}
				}
				//Compute sum deviation
				if(allowZeros || cRefConf != 0) {
					sumDev += (cRefConf - avgGold) * (cMapConf - avgMapper);
					sumSqDevGold += Math.pow((cRefConf - avgGold), 2);
					sumSqDevMapper += Math.pow((cMapConf - avgMapper), 2);
				}
			}
		}
		return sumDev / (Math.sqrt(sumSqDevGold) * Math.sqrt(sumSqDevMapper));
	}
	
	
	/**
	 * Computes the spearman rang correlation coefficient based on given goldstandard
	 * <code>Alignment</code>s and the matcher <code>Alignment</code>s. Ranks the including
	 * <code>Correspondence</code>s ascending by the confidence value and computes normalized
	 * ranks. Then computes the correlation between both normalized rankings.
	 * @param mappings <code>Alignment</code>s produced by the matcher
	 * @param references <code>Alignment</code> of the goldstandard
	 * @return spearman rang correlation coefficient
	 */
	public static double getSpearmanRangCorrelation(List<Alignment> mappings, List<Alignment> references) {
		if(mappings.size() != references.size()) {
			throw new IllegalArgumentException("Mapping and reference alignment length unequal. Mapping: " + mappings.size()
					+ " Reference: " + references.size());
		}
		Alignment a = new Alignment();
		for (int i = 0; i < mappings.size(); i++) {
			a.add(Alignment.join(mappings.get(i), references.get(i)));
		}
		int N = a.size();
		List<Rank> normRangRef = computeNormalizedRang(computeRang(references, mappings, false));
		List<Rank> normRangMap = computeNormalizedRang(computeRang(mappings, references, true));
		double T_GS = computeT(normRangRef);
		double T_M = computeT(normRangMap);
		double sqDevSum = 0;
		for(Rank rankRef : normRangRef) {
			for(Rank rankMap : normRangMap) {
				if(rankRef.equals(rankMap)) {
					sqDevSum += Math.pow(rankRef.getNormalizedRank() - rankMap.getNormalizedRank(),2);
				}
			}
		}
		return getSpearRangCorrCoef(N, T_GS, T_M, sqDevSum);
	}

	/**
	 * Computes spearman rang correlation coefficient.
	 * @param N the number of annotations in the union set of goldstandard and matcher
	 * @param T_GS sum of deviations of count on goldstandard
	 * @param T_M sum of deviations of count on matcher
	 * @param sqDevSum sum of squared differences of the normalized rank between the matcher and goldstandard
	 * @return spearman rang correlation coefficient
	 */
	public static double getSpearRangCorrCoef(final int N, final double T_GS, 
			final double T_M, double sqDevSum) {
		return (Math.pow(N, 3) - N - 0.5*T_GS - 0.5*T_M - 6*sqDevSum) / 
				(Math.sqrt((Math.pow(N, 3) - N - T_GS) * (Math.pow(N, 3) - N - T_M)));
	}
	
	/**
	 * Computes the list of <code>Rank</code>s for the first list of <code>Alignment</code>
	 * required for spearman rang correlation coefficient.
	 * @param aligns1 first alignments
	 * @param aligns2 second alignments used to include missing correspondences in the first alignment
	 * @return <code>Rank</code>s for the first <code>Alignemnt<code>s
	 */
	private static List<Rank> computeRang(List<Alignment> aligns1, List<Alignment> aligns2, boolean matcher) {
		List<Rank> ranks = new ArrayList<>();
		for(Alignment a : aligns1) {
			for(Correspondence c : a) {
				if(matcher) {
					c.setConfidence(1);
				}
				ranks.add(new Rank(c));
			}
		}
		Alignment fp = new Alignment();
		for (int i = 0; i < aligns2.size(); i++) {
			fp.add(aligns2.get(i).minus(aligns1.get(i)));
		}
		for(Correspondence c : fp) {
			ranks.add(new Rank(new Correspondence(c.getUri1(), c.getUri2(), 0)));
		}
		Collections.sort(ranks);
		for (int i = 0; i < ranks.size(); i++) {
			ranks.get(i).setRank(i+1);
		}
		return ranks;
	}
	
	/**
	 * Computes normalized list of <code>Rank</code>s for
	 * spearman rang correlation coefficient.
	 * @param ranks the ranks to normalize
	 * @return list of <code>Rank</code>s with normalized rankings
	 */
	private static List<Rank> computeNormalizedRang(List<Rank> ranks) {
		double lastVal = ranks.get(0).getC().getConfidence();
		int min = 1;
		for (int i = 0; i <= ranks.size(); i++) {
			if(i == ranks.size() || ranks.get(i).getC().getConfidence() != lastVal) {
				double mean = getMean(min, i);
				while(min <= i) {
					ranks.get(min-1).setNormalizedRank(mean);
					min++;
				}
				if(i != ranks.size()) {
					lastVal = ranks.get(i).getC().getConfidence();										
				}
	
			}
		}
		return ranks;
	}
	
	/**
	 * Computes sum of count deviations from normalized ranks
	 * for spearman rang correlation coefficient.
	 * @param vals the ranks to compute T from
	 * @return sum of count deviations from normalized ranks
	 */
	private static double computeT(List<Rank> vals) {
		double result = 0;
		int cnt = 0;
		double lastVal = vals.get(0).getC().getConfidence();
		for (int i = 0; i <= vals.size(); i++) {
			if(i == vals.size() || vals.get(i).getC().getConfidence() != lastVal) {
				result += Math.pow(cnt, 3) - cnt;
				cnt = 0;
				if(i < vals.size()) {
					lastVal = vals.get(i).getC().getConfidence();					
				}
			}
			cnt++;
		}
		return result;
	}
	
	/**
	 * Computes the mean of the sum from min to max value.
	 * @param min the value to start the sum with
	 * @param max the value to end the sum with
	 * @return mean of the given interval
	 */
	private static double getMean(int min, int max) {
		if(min > max) {
			return -1;
		}
		int curr = min;
		double result = 0;
		while(curr <= max) {
			result += curr;
			curr++;
		}
		return result / ((max+1)-min);
	}
	
	
	/**
	 * Computes the micro spearman rang correlation coefficient across the <code>Characteristc</code>s.
	 * @param characteristics the characteristics to compute the micro spearman rang correlation coefficient
	 * @return micro spearman rang correlation coefficient of multiple <code>Characterisitic</code>s
	 */
	public static double getSpearRangCorrMicro(List<? extends Characteristic> characteristics) {
		List<Alignment> mappings = new ArrayList<>();
		List<Alignment> references = new ArrayList<>();
		for(Characteristic c : characteristics) {
			mappings.add(c.getAlignmentMapping());
			references.add(c.getAlignmentReference());
		}
		return getSpearmanRangCorrelation(mappings, references);
	}

	
	/**
	 * Computes the macro spearman rang correlation coefficient across the <code>Characteristc</code>s.
	 * @param characteristics the characteristics to compute the macro spearman rang correlation coefficient
	 * @return macro spearman rang correlation coefficient of multiple <code>Characterisitic</code>s
	 */
	public static double getSpearRangCorrMacro(List<Characteristic> characteristics) {
		return computeMacro(characteristics, c -> {return c.getSpearmanRangCorrelation();});
	}
	
	/**
	 * Computes the micro correlation across the <code>Characteristc</code>s.
	 * @param characteristics the characteristics to compute the micro correlation from
	 * @param allowZeros allow zeros in correlation computation
	 * @return micro correlation of multiple <code>Characterisitic</code>s
	 */
	public static double getCorrelationMicro(List<? extends Characteristic> characteristics, boolean allowZeros) {
		List<Alignment> mappings = new ArrayList<>();
		List<Alignment> references = new ArrayList<>();
		for(Characteristic c : characteristics) {
			mappings.add(c.getAlignmentMapping());
			references.add(c.getAlignmentReference());
		}
		return getCorrelation(mappings, references, allowZeros);
	}

	
	/**
	 * Computes the macro correlation as the plain average of
	 * the sum of correlations across the <code>Characteristc</code>s.
	 * @param characteristics the characteristics to compute the macro correlation from
	 * @param allowZeros allow zeros in correlation computation
	 * @return macro correlation of multiple <code>Characterisitic</code>s
	 */
	public static double getCorrelationMacro(List<Characteristic> characteristics, boolean allowZeros) {
		return computeMacro(characteristics, c -> {return c.getCorrelation(allowZeros);});
	}
	
	
	/**
	 * General purpose method for computing micro summary
	 * of values provided by the two function parameters. That is,
	 * over a list of <code>Characteristic</code>s, this method computes
	 * the division with the sum of single values of the first function in
	 * the numerator, and the the sum of single values of the second function
	 * in the denominator. This method avoids boilerplate code and follows DRY.
	 * @param characteristics the characteristics to compute the micro summary from
	 * @param functionNum function for producing values from a single <code>Characteristic</code>
	 * for the numerator sum
	 * @param functionDenom function for producing values from a single <code>Characteristic</code>
	 * for the denominator sum
	 * @return sum{functionNumVals} / sum{functionDenomVals}
	 */
	private static double computeMicro(List<? extends Characteristic> characteristics,
			Function<Characteristic, Double> functionNum, Function<Characteristic, Double> functionDenom) {
		double sumNum = 0;
		double sumDenom = 0;
		for(Characteristic c : characteristics) {
			sumNum += functionNum.apply(c);
			sumDenom += functionDenom.apply(c);
			System.out.println();
		}
		return sumNum / sumDenom;
	}
	
	/**
	 * General purpose method for computing macro summary of
	 * values provided by the single input function. Simply sums up
	 * the individual values provided by the function for each single
	 * <code>Characteristic</code> and finally computes the average.
	 * @param characteristics the characteristics to compute the macro summary from
	 * @param function function for producing values from a single <code>Characteristic</code>
	 * @return the average as sum{functionVals} / numOfVals
	 */
	private static double computeMacro(List<? extends Characteristic> characteristics, 
			Function<Characteristic, Double> function) {
		double sum = 0;
		int numOfOcc = 0;
		for(Characteristic c : characteristics) {
			double currPrecision = function.apply(c);
			if(!Double.isNaN(currPrecision)) {
				sum += currPrecision;
				numOfOcc++;
			}
		}
		return sum / numOfOcc;
	}
	
	/**
	 * Computes the standard deviation over a list of <code>Characteristic</code>s
	 * based on values provided by the two input functions. The first function is
	 * used to compute the average value for the <code>Characteristic</code> collection,
	 * the second function provides the current value for a single <code>Characteristic</code>
	 * @param characteristics the characteristics to compute the standard deviation from
	 * @param functionAvg function for producing the average value to compare the single/current
	 * values against
	 * @param functionSum function for producing the current value for a single <code>Characteristic</code>
	 * @return the standard deviation based on the provided functions
	 */
	private static double computeStdDev(List<? extends Characteristic> characteristics, 
			Function<List<? extends Characteristic>, Double> functionAvg, Function<Characteristic, Double> functionSum) {
		double avgMacro = functionAvg.apply(characteristics);
		double dev = 0;
		int numOfOcc = 0;
		for(Characteristic c : characteristics) {
			double currVal = functionSum.apply(c);
			if(!Double.isNaN(currVal)) {
				double currDev = Math.abs(currVal - avgMacro);
				dev += Math.pow(currDev, 2);	
				numOfOcc++;
			}
		}
		return Math.sqrt(dev/numOfOcc);
	}
	
	/**
	 * Compute the macro precision over a list of characteristics. The
	 * macro precision is the average of all the precision values of
	 * all characteristics. Note that this metric can be easily biased
	 * if the testsets are not equally large.
	 * @param characteristics - the characteristic to compute the macro precision from
	 * @return macro precision
	 */
	public static double getNBPrecisionMacro(List<? extends Characteristic> characteristics) {
		return computeMacro(characteristics, c -> {return c.getNBPrecision();});
	}
	
	/**
	 * Compute the macro recall over a list of characteristics. The
	 * macro recall is the average of all the recall values of
	 * all characteristics. Note that this metric can be easily biased
	 * if the testsets are not equally large.
	 * @param characteristics - the characteristic to compute the macro recall from
	 * @return macro recall
	 */
	public static double getNBRecallMacro(List<? extends Characteristic> characteristics) {
		return computeMacro(characteristics, c -> {return c.getNBRecall();});
	}
	
	/**
	 * Compute the micro precision over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the micro precision from
	 * @return micro precision
	 */
	public static double getNBPrecisionMicro(List<? extends Characteristic> characteristics) {
		return computeMicro(characteristics, c -> {return c.getConfSumCorrect();},
				c -> {return c.getFP().size() + c.getConfSumCorrect();});
	}
	
	/**
	 * Compute the micro recall over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the micro recall from
	 * @return micro recall
	 */
	public static double getNBRecallMicro(List<? extends Characteristic> characteristics) {
		return computeMicro(characteristics, c -> {return c.getConfSumCorrect();}, 
				c -> {return c.getConfSumReference();});
	}
	
	/**
	 * Compute the macro f measure over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the f measure from
	 * @return macro f measure
	 */
	public static double getNBFMeasureMacro(List<? extends Characteristic> characteristics) {
		return computeMacro(characteristics, c -> {return c.getNBFMeasure();});
	}
	
	/**
	 * Compute the micro f measure over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the f measure from
	 * @return micro f measure
	 */
	public static double getNBFMeasureMicro(List<? extends Characteristic> characteristics) {
		double confSumRef = 0;
		double confSumCorr = 0;
		int numfp = 0;
		for(Characteristic c : characteristics) {
			confSumRef += c.getConfSumReference();
			confSumCorr += c.getConfSumCorrect();
			numfp += c.getFP().size();
		}
		double recall = confSumCorr / confSumRef;
		double precision = confSumCorr / ((double)numfp + confSumCorr) ;
		double fmeasure = Characteristic.computeFFromPR(precision, recall);
		return fmeasure;
	}
	
	/**
	 * Computes the standard deviation of the non-binary precision over
	 * a list of characteristics. As a reference for the average value,
	 * the non-binary macro precision is used.
	 * @param characteristics the characteristics to compute the standard deviation of the non-binary precision
	 * @return standard deviation of non-binary precision
	 */
	public static double getNBPrecisionStdDev(List<? extends Characteristic> characteristics) {
		return computeStdDev(characteristics, Characteristic::getNBPrecisionMacro, c -> {return c.getNBPrecision();});
	}
	
	/**
	 * Computes the standard deviation of the non-binary recall over
	 * a list of characteristics. As a reference for the average value,
	 * the non-binary macro recall is used.
	 * @param characteristics the characteristics to compute the standard deviation of the non-binary recall
	 * @return standard deviation of non-binary recall
	 */
	public static double getNBRecallStdDev(List<? extends Characteristic> characteristics) {
		return computeStdDev(characteristics, Characteristic::getNBRecallMacro, c -> {return c.getNBRecall();});
	}
	
	/**
	 * Computes the standard deviation of the non-binary f-measure over
	 * a list of characteristics. As a reference for the average value,
	 * the non-binary f-measure is used.
	 * @param characteristics the characteristics to compute the standard deviation of the non-binary f-measure
	 * @return standard deviation of non-binary f-measure
	 */
	public static double getNBFMeasureStdDev(List<? extends Characteristic> characteristics) {
		return computeStdDev(characteristics, Characteristic::getNBFMeasureMacro, c -> {return c.getNBFMeasure();});
	}
	
	/**
	 * Compute the macro precision over a list of characteristics. The
	 * macro precision is the average of all the precision values of
	 * all characteristics. Note that this metric can be easily biased
	 * if the test sets are not equally large.
	 * @param characteristics - the characteristic to compute the macro precision from
	 * @return macro precision
	 */
	public static double getPrecisionMacro(List<? extends Characteristic> charactersticstics) {
		return computeMacro(charactersticstics, c -> {return c.getPrecision();});
	}
	
	/**
	 * Compute the macro recall over a list of characteristics. The
	 * macro recall is the average of all the recall values of
	 * all characteristics. Note that this metric can be easily biased
	 * if the testsets are not equally large.
	 * @param characteristics - the characteristic to compute the macro recall from
	 * @return macro recall
	 */
	public static double getRecallMacro(List<? extends Characteristic> charactersticstics) {
		return computeMacro(charactersticstics, c -> {return c.getRecall();});
	}
	
	/**
	 * Compute the micro precision over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the micro precision from
	 * @return micro precision
	 */
	public static double getPrecisionMicro(List<? extends Characteristic> characteristics) {
		return computeMicro(characteristics, c -> {return (double) c.getNumOfRulesCorrect();},
				c -> {return (double) c.getNumOfRulesMatcher();});
	}
	
	/**
	 * Compute the micro recall over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the micro recall from
	 * @return micro recall
	 */
	public static double getRecallMicro(List<? extends Characteristic> characteristics) {
		return computeMicro(characteristics, c -> {return (double) c.getNumOfRulesCorrect();},
				c -> {return (double) c.getNumOfRulesReference();});
	}
	
	/**
	 * Compute the macro f measure over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the f measure from
	 * @return macro f measure
	 */
	public static double getFMeasureMacro(List<? extends Characteristic> characteristics) {
		return computeMacro(characteristics, c -> {return c.getFMeasure();});
	}
	
	/**
	 * Compute the micro f measure over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics the characteristic to compute the f measure from
	 * @return micro f measure
	 */
	public static double getFMeasureMicro(List<? extends Characteristic> characteristics) {
		int sumNumOfMatcher = 0;
		int sumNumOfGold = 0;
		int sumNumOfCorrect = 0;
		for(Characteristic c : characteristics) {
			sumNumOfMatcher += c.getNumOfRulesMatcher();
			sumNumOfGold += c.getNumOfRulesReference();
			sumNumOfCorrect += c.getNumOfRulesCorrect();
		}
		return Characteristic.computeFFromPR((sumNumOfCorrect / (double) sumNumOfMatcher), 
				(sumNumOfCorrect / (double) sumNumOfGold));
	}
	
	/**
	 * Computes the standard deviation of the precision over
	 * a list of characteristics. As a reference for the average value,
	 * the macro precision is used.
	 * @param characteristics the characteristics to compute the standard deviation of the precision
	 * @return standard deviation of precision
	 */
	public static double getPrecisionStdDev(List<? extends Characteristic> characteristics) {
		return computeStdDev(characteristics, Characteristic::getPrecisionMacro, c -> {return c.getPrecision();});
	}
	
	/**
	 * Computes the standard deviation of the recall over
	 * a list of characteristics. As a reference for the average value,
	 * the macro recall is used.
	 * @param characteristics the characteristics to compute the standard deviation of the recall
	 * @return standard deviation of recall
	 */
	public static double getRecallStdDev(List<? extends Characteristic> characteristics) {
		return computeStdDev(characteristics, Characteristic::getRecallMacro, c -> {return c.getRecall();});
	}
	
	/**
	 * Computes the standard deviation of the f-measure over
	 * a list of characteristics. As a reference for the average value,
	 * the macro f-measure is used.
	 * @param characteristics the characteristics to compute the standard deviation of the f-measure
	 * @return standard deviation of f-measure
	 */
	public static double getFMeasureStdDev(List<? extends Characteristic> characteristics) {
		return computeStdDev(characteristics, Characteristic::getFMeasureMacro, c -> {return c.getFMeasure();});
	}
	
	/**
	 * Computes the relative distance of the matcher alignment to
	 * the reference alignment of the gold standard, based on a single
	 * characteristic. First normalizes the matcher alignments to a 
	 * target scale, then computes the sum of squared deviations of the confidence
	 * values of the matcher and the reference alignment.
	 * @param characteristic - the characteristic to obtain matcher and reference alignment
	 * @param normalize - specifies if the correspondence confidences should be normalized
	 * @return realtive distance of matcher to reference alignment
	 */
	public double getRelativeDistance(boolean normalize) {
		List<Alignment> mappings = new ArrayList<>();
		List<Alignment> references = new ArrayList<>();
		mappings.add(alignmentMapping);
		references.add(alignmentReference);
		return getRelativeDistance(mappings, references, normalize);
	}
	
	/**
	 * Computes the relative distance of the matcher alignments to
	 * the reference alignments of the gold standard, based on a collection
	 * of characteristics. First normalizes the matcher alignments to a 
	 * target scale, then computes the sum of squared deviations of the confidence
	 * values of the matcher and the reference alignment.
	 * @param characteristics - the characteristics to obtain the matcher and reference alignments
	 * @param normalize - specifies if the correspondence confidences should be normalized
	 * @return realtive distance of matcher to reference alignment
	 */
	public static double getRelativeDistance(List<? extends Characteristic> characteristics, boolean normalize) {
		List<Alignment> mappings = new ArrayList<>();
		List<Alignment> references = new ArrayList<>();
		for(Characteristic c : characteristics) {
			mappings.add(c.getAlignmentMapping());
			references.add(c.getAlignmentReference());
		}
		return getRelativeDistance(mappings, references, normalize);
	}
	
	/**
	 * Computes the relative distance of the matcher alignments to
	 * the reference alignments of the gold standard, based on a collection
	 * of characteristics. First normalizes the matcher alignments to a 
	 * target scale, then computes the sum of squared deviations of the confidence
	 * values of the matcher and the reference alignment.
	 * @param characteristics - the characteristics to obtain the matcher and reference alignments
	 * @param normalize - specifies if the correspondence confidences should be normalized
	 * @return realtive distance of matcher to reference alignment
	 */
	public static double getRelativeDistance(List<Alignment> mappings, List<Alignment> references, boolean normalize) {
		if(mappings.size() != references.size()) {
			throw new IllegalArgumentException("Mapping and reference alignment length unequal. Mapping: " + mappings.size()
					+ " Reference: " + references.size());
		}
		List<Alignment> newAlignment = mappings;
		if(normalize) {
			newAlignment = getNormalizedAlignments(mappings);
		}
		final double POW_NORM = 1.5;
		final long POW_CONST = Math.round(NUM_OF_ANNOTATORS/POW_NORM);
		double sum = 0;
		for(Alignment a1 : newAlignment) {
			for(Correspondence cMap : a1) {
				Correspondence cRef = null;
				for(Alignment a2 : references) {
					for(Correspondence cCurr : a2) {
						if(cMap.equals(cCurr)) {
							cRef = cCurr;
							break;
						}
					}
			}
			double confRef = (cRef != null) ? cRef.getConfidence() : 0;
			double sqDev;
			if(!isFirstLineAlignment(mappings)) {
				double delta = (confRef!=0) ? confRef : 1; 
				sqDev = delta * Math.abs(Math.pow(cMap.getConfidence() - confRef, POW_CONST));
			} else {
				sqDev = Math.pow(cMap.getConfidence() - confRef, 2);
			}
			sum += sqDev;
			}
		}
		//Increase sum by squared deviation of correspondences not found by the matcher but
		//present in the reference alignment
		for(int i = 0; i < references.size(); i++) {
			Alignment alignOnlyRef = references.get(i).minus(mappings.get(i));
			for(Correspondence cOnlyRef : alignOnlyRef) {
				if(!isFirstLineAlignment(mappings)) {
					sum += Math.abs(cOnlyRef.getConfidence() * Math.pow(cOnlyRef.getConfidence(), POW_CONST));
				} else {
					sum += Math.abs(Math.pow(cOnlyRef.getConfidence(), 2));
				}
			}
		}
		return sum;
	}
	
	/**
	 * Normalizes each <code>Correspondence</code> from a collection 
	 * of <code>Alignment</code>s to a target range.
	 * Note that First Line Matcher (FLM) producing various confidence
	 * values for the correspondences, have target range [0.125,1]. Second Line Matcher
	 * (SLM) producing only binary confidence values, are mapped to 0 and 1.
	 * @param alignments the alignments which should be normalized
	 * @return a new copy of the alignment list containing normalized correspondences
	 */
	public static List<Alignment> getNormalizedAlignments(List<Alignment> alignments) {
		if(alignments.isEmpty()) {
			return Collections.emptyList();
		}
		List<Alignment> vals = Alignment.newInstance(alignments);
		List<Correspondence> allCorres = new ArrayList<>();
		for(Alignment alignment : vals) {
			allCorres.addAll(alignment.getCorrespondences());
		}
		if(allCorres.isEmpty()) {
			return Collections.emptyList();
		}
		double maxConf = Collections.max(allCorres).getConfidence();
		//Normalize FLM confidences between 0.125 and 1
		if(isFirstLineAlignment(alignments)) {
			for(Correspondence c : allCorres) {
				c.setConfidence(c.getConfidence() / maxConf);
			}
			double minConf = Collections.min(allCorres).getConfidence();
			final double TARGET_MAX = 1;
			final double TARGET_MIN = 0.125;
			double mult = (TARGET_MAX - TARGET_MIN) / (1 - minConf);
			for(Correspondence c : allCorres) {
				double finalConf = 1 - mult * (1 - c.getConfidence());
				c.setConfidence(finalConf);
			}
		} 
//		//Normalize SLM confidences to 0 and 1
		else {
			for(Correspondence c : allCorres) {
				c.setConfidence((c.getConfidence()>0) ? 1 : 0);
			}
		}
		return vals;
	}
	
	
	/**
	 * Checks weather the matcher, which produced the <code>Alignment</code>s in the each
	 * <code>Characteristic</code>, produces only one single confidence value based on a 
	 * collection of characteristics. 
	 * @param characteristics specifying the alignments produced by the matcher
	 * @return true if the Matcher is a First Line Matcher (FLM), false if the Matcher
	 * is a Second Line Matcher (SLM)
	 */
	public static boolean isFirstLineMatcher(List<Characteristic> characteristics) {
		List<Alignment> alignments = new ArrayList<>();
		for(Characteristic c : characteristics) {
			alignments.add(c.getAlignmentMapping());
		}
		return isFirstLineAlignment(alignments);
	}
	
	/**
	 * Checks weather the matcher, which produced the <code>Alignment</code>s, 
	 * produces only one single confidence value. 
	 * @param alignments specifying the alignments produced by the matcher
	 * @return true if the Matcher is a First Line Matcher (FLM), false if the Matcher
	 * is a Second Line Matcher (SLM)
	 */
	public static boolean isFirstLineAlignment(List<Alignment> alignments) {
//		for(Alignment alignment : alignments) {
//			if(isFirstLineAlignment(alignment)) {
//				return true;
//			}
//		}
//		return false;
		List<Correspondence> corres = new ArrayList<>();
		for(Alignment a : alignments) {
			corres.addAll(a.getCorrespondences());
		}
		long numDistinctVals = corres.
				stream().
				mapToDouble(c -> c.getConfidence()).
				distinct().count();
//		OptionalDouble minConf = corres.
//				stream().
//				mapToDouble(c -> c.getConfidence()).
//				distinct().min();
		return numDistinctVals > 1 ;//&& minConf.getAsDouble() < 0.70;
	}
	
	/**
	 * Checks weather the matcher, which produced the <code>Alignment</code>, 
	 * produces only one single confidence value. 
	 * @param alignment specifying the alignment produced by the matcher
	 * @return true if the Matcher is a First Line Matcher (FLM), false if the Matcher
	 * is a Second Line Matcher (SLM)
	 */
	public static boolean isFirstLineAlignment(Alignment alignment) {
		long numDistinctVals = alignment.getCorrespondences().
				stream().
				mapToDouble(c -> c.getConfidence()).
				distinct().count();
		OptionalDouble minConf = alignment.getCorrespondences().
				stream().
				mapToDouble(c -> c.getConfidence()).
				distinct().min();
		return numDistinctVals > 1;
	}
	

	/**
	 * Checks wether the matcher, which produced the alignment hold by
	 * this characteristic, only generates one single confidence value
	 * (Second Line Matcher).
	 * @return true if the Matcher is a First Line Matcher, false if
	 * the Matcher is a Second Line Matcher
	 */
	public boolean isFirstLineMatcher() {
		return isFirstLineAlignment(this.alignmentMapping);
	}
	

	public boolean isAllowZeros() {
		return allowZeros;
	}

	public void setAllowZeros(boolean allowZeros) {
		this.allowZeros = allowZeros;
	}

	/**
	 * Returns a copy of the <code>Alignment</code> generated
	 * by the matcher.
	 * @return matcher alignment
	 */
	public Alignment getAlignmentMapping() {
		return Alignment.newInstance(alignmentMapping);
	}

	/**
	 * Returns a copy of the <code>Alignment</code>
	 * of the goldstandard.
	 * @return matcher alignment
	 */
	public Alignment getAlignmentReference() {
		return Alignment.newInstance(alignmentReference);
	}

	/**
	 * Returns a copy of the <code>Alignment</code>
	 * identified as correct. Therefore, the returned
	 * <code>Alignment</code> is the intersection of matcher
	 * and goldstandard <code>Alignment</code>
	 * @return matcher alignment
	 */
	public Alignment getAlignmentCorrect() {
		return Alignment.newInstance(alignmentCorrect);
	}
	
	public static void setDiffuseEvaluation(boolean strictEvaluation) {
		Characteristic.strictEvaluation = strictEvaluation;
	}

	public static boolean strictEvaluationActive() {
		return strictEvaluation;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Precision: " + (100.0 * this.getPrecision()) + "%\n");
		sb.append("Recall:    " + (100.0 * this.getRecall()) + "%\n");
		sb.append("F-measure: " + (100.0 * this.getFMeasure()) + "%\n");
		sb.append("NB-Precision: " + (100.0 * this.getNBPrecision()) + "%\n");
		sb.append("NB-Recall:    " + (100.0 * this.getNBRecall()) + "%\n");
		sb.append("NB-F-measure: " + (100.0 * this.getNBFMeasure()) + "%\n");
		sb.append("Correlation: " + (this.getCorrelation(this.allowZeros)) + "\n");
		sb.append("Gold: " + this.alignmentReference.size() + " Matcher: " + 
					this.alignmentMapping.size() +  " Correct: " + this.alignmentCorrect.size() + "\n");
		return sb.toString();
	}
	
	public String toShortDesc() {
		double precision = this.getPrecision();
		double recall = this.getRecall();
		double f = this.getFMeasure();
		double nbPrecision = this.getNBPrecision();
		double nbRecall = this.getNBRecall();
		double nbF = this.getNBFMeasure();
		double correlation = this.getCorrelation(this.allowZeros);
		return toDecimalFormat(precision) + "\t" + toDecimalFormat(recall) + "\t" + toDecimalFormat(f)
				+ toDecimalFormat(nbPrecision) + "\t" + toDecimalFormat(nbRecall) + "\t" + toDecimalFormat(nbF)
				+ "\t" + toDecimalFormat(correlation);
	}

	private static String toDecimalFormat(double value) {
		DecimalFormat df = new DecimalFormat("0.000");
		return df.format(value).replace(',', '.');
	}

}
