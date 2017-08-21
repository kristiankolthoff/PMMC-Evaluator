package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeNBFMeasureMicro implements Metric {

	private CorrespondenceType[] type;
	
	
	public TypeNBFMeasureMicro(CorrespondenceType ...type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		double confSumRef = 0;
		double confSumCorr = 0;
		int numfp = 0;
		for(Characteristic c : characteristics) {
			confSumRef += c.getConfSumReference(type);
			confSumCorr += c.getConfSumCorrect(type);
			numfp += c.getFP(type).size();
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
