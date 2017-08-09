package de.unima.ki.pmmc.evaluator.metrics;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;


public interface Metric {

	/**
	 * 
	 * @param characteristics
	 * @return
	 */
	public double compute(List<Characteristic> characteristics);
	
	public String getName();
	
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
	static double computeMicro(List<Characteristic> characteristics,
			Function<Characteristic, Double> functionNum, Function<Characteristic, Double> functionDenom) {
		double sumNum = 0;
		double sumDenom = 0;
		for(Characteristic c : characteristics) {
			sumNum += functionNum.apply(c);
			sumDenom += functionDenom.apply(c);
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
	static double computeMacro(List<Characteristic> characteristics, 
			Function<Characteristic, Double> function) {
		double sum = 0;
		int numOfOcc = 0;
		for(Characteristic c : characteristics) {
			double currPrecision = function.apply(c);
			if(!Double.isNaN(currPrecision)) {
				numOfOcc++;
				sum += currPrecision;
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
	static double computeStdDev(List<Characteristic> characteristics, 
			Function<List<Characteristic>, Double> functionAvg, Function<Characteristic, Double> functionSum) {
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
	 * Computes the standard deviation over a list of <code>TypeCharacteristic</code>s
	 * based on values provided by the two input functions. The first function is
	 * used to compute the average value for the <code>TypeCharacteristic</code> collection,
	 * the second function provides the current value for a single <code>TypeCharacteristic</code>
	 * @param characteristics the typecharacteristics to compute the standard deviation from
	 * @param functionAvg function for producing the average value to compare the single/current
	 * values against
	 * @param functionSum function for producing the current value for a single <code>TypeCharacteristic</code>
	 * @return the standard deviation based on the provided functions
	 */
	static double computeStdDev(List<TypeCharacteristic> characteristics, 
			BiFunction<List<TypeCharacteristic>, CorrespondenceType, Double> functionAvg, 
			Function<TypeCharacteristic, Double> functionSum, CorrespondenceType type) {
		double avgMacro = functionAvg.apply(characteristics, type);
		double dev = 0;
		int numOfOcc = 0;
		for(TypeCharacteristic c : characteristics) {
			double val = functionSum.apply(c);
			if(!Double.isNaN(val)) {
				double currDev = Math.abs(val - avgMacro);
				dev += Math.pow(currDev, 2);
				numOfOcc++;
			}
		}
		return Math.sqrt(dev/numOfOcc);
		
	}
}
