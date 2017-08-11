package de.unima.ki.pmmc.evaluator.metrics.types;

import java.util.List;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class TypeFMeasureMicro implements Metric {

	private CorrespondenceType type;
	
	public TypeFMeasureMicro(CorrespondenceType type) {
		this.type = type;
	}

	@Override
	public double compute(List<Characteristic> characteristics) {
		int sumNumOfMatcher = 0;
		int sumNumOfGold = 0;
		int sumNumOfCorrect = 0;
		for(Characteristic c : characteristics) {
			sumNumOfMatcher += c.getAlignmentMapping(type).size();
			sumNumOfGold += c.getAlignmentReference(type).size();
			sumNumOfCorrect += c.getAlignmentCorrect(type).size();
		}
		return Characteristic.computeFFromPR((sumNumOfCorrect / (double) sumNumOfMatcher), 
				(sumNumOfCorrect / (double) sumNumOfGold));
	}

	@Override
	public String getName() {
		return "fm-mic-" + type.getName();
	}

}
