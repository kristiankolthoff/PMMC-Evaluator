package de.unima.ki.pmmc.synthesizer.transformation.adding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import de.unima.ki.pmmc.evaluator.model.Activity;

public class BPMNAddStrategyMeta implements BPMNAddStrategy {

	private List<BPMNAddStrategy> strategies;
	private Random random;
	
	public BPMNAddStrategyMeta(List<BPMNAddStrategy> strategies) {
		this.strategies = strategies;
		this.random = new Random();
	}
	
	public BPMNAddStrategyMeta() {
		this.strategies = new ArrayList<>();
		this.strategies.add(new BPMNAddStrategyUnconnected());
		this.strategies.add(new BPMNAddStrategyTaskSequential());
		this.strategies.add(new BPMNAddStrategyParallelGateway());
		this.random = new Random();
	}

	@Override
	public BpmnModelInstance addActivity(BpmnModelInstance model, Activity activity) {
		BPMNAddStrategy addStrategy = strategies.get(random.nextInt(strategies.size()));
		return addStrategy.addActivity(model, activity);
	}

}
