package de.unima.ki.pmmc.synthesizer.transformation.adding;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import de.unima.ki.pmmc.evaluator.model.Activity;

public interface BPMNAddStrategy extends AddStrategy {

	public BpmnModelInstance addActivity(BpmnModelInstance model, Activity activity);
}
