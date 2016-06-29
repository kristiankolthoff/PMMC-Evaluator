// *****************************************************************************
//
// Copyright (c) 2011 Christian Meilicke (University of Mannheim)
//
// Permission is hereby granted, free of charge, to any person
// obtaining a copy of this software and associated documentation
// files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge,
// publish, distribute, sublicense, and/or sell copies of the Software,
// and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
// IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
// *********************************************************************************

package de.unima.ki.pmmc.evaluator.metrics;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.unima.ki.pmmc.evaluator.alignment.Alignment;
import de.unima.ki.pmmc.evaluator.alignment.Correspondence;




/**
* Characterises the relation between two mappings in terms of recall, precision and f-value.
*/
public class Characteristic {
	
	private int numOfRulesGold;
	private int numOfRulesMatcher;
	private int numOfRulesCorrect;
	private Alignment alignmentMapping;
	private Alignment alignmentReference;
	protected Alignment alignmentCorrect;
	//TODO: note this can cause nullpointerexceptions
	private boolean allowZeros;
	
	/**
	* If set to false, it uses jeromes way of counting. 
	*/
	private static boolean strictEvaluation = false;
	
	public static final double THRESHOLD = 0.500;
	
	/**
	* Constructs an empty characteristic which is a characteristic for an mapping of cardinality zero. 
	*
	*/
	public Characteristic() {
		this(0,0,0);
	}
	
	/**
	* Constructs a characteristic.
	* 
	* @param numOfRulesGold Number of correspondences of the reference mapping.
	* @param numOfRulesMatcher Number of correspondences in the mapping under discussion
	* (in most cases the mapping generated by a matching system). 
	* @param numOfRulesCorrect Number of correspondences that are both the reference mapping and the 
	* generated mapping.
	*/
	public Characteristic(int numOfRulesGold, int numOfRulesMatcher, int numOfRulesCorrect) {
		this.numOfRulesGold = numOfRulesGold;
		this.numOfRulesMatcher = numOfRulesMatcher;
		this.numOfRulesCorrect = numOfRulesCorrect;
	}
	
	/**
	* Constructs a characteristic based by comparing two mappings.
	* 
	* @param mapping The mapping under discussion.
	* @param reference The reference mapping.
	* @throws ALCOMOException Thrown if the namespaces of the mappings differ.
	*/
	public Characteristic(Alignment mapping, Alignment reference) {
		// Mapping correct = reference.getIntersection(mapping); 
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
		this.numOfRulesGold = reference.size();
		this.numOfRulesMatcher = mapping.size();
		this.numOfRulesCorrect = correct.size();
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
		this.numOfRulesCorrect += c.getNumOfRulesCorrect();
		this.numOfRulesGold += c.getNumOfRulesGold();
		this.numOfRulesMatcher += c.getNumOfRulesMatcher();
	}
	
	/**
	* Returns a string representation. 
	* 
	* @return A string representation.
	*/
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Precision: " + (100.0 * this.getPrecision()) + "%\n");
		sb.append("Recall:    " + (100.0 * this.getRecall()) + "%\n");
		sb.append("F-measure: " + (100.0 * this.getFMeasure()) + "%\n");
		sb.append("NB-Precision: " + (100.0 * this.getNBPrecision()) + "%\n");
		sb.append("NB-Recall:    " + (100.0 * this.getNBRecall()) + "%\n");
		sb.append("NB-F-measure: " + (100.0 * this.getNBFMeasure()) + "%\n");
		sb.append("Correlation: " + (100.0 * this.getCorrelation(this.allowZeros)) + "%\n");
		sb.append("Gold: " + this.numOfRulesGold + " Matcher: " + numOfRulesMatcher +  " Correct: " + numOfRulesCorrect + "\n");
		return sb.toString();
	}

	/**
	* Returns the f-measure.
	* @return The f-measure.
	*/
	public double getFMeasure() {
		if ((this.getPrecision() == 0.0f) || (this.getRecall() == 0.0f)) { return 0.0f; }
		return (2 * this.getPrecision() * this.getRecall()) / (this.getPrecision() + this.getRecall());
	}
	
	public double getNBFMeasure() {
		if ((this.getNBPrecision() == 0.0f) || (this.getNBRecall() == 0.0f)) { return 0.0f; }
		return (2 * this.getNBPrecision() * this.getNBRecall()) / (this.getNBPrecision() + this.getNBRecall());
	}

	public static double computeFFromPR(double precision, double recall) {
		if ((precision == 0.0f) || (recall == 0.0f)) { return 0.0f; }
		return (2 * precision * recall) / (precision + recall);
	}
	
	/**
	 * 
	 * @param n1 number of rules from the matcher
	 * @param n2 number of rules from gold standard
	 * @return recall
	 */
	public static double computeRecall(int n1, int n2) {
		return (n1/(double)n2);
	}
	
	
	public String getF() {
		return toDecimalFormat(this.getFMeasure());
	}
	
	/**
	* Returns the precision.
	* 
	* @return The precision.
	*/
	public double getPrecision() {
		return (double)this.numOfRulesCorrect /  (double)this.numOfRulesMatcher;
	}

	/**
	* Returns the precision.
	* 
	* @return The precision.
	*/
	public double getNBPrecision() {
		return this.getConfSumCorrect() / ((double) this.getFalsePositives().size() + this.getConfSumCorrect());
	}
	
	
	public String getNBP() {
		return toDecimalFormat(this.getNBPrecision());
	}
	
	public String getP() {
		return toDecimalFormat(this.getPrecision());
	}
	
	public double getRecall() {
		return (double)this.numOfRulesCorrect /  (double)this.numOfRulesGold;
	}
	
	/**
	* Returns the recall.
	* 
	* @return The recall.
	*/
	public double getNBRecall() {
		return this.getConfSumCorrect() / this.getConfSumReference();
	}
	
	public String getNBR() {
		return toDecimalFormat(this.getNBRecall());
	}
	
	public String getR() {
		return toDecimalFormat(this.getRecall());
	}

	
	public int getNumOfRulesCorrect() {
		return numOfRulesCorrect;
	}

	public int getNumOfRulesGold() {
		return numOfRulesGold;
	}

	public int getNumOfRulesMatcher() {
		return numOfRulesMatcher;
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

	private static String toDecimalFormat(double precision) {
		DecimalFormat df = new DecimalFormat("0.000");
		return df.format(precision).replace(',', '.');
	}

	public static void useDiffuseEvaluation() {
		strictEvaluation = false;
		
	}

	public static boolean strictEvaluationActive() {
		return strictEvaluation;
	}
	
	public Alignment getTruePositives() {
		return this.alignmentCorrect;
	}
	
	public Alignment getFalsePositives() {
		return this.alignmentMapping.minus(this.alignmentCorrect);
	}
	
	public Alignment getFalseNegatives() {
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
	 * 
	 * @param allowZeros
	 * @return
	 */
	public double getCorrelation(boolean allowZeros) {
		Alignment joinAlign = Alignment.join(this.alignmentReference, this.alignmentMapping);
		//Compute the averages
		double avgGold = 0, avgMapper = 0;
		for(Correspondence cRef :this.alignmentReference) {
				avgGold += cRef.getConfidence();
		}
		for(Correspondence c : joinAlign) {
			for(Correspondence cMap :this.alignmentMapping) {
				if(c.equals(cMap) && (allowZeros || this.alignmentReference.contained(cMap))) {
					avgMapper += cMap.getConfidence();
					break;
				}
			}
		}
		if(allowZeros) {
			avgMapper /= joinAlign.size();
			avgGold /= joinAlign.size();
		} else {
			avgGold /= this.alignmentReference.size();
			avgMapper /= this.alignmentReference.size();
		}
		//Compute correlation
		double sumDev = 0;
		double sumSqDevGold = 0;
		double sumSqDevMapper = 0;
		for(Correspondence c : joinAlign) {
			double cRefConf = 0;
			double cMapConf = 0;
			for(Correspondence cCurr : this.alignmentReference) {
				if(c.equals(cCurr)) {
					cRefConf = cCurr.getConfidence();
					break;
				}
			}
			for(Correspondence cCurr : this.alignmentMapping) {
				if(c.equals(cCurr)) {
					cMapConf = cCurr.getConfidence();
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
		return sumDev / (Math.sqrt(sumSqDevGold) * Math.sqrt(sumSqDevMapper));
	}
	
	public static double getCorrelationMicro(List<Characteristic> characteristics, boolean allowZeros) {
		//Compute the averages
		double avgGold = 0, avgMapper = 0;
		int numRef = 0, numJoinAlign = 0;
		for(Characteristic c : characteristics) {
			Alignment alignRef = c.getAlignmentReference();
			Alignment alignMap = c.getAlignmentMapping();
			Alignment joinAlign = Alignment.join(alignRef, alignMap);
			numJoinAlign += joinAlign.size();
			numRef += alignRef.size();
			for(Correspondence cRef : alignRef) {
					avgGold += cRef.getConfidence();
			}
			for(Correspondence cCurr : joinAlign) {
				for(Correspondence cMap : alignMap) {
					if(cCurr.equals(cMap) && (allowZeros || alignRef.contained(cMap))) {
						avgMapper += cMap.getConfidence();
						break;
					}
				}
			}
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
		for(Characteristic c : characteristics) {
			Alignment alignRef = c.getAlignmentReference();
			Alignment alignMap = c.getAlignmentMapping();
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
	 * 
	 * @param characteristics
	 * @param allowZeros
	 * @return
	 */
	public static double getCorrelationMacro(List<Characteristic> characteristics, boolean allowZeros) {
		double sum = 0;
		int num = 0;
		for(Characteristic c : characteristics) {
			double corr = c.getCorrelation(allowZeros);
			if(!Double.isNaN(corr)) {
				sum += corr;
				num++;
			}
		}
		return sum / num;
	}
	
	/**
	 * Compute the macro precision over a list of characteristics. The
	 * macro precision is the average of all the precision values of
	 * all characteristics. Note that this metric can be easily biased
	 * if the testsets are not equally large.
	 * @param characteristics - the characteristic to compute the macro precision from
	 * @return macro precision
	 */
	public static double getNBPrecisionMacro(List<Characteristic> characteristics) {
		double sum = 0;
		int numOfOcc = 0;
		for(Characteristic c : characteristics) {
			double currPrecision = c.getNBPrecision();
			if(!Double.isNaN(currPrecision)) {
				sum += currPrecision;
				numOfOcc++;
			}
		}
		return sum / numOfOcc;
	}
	
	/**
	 * Compute the macro recall over a list of characteristics. The
	 * macro recall is the average of all the recall values of
	 * all characteristics. Note that this metric can be easily biased
	 * if the testsets are not equally large.
	 * @param characteristics - the characteristic to compute the macro recall from
	 * @return macro recall
	 */
	public static double getNBRecallMacro(List<Characteristic> characteristics) {
		double sum = 0;
		for(Characteristic c : characteristics) {
			sum += c.getNBRecall();
		}
		return sum / characteristics.size();
	}
	
	/**
	 * Compute the micro precision over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the micro precision from
	 * @return micro precision
	 */
	public static double getNBPrecisionMicro(List<Characteristic> characteristics) {
		double sumConfCorr = 0;
		int sumFP = 0;
		for(Characteristic c : characteristics) {
			sumConfCorr += c.getConfSumCorrect();
			sumFP += c.getFalsePositives().size();
		}
		return sumConfCorr / ((double)sumFP + sumConfCorr);
	}
	
	/**
	 * Compute the micro recall over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the micro recall from
	 * @return micro recall
	 */
	public static double getNBRecallMicro(List<Characteristic> characteristics) {
		double sumConfCorr = 0;
		double sumConfRef = 0;
		for(Characteristic c : characteristics) {
			sumConfCorr += c.getConfSumCorrect();
			sumConfRef += c.getConfSumReference();
		}
		return sumConfCorr / sumConfRef;
	}
	
	/**
	 * Compute the macro f measure over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the f measure from
	 * @return macro f measure
	 */
	public static double getNBFMeasureMacro(List<Characteristic> characteristics) {
		double sum = 0;
		for(Characteristic c : characteristics) {
			sum += c.getNBFMeasure();
		}
		return sum / characteristics.size();
	}
	
	/**
	 * Compute the micro f measure over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the f measure from
	 * @return micro f measure
	 */
	public static double getNBFMeasureMicro(List<Characteristic> characteristics) {
		double confSumRef = 0;
		double confSumCorr = 0;
		int numfp = 0;
		for(Characteristic c : characteristics) {
			confSumRef += c.getConfSumReference();
			confSumCorr += c.getConfSumCorrect();
			numfp += c.getFalsePositives().size();
		}
		double recall = confSumCorr / confSumRef;
		double precision = confSumCorr / ((double)numfp + confSumCorr) ;
		double fmeasure = Characteristic.computeFFromPR(precision, recall);
		return fmeasure;
	}
	
	public static double getNBPrecisionStdDev(List<Characteristic> characteristics) {
		double avgMacro = Characteristic.getNBPrecisionMacro(characteristics);
		double dev = 0;
		int numOfOcc = 0;
		for(Characteristic c : characteristics) {
			double currPrecision = c.getNBPrecision();
			if(!Double.isNaN(currPrecision)) {
				double currDev = Math.abs(currPrecision - avgMacro);
				dev += Math.pow(currDev, 2);	
				numOfOcc++;
			}
		}
		return Math.sqrt(dev/numOfOcc);
	}
	
	public static double getNBRecallStdDev(List<Characteristic> characteristics) {
		double avgMacro = Characteristic.getNBRecallMacro(characteristics);
		double dev = 0;
		for(Characteristic c : characteristics) {
			double currDev = Math.abs(c.getNBRecall() - avgMacro);
			dev += Math.pow(currDev, 2);
		}
		return Math.sqrt(dev/characteristics.size());
	}
	
	public static double getNBFMeasureStdDev(List<Characteristic> characteristics) {
		double avgMacro = Characteristic.getNBFMeasureMacro(characteristics);
		double dev = 0;
		for(Characteristic c : characteristics) {
			double currDev = Math.abs(c.getNBFMeasure() - avgMacro);
			dev += Math.pow(currDev, 2);
		}
		return Math.sqrt(dev/characteristics.size());
	}
	
	/**
	 * Compute the macro precision over a list of characteristics. The
	 * macro precision is the average of all the precision values of
	 * all characteristics. Note that this metric can be easily biased
	 * if the testsets are not equally large.
	 * @param characteristics - the characteristic to compute the macro precision from
	 * @return macro precision
	 */
	public static double getPrecisionMacro(List<Characteristic> characteristics) {
		double sum = 0;
		int numOfOcc = 0;
		for(Characteristic c : characteristics) {
			double currPrecision = c.getPrecision();
			if(!Double.isNaN(currPrecision)) {
				sum += currPrecision;
				numOfOcc++;
			}
		}
		return sum / numOfOcc;
	}
	
	/**
	 * Compute the macro recall over a list of characteristics. The
	 * macro recall is the average of all the recall values of
	 * all characteristics. Note that this metric can be easily biased
	 * if the testsets are not equally large.
	 * @param characteristics - the characteristic to compute the macro recall from
	 * @return macro recall
	 */
	public static double getRecallMacro(List<Characteristic> characteristics) {
		double sum = 0;
		for(Characteristic c : characteristics) {
			sum += c.getRecall();
		}
		return sum / characteristics.size();
	}
	
	/**
	 * Compute the micro precision over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the micro precision from
	 * @return micro precision
	 */
	public static double getPrecisionMicro(List<Characteristic> characteristics) {
		int sumNumOfRulesCorrect = 0;
		int sumNumOfRulesMatcher = 0;
		for(Characteristic c : characteristics) {
			sumNumOfRulesCorrect += c.getNumOfRulesCorrect();
			sumNumOfRulesMatcher += c.getNumOfRulesMatcher();
		}
		return sumNumOfRulesCorrect / (double)sumNumOfRulesMatcher;
	}
	
	/**
	 * Compute the micro recall over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the micro recall from
	 * @return micro recall
	 */
	public static double getRecallMicro(List<Characteristic> characteristics) {
		int sumNumOfRulesCorrect = 0;
		int sumNumOfRulesGold = 0;
		for(Characteristic c : characteristics) {
			sumNumOfRulesCorrect += c.getNumOfRulesCorrect();
			sumNumOfRulesGold += c.getNumOfRulesGold();
		}
		return computeRecall(sumNumOfRulesCorrect, sumNumOfRulesGold);
	}
	
	/**
	 * Compute the macro f measure over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the f measure from
	 * @return macro f measure
	 */
	public static double getFMeasureMacro(List<Characteristic> characteristics) {
		double sum = 0;
		for(Characteristic c : characteristics) {
			sum += c.getFMeasure();
		}
		return sum / characteristics.size();
	}
	
	/**
	 * Compute the micro f measure over a list of characteristics. 
	 * Avoids biasing the value by unequally large data sets.
	 * @param characteristics - the characteristic to compute the f measure from
	 * @return micro f measure
	 */
	public static double getFMeasureMicro(List<Characteristic> characteristics) {
		int sumNumOfMatcher = 0;
		int sumNumOfGold = 0;
		int sumNumOfCorrect = 0;
		for(Characteristic c : characteristics) {
			sumNumOfMatcher += c.getNumOfRulesMatcher();
			sumNumOfGold += c.getNumOfRulesGold();
			sumNumOfCorrect += c.getNumOfRulesCorrect();
		}
		return Characteristic.computeFFromPR(Characteristic.computeRecall(sumNumOfCorrect, sumNumOfMatcher), 
				Characteristic.computeRecall(sumNumOfCorrect, sumNumOfGold));
	}
	
	public static double getPrecisionStdDev(List<Characteristic> characteristics) {
		double avgMacro = Characteristic.getPrecisionMacro(characteristics);
		double dev = 0;
		int numOfOcc = 0;
		for(Characteristic c : characteristics) {
			double currPrecision = c.getPrecision();
			if(!Double.isNaN(currPrecision)) {
				double currDev = Math.abs(currPrecision - avgMacro);
				dev += Math.pow(currDev, 2);	
				numOfOcc++;
			}
		}
		return Math.sqrt(dev/numOfOcc);
	}
	
	public static double getRecallStdDev(List<Characteristic> characteristics) {
		double avgMacro = Characteristic.getRecallMacro(characteristics);
		double dev = 0;
		for(Characteristic c : characteristics) {
			double currDev = Math.abs(c.getRecall() - avgMacro);
			dev += Math.pow(currDev, 2);
		}
		return Math.sqrt(dev/characteristics.size());
	}
	
	public static double getFMeasureStdDev(List<Characteristic> characteristics) {
		double avgMacro = Characteristic.getFMeasureMacro(characteristics);
		double dev = 0;
		for(Characteristic c : characteristics) {
			double currDev = Math.abs(c.getFMeasure() - avgMacro);
			dev += Math.pow(currDev, 2);
		}
		return Math.sqrt(dev/characteristics.size());
	}
	
	
	public static double getRelativeDistance(List<Characteristic> characteristics) {
		List<Correspondence> allMatcherCorres = new ArrayList<>();
		for(Characteristic c : characteristics) {
			allMatcherCorres.addAll(c.getAlignmentMapping().getCorrespondences());
		}
		Map<Correspondence, Double> normConfs = getNormalizedConfidences(allMatcherCorres);
		double sum = 0;
		for(Map.Entry<Correspondence, Double> e: normConfs.entrySet()) {
			Correspondence cRef = null;
			for(Characteristic c : characteristics) {
				for(Correspondence cCurr : c.getAlignmentReference()) {
					if(e.getKey().equals(cCurr)) {
						cRef = cCurr;
						break;
					}
				}
			}
			double confRef = (cRef != null) ? cRef.getConfidence() : 0;
			double sqDev = Math.pow(e.getValue() - confRef, 2);
			sum += sqDev;
		}
		//Increase sum by squared deviation of correspondences not found by the matcher but
		//present in the reference alignment
		for(Characteristic c : characteristics) {
			Alignment alignOnlyRef = c.getAlignmentReference().minus(c.getAlignmentCorrect());
			for(Correspondence cOnlyRef : alignOnlyRef) {
				sum += Math.pow(cOnlyRef.getConfidence(), 2);
			}
		}
		return sum;
	}
	
	/**
	 * First line mathcer and second line matcher recogniation
	 * @param correspondences
	 * @return
	 */
	private static Map<Correspondence, Double> getNormalizedConfidences(List<Correspondence> correspondences) {
		Map<Correspondence, Double> vals = new HashMap<>();
		Correspondence maxCorrespondence = Collections.max(correspondences);
		double maxConf = maxCorrespondence.getConfidence();
		//Check for first line matcher
		long numDistinctVals = correspondences.stream()
				.mapToDouble(c -> c.getConfidence()).distinct().count();
		//The matcher is a second line matcher, simply normalize to 0 and 1
		if(numDistinctVals == 1) {
			for(Correspondence c : correspondences) {
				double confVal = (c.getConfidence()>0) ? 1 : 0;
				vals.put(c, confVal);
			}
		//The matcher is a first line matcher, normalize between 0.125 and 1	
		} else {
			for(Correspondence c : correspondences) {
				vals.put(c, c.getConfidence() / maxConf);
			}
			double minConf = Collections.min(vals.values());
			final double TARGET_MAX = 1;
			final double TARGET_MIN = 0.125;
			double mult = (TARGET_MAX - TARGET_MIN) / (1 - minConf);
			for(Map.Entry<Correspondence, Double> e : vals.entrySet()) {
				double finalConf = 1 - mult * (1 - e.getValue());
				vals.put(e.getKey(), finalConf);
			}
		}
		return vals;
	}
	

	public boolean isAllowZeros() {
		return allowZeros;
	}

	public void setAllowZeros(boolean allowZeros) {
		this.allowZeros = allowZeros;
	}

	public Alignment getAlignmentMapping() {
		return alignmentMapping;
	}

	public Alignment getAlignmentReference() {
		return alignmentReference;
	}

	public Alignment getAlignmentCorrect() {
		return alignmentCorrect;
	}
	
	
}
