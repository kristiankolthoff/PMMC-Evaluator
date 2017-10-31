package de.unima.ki.pmmc.evaluator.metrics.types;

import de.unima.ki.pmmc.evaluator.alignment.CorrespondenceType;
import de.unima.ki.pmmc.evaluator.metrics.Metric;

public abstract class AbstractTypeMetric implements Metric{

	protected CorrespondenceType[] types;
	
	public AbstractTypeMetric(CorrespondenceType ...types) {
		this.setTypes(types);
	}

	public CorrespondenceType[] getTypes() {
		return types;
	}

	public void setTypes(CorrespondenceType[] types) {
		this.types = types;
	}
}
