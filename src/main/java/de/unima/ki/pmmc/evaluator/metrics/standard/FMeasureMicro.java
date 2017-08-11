package de.unima.ki.pmmc.evaluator.metrics.standard;

import java.util.List;

import de.unima.ki.pmmc.evaluator.metrics.Characteristic;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public class FMeasureMicro implements Metric {

	@Override
	public double compute(List<Characteristic> characteristics) {
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

	@Override
	public String getName() {
		return "fm-mic";
	}

}
