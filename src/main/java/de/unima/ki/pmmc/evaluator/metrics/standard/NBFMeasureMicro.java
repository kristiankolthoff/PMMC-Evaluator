package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class NBFMeasureMicro implements Metric{

	@Override
	public double compute(List<Characteristic> characteristics) {
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

	@Override
	public String getName() {
		return "nb-fm-mic";
	}

}
